# Tasks: rename-package-to-pulse

## 任務總覽
使用 Android Studio Refactor 功能將 package 從 `com.gemini.music` 重命名為 `com.pulse.music`。

> ⚠️ **重要**：此任務需要在 Android Studio 中手動執行，建議在執行前先提交或備份當前所有變更。

---

## Phase 0: 準備工作

### Task 0.1: 備份與準備
- [ ] 確保所有變更已 commit 到 Git
- [ ] 在 Android Studio 中關閉所有編輯器分頁
- [ ] 執行 `./gradlew clean` 清理構建緩存
- [ ] 重新 Sync Gradle 確保專案狀態正確

**指令**：
```bash
git add -A && git commit -m "chore: pre-refactor checkpoint"
./gradlew clean
```

---

## Phase 1: 執行 Android Studio Refactor

### Task 1.1: 重命名 Package (核心步驟)

**在 Android Studio 中執行以下步驟：**

1. **開啟專案結構視圖**
   - 切換到 **Project** 視圖 (左側面板)
   - 展開 `app > java > com.gemini.music`

2. **執行重構**
   - 右鍵點擊 `com.gemini.music` 資料夾
   - 選擇 **Refactor > Rename** (或按 `Shift+F6`)
   - 在彈出的對話框中，選擇 **Rename package**
   - 輸入新名稱：`com.pulse.music`
   - 勾選 **Search in comments and strings**
   - 勾選 **Search for text occurrences**
   - 點擊 **Refactor**

3. **檢視預覽**
   - Android Studio 會顯示所有將被變更的檔案
   - 仔細檢查變更清單
   - 點擊 **Do Refactor** 確認執行

4. **對每個模組重複上述步驟**
   - `ui > java > com.gemini.music.ui` → `com.pulse.music.ui`
   - `data > java > com.gemini.music.data` → `com.pulse.music.data`
   - `domain > java > com.gemini.music.domain` → `com.pulse.music.domain`
   - `player > java > com.gemini.music.player` → `com.pulse.music.player`
   - `core > common > java > com.gemini.music.core.common` → `com.pulse.music.core.common`
   - `core > designsystem > java > com.gemini.music.core.designsystem` → `com.pulse.music.core.designsystem`

---

## Phase 2: 手動更新 build.gradle.kts

### Task 2.1: 更新 app/build.gradle.kts
- [ ] 更新 `namespace = "com.pulse.music"`
- [ ] 更新 `applicationId = "com.pulse.music"`
- [ ] 更新 `versionCode = 14` (新版本)
- [ ] 更新 `versionName = "2.0.0"` (品牌重塑版本)

### Task 2.2: 更新各模組 build.gradle.kts
- [ ] `ui/build.gradle.kts`: `namespace = "com.pulse.music.ui"`
- [ ] `data/build.gradle.kts`: `namespace = "com.pulse.music.data"`
- [ ] `domain/build.gradle.kts`: `namespace = "com.pulse.music.domain"`
- [ ] `player/build.gradle.kts`: `namespace = "com.pulse.music.player"`
- [ ] `core/common/build.gradle.kts`: `namespace = "com.pulse.music.core.common"`
- [ ] `core/designsystem/build.gradle.kts`: `namespace = "com.pulse.music.core.designsystem"`

---

## Phase 3: 檢查 AndroidManifest.xml

### Task 3.1: 檢查 app/src/main/AndroidManifest.xml
- [ ] 確認 Application 類別路徑正確
- [ ] 確認 Activity 類別路徑正確
- [ ] 確認 Service 類別路徑正確
- [ ] 確認 BroadcastReceiver 類別路徑正確

### Task 3.2: 檢查其他模組的 Manifest
- [ ] 檢查 `player/src/main/AndroidManifest.xml`
- [ ] 檢查 `ui/src/main/AndroidManifest.xml`

---

## Phase 4: 驗證構建

### Task 4.1: Sync 與 Build
- [ ] 執行 Gradle Sync (點擊 "Sync Now")
- [ ] 執行 `./gradlew clean`
- [ ] 執行 `./gradlew assembleDebug`
- [ ] 確認無編譯錯誤

### Task 4.2: 修復錯誤 (如有)
- [ ] 檢查 Hilt 相關錯誤
- [ ] 檢查 R 類別引用錯誤
- [ ] 檢查 BuildConfig 引用錯誤
- [ ] 修復任何遺漏的 import 語句

---

## Phase 5: 功能驗證

### Task 5.1: 安裝與測試
- [ ] 執行 `./gradlew installDebug`
- [ ] App 可正常啟動
- [ ] 首頁顯示正確
- [ ] 音樂掃描正常
- [ ] 音樂播放正常
- [ ] 通知控制正常

### Task 5.2: 進階功能測試
- [ ] 播放清單功能
- [ ] 歌詞顯示功能
- [ ] 睡眠定時器
- [ ] 駕駛模式
- [ ] 等化器

---

## Phase 6: 清理

### Task 6.1: 搜尋殘留引用
- [ ] 搜尋 `com.gemini.music`，確認無殘留
- [ ] 檢查 proguard-rules.pro (如有)
- [ ] 檢查其他配置檔案

### Task 6.2: 刪除舊目錄
- [ ] 刪除空的 `com/gemini` 目錄結構 (如果 IDE 沒有自動清理)

### Task 6.3: 提交變更
```bash
git add -A
git commit -m "refactor: rename package from com.gemini.music to com.pulse.music"
```

---

## 預估工時

| Phase | 預估時間 |
|-------|---------|
| Phase 0: 準備 | 5 分鐘 |
| Phase 1: Android Studio Refactor | 15-20 分鐘 |
| Phase 2: 更新 build.gradle.kts | 10 分鐘 |
| Phase 3: 檢查 AndroidManifest | 10 分鐘 |
| Phase 4: 驗證構建 | 10 分鐘 |
| Phase 5: 功能驗證 | 15 分鐘 |
| Phase 6: 清理 | 5 分鐘 |
| **總計** | **~60-70 分鐘** |
