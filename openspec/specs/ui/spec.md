# ui Specification

## Purpose
TBD - created by archiving change fix-core-gaps. Update Purpose after archive.
## Requirements
### Requirement: Batch Deletion
The system MUST support deleting multiple media files in a single user transaction.

#### Scenario: Batch Delete on Android 11+
- **WHEN** the user selects multiple songs and taps "Delete"
- **THEN** the system presents a **single** system dialog requesting permission to trash/delete all selected files.
- **AND** upon confirmation, all files are removed from the library and storage.

### Requirement: Sleep Timer UI
The App SHALL provide a user interface to schedule automatic playback termination.

#### Scenario: Set preset timer
- **WHEN** the user selects "Sleep Timer" from the menu
- **AND** chooses a preset duration (e.g., 30 minutes)
- **THEN** the system requests the audio service to stop playback after that duration
- **AND** a confirmation message is shown

#### Scenario: Cancel timer
- **WHEN** the user opens the Sleep Timer dialog while a timer is active
- **AND** selects "Turn Off Timer" (or similar cancellation option)
- **THEN** the pending sleep timer is cancelled

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

