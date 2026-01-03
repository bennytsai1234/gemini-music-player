package com.pulse.music.domain.repository
 
import com.pulse.music.domain.model.LyricLine
import com.pulse.music.domain.model.Song
 
interface LyricsRepository {
    suspend fun getLyrics(song: Song): List<LyricLine>
}
