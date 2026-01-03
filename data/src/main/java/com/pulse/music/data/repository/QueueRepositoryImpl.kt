package com.pulse.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pulse.music.domain.model.PlaybackHistoryItem
import com.pulse.music.domain.model.QueueAction
import com.pulse.music.domain.model.QueueSnapshot
import com.pulse.music.domain.model.QueueSource
import com.pulse.music.domain.repository.QueueRepository
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

private val Context.queueDataStore: DataStore<Preferences> by preferencesDataStore(name = "queue_state")

@Singleton
class QueueRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : QueueRepository {
    
    private val dataStore = context.queueDataStore
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _currentQueue = MutableStateFlow<QueueSnapshot?>(null)
    private val _playbackHistory = MutableStateFlow<List<PlaybackHistoryItem>>(emptyList())
    private val _savedSnapshots = MutableStateFlow<List<QueueSnapshot>>(emptyList())
    
    private object PreferencesKeys {
        val CURRENT_QUEUE_JSON = stringPreferencesKey("current_queue_json")
        val PLAYBACK_HISTORY_JSON = stringPreferencesKey("playback_history_json")
        val SAVED_SNAPSHOTS_JSON = stringPreferencesKey("saved_snapshots_json")
        val CURRENT_INDEX = intPreferencesKey("current_index")
        val CURRENT_POSITION_MS = longPreferencesKey("current_position_ms")
    }
    
    init {
        // 初始化時載入狀態 - 應該在協程中調用
    }
    
    override fun observeCurrentQueue(): Flow<QueueSnapshot?> = _currentQueue.asStateFlow()
    
    override fun observePlaybackHistory(): Flow<List<PlaybackHistoryItem>> = 
        _playbackHistory.asStateFlow()
    
    override suspend fun saveCurrentQueue(snapshot: QueueSnapshot) {
        _currentQueue.value = snapshot
        persistCurrentQueue()
    }
    
    override suspend fun restoreQueue(snapshotId: String): QueueSnapshot? {
        return _savedSnapshots.value.find { it.id == snapshotId }
    }
    
    override suspend fun getLastQueueSnapshot(): QueueSnapshot? {
        // 從 DataStore 載入
        val preferences = dataStore.data.first()
        val jsonString = preferences[PreferencesKeys.CURRENT_QUEUE_JSON] ?: return null
        
        return try {
            json.decodeFromString<QueueSnapshot>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun performQueueAction(action: QueueAction) {
        val currentSnapshot = _currentQueue.value ?: return
        
        val updatedSnapshot = when (action) {
            is QueueAction.AddToNext -> {
                val newSongIds = currentSnapshot.songIds.toMutableList()
                newSongIds.add(currentSnapshot.currentIndex + 1, action.songId)
                currentSnapshot.copy(
                    songIds = newSongIds,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.AddToEnd -> {
                currentSnapshot.copy(
                    songIds = currentSnapshot.songIds + action.songId,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.AddMultiple -> {
                val newSongIds = currentSnapshot.songIds.toMutableList()
                val insertPosition = action.position ?: newSongIds.size
                newSongIds.addAll(insertPosition, action.songIds)
                currentSnapshot.copy(
                    songIds = newSongIds,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.Remove -> {
                val newSongIds = currentSnapshot.songIds.toMutableList()
                if (action.index in newSongIds.indices) {
                    newSongIds.removeAt(action.index)
                }
                val newIndex = when {
                    action.index < currentSnapshot.currentIndex -> currentSnapshot.currentIndex - 1
                    action.index == currentSnapshot.currentIndex -> currentSnapshot.currentIndex.coerceAtMost(newSongIds.size - 1)
                    else -> currentSnapshot.currentIndex
                }
                currentSnapshot.copy(
                    songIds = newSongIds,
                    currentIndex = newIndex.coerceAtLeast(0),
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.Move -> {
                val newSongIds = currentSnapshot.songIds.toMutableList()
                if (action.fromIndex in newSongIds.indices && action.toIndex in newSongIds.indices) {
                    val item = newSongIds.removeAt(action.fromIndex)
                    newSongIds.add(action.toIndex, item)
                }
                
                // 調整當前索引
                val newIndex = when {
                    currentSnapshot.currentIndex == action.fromIndex -> action.toIndex
                    action.fromIndex < currentSnapshot.currentIndex && action.toIndex >= currentSnapshot.currentIndex -> 
                        currentSnapshot.currentIndex - 1
                    action.fromIndex > currentSnapshot.currentIndex && action.toIndex <= currentSnapshot.currentIndex -> 
                        currentSnapshot.currentIndex + 1
                    else -> currentSnapshot.currentIndex
                }
                
                currentSnapshot.copy(
                    songIds = newSongIds,
                    currentIndex = newIndex,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.JumpTo -> {
                currentSnapshot.copy(
                    currentIndex = action.index.coerceIn(0, currentSnapshot.songIds.size - 1),
                    currentPositionMs = 0,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.Clear -> {
                currentSnapshot.copy(
                    songIds = emptyList(),
                    currentIndex = 0,
                    currentPositionMs = 0,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.Shuffle -> {
                val currentSongId = currentSnapshot.songIds.getOrNull(currentSnapshot.currentIndex)
                val shuffledIds = currentSnapshot.songIds.shuffled()
                val newIndex = shuffledIds.indexOf(currentSongId).coerceAtLeast(0)
                currentSnapshot.copy(
                    songIds = shuffledIds,
                    currentIndex = newIndex,
                    shuffleMode = true,
                    updatedAt = System.currentTimeMillis()
                )
            }
            is QueueAction.Replace -> {
                currentSnapshot.copy(
                    songIds = action.songIds,
                    currentIndex = action.startIndex.coerceIn(0, (action.songIds.size - 1).coerceAtLeast(0)),
                    currentPositionMs = 0,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
        
        _currentQueue.value = updatedSnapshot
        persistCurrentQueue()
    }
    
    override suspend fun recordPlayback(historyItem: PlaybackHistoryItem) {
        _playbackHistory.update { history ->
            // 保留最近 500 條記錄
            (listOf(historyItem) + history).take(500)
        }
        persistPlaybackHistory()
    }
    
    override suspend fun getRecentlyPlayedSongIds(limit: Int): List<Long> {
        return _playbackHistory.value
            .take(limit)
            .map { it.songId }
            .distinct()
    }
    
    override suspend fun clearPlaybackHistory() {
        _playbackHistory.value = emptyList()
        persistPlaybackHistory()
    }
    
    override suspend fun saveNamedSnapshot(name: String, songIds: List<Long>) {
        val snapshot = QueueSnapshot(
            id = UUID.randomUUID().toString(),
            name = name,
            songIds = songIds,
            currentIndex = 0,
            currentPositionMs = 0,
            shuffleMode = false,
            repeatMode = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            source = QueueSource.UNKNOWN
        )
        
        _savedSnapshots.update { it + snapshot }
        persistSavedSnapshots()
    }
    
    override fun observeSavedSnapshots(): Flow<List<QueueSnapshot>> = _savedSnapshots.asStateFlow()
    
    override suspend fun deleteSnapshot(snapshotId: String) {
        _savedSnapshots.update { snapshots ->
            snapshots.filter { it.id != snapshotId }
        }
        persistSavedSnapshots()
    }
    
    override suspend fun updateCurrentPosition(index: Int, positionMs: Long) {
        _currentQueue.update { snapshot ->
            snapshot?.copy(
                currentIndex = index,
                currentPositionMs = positionMs,
                updatedAt = System.currentTimeMillis()
            )
        }
        
        // 頻繁更新位置，只更新關鍵欄位
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_INDEX] = index
            preferences[PreferencesKeys.CURRENT_POSITION_MS] = positionMs
        }
    }
    
    private suspend fun persistCurrentQueue() {
        val snapshot = _currentQueue.value ?: return
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_QUEUE_JSON] = json.encodeToString(snapshot)
        }
    }
    
    private suspend fun persistPlaybackHistory() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_HISTORY_JSON] = json.encodeToString(_playbackHistory.value)
        }
    }
    
    private suspend fun persistSavedSnapshots() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVED_SNAPSHOTS_JSON] = json.encodeToString(_savedSnapshots.value)
        }
    }
}
