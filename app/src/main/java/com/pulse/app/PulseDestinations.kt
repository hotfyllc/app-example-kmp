package com.pulse.app

/** Navigation route constants for the single-activity NavHost. */
object Routes {
    const val ONBOARDING = "onboarding"
    const val GOALS = "goals"
    const val TODAY = "today"
    const val PROGRESS = "progress"
}

/** The three bottom-navigation tabs. */
enum class PulseTab { TODAY, PROGRESS, GOAL }
