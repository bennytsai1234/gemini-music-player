package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.WaveformRepository
import javax.inject.Inject

class GetSongWaveformUseCase @Inject constructor(
    private val waveformRepository: WaveformRepository
) {
    suspend operator fun invoke(filePath: String): List<Int> {
        return waveformRepository.extractWaveform(filePath)
    }
}
