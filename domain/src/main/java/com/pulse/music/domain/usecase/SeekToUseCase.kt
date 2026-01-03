package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class SeekToUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(positionPercent: Float) {
        val duration = musicController.getDuration()
        if (duration > 0) {
            val newPosition = (duration * positionPercent).toLong()
            musicController.seekTo(newPosition)
        }
    }
}
