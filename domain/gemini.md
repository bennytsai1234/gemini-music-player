# Domain Layer Guidelines

## 核心原則 (Core Principles)
*   **Pure Kotlin**: 盡量保持為純 Kotlin 模組，不依賴 Android Framework（除了必要的 javax.inject 或少數特例）。
*   **Business Logic Center**: 包含所有的業務邏輯、實體 (Entities) 與使用案例 (UseCases)。
*   **Dependency Rule**: Domain 層**不應依賴**任何其他層級 (UI, Data, Player)。它是架構的核心。

## 結構 (Structure)
*   **model/**: 定義核心資料模型 (如 `Song`, `Playlist`)。這些應該是簡單的 Data Classes。
*   **repository/**: 定義介面 (Interfaces)。Data 層負責實作這些介面。
    *   *Example*: `MusicRepository`, `UserPreferencesRepository`.
*   **usecase/**: 封裝單一業務邏輯操作。
    *   *Naming*: `Verb` + `Noun` + `UseCase` (e.g., `PlaySongUseCase`).
    *   *Single Responsibility*: 每個 UseCase 只做一件事。

## 互動 (Interaction)
*   UseCase 應只依賴 Repository 介面或其他 UseCase。
