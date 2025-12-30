# Spec: UI Polish - Empty States & Lyrics

## ADDED Requirements

### Requirement: Premium Empty States
All list screens (Home, Playlist, etc.) MUST display a visually appealing "Empty State" when no data is available. This includes a prominent icon (or illustration) and a clear text message.

#### Scenario: User has no playlists
- **Given** the user navigates to the "Playlists" tab/screen.
- **And** no playlists exist.
- **Then** the screen displays a large "Playlist" icon (tinted/styled).
- **And** a text message "No Playlists yet".
- **And** (Optional) a button "Create Playlist".

### Requirement: Optimized Lyrics Scrolling
The Karaoke Lyrics view MUST keep the currently playing line vertically centered on the screen and animate smoothly between lines.

#### Scenario: Song plays
- **Given** the "Lyrics" view is open.
- **When** the song progresses to the next line.
- **Then** the list scrolls smoothly to bring the new line to the center.
- **And** the current line scales up/highlights.
