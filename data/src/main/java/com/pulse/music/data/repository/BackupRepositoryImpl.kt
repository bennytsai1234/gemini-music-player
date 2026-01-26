package com.pulse.music.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pulse.music.data.database.FavoriteDao
import com.pulse.music.data.database.PlaybackHistoryDao
import com.pulse.music.data.database.PlaylistDao
import com.pulse.music.data.database.ScrobbleDao
import com.pulse.music.data.database.SongDao
import com.pulse.music.data.database.SongEntity
import com.pulse.music.data.source.GoogleDriveService
import com.pulse.music.domain.model.backup.*
import com.pulse.music.domain.repository.BackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: PlaybackHistoryDao,
    private val statsDao: ScrobbleDao,
    private val driveService: GoogleDriveService
) : BackupRepository {

    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
        encodeDefaults = true
    }

    private val _isSignedIn = MutableStateFlow(false)
    private var currentAccount: GoogleSignInAccount? = null

    init {
        checkSignInStatus()
    }

    private fun checkSignInStatus() {
        val account = driveService.getLastSignedInAccount()
        if (account != null) {
            currentAccount = account
            _isSignedIn.value = true
        }
    }

    override suspend fun createBackup(): PulseBackup = withContext(Dispatchers.IO) {
        // 1. Load all songs for metadata lookup
        val allSongs = songDao.getAllSongs().first().associateBy { it.id }

        // 2. Playlists
        val playlists = playlistDao.getAllPlaylists().first()
        val playlistBackups = playlists.map { playlistWithMeta ->
            val songs = playlistDao.getSongsForPlaylist(playlistWithMeta.playlist.playlistId).first()
            PlaylistBackup(
                name = playlistWithMeta.playlist.name,
                tracks = songs.mapNotNull { song ->
                    SongMetadataBackup(song.title, song.artist, song.album)
                }
            )
        }

        // 3. Favorites
        val favorites = favoriteDao.getAllFavorites().first()
        val favoriteBackups = favorites.mapNotNull { fav ->
            allSongs[fav.songId]?.let { song ->
                SongMetadataBackup(song.title, song.artist, song.album)
            }
        }

        // 4. History
        val history = historyDao.getAllHistory().first()
        val historyBackups = history.mapNotNull { record ->
            allSongs[record.songId]?.let { song ->
                HistoryBackup(
                    song = SongMetadataBackup(song.title, song.artist, song.album),
                    playedAt = record.playedAt
                )
            }
        }

        PulseBackup(
            timestamp = System.currentTimeMillis(),
            playlists = playlistBackups,
            favorites = favoriteBackups,
            playbackHistory = historyBackups,
            songStats = emptyList() // Derived from history
        )
    }

    override suspend fun restoreBackup(backup: PulseBackup, merge: Boolean): RestoreResult = withContext(Dispatchers.IO) {
        var playlistsRestored = 0
        var favoritesRestored = 0
        var historyRestored = 0
        var statsRestored = 0
        val errors = mutableListOf<String>()

        try {
            // Pre-load all local songs for matching (Metadata -> ID)
            // Use a flexible map key: "Title|Artist|Album"
            val localSongs = songDao.getAllSongs().first()
            val songMap = localSongs.associate { song ->
                Triple(song.title, song.artist, song.album) to song
            }

            // Helper to find song ID
            fun findSong(meta: SongMetadataBackup): SongEntity? {
                return songMap[Triple(meta.title, meta.artist, meta.album)]
            }

            // 1. Restore Playlists
            backup.playlists.forEach { plBackup ->
                try {
                    val newId = playlistDao.insertPlaylist(com.pulse.music.data.database.PlaylistEntity(name = plBackup.name))
                    plBackup.tracks.forEach { trackMeta ->
                        findSong(trackMeta)?.let { song ->
                            playlistDao.insertPlaylistSongCrossRef(
                                com.pulse.music.data.database.PlaylistSongCrossRef(
                                    playlistId = newId,
                                    songId = song.id,
                                    dateAdded = System.currentTimeMillis(),
                                    sortOrder = 0
                                )
                            )
                        }
                    }
                    playlistsRestored++
                } catch (e: Exception) {
                    errors.add("Failed to restore playlist ${plBackup.name}: ${e.message}")
                }
            }

            // 2. Restore Favorites
            backup.favorites.forEach { meta ->
                findSong(meta)?.let { song ->
                    try {
                        favoriteDao.addFavorite(
                            com.pulse.music.data.database.FavoriteEntity(
                                songId = song.id,
                                dateAdded = System.currentTimeMillis()
                            )
                        )
                        favoritesRestored++
                    } catch (e: Exception) { /* Ignore duplicates */ }
                }
            }

            // 3. Restore History (Optional: heavy)
            backup.playbackHistory.forEach { historyItem ->
                findSong(historyItem.song)?.let { song ->
                    try {
                        historyDao.insertRecord(
                            com.pulse.music.data.database.PlaybackHistoryEntity(
                                songId = song.id,
                                songTitle = song.title,
                                artistName = song.artist,
                                albumName = song.album,
                                albumArtUri = null, // Will be loaded dynamically
                                playedAt = historyItem.playedAt,
                                durationPlayed = 0, // Unknown
                                completed = true // Assume completed if in history
                            )
                        )
                        historyRestored++
                    } catch (e: Exception) { }
                }
            }

        } catch (e: Exception) {
            errors.add("Critical restore error: ${e.message}")
        }

        RestoreResult(
            playlistsRestored,
            favoritesRestored,
            historyRestored,
            statsRestored,
            errors
        )
    }

    override suspend fun exportBackupToStream(backup: PulseBackup, outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val jsonString = json.encodeToString(PulseBackup.serializer(), backup)
        outputStream.write(jsonString.toByteArray())
        outputStream.flush()
    }

    override suspend fun importBackupFromStream(inputStream: InputStream): PulseBackup = withContext(Dispatchers.IO) {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        json.decodeFromString(PulseBackup.serializer(), jsonString)
    }

    // --- Google Drive Implementation (Legacy Adapter) ---

    override suspend fun performCloudBackup(): BackupResult {
        val account = currentAccount ?: return BackupResult.Error("Not signed in")
        return try {
            val backup = createBackup()
            val jsonString = json.encodeToString(PulseBackup.serializer(), backup)
            driveService.uploadBackup(account, jsonString)
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun performCloudRestore(backupId: String): RestoreResult {
        val account = currentAccount ?: return RestoreResult(errors = listOf("Not signed in"))
        return try {
            val (jsonContent, error) = driveService.downloadBackup(account)
            if (jsonContent == null) return error ?: RestoreResult(errors = listOf("Download failed"))
            
            val backup = json.decodeFromString(PulseBackup.serializer(), jsonContent)
            restoreBackup(backup)
        } catch (e: Exception) {
            RestoreResult(errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun signIn(): Boolean {
        checkSignInStatus()
        return _isSignedIn.value
    }

    override suspend fun signOut() {
        driveService.getSignInClient().signOut().addOnCompleteListener {
            currentAccount = null
            _isSignedIn.value = false
        }
    }

    override fun isSignedIn(): Flow<Boolean> = _isSignedIn

    override suspend fun getLastBackupTime(): Long? = null
}
