package com.pulse.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hotfy.sdk.Hotfy
import com.pulse.app.data.PreferencesStore
import com.pulse.app.data.WorkoutRepository
import com.pulse.app.domain.model.Goal
import com.pulse.app.domain.model.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Owns all Pulse business logic and exposes a single [uiState] for the UI to render.
 * Ports the prototype's Component class: goal selection, exercise toggling, the
 * once-per-day completion bonus/streak, and weekly history — persisted via DataStore.
 */
class PulseViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val progress = MutableStateFlow(UserProgress())
    private val loaded = MutableStateFlow(false)

    /** Derived, immutable UI snapshot recomputed whenever the persisted state changes. */
    val uiState: StateFlow<PulseUiState> =
        combine(progress, loaded) { state, isLoaded -> buildUiState(state, isLoaded) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PulseUiState())

    init {
        viewModelScope.launch {
            // Load persisted state with the daily reset applied, then persist the reset
            // so subsequent reads are consistent.
            val restored = repository.loadProgress()
            progress.value = restored
            loaded.value = true
            repository.save(restored)
        }
    }

    // region — Onboarding

    /** Marks onboarding complete and persists the choice. */
    fun finishOnboarding() {
        Hotfy.track("onboarding_concluido")
        commit(progress.value.copy(onboarded = true))
    }

    // endregion

    // region — Goals

    /** Selects [goal] as the active training goal and persists it. */
    fun chooseGoal(goal: Goal) {
        Hotfy.track("objetivo_escolhido", mapOf("goal" to goal.name))
        commit(progress.value.copy(goal = goal))
    }

    // endregion

    // region — Today

    /**
     * Toggles completion of the exercise with [id]: adds/subtracts its points, and —
     * when the whole session becomes complete for the first time today — awards the
     * bonus and increments the streak and workout count exactly once ([dayCounted]).
     */
    fun toggle(id: String) {
        val current = progress.value
        val list = repository.exercisesFor(current.goal)
        val exercise = list.firstOrNull { it.id == id } ?: return

        val completed = current.completed.toMutableSet()
        var total = current.totalScore
        if (id in completed) {
            completed.remove(id)
            total -= exercise.points
        } else {
            completed.add(id)
            total += exercise.points
        }

        var dayCounted = current.dayCounted
        var streak = current.streak
        var workouts = current.workouts
        Hotfy.track("exercicio_marcado", mapOf("exercise_id" to id, "completed" to (id in completed)))

        val allDone = list.isNotEmpty() && list.all { it.id in completed }
        if (allDone && !dayCounted) {
            dayCounted = true
            streak += 1
            workouts += 1
            total += BONUS_POINTS
            Hotfy.track("sessao_concluida", mapOf("streak" to streak, "workouts" to workouts))
        }

        commit(
            current.copy(
                completed = completed,
                totalScore = total,
                dayCounted = dayCounted,
                streak = streak,
                workouts = workouts,
            )
        )
    }

    // endregion

    // region — Progress

    /** Wipes all progress and returns to the onboarding flow. */
    fun resetAll() {
        Hotfy.track("progresso_resetado")
        viewModelScope.launch { repository.clear() }
        progress.value = UserProgress(date = repository.today())
    }

    // endregion

    /** Applies today's history entry, publishes the new state, and persists it. */
    private fun commit(updated: UserProgress) {
        val today = repository.today()
        val withHistory = updated.copy(
            history = updated.history + (today to pointsToday(updated)),
            date = today,
        )
        progress.value = withHistory
        viewModelScope.launch { repository.save(withHistory) }
    }

    /** Points earned today: completed-exercise points plus the bonus once earned. */
    private fun pointsToday(state: UserProgress): Int {
        val list = repository.exercisesFor(state.goal)
        var points = list.filter { it.id in state.completed }.sumOf { it.points }
        if (state.dayCounted) points += BONUS_POINTS
        return points
    }

    private fun buildUiState(state: UserProgress, isLoaded: Boolean): PulseUiState {
        val list = repository.exercisesFor(state.goal)
        val items = list.map { ExerciseUi(it, it.id in state.completed) }
        val total = list.size
        val completed = items.count { it.done }
        val fraction = if (total > 0) completed.toFloat() / total else 0f
        val points = pointsToday(state)
        val allDone = total > 0 && completed == total

        val headline = when {
            allDone -> Headline.CRUSHED
            completed > 0 -> Headline.GOING
            else -> Headline.READY
        }

        val greeting = when (LocalTime.now().hour) {
            in 0..11 -> Greeting.MORNING
            in 12..17 -> Greeting.AFTERNOON
            else -> Greeting.EVENING
        }

        return PulseUiState(
            loaded = isLoaded,
            onboarded = state.onboarded,
            goal = state.goal,
            greeting = greeting,
            items = items,
            completedCount = completed,
            totalCount = total,
            progressFraction = fraction,
            pointsToday = points,
            allDoneToday = allDone,
            headline = headline,
            totalScore = state.totalScore,
            streak = state.streak,
            workouts = state.workouts,
            weekDays = buildWeek(state, points),
            bonusPoints = BONUS_POINTS,
        )
    }

    /** Builds the last-7-days chart, mirroring the prototype's bar math exactly. */
    private fun buildWeek(state: UserProgress, todayPoints: Int): List<WeekDayUi> {
        val today = LocalDate.now()
        val todayIso = today.toString()

        data class Raw(val label: String, val points: Int, val isToday: Boolean)
        val raw = (6 downTo 0).map { back ->
            val date = today.minusDays(back.toLong())
            val iso = date.toString()
            val isToday = iso == todayIso
            val value = if (isToday) todayPoints else (state.history[iso] ?: 0)
            // JS Date.getDay(): 0=Sunday..6=Saturday. java DayOfWeek SUNDAY=7 -> 7%7=0.
            Raw(DAY_LETTERS[date.dayOfWeek.value % 7], value, isToday)
        }

        val maxValue = max(WEEK_MIN_SCALE, raw.maxOf { it.points })
        return raw.map {
            val pct = max(5, (it.points.toFloat() / maxValue * 100).roundToInt())
            WeekDayUi(label = it.label, points = it.points, isToday = it.isToday, fraction = pct / 100f)
        }
    }

    companion object {
        /** Bonus awarded once when the whole daily session is completed. */
        const val BONUS_POINTS = 50

        /** The weekly chart is always scaled to at least this value. */
        private const val WEEK_MIN_SCALE = 60

        private val DAY_LETTERS = listOf("S", "M", "T", "W", "T", "F", "S")

        /** Factory that wires the repository/DataStore using the application context. */
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = WorkoutRepository(PreferencesStore(context.applicationContext))
                    return PulseViewModel(repository) as T
                }
            }
    }
}
