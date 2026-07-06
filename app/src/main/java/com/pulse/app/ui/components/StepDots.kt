package com.pulse.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.White18

/**
 * Onboarding page indicator. The active dot widens and turns accent.
 *
 * @param count       number of pages
 * @param activeIndex currently selected page
 */
@Composable
fun StepDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space8),
    ) {
        repeat(count) { index ->
            val active = index == activeIndex
            val width by animateDpAsState(if (active) 26.dp else 7.dp, tween(300), label = "dotWidth")
            val color by animateColorAsState(if (active) Accent else White18, tween(300), label = "dotColor")
            Box(
                modifier = Modifier
                    .height(7.dp)
                    .width(width)
                    .clip(RoundedCornerShape(Dimens.RadiusPill))
                    .background(color),
            )
        }
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun StepDotsPreview() {
    PulseTheme { StepDots(count = 3, activeIndex = 1, modifier = Modifier.width(120.dp)) }
}
