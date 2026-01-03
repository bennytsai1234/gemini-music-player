package com.pulse.music.domain.usecase.recommendation

import com.pulse.music.domain.model.Recommendation
import com.pulse.music.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 取得個人化推薦歌曲的 UseCase。
 */
class GetRecommendationsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<Recommendation>> {
        return recommendationRepository.getRecommendations(limit)
    }
}
