package com.pulse.music.domain.usecase.smartplaylist

import com.pulse.music.domain.model.SmartPlaylist
import com.pulse.music.domain.repository.SmartPlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSmartPlaylistsUseCase @Inject constructor(
    private val repository: SmartPlaylistRepository
) {
    operator fun invoke(): Flow<List<SmartPlaylist>> = repository.getSmartPlaylists()
}
