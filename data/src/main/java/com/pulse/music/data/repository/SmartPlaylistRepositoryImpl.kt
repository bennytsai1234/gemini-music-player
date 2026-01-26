package com.pulse.music.data.repository

import com.pulse.music.domain.model.SmartPlaylist
import com.pulse.music.domain.model.SmartPlaylistType
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicRepository
import com.pulse.music.domain.repository.SmartPlaylistRepository
import com.pulse.music.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

@Singleton
class SmartPlaylistRepositoryImpl @Inject constructor(
    private val musicRepository: MusicRepository,
    private val statsRepository: StatsRepository
) : SmartPlaylistRepository {

    override fun getSmartPlaylists(): Flow<List<SmartPlaylist>> {
        return flowOf(SmartPlaylist.createAll())
    }

    override fun getSmartPlaylistSongs(type: SmartPlaylistType): Flow<List<Song>> {
        return when (type) {
            SmartPlaylistType.MOST_PLAYED -> {
                statsRepository.getMostPlayedSongs(100).combine(musicRepository.getSongs()) { stats, allSongs ->
                    val songMap = allSongs.associateBy { it.id }
                    stats.mapNotNull { songMap[it.songId] }
                }
            }
            SmartPlaylistType.RECENTLY_PLAYED -> {
                statsRepository.getRecentlyPlayed(100).combine(musicRepository.getSongs()) { history, allSongs ->
                    val songMap = allSongs.associateBy { it.id }
                    // Distinct by songId to avoid duplicates in playlist if played multiple times sequentially
                    history.distinctBy { it.songId }.mapNotNull { songMap[it.songId] }
                }
            }
            SmartPlaylistType.RECENTLY_ADDED -> {
                musicRepository.getRecentlyAdded()
            }
            SmartPlaylistType.NEVER_PLAYED -> {
                musicRepository.getSongs().combine(statsRepository.getHistory()) { allSongs, history ->
                    val playedIds = history.map { it.songId }.toSet()
                    allSongs.filter { it.id !in playedIds }.shuffled().take(100)
                }
            }
            SmartPlaylistType.FAVORITES -> {
                musicRepository.getFavoriteSongs()
            }
            SmartPlaylistType.LONG_SONGS -> {
                musicRepository.getSongs().map { songs ->
                    songs.filter { it.duration > 5 * 60 * 1000 }.sortedByDescending { it.duration }
                }
            }
            SmartPlaylistType.SHORT_SONGS -> {
                musicRepository.getSongs().map { songs ->
                    songs.filter { it.duration < 3 * 60 * 1000 }.sortedBy { it.duration }
                }
            }
            SmartPlaylistType.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -7)
                val oneWeekAgo = cal.timeInMillis
                
                statsRepository.getHistory().combine(musicRepository.getSongs()) { history, allSongs ->
                    val songMap = allSongs.associateBy { it.id }
                    history.filter { it.playedAt > oneWeekAgo }
                        .distinctBy { it.songId }
                        .mapNotNull { songMap[it.songId] }
                }
            }
            SmartPlaylistType.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -30)
                val oneMonthAgo = cal.timeInMillis
                
                statsRepository.getHistory().combine(musicRepository.getSongs()) { history, allSongs ->
                    val songMap = allSongs.associateBy { it.id }
                    history.filter { it.playedAt > oneMonthAgo }
                        .distinctBy { it.songId }
                        .mapNotNull { songMap[it.songId] }
                }
            }
        }
    }
}
