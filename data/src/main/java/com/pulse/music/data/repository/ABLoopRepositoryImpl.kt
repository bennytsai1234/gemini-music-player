package com.pulse.music.data.repository

import com.pulse.music.domain.model.ABLoopState
import com.pulse.music.domain.repository.ABLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ABLoopRepositoryImpl @Inject constructor() : ABLoopRepository {
    private val _state = MutableStateFlow(ABLoopState())
    override val state: StateFlow<ABLoopState> = _state.asStateFlow()

    override fun setA(position: Long) {
        val current = _state.value
        // If B is set and A > B, reset B? Or just clamp? 
        // Logic: A should be < B.
        // For now just set.
        _state.value = current.copy(enabled = true, aPoint = position)
    }

    override fun setB(position: Long) {
        val current = _state.value
        _state.value = current.copy(bPoint = position)
    }

    override fun clear() {
        _state.value = ABLoopState()
    }
}
