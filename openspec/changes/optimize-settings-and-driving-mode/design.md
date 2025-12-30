# Design: Optimize Settings and Driving Mode

## Architecture Changes (Equalizer)

To support the Equalizer in the UI layer without violating clean architecture:
1.  **Service Layer**: `GeminiAudioService` (or `ExoPlayer`) holds the `audioSessionId`.
2.  **Data/Domain Layer**: We need to expose this `audioSessionId` via `MusicState` or a separate flow in `MusicController`.
    - *Decision*: Add `audioSessionId: Int` to `MusicState`. This is a passive state property.
    - Update `MusicServiceConnection` (implementation of `MusicController`) to fetch/listen to `audioSessionId`. Since `audioSessionId` usually doesn't change often for a player instance, we can fetch it on connect or command.
    - *Mechanism*: `GeminiAudioService` handles a custom command `ACTION_GET_AUDIO_SESSION_ID` (which we just added) to return the ID. `MusicServiceConnection` queries this on connection and updates `MusicState`.

## UI Design

### Settings Screen
- Remove `SleepTimer` entry.
- Move `PlaybackSettings` entry: Remove from `SettingsScreen` list.
- **Home Screen Dropdown**: Add "Playback Settings" (`Icons.Rounded.Tune` or similar) next to "Sleep Timer".

### Driving Mode
- **Visuals**: High contrast, huge buttons.
- **Gestures**: Swipe left/right for prev/next. Tap for play/pause.
- **Seek**: Instead of a small slider, use "Rewind 10s" / "Fast Forward 30s" large buttons, or a distinct large seeking bar.

### Equalizer
- Already implemented in `EqualizerScreen.kt`.
- Needs `audioSessionId` passed via Navigation arguments.
- `SettingsScreen`'s "Internal Equalizer" click listener currently navigates to `Screen.Equalizer`. We need to ensure we pass the valid sessionId.

## Data Persistence
- Equalizer settings (presets, bass boost) need to be saved. `EqualizerViewModel` seems to handle some of this, but we should verify `UserPreferencesRepository` support.
