# Spec: Equalizer Persistence

## ADDED Requirements

### Requirement: Persist Loudness Enhancer Settings
The application MUST persist the user's "Loudness Enhancer" state (Enabled/Disabled) and "Gain" value across application restarts.

#### Scenario: User sets Loudness and restarts
- **Given** the user is on the Equalizer screen.
- **When** the user enables "Loudness Enhancer" and sets Gain to "50%".
- **And** the user closes and kills the application.
- **And** the user re-opens the application and navigates to Equalizer.
- **Then** the "Loudness Enhancer" switch should be ON.
- **And** the Gain slider should be at "50%".

### Requirement: Reset to Flat
When "Reset" is clicked, Loudness Enhancer settings MUST also reset to defaults (Disabled, 0 Gain).

#### Scenario: User resets equalizer
- **Given** Loudness Enhancer is Enabled with High Gain.
- **When** the user clicks the "Reset" button in the toolbar.
- **Then** all Bands reset to flat.
- **And** Loudness Enhancer resets to Disabled (or just 0 Gain? - *Decision: Reset to Disabled/0*).
