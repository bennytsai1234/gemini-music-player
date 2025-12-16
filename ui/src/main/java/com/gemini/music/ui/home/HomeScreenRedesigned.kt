package com.gemini.music.ui.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gemini.music.core.designsystem.GeminiSpacing
import com.gemini.music.core.designsystem.component.GeminiEmptyState
import com.gemini.music.core.designsystem.component.GeminiSongListItem
import com.gemini.music.domain.model.Song

/**
 * 簡化版首頁 - 只有歌曲列表和主要按鍵
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenRedesigned(
    viewModel: HomeViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onDiscoverClick: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onStatsClick: () -> Unit = {},
    onFoldersClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val recoverableAction by viewModel.recoverableAction.collectAsState()
    val listState = rememberLazyListState()

    // Launcher for Android 10+ deletion permission
    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.handleRecoverableAction(result.resultCode)
    }

    LaunchedEffect(recoverableAction) {
        recoverableAction?.let { exception ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.userAction.actionIntent.intentSender).build()
                intentSenderLauncher.launch(intentSenderRequest)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.scanMusic()
    }
    
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.scanMusic()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = uiState.isSelectionMode) {
        viewModel.exitSelectionMode()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 簡潔頂部欄
            SimpleTopBar(
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 快速操作按鈕列
            QuickActionRow(
                onPlaylistClick = onPlaylistClick,
                onAlbumsClick = onAlbumsClick,
                onFavoritesClick = onFavoritesClick,
                onFoldersClick = onFoldersClick
            )
            
            // 歌曲控制列
            SongControlRow(
                songCount = uiState.songs.size,
                currentSortOption = uiState.sortOption,
                onShuffleClick = { viewModel.shuffleAll() },
                onSortOptionSelected = { viewModel.setSortOption(it) },
                onPlayAll = {
                    if (uiState.songs.isNotEmpty()) {
                        viewModel.playSong(uiState.songs.first())
                        onSongClick(uiState.songs.first())
                    }
                }
            )

            // 歌曲列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.songs.isEmpty()) {
                GeminiEmptyState(
                    icon = Icons.Rounded.MusicNote,
                    title = "沒有歌曲",
                    subtitle = "您的音樂庫中還沒有任何歌曲"
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = GeminiSpacing.bottomSafeArea),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.songs,
                        key = { it.id }
                    ) { song ->
                        val isSelected = uiState.selectedSongIds.contains(song.id)
                        val isCurrentlyPlaying = uiState.currentPlayingSongId == song.id && uiState.isPlaying
                        GeminiSongListItem(
                            title = song.title,
                            subtitle = "${song.artist} · ${song.album}",
                            albumArtUri = song.albumArtUri,
                            isPlaying = isCurrentlyPlaying,
                            isFavorite = song.isFavorite,
                            duration = formatDuration(song.duration),
                            showDuration = true,
                            showFavorite = false, // 隱藏最愛按鈕，簡化UI
                            isSelected = isSelected,
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleSongSelection(song.id)
                                } else {
                                    viewModel.playSong(song)
                                    onSongClick(song)
                                }
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(song.id) },
                            modifier = Modifier.padding(horizontal = GeminiSpacing.xs)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 簡潔頂部欄
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Gemini Music",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "搜尋"
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "設定"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

/**
 * 快速操作按鈕列 - 四個主要入口
 */
@Composable
private fun QuickActionRow(
    onPlaylistClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onFoldersClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionButton(
            icon = Icons.Rounded.Album,
            label = "專輯",
            onClick = onAlbumsClick
        )
        QuickActionButton(
            icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
            label = "播放清單",
            onClick = onPlaylistClick
        )
        QuickActionButton(
            icon = Icons.Rounded.Favorite,
            label = "最愛",
            onClick = onFavoritesClick
        )
        QuickActionButton(
            icon = Icons.Rounded.Folder,
            label = "資料夾",
            onClick = onFoldersClick
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 歌曲控制列
 */
@Composable
private fun SongControlRow(
    songCount: Int,
    currentSortOption: SortOption,
    onShuffleClick: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    onPlayAll: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 歌曲數量
        Text(
            text = "$songCount 首歌曲",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        // 排序按鈕
        Box {
            IconButton(onClick = { showSortMenu = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                    contentDescription = "排序"
                )
            }
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (option) {
                                    SortOption.TITLE -> "標題"
                                    SortOption.ARTIST -> "藝人"
                                    SortOption.ALBUM -> "專輯"
                                    SortOption.DATE_ADDED -> "新增日期"
                                    SortOption.DURATION -> "時長"
                                }
                            )
                        },
                        onClick = {
                            onSortOptionSelected(option)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (currentSortOption == option) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
        
        // 隨機播放按鈕
        IconButton(onClick = onShuffleClick) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = "隨機播放"
            )
        }
        
        // 播放全部按鈕
        FilledTonalIconButton(
            onClick = onPlayAll,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "播放全部"
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
