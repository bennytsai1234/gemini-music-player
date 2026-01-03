package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.repository.BackupRepository
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
