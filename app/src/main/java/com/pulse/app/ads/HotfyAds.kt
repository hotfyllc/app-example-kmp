package com.pulse.app.ads

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.hotfy.debugconsole.DebugConsole
import com.hotfy.debugconsole.DebugLog
import com.pulse.app.BuildConfig
import com.hotfy.sdk.Hotfy
import com.hotfy.sdk.HotfyConfig
import com.hotfy.sdk.ads.HotfyBannerSize
import com.hotfy.sdk.ads.HotfyBannerView
import com.hotfy.sdk.core.events.AdEvent
import com.hotfy.sdk.core.events.HotfyAdListener

/**
 * Inicializa o Hotfy SDK. Usa ads de teste do Google + um `fallbackConfig` ativo
 * pra funcionar SEM depender do backend/segmento — bom pra validar rendering.
 * Em produção: apiKey real, `useTestAds = false`, e remover o `fallbackConfig`.
 * Eventos de ad são encaminhados pro `DebugLog` (aba Logs do Debug Console).
 */
suspend fun initHotfy(context: Context, debug: Boolean) {
    Hotfy.addListener(object : HotfyAdListener {
        override fun onLoad(event: AdEvent.Load) { DebugLog.info("[Hotfy:ads] LOAD ${event.format}") }
        override fun onShow(event: AdEvent.Show) { DebugLog.info("[Hotfy:ads] SHOW ${event.format} unit=${event.adUnitId} floor=${event.finalFloor}") }
        override fun onImpression(event: AdEvent.Impression) { DebugLog.info("[Hotfy:ads] PAID ${event.format} ${event.value} ${event.currency}") }
        override fun onClick(event: AdEvent.Click) { DebugLog.info("[Hotfy:ads] CLICK ${event.format}") }
        override fun onClose(event: AdEvent.Close) { DebugLog.info("[Hotfy:ads] CLOSE ${event.format}") }
        override fun onError(event: AdEvent.Error) { DebugLog.error("[Hotfy:ads] ERROR ${event.format} code=${event.code} ${event.message}") }
        override fun onSkip(event: AdEvent.Skip) { DebugLog.info("[Hotfy:ads] SKIP ${event.format} reason=${event.reason}") }
    })

    Hotfy.init(
        context,
        HotfyConfig(
            apiKey = BuildConfig.HOTFY_API_KEY,
            useTestAds = true,
            debug = debug,
            enableCdp = true,
            // Push (device token FCM/APNS) — registra o token no Console + CDP com
            // change-detection. Entregue o token via Hotfy.setNativeToken (ver push/).
            enableDeviceToken = true,
            // Config de emergência: usada só se o wrapper estiver fora do ar no
            // cold start (cache vazio + fetch falhou). Ver FallbackConfig.kt.
            fallbackConfig = buildFallbackConfig(),
            // Tráfego do SDK na aba Network do Debug Console. Sem o console
            // instalado o interceptor é passthrough puro — seguro em produção.
            httpInterceptor = DebugConsole.networkInterceptor(),
        ),
    )
    Hotfy.startAdPreload()
}

/** Banner Hotfy embutível no Compose. Cria a view e a destrói ao sair da composição. */
@Composable
fun HotfyBanner(
    screenKey: String,
    modifier: Modifier = Modifier,
    size: HotfyBannerSize = HotfyBannerSize.ADAPTIVE,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            Hotfy.createBannerView(ctx, screenKey, size) ?: FrameLayout(ctx)
        },
        onRelease = { view -> (view as? HotfyBannerView)?.destroyBanner() },
    )
}

/** Carrega um native ad e devolve a View pronta (headline + CTA) pro Compose embutir. */
@Composable
fun HotfyNative(screenKey: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx -> FrameLayout(ctx) },
        update = { container ->
            Hotfy.loadNativeAd(container.context, screenKey) { ad ->
                if (ad == null) return@loadNativeAd
                val view = com.google.android.gms.ads.nativead.NativeAdView(container.context)
                val headline = android.widget.TextView(container.context).apply {
                    text = ad.headline ?: "Ad"
                    setPadding(24, 24, 24, 24)
                }
                view.headlineView = headline
                view.addView(headline)
                view.setNativeAd(ad)
                container.removeAllViews()
                container.addView(view)
            }
        },
    )
}
