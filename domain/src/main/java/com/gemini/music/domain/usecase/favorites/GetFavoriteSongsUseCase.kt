package com.gemini.music.domain.usecase.favorites

import com.gemini.music.domain.repository.MusicRepository
import javax.inject.Inject

class GetFavoriteSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke() = repository.getFavoriteSongs()
}
