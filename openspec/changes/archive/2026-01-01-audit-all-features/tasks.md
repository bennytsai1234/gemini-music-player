# Tasks: Comprehensive Feature Audit

## Phase 1: Core Playback Audit

- [x] Audit Home Screen functionality <!-- id: audit-home-screen -->
  - ✅ Song list rendering and scrolling performance - OK
  - ✅ Fast scroller visibility - FIXED (80dp bottom padding added)
  - ✅ Mini player integration - OK
  - ✅ Drawer menu navigation - OK
  - ✅ Uses GeminiTopBar - OK

- [x] Audit Now Playing Screen <!-- id: audit-now-playing -->
  - ✅ Album artwork display and color extraction - FIXED (connected to ViewModel)
  - ✅ Playback controls (play/pause, skip, shuffle, repeat) - FIXED (icon visibility)
  - ✅ Dynamic color theming - FIXED (proper contrast for buttons)
  - ✅ Options bottom sheet works correctly

- [x] Audit Queue Screen <!-- id: audit-queue -->
  - ✅ Queue list display and current song indicator - OK
  - ✅ Swipe-to-dismiss functionality - OK
  - ✅ GeminiTopBar navigation consistency - FIXED PREVIOUSLY

- [x] Audit Mini Player <!-- id: audit-mini-player -->
  - ✅ Collapsed state rendering - OK
  - ✅ Progress indicator - OK
  - ✅ Dynamic theming from album art - OK

## Phase 2: Navigation & Discovery Audit

- [x] Audit Albums Screen <!-- id: audit-albums -->
  - ✅ Uses GeminiTopBarWithBack - OK
  - ✅ Album artwork loading - OK

- [x] Audit Album Detail Screen <!-- id: audit-album-detail -->
  - ✅ Navigation back to albums - OK
  - ✅ Play all / shuffle all actions - OK

- [x] Audit Artist Screen <!-- id: audit-artists -->
  - ✅ Uses GeminiTopBar - FIXED (migrated from CenterAlignedTopAppBar)

- [x] Audit Folder Browser <!-- id: audit-folders -->
  - ✅ Uses GeminiTopBar - FIXED (migrated from CenterAlignedTopAppBar)
  - ✅ Breadcrumb navigation - OK

- [x] Audit Favorites Screen <!-- id: audit-favorites -->
  - ✅ Uses GeminiTopBarWithBack - OK

- [x] Audit Search Screen <!-- id: audit-search -->
  - ✅ Search interface - OK

## Phase 3: Settings & Configuration Audit

- [x] Audit Settings Screen <!-- id: audit-settings -->
  - ✅ Uses GeminiTopBar - OK
  - ✅ Navigation to sub-screens - OK

- [x] Audit Theme Settings <!-- id: audit-theme-settings -->
  - ✅ Theme mode selector works

- [x] Audit Playback Settings <!-- id: audit-playback-settings -->
  - ✅ Uses GeminiTopBar - FIXED PREVIOUSLY

- [x] Audit Equalizer <!-- id: audit-equalizer -->
  - ✅ Uses GeminiTopBar - FIXED (migrated from CenterAlignedTopAppBar)
  - ✅ Band sliders functionality - OK
  - ✅ Preset selection - OK

- [x] Audit Sleep Timer <!-- id: audit-sleep-timer -->
  - ✅ Timer implementation appears correct (deferred to runtime testing)

## Phase 4: Specialized Features Audit

- [x] Audit Driving Mode <!-- id: audit-driving-mode -->
  - ✅ Large control buttons - OK

- [x] Audit Stats Screen <!-- id: audit-stats -->
  - ✅ Uses GeminiTopBar - FIXED PREVIOUSLY

- [x] Audit Playlist List <!-- id: audit-playlists -->
  - ✅ Uses GeminiTopBarWithBack - OK

- [x] Audit Playlist Detail <!-- id: audit-playlist-detail -->
  - ✅ Uses GeminiTopBar - FIXED (migrated from CenterAlignedTopAppBar)
  - ✅ Swipe-to-delete works

## Phase 6: Cross-Cutting Concerns

- [x] Verify all TopBars use GeminiTopBar <!-- id: verify-topbar-usage -->
  - ✅ All screens now use GeminiTopBar or GeminiTopBarWithBack
  - ✅ Migrated: ArtistDetailScreen, FolderBrowserScreen, EqualizerScreen, PlaylistDetailScreen
  - ✅ Previously fixed: QueueScreen, StatsScreen, PlaybackSettingsScreen

- [x] Verify Material 3 compliance <!-- id: verify-material3 -->
  - ✅ Color scheme usage - OK
  - ✅ Component styling - OK

- [x] Verify dynamic color theming <!-- id: verify-dynamic-colors -->
  - ✅ Album art color extraction - FIXED
  - ✅ Player background gradient - FIXED
  - ✅ Button icon contrast - FIXED

## Summary of Fixes Applied

### TopBar Consistency (4 screens fixed this session)
1. `ArtistDetailScreen.kt` - migrated to GeminiTopBar
2. `FolderBrowserScreen.kt` - migrated to GeminiTopBar
3. `EqualizerScreen.kt` - migrated to GeminiTopBar
4. `PlaylistDetailScreen.kt` - migrated to GeminiTopBar

### Previously Fixed (from earlier sessions)
- `QueueScreen.kt` - migrated to GeminiTopBar
- `StatsScreen.kt` - migrated to GeminiTopBar
- `PlaybackSettingsScreen.kt` - migrated to GeminiTopBar
- `NowPlayingScreen.kt` - dynamic color extraction and button contrast
- `HomeScreen.kt` - FastScroller bottom padding
