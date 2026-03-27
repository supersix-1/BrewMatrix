package com.brewmatrix.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brewmatrix_preferences")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_RATIO_PRESET_ID = longPreferencesKey("default_ratio_preset_id")
        val DEFAULT_DOSE = doublePreferencesKey("default_dose")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "system"
    }

    val defaultRatioPresetId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_RATIO_PRESET_ID]
    }

    val defaultDose: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_DOSE] ?: 18.0
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }

    suspend fun setDefaultRatioPresetId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_RATIO_PRESET_ID] = id
        }
    }

    suspend fun setDefaultDose(dose: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_DOSE] = dose
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
