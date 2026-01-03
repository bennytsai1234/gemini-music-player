package com.pulse.music.core.common.base

/**
 * 標記介面，代表 UI 的狀態 (State)。
 * 狀態應該是 Immutable 的資料類 (Data Class)。
 */
interface UiState

/**
 * 標記介面，代表 UI 的事件 (Event/Intent)。
 * 通常是使用者動作 (User Actions) 或系統事件。
 */
interface UiEvent

/**
 * 標記介面，代表 UI 的一次性副作用 (Side Effect)。
 * 例如：顯示 Toast、導航跳轉、播放音效等不需要持久化的狀態。
 */
interface UiEffect
