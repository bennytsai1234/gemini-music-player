package com.gemini.music.domain.usecase.backup

import com.gemini.music.domain.model.backup.BackupResult
import com.gemini.music.domain.repository.BackupRepository
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
