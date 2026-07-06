package com.pulse.app.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.ui.components.StatCard
import com.pulse.app.ui.components.WeekBarChart
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Accent18
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
import com.pulse.app.ui.theme.White08
import com.pulse.app.viewmodel.PulseUiState
import com.pulse.app.viewmodel.WeekDayUi

/**
 * The "Progress" screen: total-score hero, three stat tiles, the weekly bar chart,
 * a shortcut to change the goal, and a reset-all control.
 *
 * @param state        current UI snapshot
 * @param onChangeGoal navigate to the goal picker
 * @param onReset      wipe all progress
 */
@Composable
fun ProgressScreen(
    state: PulseUiState,
    onChangeGoal: () -> Unit,
    onReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenH)
            .padding(top = Dimens.ScreenTop, bottom = Dimens.Space26),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.progress), style = PulseText.ScreenTitle, color = TextPrimary)
        Spacer(Modifier.height(Dimens.Space20))

        HeroCard(totalScore = state.totalScore)
        Spacer(Modifier.height(Dimens.Space14))

        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
            StatCard(value = "${state.streak}", label = stringResource(R.string.day_streak), modifier = Modifier.weight(1f))
            StatCard(value = "${state.workouts}", label = stringResource(R.string.workouts), modifier = Modifier.weight(1f))
            StatCard(value = "${state.pointsToday}", label = stringResource(R.string.today_stat), modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(Dimens.Space14))

        WeekCard(days = state.weekDays)
        Spacer(Modifier.height(Dimens.Space14))

        CurrentGoalRow(goalLabel = state.goal?.label ?: "—", onClick = onChangeGoal)
        Spacer(Modifier.height(Dimens.Space26))

        ResetButton(onClick = onReset)
    }
}

@Composable
private fun HeroCard(totalScore: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardLg))
            .background(Brush.linearGradient(listOf(Color(0xFF122427), Surface)))
            .border(Dimens.Hairline, Accent18, RoundedCornerShape(Dimens.RadiusCardLg))
            .padding(22.dp),
    ) {
        Text(
            stringResource(R.string.total_score),
            style = PulseText.CardSub.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
            color = Accent,
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = Dimens.Space6)) {
            Text("$totalScore", style = PulseText.HeroNumber, color = TextPrimary)
            Text(
                stringResource(R.string.pts),
                style = PulseText.Body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = TextSecondary,
                modifier = Modifier.padding(start = Dimens.Space8, bottom = 4.dp),
            )
        }
    }
}

@Composable
private fun WeekCard(days: List<WeekDayUi>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardLg))
            .background(Surface)
            .border(Dimens.Hairline, White06, RoundedCornerShape(Dimens.RadiusCardLg))
            .padding(Dimens.Space20),
    ) {
        Text(stringResource(R.string.this_week), style = PulseText.Overline, color = TextMuted)
        Spacer(Modifier.height(Dimens.Space18))
        WeekBarChart(days = days)
    }
}

@Composable
private fun CurrentGoalRow(goalLabel: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCard))
            .background(Surface)
            .border(Dimens.Hairline, White06, RoundedCornerShape(Dimens.RadiusCard))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(Dimens.Space18),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space14),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(Dimens.Space12))
                .background(AccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(R.drawable.ic_target), null, tint = Accent, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.current_goal), style = PulseText.StatLabel, color = TextSecondary)
            Text(goalLabel, style = PulseText.CardTitle, color = TextPrimary, modifier = Modifier.padding(top = 2.dp))
        }
        Text(stringResource(R.string.change), style = PulseText.Action, color = Accent)
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ResetButtonHeight)
            .clip(RoundedCornerShape(Dimens.RadiusButton))
            .border(Dimens.Hairline, White08, RoundedCornerShape(Dimens.RadiusButton))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.reset_all), style = PulseText.Button, color = TextMuted, textAlign = TextAlign.Center)
    }
}

@Preview(widthDp = 412, heightDp = 892)
@Composable
private fun ProgressScreenPreview() {
    PulseTheme {
        ProgressScreen(
            state = PulseUiState(
                loaded = true,
                goal = com.pulse.app.domain.model.Goal.BUILD,
                totalScore = 340,
                streak = 5,
                workouts = 8,
                pointsToday = 70,
                weekDays = listOf(
                    WeekDayUi("M", 20, false, 0.3f),
                    WeekDayUi("T", 45, false, 0.6f),
                    WeekDayUi("W", 0, false, 0.05f),
                    WeekDayUi("T", 60, false, 0.8f),
                    WeekDayUi("F", 30, false, 0.4f),
                    WeekDayUi("S", 10, false, 0.15f),
                    WeekDayUi("S", 70, true, 1f),
                ),
            ),
            onChangeGoal = {},
            onReset = {},
        )
    }
}
