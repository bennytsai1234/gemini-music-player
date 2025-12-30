# Proposal: Polish UI Experience & Persistence

## Summary
This change focuses on refining the user interface and ensuring comprehensive state persistence. It addresses gaps in the "Equalizer" settings (specifically Loudness Enhancer), visually enhances "Empty States" across the app, and optimizes the "Now Playing" lyrics experience. This moves the app from "Functional" to "Premium" as per the design guidelines.

## Motivation
- **Equalizer**: Users expect their audio settings (Loudness, Bass Boost) to persist across sessions. Currently, Loudness Enhancer resets.
- **Empty States**: Basic text-only empty states degrade the user experience. Premium apps use visual cues.
- **Lyrics UX**: Auto-scrolling and gestures are critical for a music player. Ensuring they are smooth and bug-free is essential for audit completion.

## Proposed Changes
1.  **Equalizer Persistence**:
    - Update `UserPreferencesRepository` to store `loudnessEnabled` and `loudnessGain`.
    - Update `EqualizerViewModel` to save/restore these values.
2.  **Premium Empty States**:
    - Enhance `EmptyState` composable with better iconography/graphics (using vector assets or composition) and actionable buttons (e.g., "Rescan", "Go to Store").
    - Apply this consistent Empty State to Home, Playlist, Album, and Favorites screens.
3.  **Lyrics & Gestures**:
    - Optimize `KaraokeLyrics.kt` scroll logic for better centering.
    - Verify and tune `NowPlayingScreen` gestures (swipe sensitivity).

## Validation
- **Manual Verification**:
    - Set Loudness, restart app -> Verify it persists.
    - Clear Queue -> Check Empty State visual.
    - Play song with lyrics -> Observe scroll smoothness.
- **Unit Tests**:
    - Test `UserPreferencesRepository` flow for new keys.
