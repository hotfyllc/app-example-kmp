package com.pulse.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.Surface
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary
import com.pulse.app.ui.theme.White06

/**
 * Small statistic tile used on the Progress screen (day streak / workouts / today).
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Dimens.RadiusCardSm)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Surface)
            .border(Dimens.Hairline, White06, shape)
            .padding(Dimens.Space16),
    ) {
        Text(value, style = PulseText.StatNumber, color = TextPrimary)
        Text(
            label,
            style = PulseText.StatLabel,
            color = TextSecondary,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun StatCardPreview() {
    PulseTheme { StatCard(value = "7", label = "Day streak", modifier = Modifier.padding(16.dp)) }
}
