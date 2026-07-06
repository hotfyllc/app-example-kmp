package com.pulse.app.viewmodel

import com.pulse.app.domain.model.Exercise
import com.pulse.app.domain.model.Goal

/** Time-of-day greeting bucket, resolved to a localized string in the UI layer. */
enum class Greeting { MORNING, AFTERNOON, EVENING }

/** Dynamic headline shown beside the Today progress ring. */
enum class Headline { READY, GOING, CRUSHED }

/** An exercise paired with its current completion state for rendering a checklist row. */
data class ExerciseUi(
    val exercise: Exercise,
    val done: Boolean,
)

/** One bar in the weekly chart. */
data class WeekDayUi(
    val label: String,
    val points: Int,
    val isToday: Boolean,
    /** Bar height as a fraction (0f..1f) of the tallest bar in the week. */
    val fraction: Float,
)

/**
 * Immutable snapshot driving the entire UI. Derived in [PulseViewModel] from the
 * persisted [com.pulse.app.domain.model.UserProgress] plus the current clock, so
 * composables stay stateless and simply render what they're given.
 */
data class PulseUiState(
    val loaded: Boolean = false,
    val onboarded: Boolean = false,
    val goal: Goal? = null,

    // Today screen
    val greeting: Greeting = Greeting.MORNING,
    val items: List<ExerciseUi> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progressFraction: Float = 0f,
    val pointsToday: Int = 0,
    val allDoneToday: Boolean = false,
    val headline: Headline = Headline.READY,

    // Progress screen
    val totalScore: Int = 0,
    val streak: Int = 0,
    val workouts: Int = 0,
    val weekDays: List<WeekDayUi> = emptyList(),

    val bonusPoints: Int = 0,
) {
    /** Percentage label for the progress ring, e.g. "60%". */
    val percentLabel: String get() = "${(progressFraction * 100).toInt()}%"
}
