package com.gemini.music.ui.theme

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 動態主題狀態管理器
 * 從專輯封面提取主題色並應用到整個 App
 */
class DynamicThemeState {
    
    var dominantColor by mutableStateOf(Color(0xFF6C63FF)) // Default primary
        private set
    
    var accentColor by mutableStateOf(Color(0xFF00E5FF)) // Default accent
        private set
    
    var surfaceColor by mutableStateOf(Color(0xFF1E1E1E)) // Default surface
        private set
    
    var onSurfaceColor by mutableStateOf(Color.White)
        private set
    
    var isLight by mutableStateOf(false)
        private set
    
    /**
     * 從 Bitmap 提取主題色
     */
    suspend fun extractColorsFromBitmap(bitmap: Bitmap?) = withContext(Dispatchers.Default) {
        if (bitmap == null) {
            resetToDefaults()
            return@withContext
        }
        
        try {
            val palette = Palette.from(bitmap).generate()
            
            // 優先順序：Vibrant > Muted > DominantSwatch
            val vibrant = palette.vibrantSwatch
            val muted = palette.mutedSwatch
            val dominant = palette.dominantSwatch
            
            val primarySwatch = vibrant ?: muted ?: dominant
            val accentSwatch = palette.lightVibrantSwatch ?: palette.lightMutedSwatch ?: vibrant
            
            primarySwatch?.let { swatch ->
                val rgb = swatch.rgb
                dominantColor = Color(rgb)
                
                // 計算是否為亮色
                isLight = ColorUtils.calculateLuminance(rgb) > 0.5
                
                // 文字顏色
                onSurfaceColor = if (isLight) Color.Black else Color.White
                
                // Surface 使用暗化版本
                surfaceColor = Color(
                    ColorUtils.blendARGB(rgb, android.graphics.Color.BLACK, 0.7f)
                )
            }
            
            accentSwatch?.let { swatch ->
                accentColor = Color(swatch.rgb)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resetToDefaults()
        }
    }
    
    private fun resetToDefaults() {
        dominantColor = Color(0xFF6C63FF)
        accentColor = Color(0xFF00E5FF)
        surfaceColor = Color(0xFF1E1E1E)
        onSurfaceColor = Color.White
        isLight = false
    }
}
