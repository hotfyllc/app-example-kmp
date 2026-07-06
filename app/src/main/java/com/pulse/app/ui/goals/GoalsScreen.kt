package com.pulse.app.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.domain.model.Goal
import com.pulse.app.ui.components.GoalCard
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Background
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary

/**
 * "Choose your goal" screen — three selectable [GoalCard]s. Selecting one invokes
 * [onPick]. Reused both as the post-onboarding step and from the bottom nav.
 *
 * @param selectedGoal the currently active goal (highlighted), or null
 * @param onPick       called with the chosen goal
 * @param adSlot       conteúdo opcional (banner Hotfy) renderizado abaixo dos 3 cards
 */
@Composable
fun GoalsScreen(
    selectedGoal: Goal?,
    onPick: (Goal) -> Unit,
    adSlot: @Composable () -> Unit = {},
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
        Text(stringResource(R.string.step_1), style = PulseText.Overline, color = Accent)
        Spacer(Modifier.height(Dimens.Space8))
        Text(stringResource(R.string.choose_your_goal), style = PulseText.ScreenTitle, color = TextPrimary)
        Spacer(Modifier.height(Dimens.Space8))
        Text(stringResource(R.string.goals_subtitle), style = PulseText.BodySmall, color = TextSecondary)
        Spacer(Modifier.height(Dimens.Space26))

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space14)) {
            GoalCard(
                iconRes = R.drawable.ic_flame,
                title = stringResource(R.string.goal_shred),
                subtitle = stringResource(R.string.goal_shred_sub),
                selected = selectedGoal == Goal.CUT,
                onClick = { onPick(Goal.CUT) },
            )
            GoalCard(
                iconRes = R.drawable.ic_dumbbell,
                title = stringResource(R.string.goal_build),
                subtitle = stringResource(R.string.goal_build_sub),
                selected = selectedGoal == Goal.BUILD,
                onClick = { onPick(Goal.BUILD) },
            )
            GoalCard(
                iconRes = R.drawable.ic_heart,
                title = stringResource(R.string.goal_maintain),
                subtitle = stringResource(R.string.goal_maintain_sub),
                selected = selectedGoal == Goal.MAINTAIN,
                onClick = { onPick(Goal.MAINTAIN) },
            )
        }

        // Banner Hotfy abaixo dos 3 cards de objetivo.
        Spacer(Modifier.height(Dimens.Space20))
        adSlot()
    }
}

@Preview(widthDp = 412, heightDp = 892)
@Composable
private fun GoalsScreenPreview() {
    PulseTheme { GoalsScreen(selectedGoal = Goal.BUILD, onPick = {}) }
}
