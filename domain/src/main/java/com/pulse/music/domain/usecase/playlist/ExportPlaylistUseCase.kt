package com.pulse.music.domain.usecase.playlist

import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class ExportPlaylistUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(playlistId: Long, uri: String): Result<Unit> {
        return musicRepository.exportPlaylist(playlistId, uri)
    }
}
