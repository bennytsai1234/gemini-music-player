package com.pulse.music.domain.usecase.playlist

import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class ImportPlaylistUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(uri: String): Result<Long> {
        return musicRepository.importPlaylist(uri)
    }
}
