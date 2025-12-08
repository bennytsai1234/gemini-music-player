package com.gemini.music.domain.repository
 
import com.gemini.music.domain.model.LyricLine
import com.gemini.music.domain.model.Song
 
interface LyricsRepository {
    suspend fun getLyrics(song: Song): List<LyricLine>
}
