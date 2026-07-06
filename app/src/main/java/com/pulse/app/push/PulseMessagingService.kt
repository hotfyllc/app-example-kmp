package com.pulse.app.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hotfy.debugconsole.DebugLog
import com.hotfy.sdk.Hotfy

/**
 * Serviço do FCM. Só entra em ação quando o Firebase está configurado
 * (google-services.json presente). Delega:
 *  - `onNewToken` → [Hotfy.setNativeToken] (o SDK sincroniza no Console + CDP);
 *  - `onMessageReceived` → mostra a notificação e reporta `push_received` no CDP.
 */
class PulseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Hotfy.setNativeToken(token, "android")
        DebugLog.info("[Pulse:push] onNewToken → SDK")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data

        // Delivery analytics no CDP, se o payload trouxer o send_id.
        data["send_id"]?.toLongOrNull()?.let { Hotfy.trackPushDelivered(it) }

        // App em foreground (ou push data-only): renderiza a notificação nós mesmos,
        // carregando o `data` como extras pra o open-tracking no tap.
        PulsePush.showNotification(
            context = this,
            data = data,
            title = message.notification?.title ?: data["title"],
            body = message.notification?.body ?: data["body"],
        )
    }
}
