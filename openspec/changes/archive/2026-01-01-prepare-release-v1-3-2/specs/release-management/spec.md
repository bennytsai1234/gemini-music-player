# release-management

## MODIFIED Requirements

### Requirement: Release Documentation

> All public releases MUST be accompanied by human-readable release notes.

#### Scenario: Update release notes

- **Given** Recent bug fixes and cleanups are merged.
- **When** Preparing for release v1.3.2.
- **Then** `RELEASE_NOTES.md` MUST reflect these changes in a structured format.

### Requirement: Version Tagging

> Every major milestone MUST be market with a git tag and incremented version codes.

#### Scenario: Successful release tagging

- **Given** The workspace is clean and ready.
- **When** Building the production APK.
- **Then** The `versionCode` MUST be incremented.
- **And** A git tag following the version name (e.g., `v1.3.2`) MUST be created.
