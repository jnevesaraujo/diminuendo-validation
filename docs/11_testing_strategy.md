# 11 — Testing Strategy

> "Tests pass" is part of the Definition of Done. CI runs `./gradlew test`.

## Pyramid

| Level | Target | Tools |
|---|---|---|
| Unit (majority) | UseCases, ViewModels, Mappers, Repository (with fakes) | JUnit, kotlinx-coroutines-test, Turbine, MockK |
| Integration | Room DAO, DTO serialization | Room in-memory, JUnit |
| UI (some) | Critical Compose flows | Compose UI Test |

## What must be tested

- [ ] **Freemium** logic (free vs premium) — `EntitlementUseCase`
- [ ] **Offline** behavior (Repository serves Room without network)
- [ ] DTO ↔ Entity ↔ domain mappings
- [ ] At least one **ViewModel** (loading/content/error states)
- [ ] Synchronization / state sharing (isolated testable logic)

## Conventions

- Name: `method_condition_expectedResult`.
- Injected dispatchers; `MainDispatcherRule` for ViewModels.
- Fakes preferred over mocks for Repositories.

## Example (included in the skeleton)

See `app/src/test/.../SampleViewModelTest.kt` and `.../GetSampleItemsUseCaseTest.kt`.

## Coverage

- Focus on domain logic, not blind %. Indicate here what is covered and what is missing.
