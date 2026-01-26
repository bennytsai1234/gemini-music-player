package com.pulse.music.data.repository

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.File
import com.pulse.music.data.source.smb.SmbClientManager
import com.pulse.music.domain.model.SmbResource
import com.pulse.music.domain.repository.SmbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmbRepositoryImpl @Inject constructor(
    private val smbClientManager: SmbClientManager
) : SmbRepository {

    override fun listFiles(url: String): Flow<List<SmbResource>> = flow {
        val (host, share, path) = parseSmbUrl(url)
        
        // TODO: Handle authentication lookup based on host
        val diskShare = smbClientManager.getDiskShare(host, share)
        
        // Ensure path uses backslashes for SMB
        val smbPath = path.replace('/', '\\').trim('\\')
        
        val files = diskShare.list(smbPath)
            .filter { it.fileName != "." && it.fileName != ".." }
            .map { fileId ->
                val isDir = (fileId.fileAttributes and 16L) != 0L // FILE_ATTRIBUTE_DIRECTORY = 0x10
                SmbResource(
                    path = buildSmbUrl(host, share, path, fileId.fileName),
                    name = fileId.fileName,
                    isDirectory = isDir,
                    size = fileId.endOfFile,
                    lastModified = fileId.changeTime.toEpochMillis()
                )
            }
        
        emit(files)
    }

    override suspend fun checkConnection(url: String): Boolean {
        return try {
            val (host, share, _) = parseSmbUrl(url)
            smbClientManager.getDiskShare(host, share)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun parseSmbUrl(url: String): Triple<String, String, String> {
        // Simple parser: smb://host/share/path/to/file
        val cleanUrl = url.removePrefix("smb://")
        val parts = cleanUrl.split("/", limit = 3)
        
        val host = parts.getOrElse(0) { "" }
        val share = parts.getOrElse(1) { "" }
        val path = parts.getOrElse(2) { "" }
        
        if (host.isEmpty() || share.isEmpty()) {
            throw IllegalArgumentException("Invalid SMB URL: $url. Must be smb://host/share/...")
        }
        
        return Triple(host, share, path)
    }

    private fun buildSmbUrl(host: String, share: String, parentPath: String, fileName: String): String {
        val cleanParent = parentPath.trim('/')
        val relative = if (cleanParent.isEmpty()) fileName else "$cleanParent/$fileName"
        return "smb://$host/$share/$relative"
    }
}
