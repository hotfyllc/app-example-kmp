# Push (FCM + Hotfy device token)

O app já vem com **todo o wiring de push** pronto:

- `enableDeviceToken = true` no `HotfyConfig` (`ads/HotfyAds.kt`) — o SDK sincroniza o
  token no Console e no CDP com change-detection + retry.
- `push/PulseMessagingService.kt` — `onNewToken` → `Hotfy.setNativeToken(...)`;
  `onMessageReceived` → notificação + `Hotfy.trackPushDelivered(...)`.
- `push/PulsePush.kt` — canal de notificação, entrega do token, e **open-tracking**
  (`Hotfy.parsePushData` + `Hotfy.trackConsolePushOpened`) no tap.
- `MainActivity` — pede a permissão `POST_NOTIFICATIONS` (Android 13+) e trata o intent
  de abertura por push.

Sem o Firebase configurado, **o app compila e roda normalmente** — só não recebe token
(o `forwardToken` faz no-op silencioso). Para o FCM passar a entregar tokens/mensagens,
faça os 2 passos abaixo.

## 1. Adicione o `google-services.json`

No [Firebase Console](https://console.firebase.google.com), crie/abra um projeto, adicione
um app Android com o package `com.pulse.app`, baixe o `google-services.json` e coloque em
`app/google-services.json`.

## 2. Ligue o plugin `google-services`

No `gradle/libs.versions.toml`:

```toml
[versions]
googleServices = "4.4.2"

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

No `build.gradle.kts` (root), registre o plugin sem aplicar:

```kotlin
plugins {
    alias(libs.plugins.google.services) apply false
}
```

No `app/build.gradle.kts`, aplique:

```kotlin
plugins {
    // ...
    alias(libs.plugins.google.services)
}
```

Rebuild. A partir daí `FirebaseMessaging` inicializa e o token flui para o SDK
automaticamente.

## Testar sem servidor de push

Envie um push de teste pelo Firebase Console (Cloud Messaging) ou via API HTTP v1 com um
payload `data` incluindo, por exemplo, `campaign_id`, `target_screen` e `send_id` — o
`onMessageReceived` mostra a notificação e o tap dispara o open-tracking no Console.
