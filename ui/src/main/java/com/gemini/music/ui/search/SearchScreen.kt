package com.gemini.music.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gemini.music.ui.component.SongListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = { /* Keyboard action */ },
                active = true, // Always active for this screen style
                onActiveChange = { },
                placeholder = { Text("Search songs, artists, albums...") },
                leadingIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp), // Space for MiniPlayer
                    modifier = Modifier.fillMaxSize()
                ) {
                    val hasResults = uiState.songs.isNotEmpty() || uiState.albums.isNotEmpty() || uiState.artists.isNotEmpty()
                    
                    if (!hasResults && uiState.query.isNotEmpty()) {
                        item {
                            Text(
                                text = "No results found.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Songs Section
                        if (uiState.songs.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Songs",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(uiState.songs) { song ->
                                SongListItem(
                                    song = song,
                                    onClick = { viewModel.onSongClick(song) }
                                )
                            }
                        }
                        
                        // Albums Section
                        if (uiState.albums.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Albums",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(uiState.albums) { album ->
                                ListItem(
                                    headlineContent = { Text(album.title) },
                                    supportingContent = { Text(album.artist) },
                                    leadingContent = { Icon(Icons.Rounded.Album, null) },
                                    modifier = Modifier.clickable { 
                                        // Handle album click - maybe navigate?
                                    }
                                )
                            }
                        }
                        
                        // Artists Section
                        if (uiState.artists.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Artists",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(uiState.artists) { artist ->
                                ListItem(
                                    headlineContent = { Text(artist.name) },
                                    supportingContent = { Text("${artist.songCount} songs") },
                                    leadingContent = { Icon(Icons.Rounded.Person, null) },
                                    modifier = Modifier.clickable { 
                                        // Handle artist click
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
       // Content is handled inside SearchBar scope for "active" behavior
       // But if we wanted a separate list, we'd put it here.
       // Since SearchBar is always active, the content is inside the trailing lambda above.
       Column(modifier = Modifier.padding(padding)) {}
    }
}
