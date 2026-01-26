package com.pulse.music.domain.model.backup

import kotlinx.serialization.Serializable

/**
 * 雲端/本地備份的主要資料結構。
 * 使用 Kotlin Serialization 進行 JSON 序列化。
 *
 * Version 2: 改用 Metadata 匹配歌曲，而非 ID (ID 在重新安裝/掃描後會改變)
 */
@Serializable
data class PulseBackup(
    val version: Int = 2,
    val timestamp: Long,
    val playlists: List<PlaylistBackup> = emptyList(),
    val favorites: List<SongMetadataBackup> = emptyList(),
    val playbackHistory: List<HistoryBackup> = emptyList(),
    val songStats: List<SongStatsBackup> = emptyList()
)

@Serializable
data class SongMetadataBackup(
    val title: String,
    val artist: String,
    val album: String
)

@Serializable
data class PlaylistBackup(
    val name: String,
    val tracks: List<SongMetadataBackup>
)

@Serializable
data class HistoryBackup(
    val song: SongMetadataBackup,
    val playedAt: Long
)

@Serializable
data class SongStatsBackup(
    val song: SongMetadataBackup,
    val playCount: Int,
    val skipCount: Int,
    val lastPlayed: Long
)

/**
 * 備份操作的結果狀態
 */
sealed class BackupResult {
    data object Success : BackupResult()
    data class Error(val message: String, val throwable: Throwable? = null) : BackupResult()
}

/**
 * 還原操作的結果狀態
 */
data class RestoreResult(
    val playlistsRestored: Int = 0,
    val favoritesRestored: Int = 0,
    val historyRestored: Int = 0,
    val statsRestored: Int = 0,
    val errors: List<String> = emptyList()
)
