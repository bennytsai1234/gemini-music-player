# Design: Cleanup and Restructure

## Architecture
The project follows a standard Android Clean Architecture with Multi-Module setup.

### Modules:
- **:app**: Application entry point, DI setup (Hilt), packaging.
- **:ui**: Feature-agnostic UI components and Screens (Jetpack Compose).
- **:domain**: Pure Kotlin module containing Business Logic, Use Cases, Repository interfaces, and Models.
- **:data**: Implementation of Repository interfaces, Data Sources (local/remote), API calls.
- **:player**: Music playback logic (ExoPlayer/Media3), service handling.
- **:core:common**: Shared utility functions and extensions.
- **:core:designsystem**: Design tokens (Colors, Typography) and base UI components.

## Cleanup Strategy
- **Logs**: Files like `build_error_*.txt` are artifacts of manual debugging and should not persist. They will be deleted.
- **Scripts**: `build_deploy.bat` contains hardcoded commit messages and combines build/deploy in a way that bypasses proper review. It will be removed in favor of standard CLI usage (`./gradlew` + `git`).

## Documentation
- `openspec/project.md` will be updated to explicitly list these modules and their responsibilities if not already present.
