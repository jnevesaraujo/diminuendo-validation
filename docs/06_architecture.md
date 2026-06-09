# 06 — Architecture

> **This document is binding.** The AI must follow it (see `AGENTS.md` §2).
> Non-trivial decisions → ADR in `docs/adr/`.

## Pattern

**MVVM + Repository Pattern**, separate layers:

```
ui (Compose, ViewModel, StateFlow)
        │  (only UI / domain models)
        ▼
domain (UseCases, models, Repository interfaces)   [optional but recommended]
        ▲
        │
data (Repository impl, Room, Retrofit/Ktor, DataStore, mappers)
```

Dependency rule: `ui → domain → data` through interfaces. **The UI never knows Retrofit/Room directly.**

## Package structure (follow this)

```
dam.a50274.diminuendo
├── di/                 # Hilt (modules) for dependency injection
├── data/
│   ├── local/          # Room (DAO, Entities), DataStore (Preferences)
│   ├── remote/         # Firebase SDK (Auth, Firestore, Vertex AI) wrappers
│   ├── repository/     # Repository implementation classes
│   └── mapper/         # DTO / Entity <-> Domain Model mappers
├── domain/
│   ├── model/          # Pure Kotlin domain data classes (User, Measurement)
│   ├── repository/     # Abstract repository interfaces
│   └── usecase/        # Single-responsibility business logic units
└── ui/
    ├── navigation/     # NavHost setup and type-safe routing definitions
    ├── theme/          # Color schemes (Neon Nocturne & Acoustic Clarity)
    ├── components/     # Reusable composables (GlassCard, AcousticButton)
    └── feature/<x>/    # Screen + ViewModel + UiState per feature block
```

## Dependency injection

- Choice: **Hilt** → record in an ADR (see `docs/adr/0001-di-hilt-vs-koin.md`).
- The application uses Hilt to inject repositories, use cases, and ViewModels cleanly across layers.

## Networking

- Choice: **Firebase Native Android SDKs** (Firestore, Auth, and Vertex AI for Firebase) act as our remote infrastructure gateway. Standard HTTP rest endpoints via Retrofit or Ktor are bypassed in favor of Firebase's real-time binary transport channel.

## Offline strategy (mandatory)

- **Single Source of Truth = Room**. The UI observes the local database streams, not direct network tasks.
- Repository flow: Firebase updates → patches Room storage local rows → UI reacts dynamically.
- Offline writes: When recording a sound metric offline, entries are flagged with a `pendingSync = true` state marker inside the Room table. An Android `WorkManager` routine routinely polls
- Model detail: `docs/07_data_model.md`. State: `docs/08_state_management.md`.

## State sharing between users (mandatory)

- Backend / service: **Firebase Cloud Firestore**.
- Synchronization strategy and conflict resolution: Individual geographic acoustic coordinates are pushed continuously to a shared global collection. To build the heatmap, active clients subscribe to real-time geospatial document bounds via snapshot listeners. Structural synchronization conflicts or duplicate submissions are automatically resolved via a strict **last-write-wins (timestamp-based)** pipeline.

## Freemium (mandatory)

- Subscription state (simulated) stored in **Jetpack DataStore** (`isPremium`).
- Access validation is controlled exclusively by `CheckEntitlementUseCase` inside the domain layer, making it impossible for the user interface components to bypass plan restrictions directly.

## Environment

- Android Studio: Ladybug (2024.2.1) or later
- JDK: 17
- minSdk: 26 (Android 8.0 - required for low-overhead audio processing APIs)
- targetSdk: 35 (Android 15)
- Kotlin: 2.0.0 (Jetpack Compose Compiler integrated)
