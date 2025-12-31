# Spec: UI Dynamic Colors

## ADDED Requirements

### Requirement: Now Playing Immersion
The Full Screen Player MUST adapt its visual appearance to the content being played.

#### Scenario: Dynamic Background
- **Given** a song with colorful album art is playing
- **When** the user opens the Now Playing screen
- **Then** the background MUST be a gradient derived from the album art's vibrant colors.
- **And** text/controls MUST remain legible against this dynamic background.

#### Scenario: Fallback Color
- **Given** a song with no album art or failed color extraction
- **When** the user opens the Now Playing screen
- **Then** the background MUST default to the standard Material 3 surface colors.
