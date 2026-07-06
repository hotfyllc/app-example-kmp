package com.pulse.app.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.domain.model.Exercise
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.Accent08
import com.pulse.app.ui.theme.Accent30
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.OnAccent
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.Surface
import com.pulse.app.ui.theme.TextDone
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary
import com.pulse.app.ui.theme.White06
import com.pulse.app.ui.theme.White18

/**
 * A single tappable exercise checklist row. Tapping toggles completion, which
 * strikes through the name, fills the check circle, and tints the row.
 *
 * @param exercise the exercise to display
 * @param done     whether it is completed
 * @param onToggle invoked when the row is tapped
 */
@Composable
fun ExerciseRow(
    exercise: Exercise,
    done: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background by animateColorAsState(if (done) Accent08 else Surface, label = "rowBg")
    val borderColor by animateColorAsState(if (done) Accent30 else White06, label = "rowBorder")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusCardSm))
            .background(background)
            .border(Dimens.Hairline, borderColor, RoundedCornerShape(Dimens.RadiusCardSm))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle,
            )
            .padding(horizontal = Dimens.Space16, vertical = Dimens.Space16 - 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space14),
    ) {
        CheckCircle(done = done)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = PulseText.ExerciseName,
                color = if (done) TextDone else TextPrimary,
                textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
            )
            Text(
                text = exercise.meta,
                style = PulseText.Meta,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Text(text = "+${exercise.points}", style = PulseText.Points, color = Accent)
    }
}

@Composable
private fun CheckCircle(done: Boolean) {
    val circleColor by animateColorAsState(if (done) Accent else androidx.compose.ui.graphics.Color.Transparent, label = "checkBg")
    val ringColor by animateColorAsState(if (done) Accent else White18, label = "checkRing")
    Box(
        modifier = Modifier
            .size(Dimens.CheckCircle)
            .clip(CircleShape)
            .background(circleColor)
            .border(2.dp, ringColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = OnAccent,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun ExerciseRowPreview() {
    PulseTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ExerciseRow(Exercise("b1", "Barbell Bench Press", "4 × 8 reps", 25), done = false, onToggle = {})
            ExerciseRow(Exercise("b2", "Deadlift", "4 × 6 reps", 30), done = true, onToggle = {})
        }
    }
}
