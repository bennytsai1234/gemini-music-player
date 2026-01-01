package com.gemini.music.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for dynamic theme state
 * 允許整個 App 訪問當前的動態主題顏色
 */
val LocalDynamicTheme = compositionLocalOf { DynamicThemeState() }


