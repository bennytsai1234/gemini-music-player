package com.pulse.music.ui.playlist.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulse.music.domain.model.Playlist
import com.pulse.music.domain.model.SmartPlaylist
import com.pulse.music.domain.repository.MusicRepository
import com.pulse.music.domain.usecase.smartplaylist.GetSmartPlaylistsUseCase
import com.pulse.music.domain.usecase.playlist.ImportPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistListUiState(
    val playlists: List<Playlist> = emptyList(),
    val smartPlaylists: List<SmartPlaylist> = emptyList(),
    val showCreateDialog: Boolean = false,
    val playlistToRename: Playlist? = null
)

@HiltViewModel
class PlaylistListViewModel @Inject constructor(
    private val importPlaylistUseCase: ImportPlaylistUseCase,
    private val musicRepository: MusicRepository,
    private val getSmartPlaylistsUseCase: GetSmartPlaylistsUseCase
) : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    private val _playlistToRename = MutableStateFlow<Playlist?>(null)

    val uiState: StateFlow<PlaylistListUiState> = combine(
        musicRepository.getPlaylists(),
        getSmartPlaylistsUseCase(),
        _showCreateDialog,
        _playlistToRename
    ) { playlists, smartPlaylists, showDialog, playlistToRename ->
        PlaylistListUiState(playlists, smartPlaylists, showDialog, playlistToRename)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaylistListUiState()
    )

    fun showDialog() {
        _showCreateDialog.value = true
    }

    fun dismissDialog() {
        _showCreateDialog.value = false
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicRepository.createPlaylist(name)
            dismissDialog()
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            musicRepository.deletePlaylist(playlistId)
        }
    }

    fun showRenameDialog(playlist: Playlist) {
        _playlistToRename.value = playlist
    }

    fun dismissRenameDialog() {
        _playlistToRename.value = null
    }

    fun renamePlaylist(playlistId: Long, name: String) {
        viewModelScope.launch {
            musicRepository.renamePlaylist(playlistId, name)
            dismissRenameDialog()
        }
    }

    fun importPlaylist(uri: String) {
        viewModelScope.launch {
            importPlaylistUseCase(uri)
        }
    }
}


