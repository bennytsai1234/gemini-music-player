# UI Spec Delta: Settings and Driving Mode

## ADDED Requirements

### Requirement: Settings Screen
> The Settings screen MUST provide streamlined configuration without redundant or misplaced options.


#### Scenario: Settings Navigation
- **Given** the user is on the Settings Screen
- **When** they look for "Sleep Timer" or "Playback Settings"
- **Then** these options are NOT present in the list (moved to Home).
- **And** they see "Equalizer" which functions correctly.

### Requirement: Home Screen
> The Home screen MUST provide quick access to core playback controls and settings.


#### Scenario: Main Menu
- **Given** the user is on the Home Screen
- **When** they expand the dropdown menu
- **Then** they see "Playback Settings" option.
- **When** they tap it
- **Then** they are navigated to the Playback Settings screen.

### Requirement: Driving Mode
> The Driving mode MUST offer a simplified, safe interface for controlling music while driving.


#### Scenario: Optimized Controls
- **Given** the user is in Driving Mode
- **Then** the Play/Pause button and Skip buttons are significantly larger than standard UI.
- **And** there are explicit "Rewind" and "Fast Forward" buttons (or a large seek bar).
- **And** swiping left/right on the cover art area skips tracks.
