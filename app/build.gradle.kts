import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// local.properties (gitignored) = o "env" do Android.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val hotfyApiKey: String = localProps.getProperty("HOTFY_API_KEY", "")
// Liga o Debug Console em build-time (default on). O referrer `debug=1` liga em runtime.
val debugConsoleEnabled: String = localProps.getProperty("DEBUG_CONSOLE_ENABLED", "true")

android {
    namespace = "com.pulse.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pulse.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        vectorDrawables { useSupportLibrary = true }

        buildConfigField("String", "HOTFY_API_KEY", "\"$hotfyApiKey\"")
        buildConfigField("boolean", "DEBUG_CONSOLE_ENABLED", debugConsoleEnabled)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // java.time is used with minSdk 24 → desugar the JDK library.
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    // Hotfy SDK (monetização: ads + CDP + push).
    implementation(libs.hotfy.sdk)
    // Firebase Cloud Messaging — fornece o push token nativo que o SDK sincroniza.
    // Para o FCM ENTREGAR tokens/mensagens, adicione o plugin `com.google.gms.google-services`
    // + o `app/google-services.json` do seu projeto Firebase (ver PUSH_SETUP.md). Sem isso o
    // código compila e o app roda; o token só não é obtido (setNativeToken vira no-op silencioso).
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    // Necessário só pra usar native ads direto (o SDK expõe o tipo NativeAd do GMA).
    implementation(libs.play.services.ads)
    // Debug console flutuante (logs/network/segment/actions) — gateado em debug/QA.
    implementation(libs.hotfy.debug.console)
    // Pretty-print do WrapperConfig (@Serializable) na action de debug.
    implementation(libs.kotlinx.serialization.json)
    // Lê o Install Referrer pra flag `debug=1` (ligar o console em prod sem rebuild).
    implementation(libs.install.referrer)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    debugImplementation(libs.androidx.ui.tooling)
}
