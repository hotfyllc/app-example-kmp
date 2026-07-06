package com.pulse.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hotfy.sdk.Hotfy
import com.pulse.app.ads.initHotfy
import com.pulse.app.debug.captureDebugFlag
import com.pulse.app.debug.isDebugAllowedByReferrer
import com.pulse.app.push.PulsePush
import com.pulse.app.ui.theme.PulseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

// Orçamento de boot ~6s (como no cartões-fácil): até 2s esperando a config do
// wrapper resolver + até 4s carregando/mostrando o App Open sobre a splash.
private const val WRAPPER_WAIT_MS = 2000L
private const val BOOT_AD_BUDGET_MS = 4000L

/**
 * The single Activity hosting the entire Compose UI. Enables edge-to-edge so the app
 * draws behind the transparent system status/navigation bars, matching the dark theme.
 */
class MainActivity : ComponentActivity() {
    // Push: resultado do pedido de permissão de notificação (Android 13+).
    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* best-effort */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicializa o Hotfy SDK e mostra o App Open na abertura. O Debug Console
        // já foi instalado em PulseApplication (antes da Activity resumir).
        lifecycleScope.launch {
            captureDebugFlag(this@MainActivity) // persiste flag do referrer p/ próxima abertura
            val debugEnabled = BuildConfig.DEBUG_CONSOLE_ENABLED || isDebugAllowedByReferrer(this@MainActivity)

            // Init em paralelo — config/preload/CDP seguem mesmo depois do boot ad.
            launch { initHotfy(this@MainActivity, debug = debugEnabled) }

            // Espera a config do wrapper resolver (cache hit é instantâneo; fetch
            // capado em 2s pra não segurar a abertura).
            withTimeoutOrNull(WRAPPER_WAIT_MS) {
                while (!Hotfy.isReady()) delay(50)
            }

            // App Open sobre a splash — até 4s pra carregar e mostrar.
            Hotfy.loadAndShowBootAd(timeoutMs = BOOT_AD_BUDGET_MS)
        }

        // Push: pede permissão, cria o canal e entrega o token FCM ao SDK.
        setupPush()
        // Open-tracking: se o app abriu por um tap em notificação.
        PulsePush.handlePushOpen(intent)

        setContent {
            PulseTheme {
                PulseApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        PulsePush.handlePushOpen(intent)
    }

    private fun setupPush() {
        PulsePush.ensureChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Entrega o token atual ao SDK (rotações posteriores vêm pelo onNewToken).
        PulsePush.forwardToken()
    }
}
