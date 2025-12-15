package com.gemini.music.domain.usecase.backup

import com.gemini.music.domain.repository.BackupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBackupStatusUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    val isSignedIn: Flow<Boolean> = backupRepository.isSignedIn()
    
    suspend fun getLastBackupTime(): Long? {
        return backupRepository.getLastBackupTime()
    }
}
