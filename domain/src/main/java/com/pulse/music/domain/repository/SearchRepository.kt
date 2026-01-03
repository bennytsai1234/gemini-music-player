package com.pulse.music.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getRecentSearches(): Flow<List<String>>
    suspend fun addSearch(query: String)
    suspend fun removeSearch(query: String)
    suspend fun clearHistory()
}
