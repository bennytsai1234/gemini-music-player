# Tasks: Audit and Polish Features

## Service & Core Audit
- [x] **Audio Lifecycle**: Verify `GeminiAudioService` handles foreground/background transitions and focus loss (e.g., phone calls) correctly.
- [x] **Notification**: Check notification actions (play/pause/next/prev) sync perfectly with UI state.
- [x] **Queue Management**: Verify queue persistence and shuffle/repeat logic in `MusicControllerHelper`. (Implemented Persistence)

## Domain & Data Audit
- [x] **Scanning**: Stress test `LocalMusicRepository` with large file counts (simulation). Check duplicate handling. (Reviewed Logic)
- [x] **Playlist**: Verify CRUD operations for playlists and ensure database integrity.
- [x] **Preferences**: Confirm all settings (Theme, Audio duration, etc.) persist across restarts. (Implemented Playback State Persistence)

## UI Hand-Check & Polish
- [x] **Home Screen**:
    - [x] Test batch deletion flow (Android 11+ compatibility). (Fixed via IntentSender)
    - [x] Check "Fast Scroller" touch targets and visibility. (Increased hit box to 48dp)
- [x] **Now Playing**:
    - [x] Verify lyrics auto-scroll smoothness. (Optimized scroll logic)
    - [x] Check artwork gestures (swipe down to minimize). (Implemented onSwipeDown)
- [x] **Driving Mode**:
    - [x] Verify new Seek controls (+30s/-10s) works.
    - [x] Test Swipe gestures again for sensitivity.
    - [x] Localization (Zh/En).
- [x] **Equalizer**:
    - [x] Confirm presets apply correctly. (Verified VM logic)
    - [x] Check custom preset save/delete flow. (Verified UI components)

## Edge Cases & Error Handling
- [x] **Permissions**: Verify app behavior when Storage/Notification permissions are denied. (Handled in MainActivity)
- [x] **Empty States**: Ensure all screens (Home, Playlist, etc.) have nice empty states. (Replaced with GeminiEmptyState)
- [x] **Corrupted Files**: Simulate playback of a corrupted audio file and ensure app doesn't crash. (Added Error Handler)
