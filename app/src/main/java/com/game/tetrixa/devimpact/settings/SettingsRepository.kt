package com.game.tetrixa.devimpact.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "premium_game_settings")

class SettingsRepository(private val context: Context) {
    private val hapticFeedbackKey = booleanPreferencesKey("haptic_feedback_enabled")

    val hapticFeedbackEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[hapticFeedbackKey] ?: true
    }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[hapticFeedbackKey] = enabled
        }
    }
}
