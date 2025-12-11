package com.gemini.music.ui.equalizer

import android.media.audiofx.Equalizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EqualizerBand(
    val index: Int,
    val centerFrequency: Int, // Hz
    val minLevel: Int, // millibel
    val maxLevel: Int, // millibel
    val currentLevel: Int // millibel
) {
    val label: String
        get() = when {
            centerFrequency >= 1000 -> "${centerFrequency / 1000}k"
            else -> "$centerFrequency"
        }
    
    // Normalized level between 0 and 1
    val normalizedLevel: Float
        get() = (currentLevel - minLevel).toFloat() / (maxLevel - minLevel).toFloat()
}

data class EqualizerPreset(
    val index: Int,
    val name: String
)

data class EqualizerUiState(
    val isEnabled: Boolean = false,
    val bands: List<EqualizerBand> = emptyList(),
    val presets: List<EqualizerPreset> = emptyList(),
    val currentPresetIndex: Int = -1,
    val isAvailable: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EqualizerViewModel @Inject constructor() : ViewModel() {
    
    private var equalizer: Equalizer? = null
    
    private val _uiState = MutableStateFlow(EqualizerUiState())
    val uiState: StateFlow<EqualizerUiState> = _uiState.asStateFlow()
    
    fun initializeEqualizer(audioSessionId: Int) {
        viewModelScope.launch {
            try {
                // 0 is valid for global mix, though deprecated/restricted on some devices.
                // We proceed and let the try-catch handle failures.

                
                equalizer?.release()
                equalizer = Equalizer(0, audioSessionId).apply {
                    enabled = false
                }
                
                val eq = equalizer ?: return@launch
                
                val bands = (0 until eq.numberOfBands).map { i ->
                    val bandIndex = i.toShort()
                    EqualizerBand(
                        index = i,
                        centerFrequency = eq.getCenterFreq(bandIndex) / 1000, // Convert to Hz
                        minLevel = eq.bandLevelRange[0].toInt(),
                        maxLevel = eq.bandLevelRange[1].toInt(),
                        currentLevel = eq.getBandLevel(bandIndex).toInt()
                    )
                }
                
                val presets = (0 until eq.numberOfPresets).map { i ->
                    EqualizerPreset(
                        index = i,
                        name = eq.getPresetName(i.toShort())
                    )
                }
                
                _uiState.update {
                    it.copy(
                        isEnabled = eq.enabled,
                        bands = bands,
                        presets = presets,
                        isAvailable = true,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isAvailable = false,
                        errorMessage = "Equalizer not available: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        equalizer?.let { eq ->
            eq.enabled = enabled
            _uiState.update { it.copy(isEnabled = enabled) }
        }
    }
    
    fun setBandLevel(bandIndex: Int, normalizedLevel: Float) {
        equalizer?.let { eq ->
            val band = _uiState.value.bands.getOrNull(bandIndex) ?: return
            val level = (band.minLevel + (band.maxLevel - band.minLevel) * normalizedLevel).toInt()
            
            eq.setBandLevel(bandIndex.toShort(), level.toShort())
            
            _uiState.update { state ->
                state.copy(
                    bands = state.bands.map { 
                        if (it.index == bandIndex) it.copy(currentLevel = level) else it
                    },
                    currentPresetIndex = -1 // Custom when user adjusts
                )
            }
        }
    }
    
    fun selectPreset(presetIndex: Int) {
        equalizer?.let { eq ->
            eq.usePreset(presetIndex.toShort())
            
            // Refresh band levels
            val bands = _uiState.value.bands.map { band ->
                band.copy(currentLevel = eq.getBandLevel(band.index.toShort()).toInt())
            }
            
            _uiState.update {
                it.copy(
                    bands = bands,
                    currentPresetIndex = presetIndex
                )
            }
        }
    }
    
    fun resetToFlat() {
        equalizer?.let { eq ->
            val midLevel = ((eq.bandLevelRange[0] + eq.bandLevelRange[1]) / 2).toShort()
            
            for (i in 0 until eq.numberOfBands) {
                eq.setBandLevel(i.toShort(), midLevel)
            }
            
            val bands = _uiState.value.bands.map { 
                it.copy(currentLevel = midLevel.toInt()) 
            }
            
            _uiState.update {
                it.copy(bands = bands, currentPresetIndex = -1)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        equalizer?.release()
        equalizer = null
    }
}
