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

