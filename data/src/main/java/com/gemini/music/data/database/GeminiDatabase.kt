package com.gemini.music.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistSongCrossRef::class, FavoriteEntity::class, LyricsEntity::class, SearchHistoryEntity::class], 
    version = 6, 
    exportSchema = false
)
abstract class GeminiDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
