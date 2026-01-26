package com.pulse.music.domain.usecase.smartplaylist

import com.pulse.music.domain.model.SmartPlaylistType
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.SmartPlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSmartPlaylistSongsUseCase @Inject constructor(
    private val repository: SmartPlaylistRepository
) {
    operator fun invoke(type: SmartPlaylistType): Flow<List<Song>> = repository.getSmartPlaylistSongs(type)
}
