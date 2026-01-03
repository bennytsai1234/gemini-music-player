package com.pulse.music.domain.usecase.sleeptimer

import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class CancelSleepTimerUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() {
        musicController.cancelSleepTimer()
    }
}
