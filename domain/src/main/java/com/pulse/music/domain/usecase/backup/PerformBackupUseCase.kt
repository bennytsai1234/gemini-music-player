package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.model.backup.BackupResult
import com.pulse.music.domain.repository.BackupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PerformBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): BackupResult {
        return backupRepository.performBackup()
    }
}
