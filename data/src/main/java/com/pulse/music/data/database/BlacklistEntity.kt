package com.pulse.music.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlacklistEntity(
    @PrimaryKey val path: String, // Absolute path (file or folder)
    val isFolder: Boolean,
    val dateAdded: Long = System.currentTimeMillis()
)
