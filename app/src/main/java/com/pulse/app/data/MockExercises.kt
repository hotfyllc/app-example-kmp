package com.pulse.app.data

import com.pulse.app.domain.model.Exercise
import com.pulse.app.domain.model.Goal

/**
 * In-code mock data source. Each goal maps to its exact 6-exercise session,
 * copied verbatim (names, metadata, point values) from the HTML prototype.
 */
object MockExercises {

    private val cut = listOf(
        Exercise("c1", "Jump Rope", "3 × 60 sec", 15),
        Exercise("c2", "Burpees", "4 × 12 reps", 20),
        Exercise("c3", "Mountain Climbers", "3 × 40 reps", 15),
        Exercise("c4", "Kettlebell Swings", "4 × 15 reps", 20),
        Exercise("c5", "Incline Run", "20 min", 25),
        Exercise("c6", "Plank Hold", "3 × 45 sec", 10),
    )

    private val build = listOf(
        Exercise("b1", "Barbell Bench Press", "4 × 8 reps", 25),
        Exercise("b2", "Deadlift", "4 × 6 reps", 30),
        Exercise("b3", "Pull-Ups", "4 × 10 reps", 20),
        Exercise("b4", "Back Squat", "4 × 8 reps", 25),
        Exercise("b5", "Overhead Press", "3 × 10 reps", 15),
        Exercise("b6", "Barbell Row", "4 × 10 reps", 20),
    )

    private val maintain = listOf(
        Exercise("m1", "Brisk Walk", "25 min", 15),
        Exercise("m2", "Bodyweight Squats", "3 × 15 reps", 10),
        Exercise("m3", "Push-Ups", "3 × 12 reps", 10),
        Exercise("m4", "Yoga Flow", "15 min", 15),
        Exercise("m5", "Glute Bridge", "3 × 15 reps", 10),
        Exercise("m6", "Mobility & Stretch", "10 min", 10),
    )

    private val byGoal: Map<Goal, List<Exercise>> = mapOf(
        Goal.CUT to cut,
        Goal.BUILD to build,
        Goal.MAINTAIN to maintain,
    )

    /** Returns the 6 exercises for [goal], or an empty list when no goal is set. */
    fun forGoal(goal: Goal?): List<Exercise> = goal?.let { byGoal[it] } ?: emptyList()
}
