package com.gemini.music.domain.usecase.sleeptimer

import com.gemini.music.domain.repository.MusicController
import javax.inject.Inject

class CancelSleepTimerUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() {
        musicController.cancelSleepTimer()
    }
}
