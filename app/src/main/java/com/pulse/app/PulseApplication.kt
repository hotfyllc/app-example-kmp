package com.pulse.app

import android.app.Application
import com.pulse.app.debug.installDebugConsole
import com.pulse.app.debug.isDebugAllowedByReferrer

/**
 * Application class — instala o Debug Console ANTES de qualquer Activity, pra o
 * overlay pegar o `onActivityResumed` da primeira tela (senão o botão só
 * apareceria ao voltar do background). Gate: env `DEBUG_CONSOLE_ENABLED` ou a
 * flag `debug=1` persistida do install referrer.
 */
class PulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG_CONSOLE_ENABLED || isDebugAllowedByReferrer(this)) {
            installDebugConsole(this)
        }
    }
}
