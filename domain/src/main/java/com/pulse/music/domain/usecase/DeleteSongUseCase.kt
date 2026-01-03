package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class DeleteSongUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(song: Song) {
        musicRepository.deleteSong(song)
    }
}
