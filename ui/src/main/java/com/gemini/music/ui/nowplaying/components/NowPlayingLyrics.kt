package com.gemini.music.ui.nowplaying.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gemini.music.domain.model.LyricLine
import kotlinx.coroutines.delay

/**
 * Lyrics display component with auto-scroll and seek functionality
 * 
 * Features:
 * - Auto-scroll to current lyric line
 * - Scale and color animation for active line
 * - Tap on lyric line to seek
 * - Resume sync button when user scrolls manually
 */
@Composable
fun LyricsView(
    lyrics: List<LyricLine>,
    currentIndex: Int,
    onTap: () -> Unit,
    onSeek: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    var isUserScrolling by remember { mutableStateOf(false) }
    
    // Detect user interaction
    val isScrollInProgress = listState.isScrollInProgress
    LaunchedEffect(isScrollInProgress) {
        if (isScrollInProgress) {
            isUserScrolling = true
        } else {
            // Resume auto-scroll after 3 seconds of inactivity
            delay(3000)
            isUserScrolling = false
        }
    }

    LaunchedEffect(currentIndex, isUserScrolling) {
        if (!isUserScrolling && currentIndex >= 0 && currentIndex < lyrics.size) {
            // Calculate offset to center the item roughly
            // We use a safe estimate or specific item positioning if possible.
            // Using animateScrollToItem with offset is the best we can do without complex layout measurement.
            // Offset 0 puts it at top. 
            // We want it in middle. Assuming standard screen height ~800dp, half is 400dp.
            // But we have padding. Let's aim for top-third for better readability.
            listState.animateScrollToItem(
                index = currentIndex,
                scrollOffset = -300 // Negative offset doesn't work as "from top" in LazyColumn standardly? 
                // Actually scrollToItem(index, scrollOffset) -> offset is pixels from top of visible area.
                // So positive offset pushes it DOWN.
                // We want to push it down to the middle.
            )
        }
    }

    if (lyrics.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onTap),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Lyrics Found",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onTap),
                contentPadding = PaddingValues(top = 150.dp, bottom = 150.dp), // Add padding to allow start/end items to be centered
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isCurrent = index == currentIndex
                    // Active line: Opaque, Larger. Inactive: Faded, Smaller.
                    val targetAlpha = if (isCurrent) 1f else 0.4f
                    val targetScale = if (isCurrent) 1.1f else 0.95f
                    val targetColor = if (isCurrent) Color.White else Color.LightGray
                    
                    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "alpha")
                    val scale by animateFloatAsState(targetValue = targetScale, label = "scale")
                    val color by animateColorAsState(targetValue = targetColor, label = "color")

                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            shadow = if (isCurrent) Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = Offset(0f, 4f),
                                blurRadius = 8f
                            ) else null
                        ),
                        color = color.copy(alpha = alpha),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            // Seek to this line on click
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // No ripple for clear reading
                            ) {
                                onSeek(line.startTime)
                            }
                            .padding(horizontal = 32.dp)
                    )
                }
            }
            
            // "Resume Sync" button if user scrolled away
            AnimatedVisibility(
                visible = isUserScrolling,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 24.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = { isUserScrolling = false },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Resume Sync",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
