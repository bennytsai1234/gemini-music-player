package com.pulse.music.domain.usecase.favorites

import com.pulse.music.domain.repository.MusicRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(songId: Long) {
        repository.toggleFavorite(songId)
    }
}
