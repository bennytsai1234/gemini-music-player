package com.gemini.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gemini.music.domain.model.DrivingModeSettings
import com.gemini.music.domain.model.DrivingModeState
import com.gemini.music.domain.repository.DrivingModeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.drivingModeDataStore: DataStore<Preferences> by preferencesDataStore(name = "driving_mode_settings")

@Singleton
class DrivingModeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DrivingModeRepository {
    
    private val dataStore = context.drivingModeDataStore
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _drivingModeState = MutableStateFlow(DrivingModeState())
    
    private object PreferencesKeys {
        val IS_ENABLED = booleanPreferencesKey("driving_mode_enabled")
        val AUTO_ACTIVATE_BT = booleanPreferencesKey("auto_activate_bluetooth")
        val BT_DEVICE_NAMES = stringPreferencesKey("bluetooth_device_names")
        val USE_LARGE_CONTROLS = booleanPreferencesKey("use_large_controls")
        val ENABLE_VOICE_FEEDBACK = booleanPreferencesKey("enable_voice_feedback")
        val SIMPLIFIED_UI = booleanPreferencesKey("simplified_ui")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val NIGHT_MODE_AUTO = booleanPreferencesKey("night_mode_auto")
    }
    
    override fun observeSettings(): Flow<DrivingModeSettings> {
        return dataStore.data.map { preferences ->
            DrivingModeSettings(
                isEnabled = preferences[PreferencesKeys.IS_ENABLED] ?: false,
                autoActivateOnBluetooth = preferences[PreferencesKeys.AUTO_ACTIVATE_BT] ?: true,
                bluetoothDeviceNames = preferences[PreferencesKeys.BT_DEVICE_NAMES]?.let {
                    try { json.decodeFromString<List<String>>(it) } catch (e: Exception) { emptyList() }
                } ?: emptyList(),
                useLargeControls = preferences[PreferencesKeys.USE_LARGE_CONTROLS] ?: true,
                enableVoiceFeedback = preferences[PreferencesKeys.ENABLE_VOICE_FEEDBACK] ?: true,
                simplifiedUi = preferences[PreferencesKeys.SIMPLIFIED_UI] ?: true,
                hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
                nightModeAuto = preferences[PreferencesKeys.NIGHT_MODE_AUTO] ?: true
            )
        }
    }
    
    override fun observeState(): Flow<DrivingModeState> = _drivingModeState.asStateFlow()
    
    override suspend fun updateSettings(settings: DrivingModeSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ENABLED] = settings.isEnabled
            preferences[PreferencesKeys.AUTO_ACTIVATE_BT] = settings.autoActivateOnBluetooth
            preferences[PreferencesKeys.BT_DEVICE_NAMES] = json.encodeToString(settings.bluetoothDeviceNames)
            preferences[PreferencesKeys.USE_LARGE_CONTROLS] = settings.useLargeControls
            preferences[PreferencesKeys.ENABLE_VOICE_FEEDBACK] = settings.enableVoiceFeedback
            preferences[PreferencesKeys.SIMPLIFIED_UI] = settings.simplifiedUi
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = settings.hapticFeedback
            preferences[PreferencesKeys.NIGHT_MODE_AUTO] = settings.nightModeAuto
        }
    }
    
    override suspend fun activateDrivingMode() {
        _drivingModeState.update { it.copy(isActive = true) }
    }
    
    override suspend fun deactivateDrivingMode() {
        _drivingModeState.update { it.copy(isActive = false) }
    }
    
    override suspend fun addBluetoothDevice(deviceName: String) {
        val currentSettings = observeSettings().first()
        val updatedDevices = currentSettings.bluetoothDeviceNames + deviceName
        updateSettings(currentSettings.copy(bluetoothDeviceNames = updatedDevices.distinct()))
    }
    
    override suspend fun removeBluetoothDevice(deviceName: String) {
        val currentSettings = observeSettings().first()
        val updatedDevices = currentSettings.bluetoothDeviceNames - deviceName
        updateSettings(currentSettings.copy(bluetoothDeviceNames = updatedDevices))
    }
    
    override suspend fun isDeviceWhitelisted(deviceName: String): Boolean {
        val settings = observeSettings().first()
        return settings.bluetoothDeviceNames.contains(deviceName)
    }
    
    override suspend fun updateConnectedDevice(deviceName: String?) {
        _drivingModeState.update { it.copy(connectedBluetoothDevice = deviceName) }
    }
}
