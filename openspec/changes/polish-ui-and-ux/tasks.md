# Tasks: Polish UI Experience & Persistence

## 1. Equalizer Persistence (Data & Domain)
- [x] **Method 1**: Add `loudnessEnabled` and `loudnessGain` to `UserPreferencesRepository` (Interface & Impl).
- [x] **Method 2**: Update `EqualizerViewModel` `initializeEqualizer` to restore Loudness settings.
- [x] **Method 3**: Update `EqualizerViewModel` `setLoudnessEnabled/Gain` to save to preferences.
- [x] **Method 4**: Verify Bass Boost and Virtualizer persistence logic (already present, double check).

## 2. Premium Empty States (UI)
- [x] **Component**: Enhance `EmptyState.kt`.
    - Add support for an optional "Action Button" (e.g., "Refresh").
    - Improve layout with larger icon and better typography.
- [x] **Application**: Apply `EmptyState` to:
    - [x] `HomeScreen` (Already there, verify look).
    - [x] `PlaylistScreen` (Check if missing, add if needed).
    - [x] `AlbumScreen` / `ArtistScreen` (Check if missing).

## 3. Lyrics & UX Polish (UI)
- [x] **Lyrics**: Optimize `KaraokeLyrics.kt` scroll logic.
    - Start search from `currentLineIndex` to avoid O(N) scan.
    - Tune scroll offset to center active line better.
- [x] **Now Playing**: Audit gestures.
    - Ensure Swipe Down (Minimize) works (if implemented).
    - Ensure Swipe Left/Right (Skip) works seamlessly.

## 4. Verification
- [ ] **Manual Test**: Build and run.
- [ ] **Manual Test**: Check EQ persistence after app restart.
