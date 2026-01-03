package com.pulse.music.ui.settings.backup

import com.pulse.music.core.common.base.UiEffect
import com.pulse.music.core.common.base.UiEvent
import com.pulse.music.core.common.base.UiState

data class BackupUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val lastBackupTime: Long? = null,
    val error: String? = null,
    val successMessage: String? = null
) : UiState

sealed interface BackupUiEvent : UiEvent {
    data object SignIn : BackupUiEvent
    data object SignOut : BackupUiEvent
    data object Backup : BackupUiEvent
    data object Restore : BackupUiEvent
    data object ClearError : BackupUiEvent
    data object ClearSuccess : BackupUiEvent
    data class AuthenticateResult(val success: Boolean) : BackupUiEvent // 來自 Activity Result
}

sealed interface BackupUiEffect : UiEffect {
    data class LaunchSignIn(val intent: android.content.Intent) : BackupUiEffect
    data class ShowToast(val message: String) : BackupUiEffect
}


