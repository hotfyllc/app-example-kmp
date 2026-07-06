package com.pulse.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.hotfy.sdk.Hotfy
import com.hotfy.sdk.ads.HotfyBannerSize
import com.pulse.app.ads.HotfyBanner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pulse.app.ui.theme.Accent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.pulse.app.ui.components.BottomNavBar
import com.pulse.app.ui.goals.GoalsScreen
import com.pulse.app.ui.onboarding.OnboardingScreen
import com.pulse.app.ui.progress.ProgressScreen
import com.pulse.app.ui.theme.Background
import com.pulse.app.ui.workout.TodayScreen
import com.pulse.app.viewmodel.PulseViewModel

/**
 * Single-activity app shell: hosts the [NavHost], derives the start destination from
 * persisted state, and shows the bottom navigation bar on every screen except onboarding.
 */
@Composable
fun PulseApp(
    viewModel: PulseViewModel = viewModel(factory = PulseViewModel.factory(LocalContext.current)),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Hold a plain background until persisted state has been restored, to avoid a flash
    // of the wrong start screen.
    if (!state.loaded) {
        androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(Background))
        return
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    // Loading enquanto o rewarded do "Change" carrega sob demanda (rede).
    var isLoadingRewarded by remember { mutableStateOf(false) }
    val startDestination = when {
        !state.onboarded -> Routes.ONBOARDING
        state.goal == null -> Routes.GOALS
        else -> Routes.TODAY
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // CDP: screen view por tela, com os nomes exatos do Console. O onboarding é
    // rastreado por etapa (onboarding_etapa_N) dentro do OnboardingScreen.
    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            Routes.GOALS -> Hotfy.screen("goals")
            Routes.TODAY -> Hotfy.screen("today")
            Routes.PROGRESS -> Hotfy.screen("progress")
        }
    }

    // Switches between the three peer tabs, preserving each tab's scroll/state.
    fun navTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (currentRoute != null && currentRoute != Routes.ONBOARDING) {
                val selectedTab = when (currentRoute) {
                    Routes.PROGRESS -> PulseTab.PROGRESS
                    Routes.GOALS -> PulseTab.GOAL
                    else -> PulseTab.TODAY
                }
                BottomNavBar(
                    selected = selectedTab,
                    onSelect = { tab ->
                        when (tab) {
                            PulseTab.TODAY -> navTab(if (state.goal != null) Routes.TODAY else Routes.GOALS)
                            PulseTab.PROGRESS -> navTab(Routes.PROGRESS)
                            PulseTab.GOAL -> navTab(Routes.GOALS)
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    // Interstitial a cada "Next"/"Get started": onboarding_etapa_1, _2, _3.
                    onAdvance = { step -> Hotfy.showInterstitial("onboarding_etapa_$step") },
                    onFinish = {
                        viewModel.finishOnboarding()
                        val target = if (state.goal != null) Routes.TODAY else Routes.GOALS
                        navController.navigate(target) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Routes.GOALS) {
                GoalsScreen(
                    selectedGoal = state.goal,
                    onPick = { goal ->
                        viewModel.chooseGoal(goal)
                        navTab(Routes.TODAY)
                    },
                    // Banner (Large, 320x100) abaixo dos 3 cards de objetivo.
                    adSlot = {
                        HotfyBanner(
                            screenKey = "goals",
                            modifier = Modifier.fillMaxWidth(),
                            size = HotfyBannerSize.LARGE_BANNER,
                        )
                    },
                )
            }
            composable(Routes.TODAY) {
                TodayScreen(state = state, onToggle = viewModel::toggle)
            }
            composable(Routes.PROGRESS) {
                ProgressScreen(
                    state = state,
                    // Rewarded ao clicar em "Change" antes de ir pro seletor de objetivo.
                    // O load é sob demanda (rede), então mostra loading até resolver.
                    onChangeGoal = {
                        if (!isLoadingRewarded) {
                            scope.launch {
                                isLoadingRewarded = true
                                try {
                                    Hotfy.showRewarded("progress")
                                } finally {
                                    isLoadingRewarded = false
                                }
                                navTab(Routes.GOALS)
                            }
                        }
                    },
                    onReset = {
                        viewModel.resetAll()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }
        }
    }

        // Overlay de loading enquanto o rewarded carrega — cobre a tela e bloqueia
        // toques (o clickable sem indicação intercepta os taps). Fica atrás do ad
        // fullscreen quando ele abre; some quando o showRewarded resolve.
        if (isLoadingRewarded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Accent)
            }
        }
    }
}
