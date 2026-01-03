package com.pulse.music.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.usecase.GetMusicStateUseCase
import com.pulse.music.domain.usecase.PlayQueueItemUseCase
import com.pulse.music.domain.usecase.RemoveQueueItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    getMusicStateUseCase: GetMusicStateUseCase,
    private val playQueueItemUseCase: PlayQueueItemUseCase,
    private val removeQueueItemUseCase: RemoveQueueItemUseCase,
    private val moveQueueItemUseCase: com.pulse.music.domain.usecase.MoveQueueItemUseCase
) : ViewModel() {

    val uiState: StateFlow<QueueUiState> = getMusicStateUseCase()
        .map { musicState ->
            QueueUiState(
                queue = musicState.queue,
                currentSongIndex = musicState.queue.indexOfFirst { it.id == musicState.currentSong?.id },
                isPlaying = musicState.isPlaying
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QueueUiState()
        )

    fun playItem(index: Int) {
        playQueueItemUseCase(index)
    }

    fun removeItem(index: Int) {
        removeQueueItemUseCase(index)
    }

    fun moveItem(from: Int, to: Int) {
        moveQueueItemUseCase(from, to)
    }
}

data class QueueUiState(
    val queue: List<Song> = emptyList(),
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false
)


