package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.repository.BackupRepository
import javax.inject.Inject

class ManageBackupSessionUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend fun signIn(): Boolean {
        return backupRepository.signIn()
    }
    
    suspend fun signOut() {
        backupRepository.signOut()
    }
}
