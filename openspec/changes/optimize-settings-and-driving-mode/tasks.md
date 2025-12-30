# Tasks: Optimize Settings and Driving Mode

## Equalizer Integration
- [x] **Infrastructure**: Expose `audioSessionId` in `MusicState`. (Partially done in `MusicServiceConnection`).
    - [x] Update `MusicServiceConnection` to request `audioSessionId` on connection.
    - [x] Update `MusicServiceConnection.kt` to handle the response and update `_musicState`.
- [x] **UI**: Update `SettingsScreen` navigation to pass the correct `audioSessionId` from `MusicController.musicState`.
- [x] **Validation**: Verify Equalizer effects modify audio output.

## Settings & Menu Refinement
- [x] **Home Screen**: Add "Playback Settings" to `HomeScreenRedesigned` dropdown menu.
- [x] **Settings Screen**: Remove "Sleep Timer" and "Playback Settings" entries.
- [x] **Cleanup**: Review `SettingsScreen` for non-functional items and hide/disable them.

## Driving Mode Optimization
- [x] **UI Overhaul**: Redesign `DrivingModeScreen` for larger controls (Play/Pause, Next/Prev).
- [x] **Seek Controls**: Implement +/- 10s/30s buttons or a simplified seek bar.
- [x] **Gestures**: Add swipe gestures for track skipping.
- [x] **Functionality**: Ensure all buttons on `DrivingModeScreen` are hooked up to `MusicController`.
