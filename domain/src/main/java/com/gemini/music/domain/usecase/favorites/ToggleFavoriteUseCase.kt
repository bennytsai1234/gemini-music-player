package com.gemini.music.domain.usecase.favorites

import com.gemini.music.domain.repository.MusicRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(songId: Long) {
        repository.toggleFavorite(songId)
    }
}
