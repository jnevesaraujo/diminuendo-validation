# 08 — State Management

> StateFlow + immutable UiState per screen. One-shot events separated from state.

## UI state pattern

Each screen has a sealed/immutable `UiState`:

```kotlin
data class XxxUiState(
    val isLoading: Boolean = false,
    val items: List<Xxx> = emptyList(),
    val isOffline: Boolean = false,
    val isPremium: Boolean = false,
    val error: String? = null,
)
```

- ViewModel exposes `val uiState: StateFlow<XxxUiState>`.
- One-shot events (navigate, snackbar) via `Channel`/`SharedFlow`, **not** in the UiState.
- The Composable is a function of the state: `XxxScreen(state, onAction)`.

## State sources

| State | Source of truth | Observed by |
|---|---|---|
| Data list | Room (Flow) | HomeViewModel |
| Connectivity / offline | ConnectivityObserver | ViewModels |
| Subscription (premium) | DataStore | EntitlementUseCase → ViewModels |
| Session / user | <backend/DataStore> | |

## State sharing between users

- How the remote state reaches the app (push / polling / realtime): <...>
- How it is reflected in the `UiState` without violating the SSOT (Room): <...>

## Concurrency

- `viewModelScope` + injected dispatchers (testable).
- `stateIn(SharingStarted.WhileSubscribed(5000))` for derived flows.

## Offline in state

- `isOffline` derived from the connectivity observer.
- The UI shows a banner/degraded state; write actions become `pendingSync`.
