package com.pulse.app.domain.model

/**
 * The full persisted user state — the Kotlin equivalent of the prototype's
 * localStorage payload. Everything here survives process death via DataStore.
 *
 * @param onboarded   whether onboarding has been completed
 * @param goal        the chosen training goal, or null if not yet picked
 * @param completed   ids of exercises completed *today*
 * @param totalScore  lifetime accumulated points
 * @param streak      consecutive-day streak
 * @param workouts    total completed workouts
 * @param dayCounted  whether today's completion bonus/streak has been counted
 * @param history     points earned per calendar day (yyyy-MM-dd -> points)
 * @param date        the calendar day this state belongs to (yyyy-MM-dd)
 */
data class UserProgress(
    val onboarded: Boolean = false,
    val goal: Goal? = null,
    val completed: Set<String> = emptySet(),
    val totalScore: Int = 0,
    val streak: Int = 0,
    val workouts: Int = 0,
    val dayCounted: Boolean = false,
    val history: Map<String, Int> = emptyMap(),
    val date: String = "",
)
