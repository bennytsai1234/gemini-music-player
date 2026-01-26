package com.pulse.music.domain.model

data class SmbResource(
    val path: String, // smb://host/share/path/file.mp3
    val name: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = 0
)
