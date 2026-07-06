package com.pulse.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Accent30
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.TextMuted
import com.pulse.app.viewmodel.WeekDayUi

/**
 * Weekly points bar chart (last 7 days). Today's bar is drawn in the solid accent
 * color and its label is highlighted; other days use the translucent accent.
 */
@Composable
fun WeekBarChart(
    days: List<WeekDayUi>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space8),
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEach { day ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.WeekChartHeight),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(max(Dimens.WeekBarMinHeight, Dimens.WeekChartHeight * day.fraction))
                            .clip(RoundedCornerShape(Dimens.Space6))
                            .background(if (day.isToday) Accent else Accent30),
                    )
                }
                Spacer(Modifier.height(Dimens.Space8))
                Text(
                    text = day.label,
                    style = PulseText.Caption.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = if (day.isToday) Accent else TextMuted,
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFF14171A, showBackground = true)
@Composable
private fun WeekBarChartPreview() {
    PulseTheme {
        WeekBarChart(
            days = listOf(
                WeekDayUi("M", 20, false, 0.3f),
                WeekDayUi("T", 45, false, 0.6f),
                WeekDayUi("W", 0, false, 0.05f),
                WeekDayUi("T", 60, false, 0.8f),
                WeekDayUi("F", 30, false, 0.4f),
                WeekDayUi("S", 10, false, 0.15f),
                WeekDayUi("S", 75, true, 1f),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
