package com.pulse.music.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulse.music.domain.model.Album
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicRepository
import com.pulse.music.domain.usecase.PlaySongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailUiState(
    val artistName: String = "",
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val musicRepository: MusicRepository,
    private val playSongUseCase: PlaySongUseCase
) : ViewModel() {
    
    private val artistName: String = savedStateHandle.get<String>("artistName") ?: ""
    
    private val _uiState = MutableStateFlow(ArtistDetailUiState(artistName = artistName))
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadArtistData()
    }
    
    private fun loadArtistData() {
        viewModelScope.launch {
            combine(
                musicRepository.getSongs(),
                musicRepository.getAlbums()
            ) { allSongs, allAlbums ->
                val artistSongs = allSongs.filter { it.artist.equals(artistName, ignoreCase = true) }
                val artistAlbums = allAlbums.filter { it.artist.equals(artistName, ignoreCase = true) }
                
                ArtistDetailUiState(
                    artistName = artistName,
                    songs = artistSongs,
                    albums = artistAlbums,
                    isLoading = false
                )
            }
            .flowOn(kotlinx.coroutines.Dispatchers.Default)
            .collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun playSong(index: Int) {
        val songs = _uiState.value.songs
        if (index in songs.indices) {
            playSongUseCase(songs, index)
        }
    }
    
    fun playAll() {
        val songs = _uiState.value.songs
        if (songs.isNotEmpty()) {
            playSongUseCase(songs, 0)
        }
    }
    
    fun shuffleAll() {
        val songs = _uiState.value.songs
        if (songs.isNotEmpty()) {
            val shuffled = songs.shuffled()
            playSongUseCase(shuffled, 0)
        }
    }
}


