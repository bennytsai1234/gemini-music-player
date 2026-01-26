package com.pulse.music.ui.settings.blacklist

import androidx.lifecycle.viewModelScope
import com.pulse.music.core.common.base.BaseViewModel
import com.pulse.music.domain.usecase.blacklist.AddBlacklistItemUseCase
import com.pulse.music.domain.usecase.blacklist.GetBlacklistUseCase
import com.pulse.music.domain.usecase.blacklist.RemoveBlacklistItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlacklistSettingsViewModel @Inject constructor(
    private val getBlacklistUseCase: GetBlacklistUseCase,
    private val addBlacklistItemUseCase: AddBlacklistItemUseCase,
    private val removeBlacklistItemUseCase: RemoveBlacklistItemUseCase
) : BaseViewModel<BlacklistUiState, BlacklistUiEvent, BlacklistUiEffect>(BlacklistUiState()) {

    init {
        loadBlacklist()
    }

    private fun loadBlacklist() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            getBlacklistUseCase().collectLatest { items ->
                setState { copy(blacklist = items, isLoading = false) }
            }
        }
    }

    override fun handleEvent(event: BlacklistUiEvent) {
        when (event) {
            is BlacklistUiEvent.AddPath -> {
                viewModelScope.launch {
                    addBlacklistItemUseCase(event.path, event.isFolder)
                    setEffect { BlacklistUiEffect.ShowToast("Added to blacklist") }
                }
            }
            is BlacklistUiEvent.RemovePath -> {
                viewModelScope.launch {
                    removeBlacklistItemUseCase(event.path)
                    setEffect { BlacklistUiEffect.ShowToast("Removed from blacklist") }
                }
            }
        }
    }
}
