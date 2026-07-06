package com.pulse.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.PulseTab
import com.pulse.app.R
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Background
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.TextMuted
import com.pulse.app.ui.theme.White06

/**
 * Three-item bottom navigation (Today / Progress / Goal). The active tab is accent-tinted.
 * Hidden during onboarding by the caller.
 */
@Composable
fun BottomNavBar(
    selected: PulseTab,
    onSelect: (PulseTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Background.copy(alpha = 0.85f)),
    ) {
        // Hairline top border (rgba(255,255,255,0.06)).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(White06),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Reserve space for the system navigation bar so 3-button mode doesn't
                // overlap the tabs; in gesture mode this inset is small.
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = Dimens.Space22, end = Dimens.Space22, top = Dimens.Space12, bottom = Dimens.Space12),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(R.drawable.ic_dumbbell, stringResource(R.string.nav_today), selected == PulseTab.TODAY) { onSelect(PulseTab.TODAY) }
            NavItem(R.drawable.ic_bars, stringResource(R.string.nav_progress), selected == PulseTab.PROGRESS) { onSelect(PulseTab.PROGRESS) }
            NavItem(R.drawable.ic_target, stringResource(R.string.nav_goal), selected == PulseTab.GOAL) { onSelect(PulseTab.GOAL) }
        }
    }
}

@Composable
private fun NavItem(
    iconRes: Int,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val color = if (active) Accent else TextMuted
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = Dimens.Space14, vertical = Dimens.Space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Text(label, style = PulseText.NavLabel, color = color)
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    PulseTheme {
        BottomNavBar(selected = PulseTab.TODAY, onSelect = {}, modifier = Modifier.width(412.dp))
    }
}
