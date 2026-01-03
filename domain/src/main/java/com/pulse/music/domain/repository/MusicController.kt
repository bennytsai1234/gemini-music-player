package com.pulse.music.domain.repository

import com.pulse.music.domain.model.MusicState
import com.pulse.music.domain.model.Song
import kotlinx.coroutines.flow.StateFlow

interface MusicController {
    val musicState: StateFlow<MusicState>
    fun playSongs(songs: List<Song>, startIndex: Int = 0)
    fun removeSong(index: Int)
    fun moveSong(from: Int, to: Int)
    fun playSongAt(index: Int)
    fun pause()
    fun resume()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(positionMs: Long)
    fun toggleShuffle()
    fun cycleRepeatMode()
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun setSleepTimer(minutes: Int)
    fun cancelSleepTimer()
}
