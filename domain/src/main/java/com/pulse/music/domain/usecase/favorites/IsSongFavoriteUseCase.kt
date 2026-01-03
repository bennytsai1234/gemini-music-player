package com.pulse.music.domain.usecase.favorites

import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class IsSongFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(songId: Long) = repository.isSongFavorite(songId)
}
