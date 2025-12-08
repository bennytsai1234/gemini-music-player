package com.gemini.music.data.repository

import com.gemini.music.core.common.parser.LrcParser
import com.gemini.music.data.network.LrcLibApi
import com.gemini.music.domain.model.LyricLine
import com.gemini.music.domain.model.Song
import com.gemini.music.domain.repository.LyricsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    private val lrcLibApi: LrcLibApi
) : LyricsRepository {

    override suspend fun getLyrics(song: Song): List<LyricLine> = withContext(Dispatchers.IO) {
        // 1. 本地查找
        val localLyrics = getLocalLyrics(song.dataPath)
        if (localLyrics.isNotEmpty()) return@withContext localLyrics

        // 2. 網絡查找
        try {
            val response = lrcLibApi.getLyrics(
                artistName = song.artist,
                trackName = song.title,
                duration = (song.duration / 1000).toInt()
            )

            // 優先使用同步歌詞
            val lyricsText = response.syncedLyrics ?: response.plainLyrics
            if (!lyricsText.isNullOrBlank()) {
                // TODO: 這裡可以考慮緩存歌詞到本地文件
                return@withContext LrcParser.parse(lyricsText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext emptyList()
    }

    private fun getLocalLyrics(audioPath: String): List<LyricLine> {
        try {
            val audioFile = File(audioPath)
            val parentDir = audioFile.parentFile ?: return emptyList()
            val nameWithoutExtension = audioFile.nameWithoutExtension
            
            val lrcFile = File(parentDir, "$nameWithoutExtension.lrc")
            
            if (lrcFile.exists() && lrcFile.canRead()) {
                val content = lrcFile.readText()
                return LrcParser.parse(content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}
