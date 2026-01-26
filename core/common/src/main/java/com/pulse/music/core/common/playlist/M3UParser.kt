package com.pulse.music.core.common.playlist

import java.io.InputStream
import java.io.OutputStream

/**
 * Utility for parsing and writing M3U playlist files.
 */
object M3UParser {

    private const val EXT_M3U = "#EXTM3U"
    private const val EXT_INF = "#EXTINF"

    /**
     * Parses an M3U file from an InputStream.
     * Returns a list of file paths or URIs.
     */
    fun parse(inputStream: InputStream): List<String> {
        val paths = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    paths.add(trimmed)
                }
            }
        }
        return paths
    }

    /**
     * Writes a list of songs to an OutputStream in M3U format.
     * @param outputStream The stream to write to.
     * @param entries List of pairs containing (title, path/uri).
     */
    fun write(outputStream: OutputStream, entries: List<Pair<String, String>>) {
        outputStream.bufferedWriter().use { writer ->
            writer.write(EXT_M3U)
            writer.newLine()
            entries.forEach { (title, path) ->
                writer.write("$EXT_INF:-1,$title")
                writer.newLine()
                writer.write(path)
                writer.newLine()
            }
            writer.flush()
        }
    }
}
