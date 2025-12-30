# Change: Fix Core Logic Gaps (Android Auto, Widget, UX)

## Why
A comprehensive audit of the project revealed logical gaps that compromise the user experience and platform integration:
1. **Android Auto/Media Browser**: The service returns empty lists for content queries, rendering external media browsers useless.
2. **App Widget**: Missing album artwork creates a substandard visual experience.
3. **Batch Operations**: Deleting multiple songs triggers multiple system dialogs on Android 11+, which is a poor user experience.
4. **Hardcoded Strings**: The media browser root uses hardcoded English strings, ignoring localization.

## What Changes
- **Android Auto Integration**:
    - Implement `onGetChildren` in `GeminiAudioService` to properly fetch and return songs/albums/artists from `MusicRepository`.
    - Map domain models to `MediaItem` correctly for the media browser.
- **Widget Constraints**:
    - *Note*: Loading bitmaps in `RemoteViews` is complex and performance-heavy. For this iteration, we successfully defer this to a future update to focus on functional correctness first. We will focus on ensuring metadata (Title/Artist) and play states are robust.
- **Batch Deletion**:
    - Refactor `deleteSelected` in `HomeViewModel` to use `MediaStore.createTrashRequest` (Android 11+) or `createDeleteRequest` to batch permission requests into a single dialog.
- **Localization**:
    - Replace hardcoded "Gemini Music" and category titles with string resources.

## Impact
- **Specs**:
    - `specs/player/spec.md`: Define media browser behavior.
    - `specs/ui/spec.md`: Define batch deletion UX.
- **Code**:
    - `GeminiAudioService.kt`: Logic for `onGetChildren`.
    - `HomeViewModel.kt`: Refactor delete logic.
    - `strings.xml`: Add missing strings.
