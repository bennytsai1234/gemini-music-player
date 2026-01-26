package com.pulse.music.core.common.util

import android.net.Uri
import java.io.InputStream
import java.io.OutputStream

object M3UParser {

    data class M3UEntry(
        val uri: String?, // Or absolute path
        val duration: Int = -1,
        val title: String = "",
        val artist: String = ""
    )

    fun parse(inputStream: InputStream): List<M3UEntry> {
        val entries = mutableListOf<M3UEntry>()
        inputStream.bufferedReader().use { reader ->
            var currentDuration = -1
            var currentTitle = ""
            var currentArtist = ""

            reader.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("#EXTINF:")) {
                    // Parse metadata: #EXTINF:123,Artist - Title
                    val parts = trimmed.substringAfter("#EXTINF:").split(",", limit = 2)
                    if (parts.isNotEmpty()) {
                        currentDuration = parts[0].toIntOrNull() ?: -1
                        if (parts.size > 1) {
                            val info = parts[1]
                            val dashIndex = info.indexOf(" - ")
                            if (dashIndex != -1) {
                                currentArtist = info.substring(0, dashIndex).trim()
                                currentTitle = info.substring(dashIndex + 3).trim()
                            } else {
                                currentTitle = info.trim()
                            }
                        }
                    }
                } else if (!trimmed.startsWith("#") && trimmed.isNotEmpty()) {
                    // This is a file path
                    entries.add(
                        M3UEntry(
                            uri = trimmed,
                            duration = currentDuration,
                            title = currentTitle,
                            artist = currentArtist
                        )
                    )
                    // Reset for next entry
                    currentDuration = -1
                    currentTitle = ""
                    currentArtist = ""
                }
            }
        }
        return entries
    }

    fun write(outputStream: OutputStream, entries: List<M3UEntry>) {
        outputStream.bufferedWriter().use { writer ->
            writer.write("#EXTM3U\n")
            entries.forEach { entry ->
                // Write Metadata
                val info = if (entry.artist.isNotEmpty()) "${entry.artist} - ${entry.title}" else entry.title
                writer.write("#EXTINF:${entry.duration},$info\n")
                // Write Path/URI
                writer.write("${entry.uri}\n")
            }
        }
    }
}
