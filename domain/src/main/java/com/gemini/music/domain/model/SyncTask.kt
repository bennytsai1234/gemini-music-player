package com.gemini.music.domain.model

import kotlinx.serialization.Serializable

/**
 * 離線同步任務類型
 */
enum class SyncTaskType {
    SCROBBLE,           // Last.fm Scrobble
    BACKUP,             // 雲端備份
    LYRICS_FETCH,       // 歌詞抓取
    ALBUM_ART_FETCH,    // 專輯封面抓取
    METADATA_UPDATE     // 元數據更新
}

/**
 * 同步任務狀態
 */
enum class SyncTaskStatus {
    PENDING,    // 待處理
    RUNNING,    // 進行中
    COMPLETED,  // 已完成
    FAILED,     // 失敗
    CANCELLED   // 已取消
}

/**
 * 離線同步任務
 */
@Serializable
data class SyncTask(
    val id: String,
    val type: SyncTaskType,
    val payload: String,        // JSON 序列化的任務數據
    val status: SyncTaskStatus = SyncTaskStatus.PENDING,
    val createdAt: Long,
    val lastAttemptAt: Long? = null,
    val attemptCount: Int = 0,
    val maxRetries: Int = 3,
    val errorMessage: String? = null,
    val priority: Int = 0       // 優先級 (越高越先執行)
)

/**
 * 同步管理器狀態
 */
data class SyncManagerState(
    val pendingTaskCount: Int = 0,
    val runningTaskCount: Int = 0,
    val failedTaskCount: Int = 0,
    val lastSyncTime: Long? = null,
    val isNetworkAvailable: Boolean = true,
    val isSyncing: Boolean = false
)

/**
 * 同步結果
 */
sealed class SyncResult {
    data class Success(val taskId: String, val message: String? = null) : SyncResult()
    data class Failure(val taskId: String, val error: String, val canRetry: Boolean = true) : SyncResult()
    data class Cancelled(val taskId: String) : SyncResult()
}
