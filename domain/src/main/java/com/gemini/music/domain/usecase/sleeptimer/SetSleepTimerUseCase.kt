package com.gemini.music.domain.usecase.sleeptimer

import com.gemini.music.domain.repository.MusicController
import javax.inject.Inject

class SetSleepTimerUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(minutes: Int) {
        musicController.setSleepTimer(minutes)
    }
}
