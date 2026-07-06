package com.pulse.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.RingTrack
import com.pulse.app.ui.theme.TextMuted
import com.pulse.app.ui.theme.TextPrimary

/**
 * Circular progress ring showing the fraction of today's exercises completed.
 * The accent arc animates from the 12 o'clock position and is capped with a round end.
 *
 * @param fraction   completion in 0f..1f
 * @param percentLabel pre-formatted percentage text (e.g. "60%")
 */
@Composable
fun ProgressRing(
    fraction: Float,
    percentLabel: String,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "ringSweep",
    )
    Box(
        modifier = modifier.size(Dimens.RingSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(Dimens.RingSize)) {
            val strokePx = Dimens.RingStroke.toPx()
            val inset = strokePx / 2
            val arcSize = androidx.compose.ui.geometry.Size(
                size.width - strokePx,
                size.height - strokePx,
            )
            val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
            drawArc(
                color = RingTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx),
            )
            drawArc(
                color = Accent,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(percentLabel, style = PulseText.RingPercent, color = TextPrimary, textAlign = TextAlign.Center)
            Text("done", style = PulseText.Caption, color = TextMuted)
        }
    }
}

@Preview
@Composable
private fun ProgressRingPreview() {
    PulseTheme { ProgressRing(fraction = 0.6f, percentLabel = "60%") }
}
