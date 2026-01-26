package com.pulse.music.domain.repository

import com.pulse.music.domain.model.ABLoopState
import kotlinx.coroutines.flow.StateFlow

interface ABLoopRepository {
    val state: StateFlow<ABLoopState>
    fun setA(position: Long)
    fun setB(position: Long)
    fun clear()
}
