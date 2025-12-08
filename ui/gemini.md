# UI Layer Guidelines

## 核心原則 (Core Principles)
*   **MVVM**: 使用 Model-View-ViewModel 架構。
*   **Passive View**: UI (Composables/Activities) 應只負責顯示與接收使用者輸入，不包含複雜邏輯。
*   **Unidirectional Data Flow (UDF)**: 狀態 (State) 向下流動，事件 (Event) 向上流動。

## 結構 (Structure)
*   **Features**: 按功能分包 (e.g., `home/`, `nowplaying/`, `settings/`)。
*   **ViewModel**:
    *   持有 `UiState` (StateFlow)。
    *   透過 `UseCase` 執行業務邏輯。
    *   處理 UI Events。
*   **Screen**: Jetpack Compose 元件。

## 依賴規則 (Dependency Rules)
*   ✅ 依賴 `:domain` (使用 UseCases)。
*   ✅ 依賴 `:core:designsystem` (UI 元件與主題)。
*   ❌ **嚴禁** 直接依賴 `:data` 層。不可在 ViewModel 中直接注入 Repository 實作類別 (除非是介面，但最好透過 UseCase 封裝)。
