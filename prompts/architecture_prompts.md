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

<!-- new entries below -->
