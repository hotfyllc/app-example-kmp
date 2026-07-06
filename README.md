# Pulse — Android

A native Android port of the **Pulse** workout-tracking prototype, built with
Kotlin + Jetpack Compose (Material 3). Dark theme only, no backend — all exercise
data is mocked in code and user progress is persisted with **DataStore
(Preferences)**.

## Requirements / setup

- **JDK 17** (the project targets JVM 17).
- **Android Studio** Ladybug (2024.2) or newer, with the **Android SDK 35** platform
  installed. `minSdk` is 24, `targetSdk`/`compileSdk` are 35.
- Fonts (Space Grotesk, Manrope) are bundled as variable fonts under
  `app/src/main/res/font/` — no runtime download needed.

Open in Android Studio via **File → Open** and select this folder; Gradle sync will
pull the dependencies declared in `gradle/libs.versions.toml`. Create a
`local.properties` with `sdk.dir=/path/to/Android/sdk` if Studio doesn't generate one.

## Run & build (plain Gradle — mirrors the React Native flow)

| Purpose | React Native equivalent | Command |
| --- | --- | --- |
| Run on emulator/device | `npm run android` | `./gradlew installDebug` (or the **Run ▶** button in Android Studio) |
| Preview/debug APK | debug build | `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk` |
| Release APK | release build | `./gradlew assembleRelease` |
| Play Store bundle | the `.aab` step | `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab` |

> `installDebug` requires a running emulator or a connected device with USB debugging.

## Architecture

Clean, layered MVVM with unidirectional data flow:

```
UI (Compose)  →  PulseViewModel (StateFlow)  →  WorkoutRepository  →  DataStore + mock data
```

- **`ui/theme/`** — design tokens (`Color`, `Type`, `Dimens`) and `Theme`, extracted
  verbatim from the prototype so no magic values live in screens.
- **`ui/onboarding` · `ui/goals` · `ui/workout` · `ui/progress`** — one screen composable
  per feature, stateless and driven by an immutable `PulseUiState` + event lambdas.
- **`ui/components/`** — reusable composables: `ProgressRing`, `WeekBarChart`,
  `ExerciseRow`, `GoalCard`, `StatCard`, `PrimaryButton`, `BottomNavBar`, `StepDots`.
- **`domain/model/`** — `Goal`, `Exercise`, `UserProgress`.
- **`data/`** — `MockExercises` (the three goals × 6 exercises), `PreferencesStore`
  (DataStore I/O), `WorkoutRepository` (mock data + persistence + daily-reset rule).
- **`viewmodel/`** — `PulseViewModel` exposes UI state via `StateFlow` and owns all
  business rules (toggling, the once-per-day +50 bonus/streak, weekly history).

Navigation is a single-activity `NavHost` with four routes
(`onboarding`, `goals`, `today`, `progress`); the bottom bar switches the three peer
tabs while preserving their state. State survives rotation via the ViewModel and is
persisted across launches by DataStore, which also drives the daily progress reset.
