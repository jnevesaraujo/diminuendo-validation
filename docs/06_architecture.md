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
com.example.app
├── di/                 # Hilt (modules) — or Koin
├── data/
│   ├── local/          # Room (DAO, Entities), DataStore
│   ├── remote/         # Retrofit/Ktor (API, DTOs)
│   ├── repository/     # implementations
│   └── mapper/         # DTO/Entity <-> domain
├── domain/
│   ├── model/
│   ├── repository/     # interfaces
│   └── usecase/
└── ui/
    ├── navigation/
    ├── theme/
    ├── components/
    └── feature/<x>/    # Screen + ViewModel + UiState
```

## Dependency injection

- Choice: **Hilt** _or_ **Koin** → record in an ADR (see `docs/adr/0001-di-hilt-vs-koin.md`).
- The skeleton in `app/` uses **Hilt** as an example.

## Networking

- Choice: **Retrofit** _or_ **Ktor** → ADR.
- The skeleton uses **Retrofit + OkHttp + kotlinx.serialization**.

## Offline strategy (mandatory)

- **Single Source of Truth = Room.** The UI observes the database, not the network.
- Repository: network → updates Room → UI reacts. No network → serves Room.
- Offline writes: queue / `pendingSync` flag; syncs when the connection is restored.
- Model detail: `docs/07_data_model.md`. State: `docs/08_state_management.md`.

## State sharing between users (mandatory)

- Backend / service: <Firebase / Supabase / own API / ...> → describe in `docs/09`.
- Synchronization strategy and conflict resolution: <...>

## Freemium (mandatory)

- Subscription state (simulated) stored in **DataStore** (`isPremium`).
- Centralized check in a UseCase (`CheckEntitlementUseCase`), not spread across the UI.

## Environment

- Android Studio: <version> · JDK 17 · minSdk <xx> · targetSdk <xx> · Kotlin <version>
