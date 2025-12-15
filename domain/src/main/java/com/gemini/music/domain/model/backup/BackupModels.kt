package com.gemini.music.domain.model.backup

import kotlinx.serialization.Serializable

/**
 * 雲端備份的主要資料結構。
 * 使用 Kotlin Serialization 進行 JSON 序列化。
 */
@Serializable
data class BackupData(
    val version: Int = 1,
    val timestamp: Long,
    val playlists: List<BackupPlaylist> = emptyList(),
    val favorites: List<Long> = emptyList(), // Store song IDs
    val settings: BackupSettings = BackupSettings()
)

@Serializable
data class BackupPlaylist(
    val name: String,
    val songIds: List<Long>
)

@Serializable
data class BackupSettings(
    val themeMode: String = "system",
    val useInternalEqualizer: Boolean = false,
    val minAudioDuration: Long = 10000L,
    // Last.fm session 不應該被備份，因為這是設備綁定的敏感資訊，且 token 可能過期
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
sealed class RestoreResult {
    data class Success(val itemsRestored: Int) : RestoreResult()
    data class Error(val message: String, val throwable: Throwable? = null) : RestoreResult()
}
