package com.pulse.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.OnAccent
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme

/**
 * Full-width accent call-to-action button (onboarding "Next" / "Get started").
 * Shows a trailing arrow by default.
 *
 * @param label        button text
 * @param onClick      click handler
 * @param showArrow    whether to draw the trailing arrow icon
 */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.TouchTarget)
            .clip(RoundedCornerShape(Dimens.RadiusButton))
            .background(Accent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = PulseText.ButtonLabel, color = OnAccent)
        if (showArrow) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = OnAccent,
                modifier = Modifier
                    .padding(start = Dimens.Space8)
                    .size(18.dp),
            )
        }
    }
}

@Preview(backgroundColor = 0xFF0A0B0D, showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    PulseTheme {
        PrimaryButton(label = "Get started", onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
