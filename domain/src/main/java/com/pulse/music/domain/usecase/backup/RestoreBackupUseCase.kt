package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.model.backup.RestoreResult
import com.pulse.music.domain.repository.BackupRepository
import java.io.InputStream
import javax.inject.Inject

class RestoreBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(inputStream: InputStream, merge: Boolean = true): RestoreResult {
        val backup = backupRepository.importBackupFromStream(inputStream)
        return backupRepository.restoreBackup(backup, merge)
    }
}
