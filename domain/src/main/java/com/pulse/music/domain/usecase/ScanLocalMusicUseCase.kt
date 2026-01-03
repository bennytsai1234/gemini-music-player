package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.ScanStatus
import com.pulse.music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanLocalMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<ScanStatus> {
        return musicRepository.scanLocalMusic()
    }
}
