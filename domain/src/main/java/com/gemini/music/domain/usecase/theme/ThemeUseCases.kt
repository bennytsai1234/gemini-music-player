package com.gemini.music.domain.usecase.theme

import com.gemini.music.domain.model.AppTheme
import com.gemini.music.domain.model.CustomTheme
import com.gemini.music.domain.model.ThemeMode
import com.gemini.music.domain.model.ThemePalette
import com.gemini.music.domain.model.ThemeSettings
import com.gemini.music.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 觀察當前應用主題
 */
class ObserveAppThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<AppTheme> = themeRepository.observeAppTheme()
}

/**
 * 觀察主題設定
 */
class ObserveThemeSettingsUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<ThemeSettings> = themeRepository.observeThemeSettings()
}

/**
 * 切換主題調色盤
 */
class SetThemePaletteUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(palette: ThemePalette) {
        themeRepository.setThemePalette(palette)
    }
}

/**
 * 切換動態顏色 (Material You)
 */
class ToggleDynamicColorUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        themeRepository.setDynamicColorEnabled(enabled)
    }
}

/**
 * 切換 AMOLED 純黑模式
 */
class ToggleAmoledBlackUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        themeRepository.setAmoledBlackEnabled(enabled)
    }
}

/**
 * 管理自定義主題
 */
class ManageCustomThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    fun observeCustomThemes(): Flow<List<CustomTheme>> = 
        themeRepository.observeCustomThemes()
    
    suspend fun save(theme: CustomTheme) {
        themeRepository.saveCustomTheme(theme)
    }
    
    suspend fun delete(themeId: String) {
        themeRepository.deleteCustomTheme(themeId)
    }
    
    suspend fun select(themeId: String) {
        themeRepository.selectCustomTheme(themeId)
    }
}

/**
 * 更新完整主題設定
 */
class UpdateThemeSettingsUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(settings: ThemeSettings) {
        themeRepository.updateThemeSettings(settings)
    }
}
