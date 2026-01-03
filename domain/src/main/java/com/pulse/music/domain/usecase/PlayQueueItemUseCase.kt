package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class PlayQueueItemUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(index: Int) {
        musicController.playSongAt(index)
    }
}
