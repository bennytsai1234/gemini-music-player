package com.gemini.music.domain.model

import kotlinx.serialization.Serializable

/**
 * 駕駛模式配置
 */
@Serializable
data class DrivingModeSettings(
    val isEnabled: Boolean = false,
    val autoActivateOnBluetooth: Boolean = true,     // 連接車載藍牙時自動啟動
    val bluetoothDeviceNames: List<String> = emptyList(), // 白名單設備
    val useLargeControls: Boolean = true,            // 使用大按鈕
    val enableVoiceFeedback: Boolean = true,         // 語音反饋 (TTS)
    val simplifiedUi: Boolean = true,                // 簡化 UI (隱藏複雜功能)
    val showSpeedometer: Boolean = false,            // 顯示車速 (需要額外權限)
    val hapticFeedback: Boolean = true,              // 觸覺反饋
    val nightModeAuto: Boolean = true                // 自動夜間模式
)

/**
 * 駕駛模式狀態
 */
data class DrivingModeState(
    val isActive: Boolean = false,
    val connectedBluetoothDevice: String? = null,
    val isNightMode: Boolean = false,
    val currentSpeed: Float? = null  // km/h, 若啟用
)

/**
 * 駕駛模式快捷操作
 */
enum class DrivingAction {
    PLAY_PAUSE,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    VOICE_SEARCH,
    TOGGLE_SHUFFLE,
    TOGGLE_REPEAT,
    VOLUME_UP,
    VOLUME_DOWN,
    SHOW_QUEUE,
    EXIT_DRIVING_MODE
}
