package com.pulse.music.domain.repository

import com.pulse.music.domain.model.SmbResource
import kotlinx.coroutines.flow.Flow

interface SmbRepository {
    /**
     * List contents of an SMB directory.
     * @param url The full SMB URL (e.g., "smb://192.168.1.10/Music/")
     */
    fun listFiles(url: String): Flow<List<SmbResource>>

    /**
     * Check if a path is accessible (and validate credentials implicitly).
     */
    suspend fun checkConnection(url: String): Boolean
}
