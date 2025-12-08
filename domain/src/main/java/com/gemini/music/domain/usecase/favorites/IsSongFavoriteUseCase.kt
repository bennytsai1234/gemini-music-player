package com.gemini.music.domain.usecase.favorites

import com.gemini.music.domain.repository.MusicRepository
import javax.inject.Inject

class IsSongFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(songId: Long) = repository.isSongFavorite(songId)
}
