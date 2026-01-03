package com.pulse.music.domain.usecase.sync

import com.pulse.music.domain.model.SyncManagerState
import com.pulse.music.domain.model.SyncResult
import com.pulse.music.domain.model.SyncTask
import com.pulse.music.domain.model.SyncTaskType
import com.pulse.music.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 觀察同步狀態
 */
class ObserveSyncStateUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    operator fun invoke(): Flow<SyncManagerState> = syncRepository.observeSyncState()
}

/**
 * 觀察待處理任務
 */
class ObservePendingTasksUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    operator fun invoke(): Flow<List<SyncTask>> = syncRepository.observePendingTasks()
}

/**
 * 加入 Scrobble 同步任務
 */
class EnqueueScrobbleUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(songId: Long, timestamp: Long, duration: Long) {
        syncRepository.enqueueScrobble(songId, timestamp, duration)
    }
}

/**
 * 執行同步
 */
class PerformSyncUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend fun syncAll(): List<SyncResult> {
        return syncRepository.processPendingTasks()
    }
    
    suspend fun retryFailed(): List<SyncResult> {
        return syncRepository.retryFailedTasks()
    }
}

/**
 * 管理同步任務
 */
class ManageSyncTasksUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend fun cancel(taskId: String) {
        syncRepository.cancelTask(taskId)
    }
    
    suspend fun clearCompleted() {
        syncRepository.clearCompletedTasks()
    }
    
    suspend fun clearAll() {
        syncRepository.clearAllTasks()
    }
    
    suspend fun getPendingCount(type: SyncTaskType): Int {
        return syncRepository.getPendingTaskCount(type)
    }
}

/**
 * 排程後台同步
 */
class ScheduleBackgroundSyncUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend fun schedule() {
        syncRepository.scheduleBackgroundSync()
    }
    
    suspend fun cancel() {
        syncRepository.cancelBackgroundSync()
    }
}
