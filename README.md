# MyDiscsCollection

Android application built with Jetpack Compose to search for artists on Discogs, view artist details, and explore their discography with year, genre, and label filters.

## Table of Contents

1. [Objective](#objective)
2. [Main Features](#main-features)
3. [Design Reference](#design-reference)
4. [Tech Stack](#tech-stack)
5. [Architecture](#architecture)
6. [Project Structure](#project-structure)
7. [Project Setup](#project-setup)
8. [Running the App](#running-the-app)
9. [Unit Tests](#unit-tests)
10. [UI Testing with Maestro](#ui-testing-with-maestro)
11. [Analysis and Development Process](#analysis-and-development-process)
12. [Best Practices, Clean Code, and Patterns](#best-practices-clean-code-and-patterns)
13. [Technical Decisions and Trade-offs](#technical-decisions-and-trade-offs)
14. [Technical Debt and Recommended Improvements](#technical-debt-and-recommended-improvements)
15. [Troubleshooting](#troubleshooting)

## Objective

Build a music catalog application focused on:

- Artist search with pagination.
- Artist detail screen.
- Discography screen with combinable filters.
- Clear UI states: `Loading`, `Success`, `Empty`, `Error`.

## Main Features

- Reactive search with debounce in `SearchViewModel`.
- Artist list with incremental pagination (infinite scroll).
- Detail screen with biography, image, and band members.
- Discography screen with year, genre, and label filters.
- Albums sorted from newest to oldest using the exact release date when Discogs provides it.
- Reusable skeleton loading and feedback states.

## Design Reference

- Public Figma challenge file: [View design in Figma](https://www.figma.com/design/3hh2ALPGzZeYEVhoedCFR1/Sin-t%C3%ADtulo?node-id=0-1&t=yzQp7trrIzXrSW89-1)

## Tech Stack

- **Language**: Kotlin `2.2.21`
- **UI**: Jetpack Compose + Material 3
- **UI Architecture**: MVVM
- **DI**: Hilt (`@HiltViewModel`, `@AndroidEntryPoint`, modules with `@InstallIn(SingletonComponent::class)`)
- **Networking**: Retrofit `2.11.0` + OkHttp `4.12.0`
- **Serialization**: Moshi `1.15.2` + `converter-moshi`
- **Images**: Coil 3 (`coil-compose`, `coil-network-okhttp`)
- **Concurrency**: Kotlin Coroutines + `StateFlow`
- **Navigation**: Navigation Compose `2.9.7`
- **Testing**: JUnit4, MockK, Turbine, `kotlinx-coroutines-test`

Relevant project versions:

- Android Gradle Plugin: `8.13.2`
- Gradle Wrapper: `8.13`
- `compileSdk` / `targetSdk`: `36`
- `minSdk`: `24`

## Architecture

The project follows a layered **Clean Architecture + MVVM** approach.

```mermaid
flowchart LR
    UI["presentation (Compose + ViewModel)"] --> UC["domain/usecase"]
    UC --> REPO_IF["domain/repository (contracts)"]
    REPO_IF --> REPO_IMPL["data/repository (implementation)"]
    REPO_IMPL --> API["data/remote (Retrofit API + DTO)"]
    API --> MAPPER["data/remote/mapper"]
    MAPPER --> DOMAIN_MODEL["domain/model"]
```

### Layers

- **presentation**
  - Compose screens (`SearchScreen`, `ArtistDetailScreen`, `DiscographyScreen`)
  - One `ViewModel` per feature
  - Sealed `UiState` models to represent screen state
- **domain**
  - Business entities (`Artist`, `ArtistDetail`, `Release`)
  - Use cases (`SearchArtistsUseCase`, `GetArtistDetailUseCase`, `GetArtistReleasesUseCase`)
  - Repository contract (`ArtistRepository`)
- **data**
  - API client (`DiscogsApiService`)
  - DTOs
  - DTO -> domain mappers (`ArtistMapper`)
  - Repository implementation (`ArtistRepositoryImpl`)

### Architecture Rationale

- Keeping UI decoupled from Retrofit and DTOs makes testing and provider changes easier.
- Explicit use cases centralize business logic by feature.
- The repository pattern abstracts the data source and improves maintainability.
- `StateFlow` + sealed `UiState` models help avoid ambiguous screen states in Compose.

## Project Structure

```text
app/src/main/java/com/example/mydiscscollection
├── data
│   ├── remote
│   │   ├── dto
│   │   ├── mapper
│   │   └── DiscogsApiService.kt
│   └── repository
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── di
├── navigation
├── presentation
│   ├── search
│   ├── detail
│   ├── discography
│   └── components
└── ui/theme
```

## Project Setup

### Requirements

- Android Studio (latest stable version recommended)
- JDK 17 to run Gradle / AGP 8.x
- Android SDK 36
- Emulator or device running Android 7.0+ (API 24+)

### Installation

```bash
git clone <REPOSITORY_URL>
cd MyDiscsCollection
```

Open the project in Android Studio and sync Gradle.

### Discogs API Setup

Current repository state:

- Discogs credentials are currently defined in:
  - `app/src/main/java/com/example/mydiscscollection/di/AppModule.kt`

To configure your own credentials:

1. Create your app in Discogs and obtain a `consumer key` and `consumer secret`.
2. Replace the constants in the DI module with your values.
3. Hardcoded keys for examples and practical tests

Security recommendation (not yet implemented in code):

- Move secrets to `local.properties` + `buildConfigField`.
- Read them from `BuildConfig` instead of hardcoding them.

## Running the App

### Build debug

```bash
./gradlew assembleDebug
```

### Install on emulator / device

```bash
./gradlew installDebug
```

### Run from Android Studio

1. Select the `app` run configuration.
2. Choose a device.
3. Click Run.

## Unit Tests

Run the module unit tests:

```bash
./gradlew testDebugUnitTest
```

Current test coverage (by type):

- Use cases:
  - `SearchArtistsUseCaseTest`
  - `GetArtistReleasesUseCaseTest`
- Mappers:
  - `ArtistMapperTest`
  - `ArtistDetailUseCase` (DTO -> domain mapping test)

## UI Testing with Maestro

The repository includes a Maestro flow at:

- `MaestroTests/SearchFlow.yaml`

### Prerequisites

- Install the app in debug mode:

```bash
./gradlew installDebug
```

- Have Maestro CLI available on your machine.
- Have at least one Android emulator or physical device running.

To list available Android devices:

```bash
adb devices
```

### Open the Flow in Maestro Studio

To inspect or edit the flow visually in Maestro Studio:

```bash
maestro --device emulator-5554 studio
```

In Maestro Studio you can select the device from the top bar if you have multiple devices connected, open the project workspace, and run flow commands while visually inspecting the UI.

### Run the YAML on a Single Device

To execute the search flow on a specific device:

```bash
maestro --device emulator-5554 test MaestroTests/SearchFlow.yaml
```

If you want to store execution artifacts:

```bash
maestro --device emulator-5554 test \
  --format html \
  --output build/maestro/report-emulator-5554.html \
  --test-output-dir build/maestro/emulator-5554 \
  MaestroTests/SearchFlow.yaml
```

### Run the Same Flow on Multiple Devices

If you want to validate visual consistency across different screen sizes or Android versions, start multiple emulators and run the same flow against each one:

```bash
adb devices
maestro --device emulator-5554 test MaestroTests/SearchFlow.yaml
maestro --device emulator-5556 test MaestroTests/SearchFlow.yaml
maestro --device emulator-5558 test MaestroTests/SearchFlow.yaml
```

You can also execute the full folder in parallel if multiple devices are running:

```bash
maestro --shard-all 3 test MaestroTests/
```

If you want to explicitly control which devices are used:

```bash
maestro --device "emulator-5554,emulator-5556,emulator-5558" --shard-all 3 test MaestroTests/
```

### What to Validate for Design Consistency

- Search empty state.
- `SearchBar` spacing and alignment.
- Long text truncation in artist and album rows.
- Artist detail behavior and `View Albums` CTA.
- Filter bottom sheet presentation.
- Visual consistency of cards, images, and typography across small, medium, and large screens.

### Suggested Local Matrix

A reasonable local validation matrix for design consistency is to run the same flow on at least:

- One compact phone emulator.
- One medium-size phone emulator.
- One large screen or tablet emulator.
- More than one Android version if you want to catch rendering or system behavior differences.

## Analysis and Development Process

The process started with creating a Discogs account and analyzing the available services to determine which endpoints and DTOs were required to satisfy the challenge requirements. From there, I first built an MVP in Figma to define the general layout, components, navigation, states, and screen flow. After that, I made a second design pass to approximate the final styles, visual hierarchy, and screen appearance.

Once the design and app structure were clearer, I moved into implementation. I first defined the app stack and dependencies, trying to balance practicality, maintainability, and the time available for the exercise. After that baseline was set, I implemented the project in this order: reusable components, networking client, endpoint definitions, DTOs, mappers, ViewModels, use cases, and finally the screens. That order helped build the flow incrementally and validate each layer before wiring the next one.

I then moved into a manual testing phase to verify that the application was meeting the functional requirements. During that review, I found several issues, especially related to repeated IDs in artist and release lists, so I had to adjust how dynamic keys were built in Compose lists. Later, I noticed that discography ordering was based only on `year`, which did not fully satisfy the requirement. To fix that, I added support for the full `released` date and kept `year` as a fallback when the API does not expose an exact release date.

That change required consuming additional release metadata. To avoid unnecessary API calls, the app first validates the integrity of the available data and only requests metadata when a relevant field is missing from the main response, for example genre or the exact release date.

In a later technical review phase, I found that the project was not complying with the minimum platform requirement because it had originally been started with `minSdk 29` after overlooking that bullet in the challenge. During the adjustment to `minSdk 24`, I also found that a Wear dependency had been included by mistake, which forced the project to `minSdk 25`. The fix consisted of removing that library and adjusting two launcher resources to preserve compatibility with earlier SDK versions.

Finally, after stabilizing the functional flow and the technical adjustments, I added unit tests for several critical parts of the application to reinforce the reliability of the solution.

## Best Practices, Clean Code, and Patterns

Applied practices:

- Separation of responsibilities by layer.
- Dependency injection with Hilt.
- DTOs separated from domain models.
- Explicit mapping with `ArtistMapper`.
- Typed screen state with `sealed interface`.
- Error handling with `Result` + `onSuccess` / `onFailure`.
- Injected dispatcher (`@IoDispatcher`) for better testability.

Patterns used:

- **MVVM** in the presentation layer.
- **Repository Pattern** between domain and data.
- **Use Case Pattern** to orchestrate business actions.
- **Mapper Pattern** to transform remote models into domain models.

## Technical Decisions and Trade-offs

1. `Result<Triple<...>>` for pagination  
   Advantage: fast to implement.  
   Trade-off: less readable than a dedicated data class.

2. Additional metadata fetch for genre and exact release date  
   Advantage: improves completeness when `genre` is missing and allows album sorting by `released`, not only `year`.  
   Trade-off: more network calls and possible latency / rate-limit impact.

3. Compose + StateFlow  
   Advantage: reactive and predictable UI.  
   Trade-off: requires discipline to avoid expensive recompositions.

4. Hilt as the DI solution  
   Advantage: consistent and scalable dependency wiring.  
   Trade-off: more initial setup and slightly longer build times.
rl`.
- If it is still empty, the API may simply not have metadata for that release.
