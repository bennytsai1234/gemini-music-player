package com.gemini.music.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gemini.music.domain.model.ThemeMode
import com.gemini.music.domain.model.ThemePalette

/**
 * 主題設定畫面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    viewModel: ThemeSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // 處理效果
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ThemeSettingsUiEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ThemeSettingsUiEffect.ThemeApplied -> {
                    // 主題已應用，可以觸發 haptic feedback
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("主題設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 主題模式選擇
            ThemeModeSection(
                currentMode = uiState.currentMode,
                onModeSelected = { viewModel.onEvent(ThemeSettingsUiEvent.SetThemeMode(it)) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 動態顏色 (Material You)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DynamicColorSection(
                    enabled = uiState.useDynamicColor,
                    onToggle = { viewModel.onEvent(ThemeSettingsUiEvent.SetDynamicColor(it)) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // AMOLED 純黑
            AmoledBlackSection(
                enabled = uiState.useAmoledBlack,
                onToggle = { viewModel.onEvent(ThemeSettingsUiEvent.SetAmoledBlack(it)) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 調色盤選擇
            if (!uiState.useDynamicColor) {
                PaletteSection(
                    currentPalette = uiState.currentPalette,
                    onPaletteSelected = { viewModel.onEvent(ThemeSettingsUiEvent.SetPalette(it)) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // 自定義主題
            CustomThemesSection(
                customThemes = uiState.customThemes,
                selectedId = uiState.selectedCustomThemeId,
                onSelect = { viewModel.onEvent(ThemeSettingsUiEvent.SelectCustomTheme(it)) },
                onEdit = { viewModel.onEvent(ThemeSettingsUiEvent.EditCustomTheme(it)) },
                onDelete = { viewModel.onEvent(ThemeSettingsUiEvent.DeleteCustomTheme(it)) },
                onCreateNew = { viewModel.onEvent(ThemeSettingsUiEvent.ShowCustomThemeEditor) }
            )
        }
    }
}

@Composable
private fun ThemeModeSection(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Text(
        text = "外觀模式",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeModeChip(
            label = "跟隨系統",
            icon = Icons.Rounded.Settings,
            selected = currentMode == ThemeMode.SYSTEM,
            onClick = { onModeSelected(ThemeMode.SYSTEM) },
            modifier = Modifier.weight(1f)
        )
        ThemeModeChip(
            label = "淺色",
            icon = Icons.Rounded.LightMode,
            selected = currentMode == ThemeMode.LIGHT,
            onClick = { onModeSelected(ThemeMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )
        ThemeModeChip(
            label = "深色",
            icon = Icons.Rounded.DarkMode,
            selected = currentMode == ThemeMode.DARK,
            onClick = { onModeSelected(ThemeMode.DARK) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThemeModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "bg"
    )
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DynamicColorSection(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Material You 動態顏色",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "根據桌布自動調整主題顏色",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun AmoledBlackSection(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AMOLED 純黑模式",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "使用純黑背景以節省 OLED 螢幕電量",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun PaletteSection(
    currentPalette: ThemePalette,
    onPaletteSelected: (ThemePalette) -> Unit
) {
    Text(
        text = "主題調色盤",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ThemePalette.entries) { palette ->
            PaletteItem(
                palette = palette,
                selected = palette == currentPalette,
                onClick = { onPaletteSelected(palette) }
            )
        }
    }
}

@Composable
private fun PaletteItem(
    palette: ThemePalette,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(palette.primaryColor.toInt())
    val accentColor = Color(palette.accentColor.toInt())
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(
                if (selected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp)
        ) {
            // 主色圓圈
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
                    .align(Alignment.TopStart)
            )
            // 強調色小圓圈
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = palette.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CustomThemesSection(
    customThemes: List<com.gemini.music.domain.model.CustomTheme>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onEdit: (com.gemini.music.domain.model.CustomTheme) -> Unit,
    onDelete: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "自定義主題",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        TextButton(onClick = onCreateNew) {
            Icon(Icons.Rounded.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("新增")
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    if (customThemes.isEmpty()) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "尚無自定義主題",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            customThemes.forEach { theme ->
                CustomThemeItem(
                    theme = theme,
                    selected = theme.id == selectedId,
                    onSelect = { onSelect(theme.id) },
                    onEdit = { onEdit(theme) },
                    onDelete = { onDelete(theme.id) }
                )
            }
        }
    }
}

@Composable
private fun CustomThemeItem(
    theme: com.gemini.music.domain.model.CustomTheme,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selected) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 顏色預覽
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(theme.primaryColor.toInt()))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


