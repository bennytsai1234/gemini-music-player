# Player Layer Guidelines

## 核心原則 (Core Principles)
*   **Implementation Detail**: 這是媒體播放的具體實作層 (使用 Media3/ExoPlayer)。
*   **Service-based**: 運行 Foreground Service (`GeminiAudioService`) 以確保背景播放。

## 結構 (Structure)
*   **service/**: 包含 `MediaLibraryService` 的實作。
*   **manager/**: 包含 `MusicServiceConnection`，它實作了 Domain 層的 `MusicController` 介面。
*   **notification/**: 處理媒體通知。

## 互動 (Interaction)
*   透過實作 `domain` 層定義的 `MusicController` 介面，將播放功能暴露給其他模組 (主要是 UI 透過 UseCase 或直接觀察 State)。
*   Data 層不應知道 Player 層的存在。
