package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class MoveQueueItemUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(fromIndex: Int, toIndex: Int) {
        musicController.moveSong(fromIndex, toIndex)
    }
}
