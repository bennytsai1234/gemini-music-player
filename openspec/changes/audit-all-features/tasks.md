# Tasks: Comprehensive Feature Audit

## Phase 1: Core Playback Audit

- [ ] Audit Home Screen functionality <!-- id: audit-home-screen -->
  - Song list rendering and scrolling performance
  - Sorting options (title, artist, album, date added)
  - Multi-selection mode and actions
  - Fast scroller visibility and responsiveness
  - Mini player integration
  - Drawer menu navigation

- [ ] Audit Now Playing Screen <!-- id: audit-now-playing -->
  - Album artwork display and color extraction
  - Playback controls (play/pause, skip, shuffle, repeat)
  - Progress bar/waveform seek functionality
  - Lyrics toggle and karaoke scrolling
  - Options bottom sheet (playlist, equalizer, timer, etc.)
  - Dynamic color theming

- [ ] Audit Queue Screen <!-- id: audit-queue -->
  - Queue list display and current song indicator
  - Swipe-to-dismiss functionality
  - Drag-to-reorder in sort mode
  - Play item on tap
  - GeminiTopBar navigation consistency

- [ ] Audit Mini Player <!-- id: audit-mini-player -->
  - Collapsed state rendering
  - Expand/collapse gestures
  - Progress indicator
  - Play/pause and skip controls
  - Dynamic theming from album art

## Phase 2: Navigation & Discovery Audit

- [ ] Audit Albums Screen <!-- id: audit-albums -->
  - Album grid/list toggle
  - Album artwork loading
  - Navigation to album detail
  - Sorting and filtering

- [ ] Audit Album Detail Screen <!-- id: audit-album-detail -->
  - Album header with artwork
  - Song list with track numbers
  - Play all / shuffle all actions
  - Navigation back to albums

- [ ] Audit Artist Screen <!-- id: audit-artists -->
  - Artist listing and avatars
  - Artist detail navigation
  - Album/song breakdown per artist

- [ ] Audit Folder Browser <!-- id: audit-folders -->
  - Folder hierarchy navigation
  - Folder/file icons
  - Breadcrumb or back navigation

- [ ] Audit Favorites Screen <!-- id: audit-favorites -->
  - Favorites list rendering
  - Heart icon state sync
  - Remove from favorites action

- [ ] Audit Search Screen <!-- id: audit-search -->
  - Search input and keyboard
  - Real-time search results
  - Result categories (songs, albums, artists)
  - Clear search functionality

## Phase 3: Settings & Configuration Audit

- [ ] Audit Settings Screen <!-- id: audit-settings -->
  - Settings categories organization
  - Navigation to sub-screens
  - Back button alignment (GeminiTopBar)

- [ ] Audit Theme Settings <!-- id: audit-theme-settings -->
  - Theme mode selector (System/Light/Dark)
  - Dynamic color toggle
  - AMOLED black toggle
  - Color palette selection
  - Custom theme functionality

- [ ] Audit Playback Settings <!-- id: audit-playback-settings -->
  - Crossfade toggle and duration
  - Gapless playback option
  - Audio focus handling options
  - GeminiTopBar consistency

- [ ] Audit Equalizer <!-- id: audit-equalizer -->
  - Band sliders functionality
  - Preset selection
  - Bass boost and virtualizer
  - Loudness enhancer
  - Save/reset functionality

- [ ] Audit Sleep Timer <!-- id: audit-sleep-timer -->
  - Timer preset selection
  - Custom duration input
  - Timer cancellation
  - End-of-track option
  - Timer active indicator

## Phase 4: Specialized Features Audit

- [ ] Audit Driving Mode <!-- id: audit-driving-mode -->
  - Large control buttons
  - Simplified UI layout
  - Swipe gestures on artwork
  - Exit driving mode action

- [ ] Audit Lyrics Editor <!-- id: audit-lyrics-editor -->
  - Lyrics display and editing
  - Timestamp sync functionality
  - Save changes

- [ ] Audit Tag Editor <!-- id: audit-tag-editor -->
  - Metadata fields display
  - Edit and save functionality
  - Artwork change support

- [ ] Audit Stats Screen <!-- id: audit-stats -->
  - Listening statistics display
  - Time period filters
  - Visual charts/graphs
  - Refresh functionality
  - GeminiTopBar consistency

- [ ] Audit Visualizer <!-- id: audit-visualizer -->
  - Audio visualization rendering
  - Visualization style options
  - Performance during playback

## Phase 5: Playlist Management Audit

- [ ] Audit Playlist List <!-- id: audit-playlists -->
  - Playlist cards/list items
  - Create new playlist action
  - Playlist artwork display
  - Navigation to playlist detail

- [ ] Audit Playlist Detail <!-- id: audit-playlist-detail -->
  - Playlist header with info
  - Song list with drag reorder
  - Remove song action
  - Edit playlist name/artwork
  - Play all / shuffle actions

- [ ] Audit Add to Playlist Dialog <!-- id: audit-add-to-playlist -->
  - Dialog presentation
  - Existing playlists list
  - Create new playlist option
  - Confirmation feedback

## Phase 6: Cross-Cutting Concerns

- [ ] Verify all TopBars use GeminiTopBar <!-- id: verify-topbar-usage -->
  - Check navigation icon alignment
  - Verify title styling
  - Confirm action icons

- [ ] Verify Material 3 compliance <!-- id: verify-material3 -->
  - Color scheme usage
  - Component styling
  - Typography application

- [ ] Verify dynamic color theming <!-- id: verify-dynamic-colors -->
  - Album art color extraction
  - Player background gradient
  - Text contrast

- [ ] Verify empty states <!-- id: verify-empty-states -->
  - All list screens have empty state
  - Empty state includes icon and message
  - Optional action button present

- [ ] Verify error handling <!-- id: verify-error-handling -->
  - Network errors
  - File access errors
  - Graceful degradation
