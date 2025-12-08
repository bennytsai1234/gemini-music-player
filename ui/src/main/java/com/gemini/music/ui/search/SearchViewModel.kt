package com.gemini.music.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemini.music.domain.model.Song
import com.gemini.music.domain.usecase.GetSongsUseCase
import com.gemini.music.domain.usecase.PlaySongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

import com.gemini.music.domain.model.Album
import com.gemini.music.domain.model.Artist
import com.gemini.music.domain.usecase.GetAlbumsUseCase
import com.gemini.music.domain.usecase.GetArtistsUseCase

data class SearchUiState(
    val query: String = "",
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSongsUseCase: GetSongsUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
    private val playSongUseCase: PlaySongUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val allSongs = getSongsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val allAlbums = getAlbumsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val allArtists = getArtistsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery,
        allSongs,
        allAlbums,
        allArtists
    ) { query, songs, albums, artists ->
        if (query.isBlank()) {
            SearchUiState(query = query)
        } else {
            SearchUiState(
                query = query,
                songs = songs.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.artist.contains(query, ignoreCase = true) 
                },
                albums = albums.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.artist.contains(query, ignoreCase = true) 
                },
                artists = artists.filter { 
                    it.name.contains(query, ignoreCase = true) 
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSongClick(song: Song) {
        val currentResults = uiState.value.songs
        val index = currentResults.indexOf(song)
        if (index != -1) {
            playSongUseCase(currentResults, index)
        }
    }
}
