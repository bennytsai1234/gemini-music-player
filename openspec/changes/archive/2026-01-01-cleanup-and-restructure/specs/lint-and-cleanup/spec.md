## MODIFIED Requirements

### Requirement: Workspace Hygiene
> The repository workspace MUST remain clean and free of build artifacts, logs, or temporary files.

#### Scenario: Root Directory Cleanliness
- **Given** the project root directory
- **When** analyzed for file structure
- **Then** it MUST NOT contain temporary build logs (`.txt`, `.log`).
- **And** it MUST NOT contain ad-hoc build scripts (`.bat`, `.sh`) that bypass the standard build system or workflow.
- **And** it MUST contain `settings.gradle.kts` defining all active modules clearly.

#### Scenario: Module Definition
- **Given** `settings.gradle.kts`
- **When** defining included modules
- **Then** it MUST strictly list all active modules: `:app`, `:core:common`, `:core:designsystem`, `:data`, `:domain`, `:player`, `:ui`.
