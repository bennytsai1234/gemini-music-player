package com.pulse.music.data.repository

import com.pulse.music.data.database.PlaylistDao
import com.pulse.music.data.database.SongDao
import com.pulse.music.data.database.asDomainModel
import com.pulse.music.data.database.asEntity
import com.pulse.music.data.source.LocalAudioSource
import com.pulse.music.data.source.TagEditorSource
import com.pulse.music.domain.model.Album
import com.pulse.music.domain.model.Artist
import com.pulse.music.domain.model.Playlist
import com.pulse.music.domain.model.ScanStatus
import com.pulse.music.domain.model.Song
import com.pulse.music.domain.model.SongTags
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pulse.music.data.source.SongPagingSource
import com.pulse.music.domain.repository.MusicRepository
import com.pulse.music.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.pulse.music.domain.model.Genre

class MusicRepositoryImpl @Inject constructor(
    private val localAudioSource: LocalAudioSource,
    private val tagEditorSource: TagEditorSource,
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val favoriteDao: com.pulse.music.data.database.FavoriteDao,
    private val blacklistDao: com.pulse.music.data.database.BlacklistDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val contentResolver: android.content.ContentResolver
) : MusicRepository {

    override fun getSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override fun getGenres(): Flow<List<Genre>> {
        return getSongs().map { songs ->
            songs.asSequence()
                .mapNotNull { it.genre }
                .groupingBy { it }
                .eachCount()
                .map { (name, count) -> Genre(name, count) }
                .sortedBy { it.name }
        }
    }

    override fun getPagedSongs(sortOrder: String): Flow<PagingData<Song>> {
        val order = when (sortOrder) {
            "DATE_ADDED" -> com.pulse.music.data.source.SongSortOrder.DATE_ADDED
            "ARTIST" -> com.pulse.music.data.source.SongSortOrder.ARTIST
            "ALBUM" -> com.pulse.music.data.source.SongSortOrder.ALBUM
            else -> com.pulse.music.data.source.SongSortOrder.TITLE
        }
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SongPagingSource(songDao, sortOrder = order) }
        ).flow
    }

    override fun getSong(id: Long): Flow<Song?> {
        return songDao.getSong(id).map { it?.asDomainModel() }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query).map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override fun getRecentlyAdded(): Flow<List<Song>> {
        return songDao.getRecentlyAdded().map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return getSongs().map { songs ->
            songs.groupBy { it.albumId }
                .map { (albumId, albumSongs) ->
                    val firstSong = albumSongs.first()
                    Album(
                        id = albumId,
                        title = firstSong.album,
                        artist = firstSong.artist,
                        songCount = albumSongs.size
                    )
                }
                .sortedBy { it.title }
        }
    }

    override fun getArtists(): Flow<List<Artist>> {
        return getSongs().map { songs ->
            songs.groupBy { it.artist }
                .map { (artistName, artistSongs) ->
                    Artist(
                        name = artistName,
                        songCount = artistSongs.size
                    )
                }
                .sortedBy { it.name }
        }
    }

    override fun getSongsByAlbumId(albumId: Long): Flow<List<Song>> {
        return songDao.getSongsByAlbumId(albumId).map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override fun scanLocalMusic(): Flow<ScanStatus> = flow {
        emit(ScanStatus.Scanning(0, 0, "Initializing scan..."))
        try {
            val minDuration = userPreferencesRepository.minAudioDuration.first()
            val includedFolders = userPreferencesRepository.includedFolders.first()

            val songsFromSystem = localAudioSource.loadMusic(minDuration, includedFolders)

            val blacklist = blacklistDao.getAllSync()
            val blacklistedPaths = blacklist.map { it.path }.toSet()
            val blacklistedFolders = blacklist.filter { it.isFolder }.map { it.path }

            val filteredSongs = songsFromSystem.filter { song ->
                if (blacklistedPaths.contains(song.dataPath)) return@filter false
                val isInsideBlacklistedFolder = blacklistedFolders.any { folderPath ->
                    val folderWithSeparator = if (folderPath.endsWith("/")) folderPath else "$folderPath/"
                    song.dataPath.startsWith(folderWithSeparator)
                }
                !isInsideBlacklistedFolder
            }

            val total = filteredSongs.size
            emit(ScanStatus.Scanning(total, total, "Processing $total songs..."))
            songDao.updateMusicLibrary(filteredSongs.map { it.asEntity() })
            emit(ScanStatus.Completed(total))
        } catch (e: Exception) {
            emit(ScanStatus.Failed(e.message ?: "Unknown scanning error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map {
                Playlist(
                    id = it.playlist.playlistId,
                    name = it.playlist.name,
                    songCount = it.songCount,
                    coverArtUri = it.coverAlbumId?.let { albumId -> "content://media/external/audio/albumart/$albumId" }
                )
            }
        }
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylist(playlistId).map { it ->
            it?.let {
                Playlist(
                    id = it.playlist.playlistId,
                    name = it.playlist.name,
                    songCount = it.songCount,
                    coverArtUri = it.coverAlbumId?.let { albumId -> "content://media/external/audio/albumart/$albumId" }
                )
            }
        }
    }

    override fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsForPlaylist(playlistId).map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(
            com.pulse.music.data.database.PlaylistEntity(name = name)
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val nextOrder = playlistDao.getNextSortOrder(playlistId)
        playlistDao.insertPlaylistSongCrossRef(
            com.pulse.music.data.database.PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                sortOrder = nextOrder
            )
        )
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        var nextOrder = playlistDao.getNextSortOrder(playlistId)
        val crossRefs = songIds.map { songId ->
            com.pulse.music.data.database.PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                sortOrder = nextOrder++
            )
        }
        crossRefs.forEach { playlistDao.insertPlaylistSongCrossRef(it) }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun moveSongInPlaylist(playlistId: Long, fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val sortOrders = playlistDao.getSongsSortOrderSync(playlistId).toMutableList()
        if (fromIndex !in sortOrders.indices || toIndex !in sortOrders.indices) return
        val item = sortOrders.removeAt(fromIndex)
        sortOrders.add(toIndex, item)
        sortOrders.forEachIndexed { index, songSortOrder ->
            playlistDao.updateSongPosition(playlistId, songSortOrder.songId, index)
        }
    }

    override suspend fun renamePlaylist(playlistId: Long, name: String) {
        playlistDao.updatePlaylistName(playlistId, name)
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return favoriteDao.getFavoriteSongs().map { entities ->
            entities.map { it.asDomainModel() }
        }
    }

    override fun isSongFavorite(songId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(songId)
    }

    override suspend fun toggleFavorite(songId: Long) {
        val isFav = favoriteDao.isFavorite(songId).first()
        if (isFav) {
            favoriteDao.removeFavorite(songId)
        } else {
            favoriteDao.addFavorite(com.pulse.music.data.database.FavoriteEntity(songId))
        }
    }

    override suspend fun deleteSong(song: Song) {
        localAudioSource.deleteSong(song)
        songDao.deleteSong(id = song.id)
    }

    override suspend fun deleteSongs(songs: List<Song>): Any? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
             return localAudioSource.createDeleteRequest(songs)
        }
        return null
    }

    override suspend fun getSongTags(songId: Long): SongTags? = withContext(Dispatchers.IO) {
        val song = songDao.getSongSync(songId)
        song?.let { tagEditorSource.readTags(it.dataPath, songId) }
    }

    override suspend fun updateSongTags(tags: SongTags): Boolean = withContext(Dispatchers.IO) {
        val success = tagEditorSource.writeTags(tags)
        if (success) {
            val existingSong = songDao.getSongSync(tags.songId)
            existingSong?.let {
                val updatedEntity = it.copy(
                    title = tags.title,
                    artist = tags.artist,
                    album = tags.album,
                    year = tags.year.toIntOrNull() ?: 0,
                    trackNumber = tags.trackNumber.toIntOrNull() ?: 0
                )
                songDao.insertSong(updatedEntity)
            }
        }
        success
    }

    override suspend fun importPlaylist(uri: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val inputStream = contentResolver.openInputStream(android.net.Uri.parse(uri)) ?: return@withContext Result.failure(Exception("Failed to open input stream"))
            val entries = com.pulse.music.core.common.util.M3UParser.parse(inputStream)
            if (entries.isEmpty()) return@withContext Result.failure(Exception("Playlist is empty"))
            val playlistName = android.net.Uri.parse(uri).lastPathSegment?.substringBeforeLast(".") ?: "Imported Playlist"
            val playlistId = createPlaylist(playlistName)
            val songIds = entries.mapNotNull { entry -> 
                entry.uri?.let { path -> songDao.getSongByPath(path)?.id } 
            }
            if (songIds.isNotEmpty()) {
                addSongsToPlaylist(playlistId, songIds)
            }
            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportPlaylist(playlistId: Long, uri: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val songs = getSongsForPlaylist(playlistId).first()
            val outputStream = contentResolver.openOutputStream(android.net.Uri.parse(uri)) ?: return@withContext Result.failure(Exception("Failed to open output stream"))
            val entries = songs.map { 
                com.pulse.music.core.common.util.M3UParser.M3UEntry(
                    uri = it.dataPath,
                    duration = (it.duration / 1000).toInt(),
                    title = it.title,
                    artist = it.artist
                )
            }
            com.pulse.music.core.common.util.M3UParser.write(outputStream, entries)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
