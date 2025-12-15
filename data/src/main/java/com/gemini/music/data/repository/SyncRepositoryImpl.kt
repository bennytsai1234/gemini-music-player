package com.gemini.music.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gemini.music.domain.model.SyncManagerState
import com.gemini.music.domain.model.SyncResult
import com.gemini.music.domain.model.SyncTask
import com.gemini.music.domain.model.SyncTaskStatus
import com.gemini.music.domain.model.SyncTaskType
import com.gemini.music.domain.repository.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_tasks")

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncRepository {
    
    private val dataStore = context.syncDataStore
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _syncState = MutableStateFlow(SyncManagerState())
    private val _tasks = MutableStateFlow<List<SyncTask>>(emptyList())
    
    private object PreferencesKeys {
        val TASKS_JSON = stringPreferencesKey("sync_tasks_json")
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }
    
    init {
        // 初始化時從 DataStore 載入任務
        // 這裡簡化處理，實際應該在 ViewModel 初始化時調用
    }
    
    override fun observeSyncState(): Flow<SyncManagerState> = _syncState.asStateFlow()
    
    override fun observePendingTasks(): Flow<List<SyncTask>> {
        return _tasks.map { tasks ->
            tasks.filter { it.status == SyncTaskStatus.PENDING }
        }
    }
    
    override fun observeFailedTasks(): Flow<List<SyncTask>> {
        return _tasks.map { tasks ->
            tasks.filter { it.status == SyncTaskStatus.FAILED }
        }
    }
    
    override suspend fun enqueueTask(task: SyncTask) {
        _tasks.update { currentTasks ->
            currentTasks + task
        }
        updateSyncState()
        persistTasks()
    }
    
    override suspend fun enqueueScrobble(songId: Long, timestamp: Long, duration: Long) {
        val payload = json.encodeToString(
            mapOf(
                "songId" to songId.toString(),
                "timestamp" to timestamp.toString(),
                "duration" to duration.toString()
            )
        )
        
        val task = SyncTask(
            id = UUID.randomUUID().toString(),
            type = SyncTaskType.SCROBBLE,
            payload = payload,
            createdAt = System.currentTimeMillis(),
            priority = 1
        )
        
        enqueueTask(task)
    }
    
    override suspend fun processPendingTasks(): List<SyncResult> {
        if (!isNetworkAvailable()) {
            return emptyList()
        }
        
        _syncState.update { it.copy(isSyncing = true) }
        
        val pendingTasks = _tasks.value.filter { it.status == SyncTaskStatus.PENDING }
        val results = mutableListOf<SyncResult>()
        
        for (task in pendingTasks.sortedByDescending { it.priority }) {
            val result = executeTask(task)
            results.add(result)
            
            // 更新任務狀態
            _tasks.update { currentTasks ->
                currentTasks.map { t ->
                    if (t.id == task.id) {
                        when (result) {
                            is SyncResult.Success -> t.copy(status = SyncTaskStatus.COMPLETED)
                            is SyncResult.Failure -> t.copy(
                                status = if (t.attemptCount >= t.maxRetries) SyncTaskStatus.FAILED else SyncTaskStatus.PENDING,
                                attemptCount = t.attemptCount + 1,
                                lastAttemptAt = System.currentTimeMillis(),
                                errorMessage = result.error
                            )
                            is SyncResult.Cancelled -> t.copy(status = SyncTaskStatus.CANCELLED)
                        }
                    } else t
                }
            }
        }
        
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIME] = System.currentTimeMillis()
        }
        
        _syncState.update { 
            it.copy(
                isSyncing = false, 
                lastSyncTime = System.currentTimeMillis()
            ) 
        }
        
        persistTasks()
        updateSyncState()
        
        return results
    }
    
    private suspend fun executeTask(task: SyncTask): SyncResult {
        return try {
            when (task.type) {
                SyncTaskType.SCROBBLE -> {
                    // TODO: 實作 Last.fm Scrobble API 呼叫
                    SyncResult.Success(task.id, "Scrobble synced")
                }
                SyncTaskType.BACKUP -> {
                    // TODO: 實作雲端備份
                    SyncResult.Success(task.id, "Backup completed")
                }
                SyncTaskType.LYRICS_FETCH -> {
                    // TODO: 實作歌詞抓取
                    SyncResult.Success(task.id, "Lyrics fetched")
                }
                SyncTaskType.ALBUM_ART_FETCH -> {
                    // TODO: 實作專輯封面抓取
                    SyncResult.Success(task.id, "Album art fetched")
                }
                SyncTaskType.METADATA_UPDATE -> {
                    // TODO: 實作元數據更新
                    SyncResult.Success(task.id, "Metadata updated")
                }
            }
        } catch (e: Exception) {
            SyncResult.Failure(task.id, e.message ?: "Unknown error")
        }
    }
    
    override suspend fun retryFailedTasks(): List<SyncResult> {
        // 將失敗的任務重置為待處理
        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.status == SyncTaskStatus.FAILED && task.attemptCount < task.maxRetries) {
                    task.copy(status = SyncTaskStatus.PENDING)
                } else task
            }
        }
        
        return processPendingTasks()
    }
    
    override suspend fun cancelTask(taskId: String) {
        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.id == taskId) {
                    task.copy(status = SyncTaskStatus.CANCELLED)
                } else task
            }
        }
        persistTasks()
        updateSyncState()
    }
    
    override suspend fun clearCompletedTasks() {
        _tasks.update { currentTasks ->
            currentTasks.filter { it.status != SyncTaskStatus.COMPLETED }
        }
        persistTasks()
        updateSyncState()
    }
    
    override suspend fun clearAllTasks() {
        _tasks.value = emptyList()
        persistTasks()
        updateSyncState()
    }
    
    override suspend fun getPendingTaskCount(type: SyncTaskType): Int {
        return _tasks.value.count { it.type == type && it.status == SyncTaskStatus.PENDING }
    }
    
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    override suspend fun scheduleBackgroundSync() {
        // TODO: 使用 WorkManager 排程後台同步
        // WorkManager 實作需要在 :app 模組中配置
    }
    
    override suspend fun cancelBackgroundSync() {
        // TODO: 取消 WorkManager 任務
    }
    
    private suspend fun persistTasks() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASKS_JSON] = json.encodeToString(_tasks.value)
        }
    }
    
    private fun updateSyncState() {
        val tasks = _tasks.value
        _syncState.update { state ->
            state.copy(
                pendingTaskCount = tasks.count { it.status == SyncTaskStatus.PENDING },
                runningTaskCount = tasks.count { it.status == SyncTaskStatus.RUNNING },
                failedTaskCount = tasks.count { it.status == SyncTaskStatus.FAILED },
                isNetworkAvailable = isNetworkAvailable()
            )
        }
    }
}
