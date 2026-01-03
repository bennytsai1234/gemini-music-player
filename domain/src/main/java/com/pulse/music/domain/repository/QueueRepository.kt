package com.pulse.music.domain.repository

import com.pulse.music.domain.model.PlaybackHistoryItem
import com.pulse.music.domain.model.QueueAction
import com.pulse.music.domain.model.QueueSnapshot
import com.pulse.music.domain.model.QueueSource
import kotlinx.coroutines.flow.Flow

/**
 * 播放佇列管理 Repository
 */
interface QueueRepository {
    
    /**
     * 觀察當前佇列快照
     */
    fun observeCurrentQueue(): Flow<QueueSnapshot?>
    
    /**
     * 觀察播放歷史
     */
    fun observePlaybackHistory(): Flow<List<PlaybackHistoryItem>>
    
    /**
     * 儲存當前佇列快照
     */
    suspend fun saveCurrentQueue(snapshot: QueueSnapshot)
    
    /**
     * 恢復佇列快照
     */
    suspend fun restoreQueue(snapshotId: String): QueueSnapshot?
    
    /**
     * 取得最近的佇列快照 (用於 Process Death 恢復)
     */
    suspend fun getLastQueueSnapshot(): QueueSnapshot?
    
    /**
     * 執行佇列操作
     */
    suspend fun performQueueAction(action: QueueAction)
    
    /**
     * 記錄播放歷史
     */
    suspend fun recordPlayback(historyItem: PlaybackHistoryItem)
    
    /**
     * 取得最近播放的歌曲 IDs
     */
    suspend fun getRecentlyPlayedSongIds(limit: Int = 50): List<Long>
    
    /**
     * 清除播放歷史
     */
    suspend fun clearPlaybackHistory()
    
    /**
     * 儲存已命名的佇列快照
     */
    suspend fun saveNamedSnapshot(name: String, songIds: List<Long>)
    
    /**
     * 取得所有已儲存的佇列快照
     */
    fun observeSavedSnapshots(): Flow<List<QueueSnapshot>>
    
    /**
     * 刪除已儲存的快照
     */
    suspend fun deleteSnapshot(snapshotId: String)
    
    /**
     * 更新當前播放位置 (用於恢復)
     */
    suspend fun updateCurrentPosition(index: Int, positionMs: Long)
}
