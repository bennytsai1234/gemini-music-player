# Proposal: Cleanup and Restructure Project

## Summary
Remove redundant log files and scripts from the project root, and verify/update the project architecture documentation to align with the current Clean Architecture implementation.

## Motivation
The project root contains several temporary build logs (`.txt`) and ad-hoc scripts (`.bat`) that clutter the workspace and should not be part of the source control or active project structure. Cleaning these up improves project hygiene. Additionally, ensuring the project structure is explicitly defined in OpenSpec helps maintain architectural integrity.

## Proposed Changes
1.  **Delete Redundant Files**: Remove `build_error_*.txt`, `build_log.txt`, `build_result.txt` from the root directory.
2.  **Evaluate Scripts**: Remove `build_deploy.bat` if it's redundant to standard Gradle tasks or `openspec` workflows.
3.  **Update Documentation**: Ensure `openspec/project.md` accurately reflects the module structure (`app`, `core`, `data`, `domain`, `player`, `ui`).

## Risks
- Losing `build_deploy.bat` might disrupt a specific manual workflow if not properly documented or replaced. (Will check content first).
