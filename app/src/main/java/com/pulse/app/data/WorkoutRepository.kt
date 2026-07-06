package com.pulse.app.data

import com.pulse.app.domain.model.Exercise
import com.pulse.app.domain.model.Goal
import com.pulse.app.domain.model.UserProgress
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Single access point for workout data and persistence. Wraps the mock data source
 * and the DataStore-backed [PreferencesStore], and owns the daily-reset rule.
 */
class WorkoutRepository(private val store: PreferencesStore) {

    /** Today's calendar day as an ISO string (yyyy-MM-dd). */
    fun today(): String = LocalDate.now().toString()

    /** The 6 exercises backing [goal]'s session (empty when no goal is set). */
    fun exercisesFor(goal: Goal?): List<Exercise> = MockExercises.forGoal(goal)

    /**
     * Loads persisted progress and applies the daily reset: if the stored day differs
     * from today, today's completions and the day-counted flag are cleared while the
     * lifetime totals (score, streak, workouts, history) are preserved.
     */
    suspend fun loadProgress(): UserProgress {
        val stored = store.progress.first()
        val today = today()
        return if (stored.date.isNotEmpty() && stored.date != today) {
            stored.copy(completed = emptySet(), dayCounted = false, date = today)
        } else {
            stored.copy(date = today)
        }
    }

    /** Persists a full progress snapshot. */
    suspend fun save(progress: UserProgress) = store.save(progress)

    /** Clears all persisted progress. */
    suspend fun clear() = store.clear()
}
