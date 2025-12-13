package com.gemini.music.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
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
    onAlbumClick: (Long) -> Unit = {},
    onArtistClick: (String) -> Unit = {},
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
                    contentPadding = PaddingValues(bottom = 100.dp), // Space for MiniPlayer
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
                    } else if (uiState.query.isEmpty()) {
                        // Recent Searches
                        if (uiState.recentSearches.isNotEmpty()) {
                            item {
                                androidx.compose.foundation.layout.Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                                    androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Recent Searches",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Clear All",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.clickable { viewModel.clearHistory() }
                                    )
                                }
                            }
                            items(uiState.recentSearches) { historyItem ->
                                ListItem(
                                    headlineContent = { Text(historyItem) },
                                    leadingContent = { Icon(androidx.compose.material.icons.Icons.Rounded.History, null) },
                                    trailingContent = {
                                        IconButton(onClick = { viewModel.removeHistoryItem(historyItem) }) {
                                            Icon(Icons.Rounded.Close, contentDescription = "Remove")
                                        }
                                    },
                                    modifier = Modifier.clickable { viewModel.onQueryChange(historyItem) }
                                )
                            }
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
                                    leadingContent = { 
                                        coil.compose.AsyncImage(
                                            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                                .data(album.artUri)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Rounded.Album),
                                            placeholder = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Rounded.Album)
                                        )
                                    },
                                    modifier = Modifier.clickable { 
                                        onAlbumClick(album.id)
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
                                    modifier = Modifier.clickable { onArtistClick(artist.name) }
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
