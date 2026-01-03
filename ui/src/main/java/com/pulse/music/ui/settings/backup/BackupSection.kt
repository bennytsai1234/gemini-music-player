package com.pulse.music.ui.settings.backup

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulse.music.core.common.base.UiEffect
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BackupSection(
    viewModel: BackupSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Google Sign-In Launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            viewModel.onEvent(BackupUiEvent.AuthenticateResult(account != null))
        } catch (e: Exception) {
            viewModel.onEvent(BackupUiEvent.AuthenticateResult(false))
        }
    }

    // Handle Side Effects
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BackupUiEffect.LaunchSignIn -> {
                    signInLauncher.launch(effect.intent)
                }
                is BackupUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "雲端備份與還原",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (uiState.isSignedIn) {
                    SignedInContent(
                        lastBackupTime = uiState.lastBackupTime,
                        isBackingUp = uiState.isBackingUp,
                        isRestoring = uiState.isRestoring,
                        onBackup = { viewModel.onEvent(BackupUiEvent.Backup) },
                        onRestore = { viewModel.onEvent(BackupUiEvent.Restore) },
                        onSignOut = { viewModel.onEvent(BackupUiEvent.SignOut) }
                    )
                } else {
                    Button(
                        onClick = { viewModel.onEvent(BackupUiEvent.SignIn) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Login, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("登入 Google 雲端硬碟")
                    }
                }
                
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (uiState.successMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.successMessage ?: "",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SignedInContent(
    lastBackupTime: Long?,
    isBackingUp: Boolean,
    isRestoring: Boolean,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onSignOut: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    
    Text(
        text = if (lastBackupTime != null) 
            "上次備份: ${dateFormat.format(Date(lastBackupTime))}" 
        else "尚未備份",
        style = MaterialTheme.typography.bodyMedium
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onBackup,
            enabled = !isBackingUp && !isRestoring,
            modifier = Modifier.weight(1f)
        ) {
            if (isBackingUp) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Rounded.CloudUpload, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("備份")
            }
        }
        
        OutlinedButton(
            onClick = onRestore,
            enabled = !isBackingUp && !isRestoring,
            modifier = Modifier.weight(1f)
        ) {
            if (isRestoring) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Rounded.CloudDownload, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("還原")
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        TextButton(
            onClick = onSignOut
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("登出")
        }
    }
}


