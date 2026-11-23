package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.creatorDeskDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "creator_desk_preferences"
)

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val weeklyGoal: Int = 3
)

class ThemePreferencesRepository(
    private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val WEEKLY_GOAL = intPreferencesKey("weekly_goal")
    }

    val preferencesFlow: Flow<UserPreferences> = context.creatorDeskDataStore.data.map { prefs ->
        val mode = prefs[Keys.THEME_MODE]?.let { raw ->
            ThemeMode.entries.firstOrNull { it.name == raw }
        } ?: ThemeMode.SYSTEM
        val goal = (prefs[Keys.WEEKLY_GOAL] ?: 3).coerceIn(1, 14)
        UserPreferences(themeMode = mode, weeklyGoal = goal)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.creatorDeskDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setWeeklyGoal(goal: Int) {
        context.creatorDeskDataStore.edit { prefs ->
            prefs[Keys.WEEKLY_GOAL] = goal.coerceIn(1, 14)
        }
    }
}
