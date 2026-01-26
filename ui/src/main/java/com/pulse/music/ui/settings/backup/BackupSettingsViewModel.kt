package com.pulse.music.ui.settings.backup

import androidx.lifecycle.viewModelScope
import com.pulse.music.core.common.base.BaseViewModel
import com.pulse.music.domain.model.backup.BackupResult
import com.pulse.music.domain.model.backup.RestoreResult
import com.pulse.music.domain.usecase.backup.GetBackupStatusUseCase
import com.pulse.music.domain.usecase.backup.ManageBackupSessionUseCase
import com.pulse.music.domain.usecase.backup.PerformCloudBackupUseCase
import com.pulse.music.domain.usecase.backup.RestoreCloudBackupUseCase
import com.pulse.music.domain.usecase.backup.RestoreBackupUseCase
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.pulse.music.core.common.auth.GoogleAuthProvider

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val performCloudBackupUseCase: PerformCloudBackupUseCase,
    private val restoreCloudBackupUseCase: RestoreCloudBackupUseCase,
    private val exportBackupUseCase: com.pulse.music.domain.usecase.backup.ExportBackupUseCase,
    private val restoreBackupUseCase: RestoreBackupUseCase,
    private val getBackupStatusUseCase: GetBackupStatusUseCase,
    private val manageBackupSessionUseCase: ManageBackupSessionUseCase,
    private val googleAuthProvider: GoogleAuthProvider,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
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
                performCloudBackup()
            }
            is BackupUiEvent.Restore -> {
                performCloudRestore()
            }
            is BackupUiEvent.ExportLocal -> {
                exportLocalBackup(event.uri)
            }
            is BackupUiEvent.ImportLocal -> {
                importLocalBackup(event.uri)
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

    private fun performCloudBackup() {
        viewModelScope.launch {
            setState { copy(isBackingUp = true, error = null) }
            
            when (val result = performCloudBackupUseCase()) {
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

    private fun performCloudRestore() {
        viewModelScope.launch {
            setState { copy(isRestoring = true, error = null) }
            
            val result = restoreCloudBackupUseCase()
            if (result.errors.isEmpty()) {
                setState { 
                    copy(
                        isRestoring = false, 
                        successMessage = "成功還原 ${result.playlistsRestored} 個清單, ${result.favoritesRestored} 個最愛" 
                    ) 
                }
            } else {
                setState { copy(isRestoring = false, error = result.errors.firstOrNull()) }
            }
        }
    }

    private fun exportLocalBackup(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isBackingUp = true, error = null) }
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    exportBackupUseCase(output)
                }
                val time = System.currentTimeMillis()
                setState { 
                    copy(
                        isBackingUp = false, 
                        successMessage = "已匯出備份至檔案",
                        lastBackupTime = time
                    ) 
                }
            } catch (e: Exception) {
                setState { copy(isBackingUp = false, error = "匯出失敗: ${e.message}") }
            }
        }
    }

    private fun importLocalBackup(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isRestoring = true, error = null) }
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val result = restoreBackupUseCase(input)
                    if (result.errors.isEmpty()) {
                        setState { 
                            copy(
                                isRestoring = false, 
                                successMessage = "成功還原 ${result.playlistsRestored} 個清單, ${result.favoritesRestored} 個最愛" 
                            ) 
                        }
                    } else {
                        setState { 
                            copy(
                                isRestoring = false, 
                                error = "還原完成但有錯誤: ${result.errors.firstOrNull()}"
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                setState { copy(isRestoring = false, error = "還原失敗: ${e.message}") }
            }
        }
    }
}


