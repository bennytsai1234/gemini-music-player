package com.pulse.music.data.repository

import com.pulse.music.data.database.BlacklistDao
import com.pulse.music.data.database.BlacklistEntity
import com.pulse.music.domain.model.BlacklistItem
import com.pulse.music.domain.repository.BlacklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlacklistRepositoryImpl @Inject constructor(
    private val blacklistDao: BlacklistDao
) : BlacklistRepository {

    override fun getBlacklist(): Flow<List<BlacklistItem>> {
        return blacklistDao.getAll().map { entities ->
            entities.map { entity ->
                BlacklistItem(
                    path = entity.path,
                    isFolder = entity.isFolder,
                    dateAdded = entity.dateAdded
                )
            }
        }
    }

    override suspend fun addBlacklistItem(path: String, isFolder: Boolean) {
        blacklistDao.insert(
            BlacklistEntity(
                path = path,
                isFolder = isFolder,
                dateAdded = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeBlacklistItem(path: String) {
        blacklistDao.deleteByPath(path)
    }
}
