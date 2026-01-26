package com.pulse.music.domain.usecase.blacklist

import com.pulse.music.domain.repository.BlacklistRepository
import javax.inject.Inject

class RemoveBlacklistItemUseCase @Inject constructor(
    private val repository: BlacklistRepository
) {
    suspend operator fun invoke(path: String) {
        repository.removeBlacklistItem(path)
    }
}
