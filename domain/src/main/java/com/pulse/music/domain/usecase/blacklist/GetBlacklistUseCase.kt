package com.pulse.music.domain.usecase.blacklist

import com.pulse.music.domain.model.BlacklistItem
import com.pulse.music.domain.repository.BlacklistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlacklistUseCase @Inject constructor(
    private val repository: BlacklistRepository
) {
    operator fun invoke(): Flow<List<BlacklistItem>> {
        return repository.getBlacklist()
    }
}
