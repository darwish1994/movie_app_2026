# Popular Movies App

A modern Android application that displays popular movies from The Movie Database (TMDB) API, built with Clean Architecture, MVI pattern, and Jetpack Compose.

## Architecture

The project follows **Clean Architecture** with **modularization by layer**:

```
┌─────────────────────────────────────────────┐
│                    App                       │
│  (Entry point, DI wiring, Application class) │
├─────────────┬───────────────┬───────────────┤
│ Presentation│     Data      │    Domain     │
│  (Compose,  │  (Retrofit,   │ (Use Cases,  │
│  ViewModels,│   DTOs,       │  Models,     │
│  Navigation)│   Repository  │  Repository  │
│             │   Impl)       │  Interfaces) │
└─────────────┴───────────────┴───────────────┘
```

### Modules

| Module         | Type            | Description                                                                 |
|----------------|-----------------|-----------------------------------------------------------------------------|
| `:domain`      | Kotlin (JVM)    | Pure business logic: models, repository interfaces, use cases. No Android dependencies. |
| `:data`        | Android Library | API communication (Retrofit), DTOs, mappers, repository implementations.    |
| `:presentation`| Android Library | Jetpack Compose UI, ViewModels with MVI pattern, navigation, animations.    |
| `:app`         | Android App     | Entry point, Hilt setup, API key configuration via BuildConfig.             |

### Dependency Rule

```
app → presentation, data, domain
presentation → domain
data → domain
domain → (no dependencies)
```

## MVI Pattern

Each screen follows **Model-View-Intent**:

- **State**: Immutable data class representing the current UI state
- **Intent**: Sealed interface of user actions (click, refresh, retry)
- **Effect**: One-time side effects (navigation events)

The ViewModel receives Intents, processes them, updates State (via `StateFlow`), and emits Effects (via `Channel`).

## Tech Stack

| Technology                | Purpose                          |
|---------------------------|----------------------------------|
| Kotlin                    | Programming language (100%)      |
| Jetpack Compose           | Declarative UI framework         |
| Hilt                      | Dependency injection             |
| Retrofit + OkHttp         | Network communication            |
| Kotlinx Serialization     | JSON parsing                     |
| Kotlin Coroutines & Flow  | Asynchronous programming         |
| Navigation Compose        | Screen navigation with animations|
| Coil                      | Image loading                    |
| MockK                     | Mocking for unit tests           |
| Turbine                   | Flow testing                     |

## Prerequisites

- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK with API 36 (compile) and API 24 (min)
- A TMDB API key ([sign up here](https://www.themoviedb.org/settings/api))

## Setup & Build

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd MovieApp
   ```

2. **Add your TMDB API key** to `local.properties`:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

3. **Build and run:**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the project in Android Studio and run the `app` configuration.

4. **Run unit tests:**
   ```bash
   ./gradlew test
   ```

## Screenshots

| Movie List | Movie Detail |
|:---:|:---:|
| ![Movie List](screenshots/Screenshot_20260227_231954.png) | ![Movie Detail](screenshots/Screenshot_20260227_232012.png) |

## Features

### Movie List Screen (Master)
- Scrollable list of popular movies from TMDB API
- Each item shows poster, title, release date, and rating
- Pull-to-refresh to reload data
- Shimmer loading animation (skeleton screen)
- Pagination (infinite scroll)
- Error state with retry option

### Movie Detail Screen (Detail)
- Backdrop and poster images
- Title, tagline, overview
- Release date, runtime, vote average, vote count
- Genre chips
- Smooth slide transition animations
- Back navigation
- Loading and error states

## Assumptions

- The app targets API 24+ (Android 7.0) as minimum SDK
- API key is stored in `local.properties` and injected via `BuildConfig` (not committed to version control)
- English (en-US) is used as the default language for API requests
- Network caching relies on OkHttp's default caching mechanism
- No local database caching (Room) was implemented as the assessment focused on API interaction

## Project Structure

```
├── app/                          # Main application module
│   ├── di/AppModule.kt           # Provides API key
│   ├── MovieApp.kt               # @HiltAndroidApp
│   └── MainActivity.kt           # @AndroidEntryPoint
├── domain/                       # Pure Kotlin module
│   ├── model/                    # Movie, MovieDetail, Genre
│   ├── repository/               # MovieRepository interface
│   └── usecase/                  # GetPopularMovies, GetMovieDetail
├── data/                         # Android library module
│   ├── remote/api/               # MovieApiService (Retrofit)
│   ├── remote/dto/               # MovieResponseDto, MovieDetailDto
│   ├── mapper/                   # DTO → Domain mappers
│   ├── repository/               # MovieRepositoryImpl
│   └── di/DataModule.kt          # Hilt module for network/repo
├── presentation/                 # Android library module
│   ├── movielist/                # MVI contract, ViewModel, Screen
│   ├── moviedetail/              # MVI contract, ViewModel, Screen
│   ├── components/               # MovieCard, ShimmerEffect, ErrorState
│   └── navigation/               # NavGraph with transitions
└── gradle/libs.versions.toml     # Version catalog
```
