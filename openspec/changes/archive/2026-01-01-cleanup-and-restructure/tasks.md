<!-- OPENSPEC:START -->
- [x] 1. Delete redundant log files from root (`build_error_*.txt`, `build_log.txt`, `build_result.txt`).
- [x] 2. Delete `build_deploy.bat` to enforce standard Git/Gradle workflows.
- [x] 3. Update `openspec/project.md` to explicitly include the `:player` module in the Architecture section.
- [x] 4. Verify project compilation (`./gradlew assembleDebug`) to ensure no hidden dependencies on the deleted scripts.
<!-- OPENSPEC:END -->
