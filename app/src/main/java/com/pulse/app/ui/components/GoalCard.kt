package com.pulse.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.AccentSoft
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.OnAccent
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.Surface
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary
import com.pulse.app.ui.theme.White06

/**
 * Selectable goal card: accent icon tile, title + subtitle, and a check badge that
 * animates in when selected. A selected card gains an accent border and soft ring.
 */
@Composable
fun GoalCard(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Dimens.RadiusCard)
    Row(
        modifier = modifier
            // Soft outer ring approximating the prototype's box-shadow glow when selected.
            .then(if (selected) Modifier.border(3.dp, AccentSoft, shape) else Modifier)
            .clip(shape)
            .background(Surface)
            .border(
                width = if (selected) Dimens.BorderSelected else Dimens.Hairline,
                color = if (selected) Accent else White06,
                shape = shape,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(Dimens.Space18),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space16),
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.GoalIcon)
                .clip(RoundedCornerShape(13.dp))
                .background(AccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(26.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = PulseText.GoalName, color = TextPrimary)
            Text(
                subtitle,
                style = PulseText.CardSub,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = OnAccent,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun GoalCardPreview() {
    PulseTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GoalCard(R.drawable.ic_flame, "Shred", "Burn fat · stay lean", selected = true, onClick = {})
            GoalCard(R.drawable.ic_dumbbell, "Build Muscle", "Strength · hypertrophy", selected = false, onClick = {})
        }
    }
}
