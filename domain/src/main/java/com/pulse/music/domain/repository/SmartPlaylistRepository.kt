package com.pulse.music.domain.repository

import com.pulse.music.domain.model.SmartPlaylist
import com.pulse.music.domain.model.SmartPlaylistType
import com.pulse.music.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SmartPlaylistRepository {
    fun getSmartPlaylists(): Flow<List<SmartPlaylist>>
    fun getSmartPlaylistSongs(type: SmartPlaylistType): Flow<List<Song>>
}
