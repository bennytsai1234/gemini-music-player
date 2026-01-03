package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.model.backup.RestoreResult
import com.pulse.music.domain.repository.BackupRepository
import javax.inject.Inject

class RestoreBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(backupId: String = "latest"): RestoreResult {
        return backupRepository.performRestore(backupId)
    }
}
