package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class SkipToPreviousUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() {
        musicController.skipToPrevious()
    }
}
