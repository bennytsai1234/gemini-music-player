package com.gemini.music.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemini.music.domain.repository.ScrobbleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LastFmUiState(
    val isConnected: Boolean = false,
    val username: String? = null,
    val isLoading: Boolean = false,
    val authToken: String? = null,
    val authUrl: String? = null,
    val error: String? = null,
    val pendingScrobbleCount: Int = 0
)

@HiltViewModel
class LastFmSettingsViewModel @Inject constructor(
    private val scrobbleRepository: ScrobbleRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LastFmUiState())
    val uiState: StateFlow<LastFmUiState> = _uiState.asStateFlow()
    
    init {
        checkConnectionStatus()
        observePendingScrobbles()
    }
    
    private fun checkConnectionStatus() {
        viewModelScope.launch {
            scrobbleRepository.isExternalServiceConnected().collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
                if (connected) {
                    loadUsername()
                }
            }
        }
    }
    
    private fun loadUsername() {
        viewModelScope.launch {
            val username = scrobbleRepository.getUsername()
            _uiState.update { it.copy(username = username) }
        }
    }
    
    private fun observePendingScrobbles() {
        viewModelScope.launch {
            scrobbleRepository.getPendingScrobbles().collect { pending ->
                _uiState.update { it.copy(pendingScrobbleCount = pending.size) }
            }
        }
    }
    
    /**
     * 開始認證流程
     * 獲取 Token 並生成授權 URL
     */
    fun startAuthentication() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val token = scrobbleRepository.getAuthToken()
            if (token != null) {
                val authUrl = scrobbleRepository.getAuthUrl(token)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        authToken = token,
                        authUrl = authUrl
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "無法連接到 Last.fm，請稍後再試"
                    ) 
                }
            }
        }
    }
    
    /**
     * 完成認證
     * 用戶在瀏覽器中授權後調用
     */
    fun completeAuthentication() {
        val token = _uiState.value.authToken ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val success = scrobbleRepository.completeAuthentication(token)
            if (success) {
                loadUsername()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authToken = null,
                        authUrl = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "認證失敗，請確認已在瀏覽器中完成授權"
                    )
                }
            }
        }
    }
    
    /**
     * 登出 Last.fm
     */
    fun logout() {
        viewModelScope.launch {
            scrobbleRepository.logout()
            _uiState.update { 
                it.copy(
                    username = null,
                    authToken = null,
                    authUrl = null
                ) 
            }
        }
    }
    
    /**
     * 同步待處理的 Scrobble
     */
    fun syncPendingScrobbles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val synced = scrobbleRepository.syncToExternalService()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = if (synced > 0) null else "沒有需要同步的 Scrobble"
                ) 
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearAuthState() {
        _uiState.update { it.copy(authToken = null, authUrl = null) }
    }
}
