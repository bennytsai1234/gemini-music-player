package com.gemini.music.ui.nowplaying.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Hero image component displaying album artwork with gesture support
 *
 * Supports:
 * - Single tap: Toggle lyrics
 * - Horizontal swipe: Skip to next/previous track
 * - Double tap on left/right: Seek backward/forward 10 seconds
 * - Scale animation based on play state
 */
@Composable
fun HeroImage(
    artUri: String?,
    isPlaying: Boolean,
    onImageLoaded: (Bitmap?) -> Unit,
    onClick: () -> Unit,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onDoubleTapLeft: () -> Unit = {},
    onDoubleTapRight: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.85f,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "ImageScale"
    )

    val animatedShadowElevation by animateFloatAsState(
        targetValue = if (isPlaying) 24f else 8f,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "ShadowElevation"
    )

    // Swipe offset animation for visual feedback
    var swipeOffset by remember { mutableStateOf(0f) }
    var verticalSwipeOffset by remember { mutableStateOf(0f) }

    val animatedOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = tween(durationMillis = 150),
        finishedListener = { if (swipeOffset != 0f) swipeOffset = 0f },
        label = "SwipeOffset"
    )

    // Double tap hint indicators
    var showDoubleTapHintLeft by remember { mutableStateOf(false) }
    var showDoubleTapHintRight by remember { mutableStateOf(false) }

    val currentOnSwipeLeft by rememberUpdatedState(onSwipeLeft)
    val currentOnSwipeRight by rememberUpdatedState(onSwipeRight)
    val currentOnSwipeDown by rememberUpdatedState(onSwipeDown)
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnDoubleTapLeft by rememberUpdatedState(onDoubleTapLeft)
    val currentOnDoubleTapRight by rememberUpdatedState(onDoubleTapRight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectDragGestures(
                    onDragEnd = {
                        if (abs(swipeOffset) > 100) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (swipeOffset > 0) {
                                currentOnSwipeRight() // Previous
                            } else {
                                currentOnSwipeLeft() // Next
                            }
                        } else if (verticalSwipeOffset > 100) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentOnSwipeDown()
                        }
                        swipeOffset = 0f
                        verticalSwipeOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (abs(dragAmount.x) > abs(dragAmount.y) || abs(swipeOffset) > 10) {
                            swipeOffset = (swipeOffset + dragAmount.x).coerceIn(-200f, 200f)
                        } else {
                            verticalSwipeOffset = (verticalSwipeOffset + dragAmount.y).coerceAtLeast(0f)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { currentOnClick() },
                    onDoubleTap = { offset ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (offset.x < size.width / 2) {
                            currentOnDoubleTapLeft()
                            showDoubleTapHintLeft = true
                        } else {
                            currentOnDoubleTapRight()
                            showDoubleTapHintRight = true
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow/Glow Background
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .scale(scale * 0.95f) // Slightly smaller than image
                .shadow(
                    elevation = animatedShadowElevation.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black,
                    ambientColor = Color.Black
                )
                .background(Color.Black, RoundedCornerShape(24.dp))
        )

        // Main Image
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .scale(scale)
                .graphicsLayer {
                    translationX = animatedOffset
                    translationY = verticalSwipeOffset * 0.5f // Visual feedback for vertical swipe
                }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Handled by modifier
        ) {
             AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onSuccess = { result ->
                    val resultBitmap = result.result.drawable.toBitmap()
                    onImageLoaded(resultBitmap)
                },
                onError = {
                     onImageLoaded(null)
                },
                error = rememberVectorPainter(Icons.Rounded.Album)
            )
        }

        // Double Tap Indicators - Left
        AnimatedVisibility(
            visible = showDoubleTapHintLeft,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay10,
                    contentDescription = "-10s",
                    tint = Color.White
                )
            }
            // Auto hide hint
            LaunchedEffect(Unit) {
                delay(600)
                showDoubleTapHintLeft = false
            }
        }

        // Double Tap Indicators - Right
        AnimatedVisibility(
            visible = showDoubleTapHintRight,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp)
        ) {
             Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Forward10,
                    contentDescription = "+10s",
                    tint = Color.White
                )
            }
             // Auto hide hint
            LaunchedEffect(Unit) {
                delay(600)
                showDoubleTapHintRight = false
            }
        }
    }
}
