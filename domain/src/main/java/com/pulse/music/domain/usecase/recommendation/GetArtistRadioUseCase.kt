package com.pulse.music.domain.usecase.recommendation

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 取得藝人電台的 UseCase。
 * 根據指定藝人生成混合播放清單。
 */
class GetArtistRadioUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    operator fun invoke(artistName: String): Flow<List<Song>> {
        return recommendationRepository.getArtistRadio(artistName)
    }
}
