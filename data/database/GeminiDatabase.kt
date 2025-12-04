package com.gemini.music.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SongEntity::class], version = 1, exportSchema = false)
abstract class GeminiDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
