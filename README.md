# Cailights

Connecting candidates to recruiters through a modern, mobile-first experience.

## Features

- **Animated Splash Screen**: Snappy entrance with persistent session checking.
- **Authentication**: Multi-step Sign In and Sign Up with real-time validation.
- **Session Persistence**: Stay logged in even after closing the app.
- **Home Feed**: Dynamic feed displaying posts from multiple simulated users.
- **MVI Architecture**: Robust state management following the project's Skills Index.

## Tech Stack

- **Kotlin** & **Jetpack Compose** (Material 3)
- **Koin** (Dependency Injection)
- **Coroutines & Flow**
- **Modern Time APIs** (ZonedDateTime with API desugaring)

## Project Structure

```text
app/src/main/java/com/example/cailights/
├── ui/         # MVI Screens, ViewModels, and Theme
├── domain/     # Business logic, Repository interfaces, and Models
├── data/       # Fake Repository implementations and Auth storage
├── di/         # Koin Dependency Injection modules
└── MainActivity.kt # Navigation orchestration
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
