# 11 — Testing Strategy

> "Tests pass" is part of the Definition of Done. CI runs `./gradlew test`.

## Pyramid

| Level | Target | Tools |
|---|---|---|
| **Unit** (majority) | Domain UseCases, UI ViewModels, Data Layer Mappers, and Repository classes (isolated using local Fake implementations). | JUnit 4/5, `kotlinx-coroutines-test`, Turbine (for Flow mapping tracking), MockK. |
| **Integration** | Local SQLite Room DAOs, DataStore structural serialization schemas, and remote collection snapshot parsing logic. | Room in-memory database context configuration, AndroidX Test JUnit runners. |
| **UI** (some) | Critical, foundational Jetpack Compose state-conditional user layout flows (e.g., Paywall visibility checks). | Compose UI Test library framework, Semantic tree locators. |

---

## What must be tested

- [x] **Freemium logic (free vs premium):** Unit verification within `SubscriptionUseCase` to guarantee that user tier variables fetched from local DataStore storage elements accurately toggle features or trigger paywall navigation routes.
- [x] **Offline behavior:** Repository layer orchestration ensuring that if the connectivity observer signals a network blackout, structural read queries safely default to pulling data blocks from the Room persistence cache rather than triggering network timeouts.
- [x] **Data transformations:** Bidirectional execution assertions validating mapping layers (`MeasurementDto` ↔ `MeasurementEntity` ↔ `Measurement` domain) to verify that metadata (like Geohash IDs or timestamp longs) map correctly across borders without silent truncation.
- [x] **ViewModel architectural state changes:** Verification covering at least one primary functional view model state loop (e.g., `HeatmapViewModel`), checking that interactive execution routines smoothly transition the immutable `UiState` across `Loading`, `Content`, `Error`, and `Offline` configurations.
- [x] **Synchronization logic queue processing:** Validation tracking the local Room database offline operations queue to ensure rows marked with `pendingSync = true` are systematically processed and uploaded to the remote server once a network connection is verified.

---

## Conventions

- **Method naming scheme:** Technical specifications must strictly observe the structured format: `methodName_underSpecificCondition_returnsExpectedResult` (e.g., `evaluateAccess_userIsFree_triggersPaywallScreen`).
- **Thread model predictability:** ViewModels must rely on constructor-injected coroutine dispatchers. Unit test logic configuration routines must incorporate an active `MainDispatcherRule` to cleanly swap target background operations onto a test scheduler.
- **Data abstraction layer handling:** To avoid fragile mocking chains, the test system prioritizes lightweight **Fakes** over complex MockK mocks when isolating interface-driven layers like data repositories.

---

## Example (included in the skeleton)

See the boilerplate examples provided in the architecture root directory paths:
- `app/src/test/java/dam/a50274/diminuendo/ui/feature/heatmap/HeatmapViewModelTest.kt`
- `app/src/test/java/dam/a50274/diminuendo/domain/usecase/SubscriptionUseCaseTest.kt`

---

## Coverage

- **Targeted Scope:** Testing strategies are targeted to protect critical custom algorithmic transformations and state routing flows rather than pursuing blind code line coverage percentages.
- **Covered Elements:** 100% of the core Domain logic components (`UseCases`), Data mapping utilities (`Mappers`), and state handling operations (`ViewModels`).
- **Omitted Elements (Out of Scope):** Device-dependent platform integrations, third-party low-level libraries, and platform-specific mapping APIs (Google Maps SDK view containers) are mocked or entirely bypassed during local automated testing execution tasks.