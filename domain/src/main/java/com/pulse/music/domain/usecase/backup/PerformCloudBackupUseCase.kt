package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.model.backup.BackupResult
import com.pulse.music.domain.repository.BackupRepository
import javax.inject.Inject

class PerformCloudBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): BackupResult {
        return backupRepository.performCloudBackup()
    }
}
