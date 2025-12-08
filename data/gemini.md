# Data Layer Guidelines

## 核心原則 (Core Principles)
*   **Repository Implementation**: 負責實作 Domain 層定義的 Repository 介面。
*   **Data Sources**: 管理所有資料來源，包括資料庫 (Room)、網路 (Retrofit/OkHttp)、檔案系統 (File System) 和 DataStore。
*   **Single Source of Truth**: 確保資料的一致性 (通常由資料庫擔任)。

## 結構 (Structure)
*   **repository/**: 包含 `RepositoryImpl` 類別。
    *   *Example*: `MusicRepositoryImpl` 實作 `MusicRepository`。
*   **source/**: (可選) 區分 Local 與 Remote 資料來源。
*   **di/**: 提供 Hilt Modules (`DataModule`) 來綁定介面與實作。

## 依賴規則 (Dependency Rules)
*   ✅ 依賴 `:domain` (為了存取 Repository 介面與 Models)。
*   ✅ 依賴 `:core:common` (共用工具)。
*   ❌ **嚴禁** 被 `:ui` 層直接依賴。UI 必須透過 Domain 層與 Data 互動。
