# 12 — Implementation Plan

> Incremental. Each increment leaves `main` buildable and testable.

## Milestones

| Milestone | Content | Acceptance criterion | Status |
|---|---|---|---|
| **M0** | Project initialization, Hilt DI setup, architecture package skeleton creation, and CI pipeline green verification. | Gradle `assembleDebug` runs cleanly and automated CI assertions pass. | ☑ |
| **M1** | Jetpack Compose NavHost setup, shell bottom-bar configuration, and basic Capture Screen local Room logging framework. | US-01: Navigating across placeholder screens works smoothly; mock database logs display properly. | ☑ |
| **M2** | Firebase Auth configuration and remote Google AI (Gemini Pro/Flash) model integration via Vertex AI SDK. | RF-03 / US-06: A context log array sent to the model returns formatted auditory health suggestions. | ☑ |
| **M3** | Local-First persistence deployment using Room as the Single Source of Truth (SSOT) and offline cache. | RF-06 / US-04: The app records measurements while disconnected and pushes them via WorkManager on reconnection. | ☑ |
| **M4** | Multi-user real-time state sharing integration using Firebase Cloud Firestore snapshot listeners. | RF-02 / US-02: A measurement logged by device A appears dynamically as a heat gradient on device B's map. | ☑ |
| **M5** | Simulated subscription entitlement manager layer setup via Jetpack DataStore parameters. | RF-05 / US-03: Interacting with restricted charts triggers the paywall; simulating a purchase unlocks all features. | ☑ |
| **M6** | Hardware Audio pipeline activation to sample mic amplitude input and calculate live decibel metrics. | RF-04 / US-05: Real-time sound variations dynamically update the primary UI gauge and canvas waveform. | ☑ |
| **M7** | System-wide code optimization, unit test validation coverage completion, accessibility sweeps, and final documentation. | Application matches Global Definition of Done (DoD) benchmarks. | ☑ |

---

## Prioritized backlog

1. **Task-01:** Create an Android Studio Empty Compose Activity project, establish the package architecture directory layout, and implement structural Hilt modules.
2. **Task-02:** Declare the type-safe navigation graph parameters, implement the bottom-bar navigation view, and deploy theme variations (Dark/Light).
3. **Task-03:** Set up the local Room persistence layer (Entities, DAOs, Database instance wrappers) to support core data tracking logs.
4. **Task-04:** Wire up the Android `AudioRecord` / `MediaRecorder` frameworks to capture ambient sound pressures and convert them into live mathematical decibel integers.
5. **Task-05:** Deploy the custom layout `Canvas` metrics gauge and live waveform animation layers inside the primary Capture tab interface.
6. **Task-06:** Link Firebase Auth pipelines (Email/Alternative Matrix) and map out Cloud Firestore global structural schema constraints.
7. **Task-07:** Integrate Google Maps Android SDK and implement the real-time Geohash snapshot listeners to render community heat grids.
8. **Task-08:** Integrate the Vertex AI for Firebase SDK, build the `AiViewModel` message timelines, and bind localized context query injection parameters.
9. **Task-09:** Build the simulated Freemium architecture tier using a Jetpack DataStore configurations manager and overlay paywall ui blockers.
10. **Task-10:** Configure background syncing routines utilizing Android `WorkManager` routines to process records flagged with `pendingSync`.
11. **Task-11:** Finalize all unit testing matrices (ViewModels, UseCases, Mappers), run accessibility contrast inspections, and export the functional APK build.

---

## Risks

| Risk | Impact | Mitigation |
|---|---|---|
| **Android background processing limitations** (System throttling background jobs or worker queues). | Medium | Utilize the native Android `WorkManager` API architecture bundled with strict expedited constraints to safely schedule data synchronization tasks. |
| **Microphone hardware reading variance** (Different smartphone models yielding inconsistent decibel calculations). | Medium | Implement an architectural baseline calibration multiplier offset factor within the data repository layer to normalize values cleanly across standard devices. |
| **Firebase Firestore daily usage quota exhaustion** during multi-client active synchronization cycles. | High | Group localized coordinates using a structured Geohash index grid system to update aggregate records, reducing direct cloud write footprints. |
| **User runtime sensor permission denial** (Rejecting continuous audio or location monitoring handlers). | High | Build a robust UI rationale alert panel detailing that no audio recordings are saved, providing an explicit link button to restore sensor access via system settings. |

---

## Work distribution

| Person | Responsibility |
|---|---|
| **Lead Developer** *(Student)* | End-to-end local software construction, data schema mappings, multi-user Firestore coordination, Gemini configuration, and interface execution. |