# ui-theming Specification

## Purpose
TBD - created by archiving change verify-material3-compliance. Update Purpose after archive.
## Requirements
### Requirement: Strict Material 3 Implementation
The application MUST use `androidx.compose.material3` artifacts exclusively for UI components requiring Material Design.

#### Scenario: No Legacy Imports
- **Given** the developer adds a new UI component
- **When** they import a specific component
- **Then** the import package MUST NOT be `androidx.compose.material` (M2)

### Requirement: Semantic Coloring
All UI components MUST use `MaterialTheme.colorScheme` tokens instead of hardcoded hex values or raw colors, to ensure support for Dark/Light mode switching.

#### Scenario: Dark Mode Switching
- **Given** a custom card component is displayed
- **When** the system theme switches from Light to Dark
- **Then** the card background color MUST update automatically based on `colorScheme.surface` or `surfaceVariant`

### Requirement: Typography Scale
All text elements MUST use `MaterialTheme.typography` styles (`bodyLarge`, `labelSmall`, etc.) rather than manually setting `fontSize` and `fontFamily` repeatedly.

#### Scenario: Consistent Font Sizing
- **Given** a new screen is created
- **When** a paragraph of text is added
- **Then** it MUST use `style = MaterialTheme.typography.bodyMedium` (or similar) instead of raw text styling

