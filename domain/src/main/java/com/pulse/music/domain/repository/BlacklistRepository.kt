package com.pulse.music.domain.repository

import com.pulse.music.domain.model.BlacklistItem
import kotlinx.coroutines.flow.Flow

interface BlacklistRepository {
    fun getBlacklist(): Flow<List<BlacklistItem>>
    suspend fun addBlacklistItem(path: String, isFolder: Boolean)
    suspend fun removeBlacklistItem(path: String)
}
