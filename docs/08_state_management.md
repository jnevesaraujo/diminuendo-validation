# 08 — State Management

> StateFlow + immutable UiState per screen. One-shot events separated from state.

## UI state pattern

Each screen has a sealed/immutable `UiState`. Here is the architectural layout representing our primary **Heatmap (Home)** state:

```kotlin
data class HeatmapUiState(
    val isLoading: Boolean = false,
    val noiseZones: List<NoiseZone> = emptyList(),
    val selectedZoneDetails: NoiseZone? = null,
    val isOffline: Boolean = false,
    val isPremium: Boolean = false,
    val error: String? = null,
)
```

- ViewModel Exposure: ViewModels encapsulate mutable states internally via private `MutableStateFlow` structures, exposing them safely to the Compose UI layer as a read-only `val uiState: StateFlow<XxxUiState>`.
- One-shot Events: Transient execution scenarios (such as navigating to the `PaywallScreen` or rendering temporary system error message Snackbars) are distributed through an asynchronous Kotlin `Channel` or `SharedFlow`, ensuring they are processed exactly once and never baked directly into the persistent `UiState`.
- State-Driven UI: Composable screens act as a direct, pure mathematical function of the current state slice: `HeatmapScreen(state = uiState.collectAsStateWithLifecycle().value, onAction = { viewModel.handleAction(it) })`.

## State sources

| State | Source of truth | Observed by |
|---|---|---|
| Exposure Diary History | Room (Flow stream query) | `DiaryViewModel` |
| Connectivity / Offline Banner | Network ConnectivityObserver | All active ViewModels |
| Subscription Status (Premium) | Jetpack DataStore preferences file | `SubscriptionUseCase` → `HeatmapViewModel` / `AiViewModel` |
| User Identity Session | Firebase Auth State Listener | `AuthViewModel` |
| Global Acoustic Heatmap Data | Cloud Firestore Real-time Snapshots | Data Layer Synchronization Pipeline → Room Cache |

## State sharing between users

- Remote State Delivery: Global community environmental decibel data points reach individual mobile clients via active Firebase Firestore Realtime Snapshot Listeners. These network listeners observe specific geo-bounding box updates surrounding coordinates without manual polling cycles.
- SSOT Alignment Rule: Remote data streams never feed the user interface components directly. When a real-time Firestore document delta occurs, the synchronization system writes the incoming DTO dataset immediately into the local SQLite database via Room DAO abstractions. The `HeatmapViewModel` continues to observe the local Room table stream exclusively. This mechanism ensures Room remains the absolute Single Source of Truth (SSOT) while the UI updates smoothly in response to database adjustments.

## Concurrency

- Scoped Threading: Asynchronous logic loops are strictly confined within the `viewModelScope` lifecycle context. To guarantee proper automated Unit Testing execution, coroutine background workers utilize constructor-injected thread dispatchers (`CoroutineDispatchers` abstraction wrapping `Dispatchers.IO`).
- Resource Optimization: Cold domain repository flows are transformed efficiently into lifecycle-aware hot UI state flows inside the ViewModel utilizing:
```
val uiState: StateFlow<HeatmapUiState> = repository.getNoiseZones()
    .map { zones -> HeatmapUiState(noiseZones = zones) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HeatmapUiState(isLoading = true)
    )
```
The `WhileSubscribed(5000)` configuration window cleanly keeps data pipelines active across orientation modifications while preventing empty background compute utilization when screens become hidden.

## Offline in state

- Reactive Offline Flags: The boolean `isOffline` component property inside each screen's state is derived directly from a continuous global system network pipeline observer.
- Degraded UI Execution: When a network disconnect state occurs, the user interface layers render a top-aligned, non-intrusive notification warning bar. For write operations occurring while offline (such as logging an environment decibel snapshot inside the diary), the local `MeasurementEntity` transaction row is persistently saved to Room with a `pendingSync = true` status attribute flag. This configuration prevents interface lockouts and triggers background upload attempts via `WorkManager` upon internet connection recovery.
