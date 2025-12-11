package com.gemini.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gemini.music.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val dataStore = context.dataStore

    override val minAudioDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[MIN_AUDIO_DURATION] ?: 10000L
    }

    override val includedFolders: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[INCLUDED_FOLDERS] ?: emptySet()
    }
    
    override val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: UserPreferencesRepository.THEME_SYSTEM
    }

    override val useInternalEqualizer: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[USE_INTERNAL_EQUALIZER] ?: false
    }

    override suspend fun setMinAudioDuration(durationMs: Long) {
        dataStore.edit { preferences ->
            preferences[MIN_AUDIO_DURATION] = durationMs
        }
    }

    override suspend fun setIncludedFolders(folders: Set<String>) {
        dataStore.edit { preferences ->
            preferences[INCLUDED_FOLDERS] = folders
        }
    }
    
    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    override suspend fun setUseInternalEqualizer(useInternal: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_INTERNAL_EQUALIZER] = useInternal
        }
    }

    companion object {
        private val MIN_AUDIO_DURATION = longPreferencesKey("min_audio_duration")
        private val INCLUDED_FOLDERS = stringSetPreferencesKey("included_folders")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val USE_INTERNAL_EQUALIZER = androidx.datastore.preferences.core.booleanPreferencesKey("use_internal_equalizer")
    }
}
