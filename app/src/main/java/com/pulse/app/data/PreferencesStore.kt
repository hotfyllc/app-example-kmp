package com.pulse.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pulse.app.domain.model.Goal
import com.pulse.app.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Single DataStore instance for the whole process, keyed off the Application context.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pulse_v1")

/**
 * DataStore (Preferences) persistence — the native replacement for the prototype's
 * localStorage. Reads emit a fully-decoded [UserProgress]; writes serialize it back.
 *
 * Complex fields are encoded as primitive-friendly forms: [completed] as a string set
 * of ids, [history] as a string set of "date|points" pairs.
 */
class PreferencesStore(private val context: Context) {

    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val GOAL = stringPreferencesKey("goal")
        val COMPLETED = stringSetPreferencesKey("completed")
        val TOTAL_SCORE = intPreferencesKey("totalScore")
        val STREAK = intPreferencesKey("streak")
        val WORKOUTS = intPreferencesKey("workouts")
        val DAY_COUNTED = booleanPreferencesKey("dayCounted")
        val HISTORY = stringSetPreferencesKey("history")
        val DATE = stringPreferencesKey("date")
    }

    /** Reactive stream of the persisted progress (raw — no daily-reset applied). */
    val progress: Flow<UserProgress> = context.dataStore.data.map { prefs ->
        UserProgress(
            onboarded = prefs[Keys.ONBOARDED] ?: false,
            goal = Goal.fromKey(prefs[Keys.GOAL]),
            completed = prefs[Keys.COMPLETED] ?: emptySet(),
            totalScore = prefs[Keys.TOTAL_SCORE] ?: 0,
            streak = prefs[Keys.STREAK] ?: 0,
            workouts = prefs[Keys.WORKOUTS] ?: 0,
            dayCounted = prefs[Keys.DAY_COUNTED] ?: false,
            history = decodeHistory(prefs[Keys.HISTORY]),
            date = prefs[Keys.DATE] ?: "",
        )
    }

    /** Persists the full [progress] snapshot. */
    suspend fun save(progress: UserProgress) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDED] = progress.onboarded
            if (progress.goal != null) prefs[Keys.GOAL] = progress.goal.key else prefs.remove(Keys.GOAL)
            prefs[Keys.COMPLETED] = progress.completed
            prefs[Keys.TOTAL_SCORE] = progress.totalScore
            prefs[Keys.STREAK] = progress.streak
            prefs[Keys.WORKOUTS] = progress.workouts
            prefs[Keys.DAY_COUNTED] = progress.dayCounted
            prefs[Keys.HISTORY] = encodeHistory(progress.history)
            prefs[Keys.DATE] = progress.date
        }
    }

    /** Wipes all persisted progress (used by "Reset all progress"). */
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private fun encodeHistory(history: Map<String, Int>): Set<String> =
        history.entries.map { "${it.key}|${it.value}" }.toSet()

    private fun decodeHistory(raw: Set<String>?): Map<String, Int> =
        raw.orEmpty().mapNotNull { entry ->
            val sep = entry.lastIndexOf('|')
            if (sep <= 0) return@mapNotNull null
            val date = entry.substring(0, sep)
            val points = entry.substring(sep + 1).toIntOrNull() ?: return@mapNotNull null
            date to points
        }.toMap()
}
