# Proposal: Optimize Settings and Driving Mode

## Abstract
Improve the application's usability and feature set by optimizing the `DrivingModeScreen` for better safety and control, implementing a functional Equalizer in `SettingsScreen` (leveraging internal audio session tracking), and refining the `SettingsScreen` hierarchy by moving "Playback Settings" to a more prominent location and removing redundant entries like "Sleep Timer".

## Why
- **Driving Mode Gaps**: The current driving mode is skeletal, missing key functionalities like full voice interaction support (TTS, Voice Search) and optimized touch controls (seek).
- **Equalizer Availability**: Users expect an equalizer for a premium music experience. We have an implementation, but it needs to be properly connected with the audio session.
- **Settings Clutter**: "Sleep Timer" is now in the Home dropdown, making the Settings entry redundant. "Playback Settings" are core to the music experience and should be more accessible.
- **General Polish**: Various settings items need to be validated as functional or removed if out of scope.

## What Changes
1.  **Driving Mode Optimization**:
    - Refine `DrivingModeScreen` UI for larger touch targets and clearer visual feedback.
    - Implement simplified "Seek" using gestures or large buttons.
    - (Optional/Phase 2) Investigate TTS/voice search feasibility.

2.  **Equalizer Implementation**:
    - Connect `EqualizerScreen` to the active ExoPlayer session.
    - Ensure `AudioSessionId` is propagated from `GeminiAudioService` -> `domain` -> `UI`.
    - Handle Equalizer presets and bands properly.

3.  **Settings Refinement**:
    - **Remove**: "Sleep Timer" entry from `SettingsScreen`.
    - **Move**: "Playback Settings" from `SettingsScreen` to the main navigation (Side Drawer or Home Dropdown) - *Correction*: User asked to move it to "main menu". Since we don't have a side drawer, the Home Dropdown (where Sleep Timer went) or a top-level route accessible from Home is best. The prompt says "Move Playback Settings from SettingsScreen.kt to the main menu (like the Sleep Timer)". This implies adding it to the `HomeScreenRedesigned.kt` dropdown.
    - **Cleanup**: Verify other settings (Language, Last.fm, etc.) and remove/stub if non-functional for this release.

## Impact
- **UI/UX**: Significant changes to Home Dropdown and Settings Screen structure. New capabilities in Driving Mode.
- **Architecture**: `MusicState` needs to carry `audioSessionId` to support the Equalizer without breaking clean architecture rules (UI shouldn't query Service directly).
- **Performance**: Negligible impact expected.

## Open Questions
- Voice Search/TTS strict requirements? We will implement placeholders or basic intent launching if native integration is too complex for this cycle.
