package com.pulse.app.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hotfy.debugconsole.DebugLog
import com.hotfy.sdk.Hotfy
import com.pulse.app.MainActivity
import com.pulse.app.R

/**
 * Cola entre o FCM e o Hotfy device-token, no lado do app:
 *  - encaminha o token nativo pro SDK ([forwardToken]) — o SDK cuida do sync
 *    (change-detection + retry) no Console e no CDP;
 *  - mostra a notificação de um push data-only ([showNotification]);
 *  - faz open-tracking quando o usuário toca a notificação ([handlePushOpen]).
 *
 * Pedir permissão de notificação e configurar o Firebase (google-services.json)
 * é responsabilidade do app — ver PUSH_SETUP.md.
 */
object PulsePush {
    private const val EXTRA_FROM_PUSH = "hotfy_from_push"

    /** Cria o canal de notificação (idempotente). Necessário no Android 8+. */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val id = context.getString(R.string.push_channel_id)
        if (manager.getNotificationChannel(id) != null) return
        manager.createNotificationChannel(
            NotificationChannel(id, context.getString(R.string.push_channel_name), NotificationManager.IMPORTANCE_DEFAULT),
        )
    }

    /**
     * Busca o token atual do FCM e entrega pro SDK. No-op silencioso se o Firebase
     * não estiver configurado (sem google-services.json) — o app roda normalmente.
     * `onNewToken` do [PulseMessagingService] cobre rotações posteriores.
     */
    fun forwardToken() {
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Hotfy.setNativeToken(token, "android")
                    DebugLog.info("[Pulse:push] token FCM entregue ao SDK")
                }
                .addOnFailureListener { err ->
                    DebugLog.error("[Pulse:push] falha ao obter token FCM: ${err.message}")
                }
        } catch (err: Throwable) {
            // FirebaseApp não inicializado (sem google-services.json). Ver PUSH_SETUP.md.
            DebugLog.info("[Pulse:push] Firebase não configurado — push desativado (${err.message})")
        }
    }

    /** Mostra uma notificação a partir do `data` de um push data-only. */
    fun showNotification(context: Context, data: Map<String, String>, title: String?, body: String?) {
        ensureChannel(context)
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FROM_PUSH, true)
            data.forEach { (k, v) -> putExtra(k, v) }
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val contentIntent = PendingIntent.getActivity(context, 0, intent, flags)

        val notification: Notification = NotificationCompat.Builder(context, context.getString(R.string.push_channel_id))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title ?: context.getString(R.string.app_name))
            .setContentText(body ?: "")
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        manager.notify(System.identityHashCode(data), notification)
    }

    /**
     * Chamado no `onCreate`/`onNewIntent` da Activity. Se o launch veio de um push,
     * faz o parse tipado e reporta o "open" pro Console (`/v1/push-events`).
     * Retorna os dados parseados pra o app navegar (ou null se não foi push).
     */
    fun handlePushOpen(intent: Intent?): com.hotfy.sdk.core.devicetoken.ParsedPushData? {
        if (intent?.getBooleanExtra(EXTRA_FROM_PUSH, false) != true) return null

        val data = intent.extras?.keySet().orEmpty()
            .filter { it != EXTRA_FROM_PUSH }
            .mapNotNull { key -> intent.getStringExtra(key)?.let { key to it } }
            .toMap()

        val parsed = Hotfy.parsePushData(data)
        parsed.campaignId?.let {
            Hotfy.trackConsolePushOpened(it)
            DebugLog.info("[Pulse:push] open reportado campaign=$it target=${parsed.targetScreen}")
        }
        return parsed
    }
}
