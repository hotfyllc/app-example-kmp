package com.pulse.app.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.domain.model.Exercise
import com.pulse.app.ui.components.ExerciseRow
import com.pulse.app.ui.components.ProgressRing
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Accent30
import com.pulse.app.ui.theme.AccentSoft
import com.pulse.app.ui.theme.Background
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.Surface
import com.pulse.app.ui.theme.TextMuted
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary
import com.pulse.app.ui.theme.White06
import com.pulse.app.viewmodel.ExerciseUi
import com.pulse.app.viewmodel.Greeting
import com.pulse.app.viewmodel.Headline
import com.pulse.app.viewmodel.PulseUiState

/**
 * The "Today" home screen: greeting, streak chip, progress-ring card, the goal chip,
 * and the tappable exercise checklist with a session-complete banner.
 *
 * @param state    the current UI snapshot
 * @param onToggle called with an exercise id when a row is tapped
 */
@Composable
fun TodayScreen(
    state: PulseUiState,
    onToggle: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenH)
            .padding(top = Dimens.ScreenTop, bottom = Dimens.Space26),
    ) {
        Spacer(Modifier.height(Dimens.Space12))
        Header(greeting = state.greeting, streak = state.streak)
        Spacer(Modifier.height(Dimens.Space20))
        ProgressCard(state)
        Spacer(Modifier.height(Dimens.Space22))
        SessionHeader(goalLabel = state.goal?.label ?: "—")
        Spacer(Modifier.height(Dimens.Space14))
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space10)) {
            state.items.forEach { item ->
                ExerciseRow(
                    exercise = item.exercise,
                    done = item.done,
                    onToggle = { onToggle(item.exercise.id) },
                )
            }
        }
        if (state.allDoneToday) {
            Spacer(Modifier.height(Dimens.Space16))
            CompleteBanner(bonus = state.bonusPoints, streak = state.streak)
        }
    }
}

@Composable
private fun Header(greeting: Greeting, streak: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(greetingText(greeting), style = PulseText.Greeting, color = TextSecondary)
            Text(stringResource(R.string.today), style = PulseText.ScreenTitle, color = TextPrimary, modifier = Modifier.padding(top = 3.dp))
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(AccentSoft)
                .padding(horizontal = 13.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space7),
        ) {
            Icon(painterResource(R.drawable.ic_flame_solid), null, tint = Accent, modifier = Modifier.size(15.dp))
            Text("$streak", style = PulseText.StreakValue, color = Accent)
        }
    }
}

@Composable
private fun ProgressCard(state: PulseUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardLg))
            .background(Surface)
            .border(Dimens.Hairline, White06, RoundedCornerShape(Dimens.RadiusCardLg))
            .padding(Dimens.Space20),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space18),
    ) {
        ProgressRing(fraction = state.progressFraction, percentLabel = state.percentLabel)
        Column(modifier = Modifier.weight(1f)) {
            Text(headlineText(state.headline), style = PulseText.CardTitle, color = TextPrimary)
            Text(
                stringResource(R.string.exercises_complete, state.completedCount, state.totalCount),
                style = PulseText.CardSub,
                color = TextSecondary,
                modifier = Modifier.padding(top = 3.dp),
            )
            Row(
                modifier = Modifier
                    .padding(top = Dimens.Space12)
                    .clip(RoundedCornerShape(Dimens.Space8))
                    .background(Background)
                    .padding(horizontal = 11.dp, vertical = Dimens.Space6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space6),
            ) {
                Icon(painterResource(R.drawable.ic_bolt), null, tint = Accent, modifier = Modifier.size(13.dp))
                Text("${state.pointsToday}", style = PulseText.PointsChip, color = TextPrimary)
                Text(stringResource(R.string.pts_today), style = PulseText.Label12, color = TextMuted)
            }
        }
    }
}

@Composable
private fun SessionHeader(goalLabel: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.todays_session), style = PulseText.Overline, color = TextMuted)
        Text(
            text = goalLabel,
            style = PulseText.Chip,
            color = Accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(AccentSoft)
                .padding(horizontal = Dimens.Space10, vertical = 5.dp),
        )
    }
}

@Composable
private fun CompleteBanner(bonus: Int, streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardSm))
            .background(AccentSoft)
            .border(Dimens.Hairline, Accent30, RoundedCornerShape(Dimens.RadiusCardSm))
            .padding(Dimens.Space16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
    ) {
        Icon(painterResource(R.drawable.ic_bolt), null, tint = Accent, modifier = Modifier.size(22.dp))
        Column {
            Text(stringResource(R.string.session_complete), style = PulseText.BannerTitle, color = TextPrimary)
            Text(
                stringResource(R.string.session_complete_sub, bonus, streak),
                style = PulseText.Meta,
                color = TextSecondary,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
    }
}

@Composable
private fun greetingText(greeting: Greeting): String = stringResource(
    when (greeting) {
        Greeting.MORNING -> R.string.good_morning
        Greeting.AFTERNOON -> R.string.good_afternoon
        Greeting.EVENING -> R.string.good_evening
    }
)

@Composable
private fun headlineText(headline: Headline): String = stringResource(
    when (headline) {
        Headline.READY -> R.string.headline_ready
        Headline.GOING -> R.string.headline_going
        Headline.CRUSHED -> R.string.headline_crushed
    }
)

@Preview(widthDp = 412, heightDp = 892)
@Composable
private fun TodayScreenPreview() {
    val items = listOf(
        ExerciseUi(Exercise("b1", "Barbell Bench Press", "4 × 8 reps", 25), true),
        ExerciseUi(Exercise("b2", "Deadlift", "4 × 6 reps", 30), false),
        ExerciseUi(Exercise("b3", "Pull-Ups", "4 × 10 reps", 20), false),
    )
    PulseTheme {
        TodayScreen(
            state = PulseUiState(
                loaded = true,
                onboarded = true,
                goal = com.pulse.app.domain.model.Goal.BUILD,
                greeting = Greeting.MORNING,
                items = items,
                completedCount = 1,
                totalCount = 3,
                progressFraction = 1f / 3f,
                pointsToday = 25,
                headline = Headline.GOING,
                streak = 4,
            ),
            onToggle = {},
        )
    }
}
