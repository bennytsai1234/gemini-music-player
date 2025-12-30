## 1. Android Auto / Media Browser Support
- [x] 1.1 Add string resources for root categories (All Songs, Albums, Artists, Recently Added) in `strings.xml`.
- [x] 1.2 Inject `MusicRepository` into `GeminiAudioService` (ensure Scope compatibility).
- [x] 1.3 Implement `onGetChildren` in `GeminiAudioService`:
    - [x] Handle `MEDIA_ALL_SONGS_ID`: particular query to repository.
    - [x] Handle `MEDIA_ALBUMS_ID`: query albums.
    - [x] Handle `MEDIA_ARTISTS_ID`: query artists.
    - [x] Map Domain objects to `MediaItem` with correct metadata extras.

## 2. Batch Deletion UX (Android 11+)
- [x] 2.1 Refactor `HomeViewModel.deleteSelected`:
    - [x] Collect all execution `Uri`s.
    - [x] Check SDK version (Android 11/R+).
    - [x] Use `MediaStore.createTrashRequest` (preferred) or `createDeleteRequest` for the list of URIs.
    - [x] Catch `IntentSender` and expose to UI via `recoverableAction`.
- [x] 2.2 Update `MainScreen` or `HomeScreen` to launch the `IntentSender` correctly for the batch request.

## 3. Localization
- [x] 3.1 Replace hardcoded "Gemini Music" in `onGetLibraryRoot` with resource string.
