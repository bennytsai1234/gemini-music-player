package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.LyricLine
import com.pulse.music.domain.repository.LyricsRepository
import javax.inject.Inject

import com.pulse.music.domain.model.Song

class GetLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(song: Song): List<LyricLine> {
        return lyricsRepository.getLyrics(song)
    }
}
