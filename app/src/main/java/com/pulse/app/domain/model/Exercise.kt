package com.pulse.app.domain.model

/**
 * A single exercise in a goal's daily session.
 *
 * @param id     stable identifier used to track completion (e.g. "c1", "b3")
 * @param name   display name (e.g. "Barbell Bench Press")
 * @param meta   sets/reps or duration descriptor (e.g. "4 × 8 reps", "20 min")
 * @param points points awarded when the exercise is completed
 */
data class Exercise(
    val id: String,
    val name: String,
    val meta: String,
    val points: Int,
)
