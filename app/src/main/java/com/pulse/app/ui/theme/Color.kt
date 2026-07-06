package com.pulse.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Pulse color tokens, extracted verbatim from the HTML prototype's design system.
 * Reference these instead of hard-coding hex values in screens.
 */

// Base surfaces
val Background = Color(0xFF0A0B0D)
val Surface = Color(0xFF14171A)
val Deep = Color(0xFF050607)

// Accent (cyan) and its translucent variants
val Accent = Color(0xFF22D3EE)
val OnAccent = Color(0xFF04121A)          // dark text drawn on top of the accent
val AccentSoft = Accent.copy(alpha = 0.12f) // "accent-soft"
val Accent30 = Accent.copy(alpha = 0.30f)   // "accent-30"
val Accent08 = Accent.copy(alpha = 0.08f)   // completed exercise row background
val Accent18 = Accent.copy(alpha = 0.18f)   // hero card / onboarding ring border

// Text
val TextPrimary = Color(0xFFF5F7F8)
val TextSecondary = Color(0xFF9AA4AA)
val TextMuted = Color(0xFF5C666C)
val TextDone = Color(0xFF5C7E80)            // struck-through completed exercise name

// Hairline borders / fills (white at low alpha, matching rgba(255,255,255,x))
val White06 = Color.White.copy(alpha = 0.06f)
val White08 = Color.White.copy(alpha = 0.08f)
val White09 = Color.White.copy(alpha = 0.09f)
val White10 = Color.White.copy(alpha = 0.10f)
val White18 = Color.White.copy(alpha = 0.18f)

// Progress ring track
val RingTrack = Color.White.copy(alpha = 0.08f)
