package com.pulse.app.debug

import android.app.Application
import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.hotfy.debugconsole.DebugAction
import com.hotfy.debugconsole.DebugConsole
import com.hotfy.debugconsole.DebugConsoleConfig
import com.hotfy.debugconsole.DebugLog
import com.hotfy.debugconsole.SegmentControl
import com.hotfy.sdk.Hotfy
import com.hotfy.sdk.core.model.WrapperConfig
import kotlinx.serialization.json.Json
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Debug flag — feature do APP (não do SDK). Liga o Debug Console em prod sem
// rebuild: QA instala via link com `?debug=1` (ou `?pulse_debug=1`) e o overlay
// aparece na próxima abertura. `debug=0` desliga. Persistido em SharedPreferences.

private const val PREFS = "pulse_debug"
private const val KEY = "debug_console_via_referrer"

// Espelha AVAILABLE_SEGMENTS do SDK. Inline porque o símbolo é R8-strippado no
// AAR publicado; novos d0_*/d24_* são resolvidos pelo backend automaticamente.
private val AVAILABLE_SEGMENTS = listOf(
    "default",
    "d0_organic", "d0_google", "d0_meta",
    "d24_organic", "d24_google", "d24_meta",
)

private val DEBUG_ON = Regex("(?:^|[?&])(?:pulse_)?debug=1(?:&|\$)")
private val DEBUG_OFF = Regex("[?&](?:pulse_)?debug=0(?:&|\$)")

fun isDebugAllowedByReferrer(context: Context): Boolean =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

/** Lê o Install Referrer e persiste a flag `debug=1`/`debug=0`. */
suspend fun captureDebugFlag(context: Context) {
    val raw = readInstallReferrer(context) ?: return
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    when {
        DEBUG_ON.containsMatchIn(raw) -> prefs.edit().putBoolean(KEY, true).apply()
        DEBUG_OFF.containsMatchIn(raw) -> prefs.edit().putBoolean(KEY, false).apply()
    }
}

/** Instala o Debug Console com actions + segment control ligados ao Hotfy SDK. */
fun installDebugConsole(app: Application) {
    DebugConsole.install(
        app,
        DebugConsoleConfig(
            version = "sdk ${Hotfy.version}",
            actions = listOf(
                DebugAction("Wrapper Config", revealLogs = true) {
                    DebugLog.info("[Hotfy] segment: ${Hotfy.getSegment() ?: "(sem config)"}")
                    DebugLog.info("[Hotfy] segment override: ${Hotfy.getSegmentOverride() ?: "(nenhum)"}")
                    DebugLog.info("[Hotfy] active=${Hotfy.isActive()}")
                    DebugLog.info(formatWrapperConfig(Hotfy.getConfig()))
                },
                DebugAction("Preload", revealLogs = true) { Hotfy.startAdPreload() },
                DebugAction("Interstitial") { Hotfy.showInterstitial("debug") },
                DebugAction("Rewarded") { Hotfy.showRewarded("debug") },
                DebugAction("App Open") { Hotfy.loadAndShowBootAd() },
                DebugAction("Refresh Wrapper", revealLogs = true) {
                    // Limpa o cache em memória (config + ad pool) e refaz o fetch na hora.
                    Hotfy.refresh()
                    DebugLog.warn("[Hotfy] Wrapper recarregado: ${Hotfy.getSegment() ?: "(sem config)"}")
                },
                DebugAction("Segment: clear override", revealLogs = true) {
                    // Volta à resolução natural de segmento (attribution + daysSinceInstall).
                    Hotfy.setSegmentOverride(null)
                    Hotfy.refresh()
                    DebugLog.info("[Hotfy] segmento natural: ${Hotfy.getSegment() ?: "(sem config)"}")
                },
                DebugAction("CDP Anonymous ID", revealLogs = true) {
                    DebugLog.info("[Hotfy] anonymousId: ${Hotfy.getAnonymousId()}")
                    DebugLog.info("[Hotfy] userId (após identify): ${Hotfy.getUserId() ?: "(não identificado)"}")
                },
                DebugAction("Debug Flag", revealLogs = true) {
                    DebugLog.info("[Hotfy] debug via referrer: ${isDebugAllowedByReferrer(app)}")
                },
                DebugAction("Hide Console") {
                    app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, false).apply()
                    DebugLog.warn("[Hotfy] Console oculto na próxima abertura. Reative via link com debug=1.")
                },
            ),
            segments = SegmentControl(
                current = Hotfy.getSegment(),
                options = AVAILABLE_SEGMENTS,
                onSelect = { slug ->
                    // Força o segmento: refetch da config + re-preload do ad pool.
                    Hotfy.setSegmentOverride(slug)
                    Hotfy.refresh()
                    DebugLog.info("[Hotfy] segment override → $slug (segment=${Hotfy.getSegment()})")
                },
            ),
        ),
    )
}

private suspend fun readInstallReferrer(context: Context): String? = suspendCancellableCoroutine { cont ->
    val client = InstallReferrerClient.newBuilder(context).build()
    client.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            var result: String? = null
            try {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    result = client.installReferrer.installReferrer
                }
            } catch (_: Exception) {
                // ignore
            } finally {
                runCatching { client.endConnection() }
            }
            if (cont.isActive) cont.resume(result)
        }

        override fun onInstallReferrerServiceDisconnected() {
            if (cont.isActive) cont.resume(null)
        }
    })
    cont.invokeOnCancellation { runCatching { client.endConnection() } }
}

// Json indentado, estilo JSON.stringify(x, null, 2). WrapperConfig é @Serializable
// e o SDK mantém o serializer no R8, então dá pra serializar a partir do app.
private val prettyJson = Json { prettyPrint = true }

/** WrapperConfig como JSON formatado pra aba Logs (o toString da data class sai inline). */
private fun formatWrapperConfig(config: WrapperConfig?): String {
    if (config == null) return "[Hotfy] config: (null)"
    val json = prettyJson.encodeToString(WrapperConfig.serializer(), config)
    return "[Hotfy] WrapperConfig:\n$json"
}
