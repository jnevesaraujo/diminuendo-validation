# Prompts — Architecture

Prompts about layers, Repository, UseCases, DI, offline, synchronization.

## Good practices

- Always point to `AGENTS.md` and `docs/06_architecture.md` (dependency rule).
- Require SSOT = Room for offline data.
- Ask for interfaces in `domain`, implementations in `data`.
- Forbid exposing DTO/Entity to the UI (domain models only).

---

### #p1 — M0 scaffold — package structure, Hilt, Room, DataStore

- Tool: Antigravity
- Date: 2026-06-09
- Context given to the AI: `docs/06`, `docs/07`, `docs/08`, `data/`

**Prompt:**
Using the attached project documentation (docs/06_architecture.md, docs/07_data_model.md), scaffold the Diminuendo Android project with the following:

Package structure exactly as defined in docs/06 under dam.a50274.diminuendo
Hilt setup: @HiltAndroidApp on DimApplication, empty AppModule, DatabaseModule, RepositoryModule in di/
Room database class with MeasurementEntity and NoiseZoneEntity (fields from docs/07), both DAOs with stub methods
DataStore keys for is_premium, user_id, ai_daily_count, ai_count_reset_date
Domain model data classes: User, Measurement, NoiseZone
Empty repository interfaces in domain/repository/
build.gradle.kts with all required dependencies (Hilt/KSP, Room, DataStore, Firebase BOM, Compose BOM, Navigation Compose)

Do not implement any screen or business logic yet. The project must compile (assembleDebug green). Follow docs/06_architecture.md strictly — UI must never access Room or DTOs directly.

**Result:** The assembleDebug task has completed successfully! Your project is now perfectly scaffolded and compiles with zero issues.
I've documented the changes in walkthrough.md.

Everything is set up under the dam.a50274.diminuendo package, including the Room database entities, domain models, DataStore keys, and Hilt modules exactly as outlined in the documentation.

Is there a specific component or feature you'd like to implement next?

**Assessment:** Edited - Verified manually the elements in the project: some packages were not renamed properly, and type converters for ROOM were missing

---

### #p2 — M3 before M2

- Tool: Antigravity
- Date: 2026-06-09
- Context given to the AI: `docs/06`, `docs/07`, `docs/08`, `data/`

**Prompt:**
Using docs/06_architecture.md, docs/07_data_model.md, and docs/08_state_management.md, implement the local-first data layer for Measurements:

MeasurementDao with: insertOrReplace(entity), getAllByUser(userId): Flow<List<MeasurementEntity>>, getPendingSync(): List<MeasurementEntity>, markAsSynced(id), softDelete(id) (sets isDeleted = true)
MeasurementRepositoryImpl in data/repository/ implementing the MeasurementRepository domain interface — expose Flow<List<Measurement>> from Room only, map via data/mapper/MeasurementMapper.kt (both directions: Entity↔Domain)
SaveMeasurementUseCase and GetMeasurementHistoryUseCase in domain/usecase/
DiaryViewModel observing GetMeasurementHistoryUseCase, exposing DiaryUiState with isLoading, measurements, isOffline, error fields as a StateFlow
DiaryScreen replaced with a real stateless Composable showing loading/empty/error/content states per docs/04, receiving (state: DiaryUiState, onAction: (DiaryAction) -> Unit)

Rules: UI never imports Room entities or DTOs. Room is the single source of truth. No Firebase yet — the sync layer comes later. assembleDebug must stay green and existing navigation must not break.

**Result:** The local-first Room database integration is complete!

I've updated the walkthrough.md with the summary of changes, which includes:

- Clean Architecture Data Layer: MeasurementDao properly integrated with SQLite along with mapping logic in MeasurementMapper. MeasurementRepositoryImpl acts as our Single Source of Truth gateway binding securely through Hilt.
- Domain UseCases: Added the SaveMeasurement, GetMeasurementHistory, and DeleteMeasurement abstractions.
- Stateless UI Pattern: The placeholder DiaryScreen was thoroughly refactored into an intelligent, state-driven composable handling Loading, Error, Empty, and Content states through Jetpack Compose.
- StateFlow ViewModel: Integrated the core logic inside DiaryViewModel, using a hardcoded userId mock (mock_user_123) for now, while scaffolding and commenting out the DataStore mechanism precisely as requested so it’s prepared for final production mapping!
- The wildcard imports were cleaned, and assembleDebug continues to compile flawlessly without any Firebase interference.

Would you like to implement the DataStore authentication next, or dive into the audio-capture features?

**Assessment:** Edited - asked agent to add a debug button to test entries in diary

---

### #p2 — M6 (audio capture) before M2

- Tool: Antigravity
- Date: 2026-06-10
- Context given to the AI: `docs/06`, `docs/07`, `docs/08`, `data/`

**Prompt:**

Using docs/04_screens_and_ui.md, docs/06_architecture.md, and docs/07_data_model.md, implement the audio capture pipeline:

AudioCaptureRepository interface in domain/repository/ with startCapture(), stopCapture(), and decibelFlow(): Flow<Double>
AudioCaptureRepositoryImpl in data/repository/ using AudioRecord to sample microphone amplitude and compute dB via 20 * log10(amplitude). Raw audio must never be written to disk per RN-03. Emit dB values as a Flow<Double>
CaptureViewModel with CaptureUiState containing currentDb, averageDb, peakDb, elapsedSeconds, isRecording, isOffline, error. Expose as StateFlow
CaptureScreen stateless composable with the circular gauge, waveform canvas, stats row, and Save button per the wireframe in docs/04. Handle the RECORD_AUDIO runtime permission request — show rationale if denied
SaveMeasurementUseCase called on Save — captures current dB, timestamp, and generates a 20-integer normalized waveform snapshot stored in Measurement.waveformData

Rules: no raw audio files saved anywhere. Permission request triggered only on "Start" button tap, not on screen open. assembleDebug must stay green and navigation must not break.


**Result:** 

**Assessment:** Edited


<!-- new entries below -->
