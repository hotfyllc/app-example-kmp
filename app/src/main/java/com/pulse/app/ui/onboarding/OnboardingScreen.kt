package com.pulse.app.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.hotfy.sdk.Hotfy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.app.R
import com.pulse.app.ui.components.PrimaryButton
import com.pulse.app.ui.components.StepDots
import com.pulse.app.ui.theme.Accent
import com.pulse.app.ui.theme.AccentSoft
import com.pulse.app.ui.theme.Background
import com.pulse.app.ui.theme.Dimens
import com.pulse.app.ui.theme.PulseText
import com.pulse.app.ui.theme.PulseTheme
import com.pulse.app.ui.theme.Surface
import com.pulse.app.ui.theme.TextMuted
import com.pulse.app.ui.theme.TextPrimary
import com.pulse.app.ui.theme.TextSecondary
import com.pulse.app.ui.theme.White10
import kotlinx.coroutines.launch

private data class OnbPage(
    val iconRes: Int,
    val titleRes: Int,
    val bodyRes: Int,
    val tintIcon: Boolean,
)

private val onboardingPages = listOf(
    OnbPage(R.drawable.ic_dumbbell, R.string.onb_title_1, R.string.onb_body_1, tintIcon = true),
    OnbPage(R.drawable.ic_checklist, R.string.onb_title_2, R.string.onb_body_2, tintIcon = false),
    OnbPage(R.drawable.ic_bolt, R.string.onb_title_3, R.string.onb_body_3, tintIcon = true),
)

/**
 * Three-page onboarding carousel with a pulsing hero icon, page dots, Skip/Back
 * controls and a Next → "Get started" primary action.
 *
 * @param onFinish invoked when onboarding is skipped or completed
 * @param onAdvance invoked with the 1-based step number when the primary
 *   (Next / Get started) button is pressed — usado pra disparar o interstitial
 *   `onboarding_etapa_<n>`.
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onAdvance: (stepNumber: Int) -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val page = pagerState.currentPage
    val isLast = page == onboardingPages.lastIndex

    // Screen view por etapa: onboarding_etapa_1 / _2 / _3 (nomes do Console).
    LaunchedEffect(page) { Hotfy.screen("onboarding_etapa_${page + 1}") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = Dimens.Space26)
            .padding(top = Dimens.Space8, bottom = 34.dp),
    ) {
        // Header: brand + Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(AccentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_waveform), null, tint = Accent, modifier = Modifier.size(18.dp))
                }
                Text(stringResource(R.string.brand), style = PulseText.Brand, color = TextPrimary)
            }
            AnimatedVisibility(visible = !isLast, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = stringResource(R.string.skip),
                    style = PulseText.Skip,
                    color = TextMuted,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onFinish,
                        )
                        .padding(8.dp),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { index ->
            OnboardingPage(onboardingPages[index])
        }

        StepDots(
            count = onboardingPages.size,
            activeIndex = page,
            modifier = Modifier.padding(top = 30.dp, bottom = Dimens.Space26),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(visible = page > 0) {
                Box(
                    modifier = Modifier
                        .size(Dimens.TouchTarget)
                        .clip(RoundedCornerShape(Dimens.RadiusButton))
                        .background(Surface)
                        .border(Dimens.Hairline, White10, RoundedCornerShape(Dimens.RadiusButton))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { scope.launch { pagerState.animateScrollToPage(page - 1) } },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
            PrimaryButton(
                label = if (isLast) stringResource(R.string.get_started) else stringResource(R.string.next),
                onClick = {
                    // Interstitial da etapa atual (onboarding_etapa_1, _2, _3) ao avançar.
                    onAdvance(page + 1)
                    if (isLast) onFinish() else scope.launch { pagerState.animateScrollToPage(page + 1) }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OnboardingPage(page: OnbPage) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            PulsingIcon(iconRes = page.iconRes, tintIcon = page.tintIcon)
        }
        Text(stringResource(page.titleRes), style = PulseText.OnboardingTitle, color = TextPrimary)
        Spacer(Modifier.height(Dimens.Space12))
        Text(
            stringResource(page.bodyRes),
            style = PulseText.Body,
            color = TextSecondary,
            modifier = Modifier.widthIn(max = 300.dp),
        )
    }
}

@Composable
private fun PulsingIcon(iconRes: Int, tintIcon: Boolean) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val haloAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = InfiniteRepeatableSpec(tween(1700), RepeatMode.Reverse),
        label = "halo",
    )
    Box(
        modifier = Modifier.size(Dimens.OnboardingHalo),
        contentAlignment = Alignment.Center,
    ) {
        // Radial glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(haloAlpha)
                .background(Brush.radialGradient(listOf(AccentSoft, Color.Transparent), radius = 300f)),
        )
        // Thin inner ring
        Box(
            modifier = Modifier
                .size(Dimens.OnboardingHalo - 68.dp)
                .clip(CircleShape)
                .border(1.dp, Accent.copy(alpha = 0.18f), CircleShape),
        )
        // Core disc
        Box(
            modifier = Modifier
                .size(Dimens.OnboardingCore)
                .clip(CircleShape)
                .background(Color(0xFF0D1517))
                .border(Dimens.BorderSelected, Accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (tintIcon) {
                Icon(painterResource(iconRes), null, tint = Accent, modifier = Modifier.size(54.dp))
            } else {
                // Two-tone icon keeps its baked colors.
                Image(painterResource(iconRes), null, modifier = Modifier.size(54.dp))
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 892)
@Composable
private fun OnboardingScreenPreview() {
    PulseTheme { OnboardingScreen(onFinish = {}) }
}
