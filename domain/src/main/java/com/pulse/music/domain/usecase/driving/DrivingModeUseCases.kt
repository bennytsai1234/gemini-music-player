package com.pulse.music.domain.usecase.driving

import com.pulse.music.domain.model.DrivingModeSettings
import com.pulse.music.domain.model.DrivingModeState
import com.pulse.music.domain.repository.DrivingModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 觀察駕駛模式狀態
 */
class ObserveDrivingModeUseCase @Inject constructor(
    private val drivingModeRepository: DrivingModeRepository
) {
    fun observeState(): Flow<DrivingModeState> = drivingModeRepository.observeState()
    fun observeSettings(): Flow<DrivingModeSettings> = drivingModeRepository.observeSettings()
}

/**
 * 切換駕駛模式
 */
class ToggleDrivingModeUseCase @Inject constructor(
    private val drivingModeRepository: DrivingModeRepository
) {
    suspend fun activate() {
        drivingModeRepository.activateDrivingMode()
    }
    
    suspend fun deactivate() {
        drivingModeRepository.deactivateDrivingMode()
    }
}

/**
 * 更新駕駛模式設定
 */
class UpdateDrivingModeSettingsUseCase @Inject constructor(
    private val drivingModeRepository: DrivingModeRepository
) {
    suspend operator fun invoke(settings: DrivingModeSettings) {
        drivingModeRepository.updateSettings(settings)
    }
}

/**
 * 管理藍牙白名單
 */
class ManageBluetoothWhitelistUseCase @Inject constructor(
    private val drivingModeRepository: DrivingModeRepository
) {
    suspend fun addDevice(deviceName: String) {
        drivingModeRepository.addBluetoothDevice(deviceName)
    }
    
    suspend fun removeDevice(deviceName: String) {
        drivingModeRepository.removeBluetoothDevice(deviceName)
    }
    
    suspend fun isWhitelisted(deviceName: String): Boolean {
        return drivingModeRepository.isDeviceWhitelisted(deviceName)
    }
}

/**
 * 處理藍牙連接事件 (用於自動啟動駕駛模式)
 */
class HandleBluetoothConnectionUseCase @Inject constructor(
    private val drivingModeRepository: DrivingModeRepository
) {
    suspend operator fun invoke(deviceName: String?, isConnected: Boolean) {
        if (isConnected && deviceName != null) {
            drivingModeRepository.updateConnectedDevice(deviceName)
            
            // 檢查是否需要自動啟動駕駛模式
            if (drivingModeRepository.isDeviceWhitelisted(deviceName)) {
                drivingModeRepository.activateDrivingMode()
            }
        } else {
            drivingModeRepository.updateConnectedDevice(null)
            // 斷開連接時可選擇自動停用
        }
    }
}
