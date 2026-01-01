# release-management Specification

## Purpose
TBD - created by archiving change publish-release-v1-2. Update Purpose after archive.
## Requirements
### Requirement: Version Tagging

> Every major milestone MUST be market with a git tag and incremented version codes.

#### Scenario: Successful release tagging

- **Given** The workspace is clean and ready.
- **When** Building the production APK.
- **Then** The `versionCode` MUST be incremented.
- **And** A git tag following the version name (e.g., `v1.3.2`) MUST be created.

### Requirement: Release Documentation

> All public releases MUST be accompanied by human-readable release notes.

#### Scenario: Update release notes

- **Given** Recent bug fixes and cleanups are merged.
- **When** Preparing for release v1.3.2.
- **Then** `RELEASE_NOTES.md` MUST reflect these changes in a structured format.

### Requirement: Versioning
The application MUST adhere to semantic versioning.

#### Scenario: v1.3.1 Release
Given the application source code includes recent UI consistency fixes
When the release build is generated
Then the version name MUST be "1.3.1"
And the version code MUST be greater than the previous version (8).

