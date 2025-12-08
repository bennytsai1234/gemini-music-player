package com.gemini.music.domain.usecase

import com.gemini.music.domain.model.LyricLine
import com.gemini.music.domain.repository.LyricsRepository
import javax.inject.Inject

import com.gemini.music.domain.model.Song

class GetLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(song: Song): List<LyricLine> {
        return lyricsRepository.getLyrics(song)
    }
}
