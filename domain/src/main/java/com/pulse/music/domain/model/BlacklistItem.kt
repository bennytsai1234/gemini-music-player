package com.pulse.music.domain.model

data class BlacklistItem(
    val path: String,
    val isFolder: Boolean,
    val dateAdded: Long
)
