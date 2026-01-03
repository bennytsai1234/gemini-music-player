package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.Album
import com.pulse.music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<Album>> {
        return musicRepository.getAlbums()
    }
}
