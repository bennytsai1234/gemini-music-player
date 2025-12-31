# Spec: UI Polish

## ADDED Requirements

### Requirement: Album Artwork Dynamic Colors
NowPlayingScreen MUST extract and apply colors from the album artwork to create an immersive visual experience.

#### Scenario: Dynamic Player Background
Given the user is viewing the full-screen player
When an album with artwork is playing
Then the player background MUST display a gradient using colors extracted from the album artwork
And the play/pause button MUST use the vibrant extracted color with white icon for contrast
And all text MUST maintain proper contrast against the dynamic background.

### Requirement: Fast Scroller Visibility
The alphabetical fast scroller MUST remain fully visible and interactive regardless of mini player state.

#### Scenario: Fast Scroller with Mini Player
Given the user is on the home screen with songs displayed
And the mini player is visible at the bottom
When the user views the fast scroller
Then the 'Z' section button MUST be fully visible and tappable
And the scroller MUST have sufficient bottom padding to clear the mini player (at least 80dp).

(Previously: Fast scroller had no bottom padding, causing 'Z' to be obscured)

## MODIFIED Requirements

### Requirement: Navigation Icon Alignment
All top app bars MUST use consistent heights and icon positioning.

#### Scenario: Consistent Back Button Position
Given the user navigates from Home to Settings
When viewing both screens' top bars
Then the Back button in Settings MUST appear in the same screen coordinates as the Menu button in Home.


(This requirement is a refinement of the existing ui-consistency spec and has now been fully implemented across all screens)

