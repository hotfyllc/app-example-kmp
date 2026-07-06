package com.pulse.app.ads

import com.hotfy.sdk.core.model.AdUnits
import com.hotfy.sdk.core.model.BootAdType
import com.hotfy.sdk.core.model.ScreenAdUnits
import com.hotfy.sdk.core.model.WrapperConfig

// Config de EMERGÊNCIA. O SDK só usa isto quando, no cold start, o cache local
// está vazio E o fetch do wrapper falhou (backend fora do ar / sem rede). Sem
// ela, num outage o app abriria sem nenhum ad. Os ad units abaixo ficam
// "hardcoded" no app — mantenha em sincronia com o que está configurado no
// Console pros mesmos screen keys.
//
// IMPORTANTE: enquanto `useTestAds = true` (debug), o SDK substitui estes IDs
// pelos test units do Google, então os valores aqui não importam. Em produção
// (`useTestAds = false`), TROQUE pelos ad units REAIS do app (AdMob/GAM) —
// senão o fallback serve os test units abaixo, que não geram receita.

// Test units oficiais do Google — placeholders. Substituir pelos reais em prod.
private const val APP_OPEN_UNIT = "ca-app-pub-3940256099942544/9257395921"
private const val INTERSTITIAL_UNIT = "ca-app-pub-3940256099942544/1033173712"
private const val BANNER_UNIT = "ca-app-pub-3940256099942544/6300978111"
private const val REWARDED_UNIT = "ca-app-pub-3940256099942544/5224354917"

/**
 * WrapperConfig de emergência com os ad units de cada placement do app. Passado
 * como `fallbackConfig` no `Hotfy.init`. `isActive = true` pra que os ads
 * apareçam mesmo no modo degradado.
 */
fun buildFallbackConfig(): WrapperConfig =
    WrapperConfig(
        segment = "fallback",
        isActive = true,
        adUnits = AdUnits(
            appOpen = APP_OPEN_UNIT,
            appOpenType = BootAdType.APP_OPEN,
            screens = mapOf(
                // Onboarding: interstitial a cada "Next" (etapas 1..3).
                "onboarding_etapa_1" to ScreenAdUnits(interstitial = INTERSTITIAL_UNIT),
                "onboarding_etapa_2" to ScreenAdUnits(interstitial = INTERSTITIAL_UNIT),
                "onboarding_etapa_3" to ScreenAdUnits(interstitial = INTERSTITIAL_UNIT),
                // Goals: banner abaixo dos 3 cards de objetivo.
                "goals" to ScreenAdUnits(banner = BANNER_UNIT),
                // Progress: rewarded no "Change".
                "progress" to ScreenAdUnits(rewarded = REWARDED_UNIT),
            ),
        ),
    )
