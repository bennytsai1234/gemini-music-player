package com.gemini.music.domain.repository

import com.gemini.music.domain.model.DrivingModeSettings
import com.gemini.music.domain.model.DrivingModeState
import kotlinx.coroutines.flow.Flow

/**
 * 駕駛模式 Repository
 */
interface DrivingModeRepository {
    
    /**
     * 觀察駕駛模式設定
     */
    fun observeSettings(): Flow<DrivingModeSettings>
    
    /**
     * 觀察駕駛模式狀態
     */
    fun observeState(): Flow<DrivingModeState>
    
    /**
     * 更新駕駛模式設定
     */
    suspend fun updateSettings(settings: DrivingModeSettings)
    
    /**
     * 啟動駕駛模式
     */
    suspend fun activateDrivingMode()
    
    /**
     * 停用駕駛模式
     */
    suspend fun deactivateDrivingMode()
    
    /**
     * 新增藍牙白名單設備
     */
    suspend fun addBluetoothDevice(deviceName: String)
    
    /**
     * 移除藍牙白名單設備
     */
    suspend fun removeBluetoothDevice(deviceName: String)
    
    /**
     * 檢查設備是否在白名單中
     */
    suspend fun isDeviceWhitelisted(deviceName: String): Boolean
    
    /**
     * 更新連接的藍牙設備
     */
    suspend fun updateConnectedDevice(deviceName: String?)
}
