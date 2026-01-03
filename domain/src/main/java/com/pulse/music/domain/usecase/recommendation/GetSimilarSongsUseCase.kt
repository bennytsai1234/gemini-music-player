package com.pulse.music.domain.usecase.recommendation

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 取得類似歌曲推薦的 UseCase。
 */
class GetSimilarSongsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    operator fun invoke(songId: Long, limit: Int = 10): Flow<List<Song>> {
        return recommendationRepository.getSimilarSongs(songId, limit)
    }
}
