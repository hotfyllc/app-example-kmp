package com.pulse.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pulse.app.R

/**
 * Two variable font families bundled in res/font:
 *  - [SpaceGrotesk] for display/headings (weights 400–700)
 *  - [Manrope] for body copy
 *
 * Each weight is registered with an explicit variation axis so the correct
 * instance is selected on API 26+ (API 24–25 gracefully falls back to the
 * nearest static instance).
 */
@OptIn(ExperimentalTextApi::class)
private fun spaceGrotesk(weight: Int) = Font(
    R.font.space_grotesk_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

@OptIn(ExperimentalTextApi::class)
private fun manrope(weight: Int) = Font(
    R.font.manrope_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val SpaceGrotesk = FontFamily(
    spaceGrotesk(400),
    spaceGrotesk(500),
    spaceGrotesk(600),
    spaceGrotesk(700),
)

val Manrope = FontFamily(
    manrope(400),
    manrope(500),
    manrope(600),
    manrope(700),
)

/**
 * Named text styles mirroring the exact font/size/weight combinations used in the
 * prototype. Screens use these directly so no font sizes are hard-coded inline.
 */
object PulseText {
    // Space Grotesk — display & headings
    val OnboardingTitle = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 34.sp, letterSpacing = (-0.5).sp,
    )
    val ScreenTitle = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 27.sp, letterSpacing = (-0.4).sp,
    )
    val Brand = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, letterSpacing = 1.5.sp,
    )
    val HeroNumber = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 46.sp, letterSpacing = (-1).sp,
    )
    val RingPercent = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 24.sp,
    )
    val StatNumber = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 24.sp,
    )
    val CardTitle = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
    )
    val GoalName = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
    )
    val ExerciseName = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp,
    )
    val ButtonLabel = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
    )
    val Points = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 13.sp,
    )
    val PointsChip = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
    )
    val BannerTitle = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
    )
    val StreakValue = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 14.sp,
    )

    // Manrope — body & labels
    val Body = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.5.sp, lineHeight = 24.sp,
    )
    val BodySmall = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 14.5.sp, lineHeight = 21.sp,
    )
    val Meta = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 13.sp,
    )
    val Caption = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 11.sp,
    )
    val StatLabel = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 12.5.sp,
    )
    val CardSub = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 19.sp,
    )
    val Greeting = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Medium, fontSize = 13.5.sp,
    )
    val Overline = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.4.sp,
    )
    val Chip = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
    )
    val Label12 = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 12.sp,
    )
    val NavLabel = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
    )
    val Action = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
    )
    val Button = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
    )
    val Skip = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
    )
}

/** Minimal Material 3 [Typography] so MaterialTheme has a coherent Manrope-based scale. */
val PulseTypography = Typography(
    bodyLarge = PulseText.Body,
    bodyMedium = PulseText.BodySmall,
    titleLarge = PulseText.ScreenTitle,
    labelSmall = PulseText.NavLabel,
)
