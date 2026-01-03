package com.pulse.music.domain.usecase

import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return musicRepository.getSongs()
    }
}
