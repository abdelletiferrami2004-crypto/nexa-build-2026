package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nexa_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        private val KEY_SCREENSHOT_PROTECTION_ENABLED = booleanPreferencesKey("screenshot_protection_enabled")
        private val KEY_PROFILE_LOCKED = booleanPreferencesKey("profile_locked")
        private val KEY_SOUNDS_ENABLED = booleanPreferencesKey("sounds_enabled")
        private val KEY_REACTIONS_COUNT_VISIBLE = booleanPreferencesKey("reactions_count_visible")
        private val KEY_AUTO_BLOCK_ENABLED = booleanPreferencesKey("auto_block_enabled")

        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_NOTIFICATIONS_ENABLED] ?: true }

    val darkModeEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_DARK_MODE_ENABLED] ?: true }

    val screenshotProtectionEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_SCREENSHOT_PROTECTION_ENABLED] ?: false }

    val profileLocked: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_PROFILE_LOCKED] ?: false }

    val soundsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_SOUNDS_ENABLED] ?: true }

    val reactionsCountVisible: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_REACTIONS_COUNT_VISIBLE] ?: true }

    val autoBlockEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[KEY_AUTO_BLOCK_ENABLED] ?: true }

    @Volatile
    var areNotificationsEnabledCached: Boolean = true
        private set

    @Volatile
    var areSoundsEnabledCached: Boolean = true
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationsEnabled.collect { areNotificationsEnabledCached = it }
            } catch (_: Throwable) {}
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                soundsEnabled.collect { areSoundsEnabledCached = it }
            } catch (_: Throwable) {}
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setScreenshotProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SCREENSHOT_PROTECTION_ENABLED] = enabled
        }
    }

    suspend fun setProfileLocked(locked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROFILE_LOCKED] = locked
        }
    }

    suspend fun setSoundsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SOUNDS_ENABLED] = enabled
        }
    }

    suspend fun setReactionsCountVisible(visible: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_REACTIONS_COUNT_VISIBLE] = visible
        }
    }

    suspend fun setAutoBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_BLOCK_ENABLED] = enabled
        }
    }
}
