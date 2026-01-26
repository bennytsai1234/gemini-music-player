package com.pulse.music.domain.usecase

import com.pulse.music.domain.repository.ABLoopRepository
import javax.inject.Inject

class SetALoopPointUseCase @Inject constructor(private val repository: ABLoopRepository) {
    operator fun invoke(position: Long) = repository.setA(position)
}

class SetBLoopPointUseCase @Inject constructor(private val repository: ABLoopRepository) {
    operator fun invoke(position: Long) = repository.setB(position)
}

class ClearABLoopUseCase @Inject constructor(private val repository: ABLoopRepository) {
    operator fun invoke() = repository.clear()
}

class GetABLoopStateUseCase @Inject constructor(private val repository: ABLoopRepository) {
    operator fun invoke() = repository.state
}
