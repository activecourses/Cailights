# Cailights

Connecting candidates to recruiters

## Features

1. [x] Splash Screen
2. [x] simple login/sign up pages

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: [Material Design 3](https://m3.material.io/)
- **Build System**: Gradle (Kotlin DSL)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36

## Project Structure

```text
app/src/main/java/com/example/cailights/
├── ui/
│   ├── theme/          # Material 3 Theme configurations
│   ├── SignInScreen.kt # Multi-state Sign In UI and logic
│   ├── SignUpScreen.kt # Registration form with validation and verification
│   └── SplashScreen.kt # Animated entry screen
└── MainActivity.kt      # Main entry point managing navigation state
```

## Getting Started

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   ```
2. **Open in Android Studio**:
   Use the latest version of Android Studio (Ladybug or newer recommended).
3. **Sync Gradle**:
   Ensure all dependencies are downloaded.
4. **Run the app**:
   Deploy to an emulator or physical device running Android 7.0 (API 24) or higher.
