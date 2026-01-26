package com.pulse.music.data.source.smb

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.File
import kotlinx.coroutines.runBlocking
import java.io.EOFException
import java.io.IOException
import java.util.EnumSet
import javax.inject.Inject

class SmbDataSource(
    private val smbClientManager: SmbClientManager
) : BaseDataSource(true) {

    private var file: File? = null
    private var uri: Uri? = null
    private var bytesRemaining: Long = 0
    private var currentPosition: Long = 0
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        transferInitializing(dataSpec)

        try {
            val url = dataSpec.uri.toString()
            val (host, share, path) = parseSmbUrl(url)
            val smbPath = path.replace('/', '\\').trim('\\')

            // Blocking call to get share/file on the playback thread
            val diskShare = runBlocking {
                smbClientManager.getDiskShare(host, share)
            }

            val openedFile = diskShare.openFile(
                smbPath,
                EnumSet.of(AccessMask.GENERIC_READ),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
            file = openedFile

            val fileLength = openedFile.fileInformation.standardInformation.endOfFile
            if (dataSpec.position > fileLength) {
                throw EOFException("Position ${dataSpec.position} > length $fileLength")
            }
            
            val length = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
                dataSpec.length
            } else {
                fileLength - dataSpec.position
            }
            
            bytesRemaining = length
            currentPosition = dataSpec.position
            opened = true
            transferStarted(dataSpec)
            return length

        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        if (readLength == 0) return 0
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT

        val bytesToRead = if (bytesRemaining == C.LENGTH_UNSET.toLong()) {
            readLength
        } else {
            minOf(bytesRemaining, readLength.toLong()).toInt()
        }

        val currentFile = file ?: throw IOException("File not opened")
        
        return try {
            // smbj read signature: read(buffer, fileOffset, bufferOffset, length)
            val bytesRead = currentFile.read(buffer, currentPosition, offset, bytesToRead)
            
            if (bytesRead > 0) {
                currentPosition += bytesRead
                if (bytesRemaining != C.LENGTH_UNSET.toLong()) {
                    bytesRemaining -= bytesRead
                }
                bytesTransferred(bytesRead)
            }
            
            if (bytesRead == -1 && bytesRemaining > 0) {
                 // Should not happen if size check passed, but handle EOF
                 return C.RESULT_END_OF_INPUT
            }
            
            // smbj returns 0 if no bytes read but not EOF? Or -1?
            // Usually -1 for EOF. If 0, we might need to return 0.
             if (bytesRead == -1) C.RESULT_END_OF_INPUT else bytesRead
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        uri = null
        if (opened) {
            opened = false
            transferEnded()
        }
        file?.close()
        file = null
    }
    
    // Duplicated private parser - in real app should be in utility
    private fun parseSmbUrl(url: String): Triple<String, String, String> {
        val cleanUrl = url.removePrefix("smb://")
        val parts = cleanUrl.split("/", limit = 3)
        val host = parts.getOrElse(0) { "" }
        val share = parts.getOrElse(1) { "" }
        val path = parts.getOrElse(2) { "" }
        if (host.isEmpty() || share.isEmpty()) throw IOException("Invalid SMB URL")
        return Triple(host, share, path)
    }
    
    class Factory @Inject constructor(
        private val smbClientManager: SmbClientManager
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return SmbDataSource(smbClientManager)
        }
    }
}
