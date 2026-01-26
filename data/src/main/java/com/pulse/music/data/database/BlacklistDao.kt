package com.pulse.music.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistDao {
    @Query("SELECT * FROM blacklist ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<BlacklistEntity>>

    @Query("SELECT * FROM blacklist")
    suspend fun getAllSync(): List<BlacklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BlacklistEntity)

    @Delete
    suspend fun delete(entity: BlacklistEntity)
    
    @Query("DELETE FROM blacklist WHERE path = :path")
    suspend fun deleteByPath(path: String)
}
