package com.gemini.music.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.gemini.music.domain.repository.UserPreferencesRepository
import com.gemini.music.core.designsystem.GeminiTheme
import com.gemini.music.ui.navigation.MusicNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permissions
        // Optional: Trigger UI update or VM scan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Splash Screen
        installSplashScreen()
        
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        checkPermissions()

        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = UserPreferencesRepository.THEME_SYSTEM)
            val isSystemDark = isSystemInDarkTheme()
            
            val darkTheme = when (themeMode) {
                 UserPreferencesRepository.THEME_LIGHT -> false
                 UserPreferencesRepository.THEME_DARK -> true
                 else -> isSystemDark
            }

            GeminiTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        val permissionsToRequest = permissions.filter {
             ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
