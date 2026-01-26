package com.pulse.music.domain.usecase.backup

import com.pulse.music.domain.model.backup.PulseBackup
import com.pulse.music.domain.repository.BackupRepository
import kotlinx.coroutines.flow.flow
import java.io.OutputStream
import javax.inject.Inject

class CreateBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): PulseBackup {
        return backupRepository.createBackup()
    }
}

class ExportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(outputStream: OutputStream) {
        val backup = backupRepository.createBackup()
        backupRepository.exportBackupToStream(backup, outputStream)
    }
}
