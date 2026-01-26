package com.pulse.music.ui.smb

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulse.music.domain.model.SmbResource
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.repository.MusicController
import com.pulse.music.domain.repository.SmbRepository
import com.pulse.music.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SmbUiState(
    val currentPath: String? = null, // null means showing server list
    val servers: List<String> = emptyList(),
    val files: List<SmbResource> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val breadcrumbs: List<Pair<String, String>> = emptyList() // path to name
)

@HiltViewModel
class SmbBrowserViewModel @Inject constructor(
    private val smbRepository: SmbRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val musicController: MusicController
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmbUiState())
    
    val uiState: StateFlow<SmbUiState> = combine(
        _uiState,
        userPreferencesRepository.smbServers
    ) { state, servers ->
        if (state.currentPath == null) {
            state.copy(servers = servers.toList().sorted())
        } else {
            state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SmbUiState())

    fun navigateTo(path: String) {
        _uiState.update { it.copy(currentPath = path, isLoading = true, error = null) }
        loadPath(path)
    }

    fun navigateUp() {
        val current = _uiState.value.currentPath ?: return
        if (isRoot(current)) {
            _uiState.update { it.copy(currentPath = null, files = emptyList()) }
        } else {
            val parent = getParentPath(current)
            navigateTo(parent)
        }
    }

    fun addServer(url: String) {
        viewModelScope.launch {
            val validUrl = if (url.startsWith("smb://")) url else "smb://$url"
            // Simple validation check
            _uiState.update { it.copy(isLoading = true) }
            val success = smbRepository.checkConnection(validUrl)
            
            if (success) {
                val currentServers = userPreferencesRepository.smbServers.first().toMutableSet()
                currentServers.add(validUrl)
                userPreferencesRepository.setSmbServers(currentServers)
                _uiState.update { it.copy(isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to connect to $validUrl") }
            }
        }
    }
    
    fun removeServer(url: String) {
        viewModelScope.launch {
             val currentServers = userPreferencesRepository.smbServers.first().toMutableSet()
             currentServers.remove(url)
             userPreferencesRepository.setSmbServers(currentServers)
        }
    }

    fun playFile(resource: SmbResource) {
        // Create a temporary Song object for the player
        // Ideally we should extract metadata, but for now we stream directly
        val song = Song(
            id = resource.path.hashCode().toLong(),
            title = resource.name,
            artist = "SMB Stream",
            album = "Network",
            duration = 0,
            contentUri = resource.path, // Use path as contentUri for now, SmbDataSource handles smb://
            dataPath = resource.path, // This is smb://...
            albumId = 0,
            dateAdded = resource.lastModified / 1000
        )
        
        // We need to tell the player this is a remote file?
        // Actually the PlayerController just takes Song list.
        // The ExoPlayer setup needs to handle the smb:// scheme.
        // We registered SmbDataSourceFactory so it should work if we pass Uri.
        
        musicController.playSongs(listOf(song))
    }

    private fun loadPath(path: String) {
        viewModelScope.launch {
            try {
                smbRepository.listFiles(path).collect { files ->
                    _uiState.update { 
                        it.copy(
                            files = files.sortedWith(compareBy({ !it.isDirectory }, { it.name })),
                            isLoading = false,
                            breadcrumbs = buildBreadcrumbs(path)
                        ) 
                    }
                }
            } catch (e: Exception) {
                 _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private fun isRoot(path: String): Boolean {
         // Check if path is just the server root e.g. smb://host/ or smb://host/share/
         // Ideally our smbRepository.listFiles handles shares listing on root?
         // SmbRepositoryImpl needs explicit share name currently: smb://host/share
         
         // If path is smb://host/share, parent is smb://host (which we don't support browsing yet, we list saved servers)
         // So if we are at a saved server URL, navigating up goes to server list (null)
         
         val servers = uiState.value.servers
         // Better check:
         return servers.any { server -> 
             path.trim('/') == server.trim('/') 
         }
    }
    
    private fun getParentPath(path: String): String {
        val clean = path.trimEnd('/')
        return clean.substringBeforeLast("/") + "/"
    }
    
    private fun buildBreadcrumbs(path: String): List<Pair<String, String>> {
        // smb://host/share/folder/subfolder
        // -> host, share, folder, subfolder
        val parts = path.removePrefix("smb://").split("/").filter { it.isNotEmpty() }
        val crumbs = mutableListOf<Pair<String, String>>()
        var current = "smb://"
        
        parts.forEach { part ->
            current += "$part/"
            crumbs.add(current to part)
        }
        return crumbs
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
