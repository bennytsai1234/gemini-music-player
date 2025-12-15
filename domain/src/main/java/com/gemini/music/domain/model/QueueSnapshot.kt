package com.gemini.music.domain.model

import kotlinx.serialization.Serializable

/**
 * 播放佇列快照 - 用於持久化和恢復
 */
@Serializable
data class QueueSnapshot(
    val id: String,                     // 快照 ID
    val name: String,                   // 快照名稱 (e.g., "Now Playing", "Saved Queue 1")
    val songIds: List<Long>,            // 歌曲 ID 列表
    val currentIndex: Int,              // 當前播放索引
    val currentPositionMs: Long,        // 當前播放位置 (毫秒)
    val shuffleMode: Boolean,           // 隨機模式
    val repeatMode: Int,                // 重複模式 (0=None, 1=All, 2=One)
    val createdAt: Long,                // 創建時間
    val updatedAt: Long,                // 更新時間
    val source: QueueSource             // 佇列來源
)

/**
 * 佇列來源
 */
@Serializable
enum class QueueSource {
    ALL_SONGS,
    ALBUM,
    ARTIST,
    PLAYLIST,
    FOLDER,
    SEARCH,
    RECOMMENDATION,
    SMART_PLAYLIST,
    UNKNOWN
}

/**
 * 播放歷史項目
 */
@Serializable
data class PlaybackHistoryItem(
    val songId: Long,
    val playedAt: Long,
    val durationPlayed: Long,   // 實際播放時長 (毫秒)
    val wasCompleted: Boolean,  // 是否播放完畢
    val source: QueueSource
)

/**
 * 佇列操作
 */
sealed class QueueAction {
    data class AddToNext(val songId: Long) : QueueAction()
    data class AddToEnd(val songId: Long) : QueueAction()
    data class AddMultiple(val songIds: List<Long>, val position: Int? = null) : QueueAction()
    data class Remove(val index: Int) : QueueAction()
    data class Move(val fromIndex: Int, val toIndex: Int) : QueueAction()
    data class JumpTo(val index: Int) : QueueAction()
    object Clear : QueueAction()
    object Shuffle : QueueAction()
    data class Replace(val songIds: List<Long>, val startIndex: Int = 0) : QueueAction()
}
