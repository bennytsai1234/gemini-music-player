package com.pulse.music.ui.driving

import android.content.Intent
import com.pulse.music.core.common.base.UiEffect
import com.pulse.music.core.common.base.UiEvent
import com.pulse.music.core.common.base.UiState
import com.pulse.music.domain.model.DrivingAction
import com.pulse.music.domain.model.Song

/**
 * 駕駛模式 UI 狀態
 */
data class DrivingModeUiState(
    val isActive: Boolean = false,
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val albumArtUri: String? = null,
    val connectedDevice: String? = null,
    val isNightMode: Boolean = false,
    val showQueue: Boolean = false,
    val queueSongs: List<Song> = emptyList()
) : UiState

/**
 * 駕駛模式 UI 事件
 */
sealed class DrivingModeUiEvent : UiEvent {
    object PlayPause : DrivingModeUiEvent()
    object NextTrack : DrivingModeUiEvent()
    object PreviousTrack : DrivingModeUiEvent()
    object ToggleShuffle : DrivingModeUiEvent()
    object ToggleRepeat : DrivingModeUiEvent()
    object VolumeUp : DrivingModeUiEvent()
    object VolumeDown : DrivingModeUiEvent()
    object ToggleQueue : DrivingModeUiEvent()
    object ExitDrivingMode : DrivingModeUiEvent()
    object VoiceSearch : DrivingModeUiEvent()
    data class SeekTo(val positionMs: Long) : DrivingModeUiEvent()
    data class SelectFromQueue(val index: Int) : DrivingModeUiEvent()
}

/**
 * 駕駛模式 UI 效果
 */
sealed class DrivingModeUiEffect : UiEffect {
    object NavigateBack : DrivingModeUiEffect()
    data class Speak(val message: String) : DrivingModeUiEffect()
    object Vibrate : DrivingModeUiEffect()
    data class LaunchVoiceSearch(val intent: Intent) : DrivingModeUiEffect()
}


