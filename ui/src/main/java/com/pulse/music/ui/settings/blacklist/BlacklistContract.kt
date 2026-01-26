package com.pulse.music.ui.settings.blacklist

import com.pulse.music.core.common.base.UiEffect
import com.pulse.music.core.common.base.UiEvent
import com.pulse.music.core.common.base.UiState
import com.pulse.music.domain.model.BlacklistItem

data class BlacklistUiState(
    val blacklist: List<BlacklistItem> = emptyList(),
    val isLoading: Boolean = false
) : UiState

sealed interface BlacklistUiEvent : UiEvent {
    data class AddPath(val path: String, val isFolder: Boolean) : BlacklistUiEvent
    data class RemovePath(val path: String) : BlacklistUiEvent
}

sealed interface BlacklistUiEffect : UiEffect {
    data class ShowToast(val message: String) : BlacklistUiEffect
}
