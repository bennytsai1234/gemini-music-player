package com.pulse.music.domain.usecase.queue

import com.pulse.music.domain.model.PlaybackHistoryItem
import com.pulse.music.domain.model.QueueAction
import com.pulse.music.domain.model.QueueSnapshot
import com.pulse.music.domain.model.QueueSource
import com.pulse.music.domain.repository.QueueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 觀察當前播放佇列
 */
class ObserveQueueUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    operator fun invoke(): Flow<QueueSnapshot?> = queueRepository.observeCurrentQueue()
}

/**
 * 觀察播放歷史
 */
class ObservePlaybackHistoryUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    operator fun invoke(): Flow<List<PlaybackHistoryItem>> = 
        queueRepository.observePlaybackHistory()
}

/**
 * 儲存當前佇列
 */
class SaveQueueSnapshotUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend operator fun invoke(snapshot: QueueSnapshot) {
        queueRepository.saveCurrentQueue(snapshot)
    }
    
    suspend fun saveAsNamed(name: String, songIds: List<Long>) {
        queueRepository.saveNamedSnapshot(name, songIds)
    }
}

/**
 * 恢復佇列 (Process Death 恢復)
 */
class RestoreQueueUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend fun restoreLast(): QueueSnapshot? {
        return queueRepository.getLastQueueSnapshot()
    }
    
    suspend fun restoreById(snapshotId: String): QueueSnapshot? {
        return queueRepository.restoreQueue(snapshotId)
    }
}

/**
 * 執行佇列操作
 */
class QueueActionUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend fun addToNext(songId: Long) {
        queueRepository.performQueueAction(QueueAction.AddToNext(songId))
    }
    
    suspend fun addToEnd(songId: Long) {
        queueRepository.performQueueAction(QueueAction.AddToEnd(songId))
    }
    
    suspend fun addMultiple(songIds: List<Long>, position: Int? = null) {
        queueRepository.performQueueAction(QueueAction.AddMultiple(songIds, position))
    }
    
    suspend fun remove(index: Int) {
        queueRepository.performQueueAction(QueueAction.Remove(index))
    }
    
    suspend fun move(fromIndex: Int, toIndex: Int) {
        queueRepository.performQueueAction(QueueAction.Move(fromIndex, toIndex))
    }
    
    suspend fun jumpTo(index: Int) {
        queueRepository.performQueueAction(QueueAction.JumpTo(index))
    }
    
    suspend fun clear() {
        queueRepository.performQueueAction(QueueAction.Clear)
    }
    
    suspend fun shuffle() {
        queueRepository.performQueueAction(QueueAction.Shuffle)
    }
    
    suspend fun replace(songIds: List<Long>, startIndex: Int = 0) {
        queueRepository.performQueueAction(QueueAction.Replace(songIds, startIndex))
    }
}

/**
 * 記錄播放
 */
class RecordPlaybackUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend operator fun invoke(
        songId: Long,
        playedAt: Long,
        durationPlayed: Long,
        wasCompleted: Boolean,
        source: QueueSource
    ) {
        queueRepository.recordPlayback(
            PlaybackHistoryItem(
                songId = songId,
                playedAt = playedAt,
                durationPlayed = durationPlayed,
                wasCompleted = wasCompleted,
                source = source
            )
        )
    }
}

/**
 * 管理已儲存的佇列快照
 */
class ManageSavedSnapshotsUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    fun observeSnapshots(): Flow<List<QueueSnapshot>> = 
        queueRepository.observeSavedSnapshots()
    
    suspend fun delete(snapshotId: String) {
        queueRepository.deleteSnapshot(snapshotId)
    }
}

/**
 * 更新當前播放位置
 */
class UpdatePlaybackPositionUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend operator fun invoke(index: Int, positionMs: Long) {
        queueRepository.updateCurrentPosition(index, positionMs)
    }
}
