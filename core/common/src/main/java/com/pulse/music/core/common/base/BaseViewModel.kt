package com.pulse.music.core.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * MVI 架構的基礎 ViewModel。
 *
 * @param S UI 狀態類型 (State)
 * @param E UI 事件類型 (Event)
 * @param F UI 副作用類型 (Effect)
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S
) : ViewModel() {

    // UI 狀態 (Single Source of Truth)
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    // 接收 UI 事件的 Channel (可選，也可以直接呼叫 function)
    private val _uiEvent = Channel<E>()
    
    // 發送 UI 副作用的 Channel (Hot Stream)
    private val _uiEffect = Channel<F>()
    val uiEffect: Flow<F> = _uiEffect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /**
     * 更新 UI 狀態。
     * 這裡使用 update 確保線程安全與原子性。
     */
    protected fun setState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    /**
     * 讀取當前 UI 狀態。
     */
    protected val currentState: S
        get() = _uiState.value

    /**
     * 發送一次性副作用 (Effect)。
     */
    protected fun setEffect(builder: () -> F) {
        val effect = builder()
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    /**
     * 接收並處理 UI 事件。
     * 子類別必須實作此方法來回應使用者動作。
     */
    protected abstract fun handleEvent(event: E)

    /**
     * 公開方法供 UI 觸發事件。
     */
    fun onEvent(event: E) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _uiEvent.receiveAsFlow().collect {
                handleEvent(it)
            }
        }
    }
}
