package com.pulse.music.domain.repository

import com.pulse.music.domain.model.backup.BackupResult
import com.pulse.music.domain.model.backup.RestoreResult
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream

/**
 * 雲端備份儲存庫介面。
 * 負責處理備份數據的導入導出與雲端同步。
 */
interface BackupRepository {
    
    /**
     * 觸發備份流程。
     * 將會讀取所有需要備份的本地數據 (Playlists, Favorites, Settings)，
     * 打包成 JSON 並上傳到雲端。
     */
    suspend fun performBackup(): BackupResult
    
    /**
     * 從雲端還原數據。
     * 將會下載備份檔，解析並覆蓋/合併到本地數據庫。
     */
    suspend fun performRestore(backupId: String): RestoreResult
    
    /**
     * 登入雲端服務 (如 Google Drive)。
     */
    suspend fun signIn(): Boolean
    
    /**
     * 登出雲端服務。
     */
    suspend fun signOut()
    
    /**
     * 檢查是否已登入。
     */
    fun isSignedIn(): Flow<Boolean>
    
    /**
     * 獲取最近的備份時間。
     */
    suspend fun getLastBackupTime(): Long?
}
