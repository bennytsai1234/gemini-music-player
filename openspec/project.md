# Project Context

## Purpose
Pulse is a modern, premium Android music playback application focusing on user experience, aesthetic excellence, and robust architecture.

## Tech Stack
- **Language**: Kotlin (Target JVM 17)
- **Framework**: Android SDK (Min SDK 26, Target SDK 36)
- **UI Framework**: Jetpack Compose (Declarative UI)
- **Architecture**: MVVM + Clean Architecture (Multi-module: app, core, data, domain, player, ui)
- **Dependency Injection**: Hilt (Dagger)
- **Build System**: Gradle (Kotlin DSL)
- **Asynchronous**: Coroutines & Flow

## Project Conventions

### Code Style
- **Language**: Traditional Chinese (繁體中文) for all documentation and comments.
- **Architecture**: Strict separation of concerns.
    - **Domain**: Pure Kotlin, no Android dependencies.
    - **Data**: Hides implementation details (API arm, DB, etc.).
    - **Player**: Media playback implementation (ExoPlayer), kept separate from UI.
    - **UI**: MVVM pattern, Unidirectional Data Flow (UDF), Statelsss components where possible.
- **State Management**:
    - Use `rememberSaveable` for process death resilience.
    - `SavedStateHandle` in ViewModels.
    - `@Immutable` state classes for composition optimization.

### Interaction Protocol
- **Communication Language**: The agent MUST always reply in **Traditional Chinese (繁體中文)**, regardless of the language of the user's prompt, unless explicitly asked otherwise.

### Architecture Patterns
- **Clean Architecture**: `UI -> Domain <- Data` dependency rule.
- **Modularization**: Feature-based or Layer-based modules.
- **Repository Pattern**: Mediates between domain and data sources.

### Testing Strategy
- Unit tests for Domain/Data layers (JUnit).
- Android Instrumentation tests for UI (Compose Test Rule).

### Git Workflow
- **Commit Style**: Conventional Commits (`feat:`, `fix:`, `chore:`, etc.).
- **Branching**: Feature branching strategy.
- **Sync Rule**: You MUST push changes to the remote repository (GitHub) immediately after completing a task or archiving a change.
- **Command**: `git push origin <branch>` (or just `git push` if upstream is set).

## Domain Context
- **Music Playback**: Core domain involves playback control, playlist management, and media library organization.
- **UX**: High priority on "Premium" feel, smooth animations, and error tolerance.

## Important Constraints
- **Performance**: Heavy data operations must run on `Dispatchers.Default`.
- **UI Stability**: Optimize for layout stability during window resizing or state changes.

## External Dependencies
- **Hilt**: Dependency Injection
- **Kotlin Coroutines**: Concurrency
- **Jetpack Compose**: UI Toolkit
- **Gradle**: Build Tool

## Brand Identity
- **English Name**: Pulse
- **Chinese Name**: 脈動
- **Brand Concept**: Music is the pulse of life - rhythm, heartbeat, and energy
- **Primary Colors**: Cyan (#00F2FF) to Blue (#0066FF) gradient with Magenta (#FF0080) accent
