package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.MusicState
import com.pulse.music.domain.repository.MusicController
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetMusicStateUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(): StateFlow<MusicState> {
        return musicController.musicState
    }
}
