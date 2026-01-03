package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(songs: List<Song>, index: Int) {
        musicController.playSongs(songs, index)
    }
}
