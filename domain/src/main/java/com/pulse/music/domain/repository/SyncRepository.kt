package com.pulse.music.domain.repository

import com.pulse.music.domain.model.SyncManagerState
import com.pulse.music.domain.model.SyncResult
import com.pulse.music.domain.model.SyncTask
import com.pulse.music.domain.model.SyncTaskType
import kotlinx.coroutines.flow.Flow

/**
 * 離線同步 Repository
 */
interface SyncRepository {
    
    /**
     * 觀察同步管理器狀態
     */
    fun observeSyncState(): Flow<SyncManagerState>
    
    /**
     * 觀察待處理任務
     */
    fun observePendingTasks(): Flow<List<SyncTask>>
    
    /**
     * 觀察失敗任務
     */
    fun observeFailedTasks(): Flow<List<SyncTask>>
    
    /**
     * 加入同步任務
     */
    suspend fun enqueueTask(task: SyncTask)
    
    /**
     * 加入 Scrobble 任務
     */
    suspend fun enqueueScrobble(songId: Long, timestamp: Long, duration: Long)
    
    /**
     * 執行所有待處理任務
     */
    suspend fun processPendingTasks(): List<SyncResult>
    
    /**
     * 重試失敗的任務
     */
    suspend fun retryFailedTasks(): List<SyncResult>
    
    /**
     * 取消任務
     */
    suspend fun cancelTask(taskId: String)
    
    /**
     * 清除已完成的任務
     */
    suspend fun clearCompletedTasks()
    
    /**
     * 清除所有任務
     */
    suspend fun clearAllTasks()
    
    /**
     * 取得特定類型的待處理任務數量
     */
    suspend fun getPendingTaskCount(type: SyncTaskType): Int
    
    /**
     * 檢查網路狀態
     */
    fun isNetworkAvailable(): Boolean
    
    /**
     * 排程後台同步 (WorkManager)
     */
    suspend fun scheduleBackgroundSync()
    
    /**
     * 取消後台同步
     */
    suspend fun cancelBackgroundSync()
}
