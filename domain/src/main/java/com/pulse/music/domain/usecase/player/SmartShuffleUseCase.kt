package com.pulse.music.domain.usecase.player

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicController
import com.pulse.music.domain.repository.MusicRepository
import com.pulse.music.domain.repository.StatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

/**
 * Smart Shuffle: Plays all songs shuffled, but weighted by play count.
 * Songs with HIGHER play counts have a SLIGHTLY higher chance of appearing earlier,
 * but we also ensure randomness to keep it fresh.
 * 
 * Algorithm:
 * 1. Get all songs and play stats.
 * 2. Assign weight = 1.0 + (totalPlayCount * 0.05).
 * 3. Shuffle based on weight.
 */
class SmartShuffleUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
    private val statsRepository: StatsRepository,
    private val musicController: MusicController
) {
    suspend operator fun invoke() {
        val allSongs = musicRepository.getSongs().first()
        if (allSongs.isEmpty()) return

        val stats = statsRepository.getMostPlayedSongs(limit = 10000).first().associateBy { it.songId }

        val weightedSongs = allSongs.map { song ->
            val totalPlayCount = stats[song.id]?.totalPlayCount ?: 0
            // Weight formula: Base 1.0 + 5% boost per play. Cap at 5.0 (20x boost max) to avoid starvation.
            val weight = (1.0 + (totalPlayCount * 0.05)).coerceAtMost(5.0)
            song to weight
        }

        val shuffledSongs = weightedShuffle(weightedSongs)

        musicController.playSongs(shuffledSongs, 0)
    }

    private fun weightedShuffle(items: List<Pair<Song, Double>>): List<Song> {
        val result = mutableListOf<Song>()
        val pool = items.toMutableList()
        val random = Random.Default

        while (pool.isNotEmpty()) {
            val totalWeight = pool.sumOf { it.second }
            var r = random.nextDouble() * totalWeight
            
            var selectedIndex = -1
            for (i in pool.indices) {
                r -= pool[i].second
                if (r <= 0) {
                    selectedIndex = i
                    break
                }
            }
            // Fallback (shouldn't happen usually)
            if (selectedIndex == -1) selectedIndex = pool.lastIndex

            result.add(pool[selectedIndex].first)
            pool.removeAt(selectedIndex)
        }
        return result
    }
}
