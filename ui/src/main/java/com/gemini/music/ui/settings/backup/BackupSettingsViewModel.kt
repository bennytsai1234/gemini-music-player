package com.gemini.music.ui.settings.backup

import androidx.lifecycle.viewModelScope
import com.gemini.music.core.common.base.BaseViewModel
import com.gemini.music.domain.model.backup.BackupResult
import com.gemini.music.domain.model.backup.RestoreResult
import com.gemini.music.domain.usecase.backup.GetBackupStatusUseCase
import com.gemini.music.domain.usecase.backup.ManageBackupSessionUseCase
import com.gemini.music.domain.usecase.backup.PerformBackupUseCase
import com.gemini.music.domain.usecase.backup.RestoreBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.gemini.music.core.common.auth.GoogleAuthProvider

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val performBackupUseCase: PerformBackupUseCase,
    private val restoreBackupUseCase: RestoreBackupUseCase,
    private val getBackupStatusUseCase: GetBackupStatusUseCase,
    private val manageBackupSessionUseCase: ManageBackupSessionUseCase,
    private val googleAuthProvider: GoogleAuthProvider
) : BaseViewModel<BackupUiState, BackupUiEvent, BackupUiEffect>(BackupUiState()) {

    init {
        observeSignInStatus()
        refreshLastBackupTime()
    }

    private fun observeSignInStatus() {
        viewModelScope.launch {
            getBackupStatusUseCase.isSignedIn.collect { signedIn ->
                setState { copy(isSignedIn = signedIn) }
                if (signedIn) {
                    refreshLastBackupTime()
                }
            }
        }
    }

    private fun refreshLastBackupTime() {
        viewModelScope.launch {
            val time = getBackupStatusUseCase.getLastBackupTime()
            setState { copy(lastBackupTime = time) }
        }
    }

    override fun handleEvent(event: BackupUiEvent) {
        when (event) {
            is BackupUiEvent.SignIn -> {
                val intent = googleAuthProvider.getSignInIntent()
                setEffect { BackupUiEffect.LaunchSignIn(intent) }
            }
            is BackupUiEvent.SignOut -> {
                viewModelScope.launch {
                    manageBackupSessionUseCase.signOut()
                    setEffect { BackupUiEffect.ShowToast("已登出 Google 雲端硬碟") }
                }
            }
            is BackupUiEvent.Backup -> {
                performBackup()
            }
            is BackupUiEvent.Restore -> {
                performRestore()
            }
            is BackupUiEvent.AuthenticateResult -> {
                if (event.success) {
                    viewModelScope.launch {
                        manageBackupSessionUseCase.signIn() // 這裡可能只是確認狀態
                        refreshLastBackupTime()
                    }
                } else {
                    setState { copy(error = "登入失敗") }
                }
            }
            is BackupUiEvent.ClearError -> {
                setState { copy(error = null) }
            }
            is BackupUiEvent.ClearSuccess -> {
                setState { copy(successMessage = null) }
            }
        }
    }

    private fun performBackup() {
        viewModelScope.launch {
            setState { copy(isBackingUp = true, error = null) }
            
            when (val result = performBackupUseCase()) {
                is BackupResult.Success -> {
                    val time = System.currentTimeMillis()
                    setState { 
                        copy(
                            isBackingUp = false, 
                            successMessage = "備份成功",
                            lastBackupTime = time
                        ) 
                    }
                }
                is BackupResult.Error -> {
                    setState { copy(isBackingUp = false, error = result.message) }
                }
            }
        }
    }

    private fun performRestore() {
        viewModelScope.launch {
            setState { copy(isRestoring = true, error = null) }
            
            when (val result = restoreBackupUseCase()) {
                is RestoreResult.Success -> {
                    setState { 
                        copy(
                            isRestoring = false, 
                            successMessage = "成功還原 ${result.itemsRestored} 個項目" 
                        ) 
                    }
                    // 可能需要重整某些 UI 或數據
                }
                is RestoreResult.Error -> {
                    setState { copy(isRestoring = false, error = result.message) }
                }
            }
        }
    }
}
