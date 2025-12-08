package com.gemini.music.ui.playlist.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemini.music.domain.model.Playlist
import com.gemini.music.domain.repository.MusicRepository
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
    val showCreateDialog: Boolean = false,
    val playlistToRename: Playlist? = null
)

@HiltViewModel
class PlaylistListViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    private val _playlistToRename = MutableStateFlow<Playlist?>(null)

    val uiState: StateFlow<PlaylistListUiState> = combine(
        musicRepository.getPlaylists(),
        _showCreateDialog,
        _playlistToRename
    ) { playlists, showDialog, playlistToRename ->
        PlaylistListUiState(playlists, showDialog, playlistToRename)
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
}
