package com.pulse.music.data.repository

import com.pulse.music.data.database.SearchHistoryDao
import com.pulse.music.data.database.SearchHistoryEntity
import com.pulse.music.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao
) : SearchRepository {
    override fun getRecentSearches(): Flow<List<String>> {
        return dao.getRecentSearches().map { list -> list.map { it.query } }
    }

    override suspend fun addSearch(query: String) {
        dao.insertSearch(SearchHistoryEntity(query, System.currentTimeMillis()))
    }

    override suspend fun removeSearch(query: String) {
        dao.deleteSearch(query)
    }

    override suspend fun clearHistory() {
        dao.clearHistory()
    }
}
