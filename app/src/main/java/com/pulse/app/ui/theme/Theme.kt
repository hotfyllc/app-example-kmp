package com.pulse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Pulse is a dark-only app. This scheme wires the extracted tokens into Material 3
 * so any stock M3 component picks up the correct surface/accent colors.
 */
private val PulseColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
)

@Composable
fun PulseTheme(
    // Dark theme is fixed; the parameter exists only so previews can force it.
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = PulseColorScheme,
        typography = PulseTypography,
        content = content,
    )
}
