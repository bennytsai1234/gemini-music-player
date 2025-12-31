# Change: Enhance Now Playing Colors

## Why
To create a more immersive and premium listening experience, the Full Screen Player should dynamically adapt its color scheme to match the currently playing album art, rather than using a static theme background.

## Context
*   The `NowPlayingViewModel` already extracts vibrant colors from the album art using the Palette API.
*   The `NowPlayingScreen` currently ignores these extracted colors and uses a static `MaterialTheme` gradient.
*   The `MiniPlayer` already supports dynamic theming.
*   Both components have been verified to use Material 3 artifacts.

## What Changes
1.  **Modify `NowPlayingScreen.kt`**: Update the root `Box` background to use `uiState.gradientColors` provided by the ViewModel.
2.  **Verify `MiniPlayer`**: Re-confirm (no changes needed) that it handles M3 and dynamic theme injection.
3.  **Spec**: Define the requirement for dynamic background coloring in the player UI.
