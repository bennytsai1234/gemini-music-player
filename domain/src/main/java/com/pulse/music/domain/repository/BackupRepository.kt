package com.pulse.music.domain.repository

import com.pulse.music.domain.model.backup.BackupResult
import com.pulse.music.domain.model.backup.PulseBackup
import com.pulse.music.domain.model.backup.RestoreResult
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream

/**
 * 備份儲存庫介面。
 * 負責處理備份數據的導入導出與雲端同步。
 */
interface BackupRepository {
    
    /**
     * 建立完整的備份物件 (In-Memory)
     */
    suspend fun createBackup(): PulseBackup

    /**
     * 將備份物件還原至資料庫
     * @param backup 備份資料
     * @param merge 若為 true，則保留現有資料並合併；若為 false，則覆蓋 (未實作覆蓋，通常建議合併)
     */
    suspend fun restoreBackup(backup: PulseBackup, merge: Boolean = true): RestoreResult

    // --- File I/O (Local / SAF) ---
    suspend fun exportBackupToStream(backup: PulseBackup, outputStream: OutputStream)
    suspend fun importBackupFromStream(inputStream: InputStream): PulseBackup

    // --- Cloud (Google Drive) - Legacy/Future ---
    suspend fun performCloudBackup(): BackupResult
    suspend fun performCloudRestore(backupId: String): RestoreResult
    suspend fun signIn(): Boolean
    suspend fun signOut()
    fun isSignedIn(): Flow<Boolean>
    suspend fun getLastBackupTime(): Long?
}
