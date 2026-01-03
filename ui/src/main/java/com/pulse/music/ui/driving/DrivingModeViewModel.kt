package com.pulse.music.ui.driving

import androidx.lifecycle.viewModelScope
import com.pulse.music.core.common.base.BaseViewModel
import com.pulse.music.domain.model.MusicState
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicController
import com.pulse.music.domain.usecase.driving.ObserveDrivingModeUseCase
import com.pulse.music.domain.usecase.driving.ToggleDrivingModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrivingModeViewModel @Inject constructor(
    private val observeDrivingModeUseCase: ObserveDrivingModeUseCase,
    private val toggleDrivingModeUseCase: ToggleDrivingModeUseCase,
    private val musicController: MusicController
) : BaseViewModel<DrivingModeUiState, DrivingModeUiEvent, DrivingModeUiEffect>(
    DrivingModeUiState()
) {
    
    init {
        observePlaybackState()
        observeDrivingModeState()
    }
    
    private fun observePlaybackState() {
        musicController.musicState
            .onEach { state ->
                val currentSong = state.currentSong
                setState {
                    copy(
                        currentSong = currentSong,
                        isPlaying = state.isPlaying,
                        currentPositionMs = musicController.getCurrentPosition(),
                        durationMs = currentSong?.duration ?: 0L,
                        albumArtUri = currentSong?.albumArtUri,
                        queueSongs = state.queue
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeDrivingModeState() {
        observeDrivingModeUseCase.observeState()
            .onEach { state ->
                setState {
                    copy(
                        isActive = state.isActive,
                        connectedDevice = state.connectedBluetoothDevice,
                        isNightMode = state.isNightMode
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    override fun handleEvent(event: DrivingModeUiEvent) {
        when (event) {
            is DrivingModeUiEvent.PlayPause -> handlePlayPause()
            is DrivingModeUiEvent.NextTrack -> handleNextTrack()
            is DrivingModeUiEvent.PreviousTrack -> handlePreviousTrack()
            is DrivingModeUiEvent.ToggleShuffle -> handleToggleShuffle()
            is DrivingModeUiEvent.ToggleRepeat -> handleToggleRepeat()
            is DrivingModeUiEvent.VolumeUp -> handleVolumeUp()
            is DrivingModeUiEvent.VolumeDown -> handleVolumeDown()
            is DrivingModeUiEvent.ToggleQueue -> handleToggleQueue()
            is DrivingModeUiEvent.ExitDrivingMode -> handleExitDrivingMode()
            is DrivingModeUiEvent.VoiceSearch -> handleVoiceSearch()
            is DrivingModeUiEvent.SeekTo -> handleSeekTo(event.positionMs)
            is DrivingModeUiEvent.SelectFromQueue -> handleSelectFromQueue(event.index)
        }
    }
    
    private fun handlePlayPause() {
        if (currentState.isPlaying) {
            musicController.pause()
        } else {
            musicController.resume()
        }
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleNextTrack() {
        musicController.skipToNext()
        setEffect { DrivingModeUiEffect.Vibrate }
        currentState.currentSong?.let { song ->
            setEffect { DrivingModeUiEffect.Speak("正在播放 ${song.title}") }
        }
    }
    
    private fun handlePreviousTrack() {
        musicController.skipToPrevious()
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleToggleShuffle() {
        musicController.toggleShuffle()
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleToggleRepeat() {
        musicController.cycleRepeatMode()
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleVolumeUp() {
        // 使用 AudioManager 調整音量
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleVolumeDown() {
        setEffect { DrivingModeUiEffect.Vibrate }
    }
    
    private fun handleToggleQueue() {
        setState { copy(showQueue = !showQueue) }
    }
    
    private fun handleExitDrivingMode() {
        viewModelScope.launch {
            toggleDrivingModeUseCase.deactivate()
            setEffect { DrivingModeUiEffect.NavigateBack }
        }
    }
    
    private fun handleVoiceSearch() {
        // TODO: 實作語音搜尋
    }
    
    private fun handleSeekTo(positionMs: Long) {
        musicController.seekTo(positionMs)
    }
    
    private fun handleSelectFromQueue(index: Int) {
        musicController.playSongAt(index)
        setState { copy(showQueue = false) }
    }
}


