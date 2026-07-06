package com.pulse.app.domain.model

/**
 * The three training goals. [key] is the stable identifier persisted to DataStore
 * (matching the prototype's 'cut' / 'build' / 'maintain' ids); [label] is the
 * user-facing name shown throughout the UI.
 */
enum class Goal(val key: String, val label: String) {
    CUT("cut", "Shred"),
    BUILD("build", "Build Muscle"),
    MAINTAIN("maintain", "Stay Active");

    companion object {
        /** Resolves a persisted key back to a [Goal], or null if unknown/absent. */
        fun fromKey(key: String?): Goal? = entries.firstOrNull { it.key == key }
    }
}
