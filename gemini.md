# Gemini CLI 核心行為準則 (Core Guidelines)

本檔案定義了 Gemini CLI 的核心操作規範、架構偏好與技術最佳實踐。此準則適用於所有 Android/Kotlin 專案開發，旨在確保代碼品質、一致性與使用者體驗。

---

### **第一章：互動協議 (Interaction Protocol)**

*   **溝通語言**: 預設使用 **繁體中文 (Traditional Chinese)**。
*   **自主執行 (Autonomous Execution)**: 對於多步驟任務，應自動連續執行，僅在遇到致命錯誤或需人工決策時暫停。
---

### **第二章：工作方法論 (Working Methodology)**

#### **§1 代碼完整性 (Code Integrity)**
*   **尊重現狀**: 修改前必須理解既有邏輯。
*   **優先順序**: **可讀性** > **維護性** > **效能**。
*   **原子化提交 (Atomic Commits)**: 
    *   將變更分解為最小邏輯單元。
    *   Commit Message 遵循 Conventional Commits (e.g., `feat:`, `fix:`, `chore:`).

#### **§2 增量交付 (Incremental Delivery)**
*   避免一次性提交數百行的巨大變更。
*   每個階段性成果（如：完成 UI 佈局、完成資料層連接）都應可被獨立驗證。

---

### **第三章：通用架構規範 (Universal Architecture Standards)**

本規範基於 **Clean Architecture** 與 **Modularization** 原則。

#### **§1 分層職責 (Layer Responsibilities)**
1.  **Domain Layer** (Kotlin Rules):
    *   **純粹性**: 嚴禁依賴 Android Framework。
    *   **職責**: 定義核心業務規則 (UseCases) 與抽象介面 (Repository Interfaces)。
2.  **Data Layer** (implementation):
    *   **職責**: 提供資料源實作 (API, DB, Preferences)。
    *   **封裝**: 對外隱藏具體資料來源細節，僅暴露 Domain 模型。
3.  **UI Layer** (Interaction):
    *   **職責**: 狀態呈現與使用者互動。
    *   **限制**: **嚴禁**直接依賴 Data Layer。必須透過 UseCases 與 Domain 互動。

#### **§2 依賴與數據流 (Dependency & Data Flow)**
*   **Dependency Rule**: `UI -> Domain <- Data`
*   **Data Flow**: `UI (Event) -> Domain -> Data` ... `Data (Stream/Flow) -> Domain -> UI (State)`

---

### **第四章：技術最佳實踐 (Technical Excellence)**

本章節總結了跨專案通用的技術陷阱與解決方案，**必須**在設計階段納入考量。

#### **§1 狀態韌性 (State Resilience)**
*   **Process Death 防護**: 所有關鍵 UI 狀態（尤其是 **Bottom Sheets**, **Expanded Views**, **Complex Navigation State**）**必須**使用 `rememberSaveable` 而非 `remember`。對於 ViewModel 層級的狀態，**必須**使用 `SavedStateHandle` 進行持久化，確保應用在後台被系統回收後，用戶返回時能看到一致的畫面（如排序選項、選取模式）。
*   **動態佈局適配**: 對於依賴容器尺寸計算的狀態（如 `AnchoredDraggableState` 的 anchors），應在 `LaunchedEffect` 或 `onSizeChanged` 中更新配置，**嚴禁**因尺寸變化而重新創建 (Re-create) 狀態物件，這會導致互動中斷或狀態重置。

#### **§2 極致使用者體驗 (UX Excellence)**
*   **智慧列表定位 (Smart List Positioning)**: 
    *   當進入一個長列表且有明確「活躍項目」時，應自動滾動至該項目 (Auto-scroll to active item)。
*   **容錯互動設計 (Forgiving Interactions)**:
    *   **手勢導航**: 自定義滑動控件（如側邊索引欄）應具備「吸附」或「智慧查找」功能。若用戶手指滑到無效區域，應自動定位至最近的有效內容，而非無反應。
    *   **視覺反饋**: 任何拖動、長按操作都必須提供即時的視覺提示（如氣泡、高亮、震動回饋）。
*   **誠實 UI (Honest UI)**: 
    *   若某 UI 元素看似可互動（如 Drag Handle），則必須具備相應功能。若功能未實作，應隱藏該元素或替換為資訊性組件（如 Metadata 標籤），避免欺騙用戶預期。

#### **§3 系統兼容性與除錯 (System Compatibility & Debugging)**
*   **權限策略 (Permission Strategy)**: 
    *   針對 Android 13+ (API 33+) 的 `POST_NOTIFICATIONS` 與細分媒體權限 (`READ_MEDIA_AUDIO` 等)，**必須**在相關功能啟動前 (如 `MainActivity.onCreate` 或播放前) 進行檢查與請求。永遠不要假設權限已被授予。
*   **Release 模式驗證 (Verifying in Release Mode)**:
    *   Jetpack Compose 在 Debug 模式下會有顯著的效能開銷（尤其是 120Hz 滾動）。遇到「卡頓」問題時，**優先**檢查 Release Build 是否正常，避免在 Debug 模式下過早優化。

#### **§4 效能優化 (Performance Optimization)**
*   **後台線程優先 (Background Thread First)**:
    *   所有資料處理（如 `combine`, `map`, `filter`, `sortedBy`）若涉及大量數據，**必須**透過 `.flowOn(Dispatchers.Default)` 移至後台線程執行，嚴禁阻塞 UI 線程。
*   **狀態穩定性 (State Stability)**:
    *   所有 UI State 類別（特別是包含 `List` 的）**必須**標註 `@Immutable` 或 `@Stable`。這能啟用 Compose 的智慧重組跳過機制 (Smart Recomposition Skipping)。
*   **佈局穩定性 (Layout Stability)**:
    *   **固定高度優先**: 在複雜列表 (`LazyColumn`) 中，如果可能，**強制設定項目為固定高度** (e.g., `Modifier.height(72.dp)`)。這能大幅減少測量開銷並消除滾動抖動 (Scroll Jitter)。
*   **輕量化渲染 (Lightweight Rendering)**:
    *   **移除 Heavy Wrappers**: 避免在列表項中使用高開銷容器（如 `Card`），改用輕量級 Modifier（如 `Modifier.clip()`, `Modifier.border()`）。
    *   **圖片載入優化**: 使用 Coil 時，**必須**指定 `.size()` (載入縮圖)、`.memoryCacheKey()` 並明確指定 `Dispatchers.IO`，避免主線程阻塞。
    *   **Callback 穩定化**: 傳遞給子組件的 Lambda，若依賴外部狀態，應使用 `rememberUpdatedState` 包裝或優化為方法引用，避免導致子組件無效重組。
    *   **搜尋輸入防抖 (Search Debounce)**:
        *   針對本地搜尋 (Local Search) 或頻繁過濾操作，**必須**實作 Debounce (建議 300ms) 並配合 `.flowOn(Dispatchers.Default)`。這能避免每次按鍵都觸發重計算，導致輸入卡頓。
    *   **導航解耦 (Decoupled Navigation)**:
        *   深層組件 (如 `NowPlayingScreen`) 不應持有 `NavController`。應透過 Lambda 回調 (e.g. `onAlbumClick: (Long) -> Unit`) 將導航事件向外傳遞給頂層 NavHost 處理。這能保持組件純淨且易於測試。

#### §5 命令行操作 (Command Line Operations)
*   **無輸出處理 (No Output Handling)**: 
    *   當發現 `run_command` 沒有預期輸出時，**強制**改用 `run_command` 啟動一個持續的 Shell Session (如 `cmd`)，接著使用 `send_command_input` 發送指令，以確保能獲取執行結果。
---
---

## Gemini Added Memories
- User prefers Traditional Chinese (繁體中文) for interaction.
