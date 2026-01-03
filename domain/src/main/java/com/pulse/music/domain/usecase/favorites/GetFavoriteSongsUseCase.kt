package com.pulse.music.domain.usecase.favorites

import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class GetFavoriteSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke() = repository.getFavoriteSongs()
}
