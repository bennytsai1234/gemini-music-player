package com.pulse.music.domain.repository

import com.pulse.music.domain.model.AppTheme
import com.pulse.music.domain.model.CustomTheme
import com.pulse.music.domain.model.ThemePalette
import com.pulse.music.domain.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

/**
 * 主題管理 Repository
 */
interface ThemeRepository {
    
    /**
     * 觀察主題設定變化
     */
    fun observeThemeSettings(): Flow<ThemeSettings>
    
    /**
     * 觀察運行時主題 (包含計算後的顏色)
     */
    fun observeAppTheme(): Flow<AppTheme>
    
    /**
     * 更新主題設定
     */
    suspend fun updateThemeSettings(settings: ThemeSettings)
    
    /**
     * 設置主題調色盤
     */
    suspend fun setThemePalette(palette: ThemePalette)
    
    /**
     * 啟用/禁用動態顏色 (Material You)
     */
    suspend fun setDynamicColorEnabled(enabled: Boolean)
    
    /**
     * 啟用/禁用 AMOLED 純黑
     */
    suspend fun setAmoledBlackEnabled(enabled: Boolean)
    
    /**
     * 取得所有自定義主題
     */
    fun observeCustomThemes(): Flow<List<CustomTheme>>
    
    /**
     * 儲存自定義主題
     */
    suspend fun saveCustomTheme(theme: CustomTheme)
    
    /**
     * 刪除自定義主題
     */
    suspend fun deleteCustomTheme(themeId: String)
    
    /**
     * 選擇自定義主題
     */
    suspend fun selectCustomTheme(themeId: String)
    
    /**
     * 取得當前主題設定 (非 Flow)
     */
    suspend fun getCurrentThemeSettings(): ThemeSettings
}
