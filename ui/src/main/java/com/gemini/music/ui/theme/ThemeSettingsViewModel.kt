package com.gemini.music.ui.theme

import androidx.lifecycle.viewModelScope
import com.gemini.music.core.common.base.BaseViewModel
import com.gemini.music.domain.model.CustomTheme
import com.gemini.music.domain.model.ThemeMode
import com.gemini.music.domain.model.ThemePalette
import com.gemini.music.domain.model.ThemeSettings
import com.gemini.music.domain.usecase.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val observeThemeSettingsUseCase: ObserveThemeSettingsUseCase,
    private val setThemePaletteUseCase: SetThemePaletteUseCase,
    private val toggleDynamicColorUseCase: ToggleDynamicColorUseCase,
    private val toggleAmoledBlackUseCase: ToggleAmoledBlackUseCase,
    private val manageCustomThemeUseCase: ManageCustomThemeUseCase,
    private val updateThemeSettingsUseCase: UpdateThemeSettingsUseCase
) : BaseViewModel<ThemeSettingsUiState, ThemeSettingsUiEvent, ThemeSettingsUiEffect>(
    ThemeSettingsUiState()
) {
    init {
        observeThemeSettings()
        observeCustomThemes()
    }
    
    private fun observeThemeSettings() {
        observeThemeSettingsUseCase()
            .onEach { settings ->
                setState {
                    copy(
                        currentMode = settings.themeMode,
                        currentPalette = settings.selectedPalette,
                        useDynamicColor = settings.useDynamicColor,
                        useAmoledBlack = settings.useAmoledBlack,
                        contrastLevel = settings.contrastLevel,
                        selectedCustomThemeId = settings.customThemeId
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeCustomThemes() {
        manageCustomThemeUseCase.observeCustomThemes()
            .onEach { themes ->
                setState { copy(customThemes = themes) }
            }
            .launchIn(viewModelScope)
    }
    
    override fun handleEvent(event: ThemeSettingsUiEvent) {
        when (event) {
            is ThemeSettingsUiEvent.SetThemeMode -> handleSetThemeMode(event.mode)
            is ThemeSettingsUiEvent.SetPalette -> handleSetPalette(event.palette)
            is ThemeSettingsUiEvent.SetDynamicColor -> handleSetDynamicColor(event.enabled)
            is ThemeSettingsUiEvent.SetAmoledBlack -> handleSetAmoledBlack(event.enabled)
            is ThemeSettingsUiEvent.SetContrastLevel -> handleSetContrastLevel(event.level)
            is ThemeSettingsUiEvent.SelectCustomTheme -> handleSelectCustomTheme(event.themeId)
            is ThemeSettingsUiEvent.DeleteCustomTheme -> handleDeleteCustomTheme(event.themeId)
            is ThemeSettingsUiEvent.ShowCustomThemeEditor -> setState { copy(showCustomThemeEditor = true, editingTheme = null) }
            is ThemeSettingsUiEvent.HideCustomThemeEditor -> setState { copy(showCustomThemeEditor = false, editingTheme = null) }
            is ThemeSettingsUiEvent.SaveCustomTheme -> handleSaveCustomTheme(event.theme)
            is ThemeSettingsUiEvent.EditCustomTheme -> setState { copy(showCustomThemeEditor = true, editingTheme = event.theme) }
        }
    }
    
    private fun handleSetThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            updateThemeSettingsUseCase(
                ThemeSettings(
                    themeMode = mode,
                    useDynamicColor = currentState.useDynamicColor,
                    selectedPalette = currentState.currentPalette,
                    useAmoledBlack = currentState.useAmoledBlack || mode == ThemeMode.AMOLED,
                    contrastLevel = currentState.contrastLevel
                )
            )
            setEffect { ThemeSettingsUiEffect.ThemeApplied }
        }
    }
    
    private fun handleSetPalette(palette: ThemePalette) {
        viewModelScope.launch {
            setThemePaletteUseCase(palette)
            setEffect { ThemeSettingsUiEffect.ThemeApplied }
        }
    }
    
    private fun handleSetDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            toggleDynamicColorUseCase(enabled)
            setEffect { ThemeSettingsUiEffect.ThemeApplied }
        }
    }
    
    private fun handleSetAmoledBlack(enabled: Boolean) {
        viewModelScope.launch {
            toggleAmoledBlackUseCase(enabled)
            setEffect { ThemeSettingsUiEffect.ThemeApplied }
        }
    }
    
    private fun handleSetContrastLevel(level: Float) {
        viewModelScope.launch {
            updateThemeSettingsUseCase(
                ThemeSettings(
                    themeMode = currentState.currentMode,
                    useDynamicColor = currentState.useDynamicColor,
                    selectedPalette = currentState.currentPalette,
                    useAmoledBlack = currentState.useAmoledBlack,
                    contrastLevel = level
                )
            )
        }
    }
    
    private fun handleSelectCustomTheme(themeId: String) {
        viewModelScope.launch {
            manageCustomThemeUseCase.select(themeId)
            setEffect { ThemeSettingsUiEffect.ThemeApplied }
        }
    }
    
    private fun handleDeleteCustomTheme(themeId: String) {
        viewModelScope.launch {
            manageCustomThemeUseCase.delete(themeId)
            setEffect { ThemeSettingsUiEffect.ShowMessage("主題已刪除") }
        }
    }
    
    private fun handleSaveCustomTheme(theme: CustomTheme) {
        viewModelScope.launch {
            manageCustomThemeUseCase.save(theme)
            setState { copy(showCustomThemeEditor = false, editingTheme = null) }
            setEffect { ThemeSettingsUiEffect.ShowMessage("主題已儲存") }
        }
    }
}


