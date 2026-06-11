# Prompts — Debugging

Prompts to diagnose errors. Include the **exact error/stacktrace** in the prompt.

## Good practices

- Paste the full error message / stacktrace and the relevant code.
- State what you have already tried and the expected vs observed behavior.
- Ask for the **root cause**, not just the patch. Be suspicious of "fixes" that silence the symptom.
- Always verify the explanation — the AI may hallucinate plausible but wrong causes
  (record it in `docs/14_ai_usage_log.md`).

---

### #p1 — Debugging screen navigation

- Tool: Antigravity
- Date: 2026-06-9

**Prompt:**
Add a temporary "Skip Auth (Debug)" TextButton to the Auth placeholder screen that navigates to the heatmap route. Mark it with a // TODO: remove before final build comment. Do not touch any other file.

The deep link diminuendo://capture fails with "unable to resolve Intent". The NavGraph may have the deep link declared but the AndroidManifest.xml is missing the corresponding <intent-filter> on the MainActivity. Add the intent filter to the manifest. Do not change any other file. Verify the adb command adb shell am start -W -a android.intent.action.VIEW -d "diminuendo://capture" dam.a50274.diminuendo resolves correctly after the fix.

**Identified cause:** Placeholder auth screen doesn't allow navigation to test remaining ones and deep link is failing.
**Fix:** The assembleDebug background task just finished, and the build remains completely green! The new Android manifest intent filter and the Debug button are working perfectly without causing any compilation issues.

Everything is in place. You should now be able to run that adb shell am start command in your local environment and see it immediately route to the empty Capture screen. Let me know what you'd like to work on next!
**Did the AI get the cause right?** yes

---

### #p2 — Debugging M2 Firebase auth

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**
Please address the following errors:

on AuthRepositoryImpl.kt the function signOut() doesn't also clear DataStore user_id

Please refactor AuthViewModel.kt to address the following issues caught during verification:

1. Navigation must be a one-shot event. Remove "isAuthenticated" from the AuthUiState and implement a Channel or SharedFlow (e.g., _navigationEvent) to handle the one-shot navigation to the heatmap screen upon successful authentication.
2. Do not expose raw Firebase exception messages. In the catch block, parse the Exception (or check FirebaseAuth exceptions) and map them to user-friendly error strings instead of directly using e.message.

**Identified cause:** Missing some elements in repository and viewmodel
**Fix:** Everything is compiling and the build is green (assembleDebug finished successfully). Let me know what you would like to work on next!	
**Did the AI get the cause right?** yes

---

### #p3 — Debugging M4 Firestore heatmap 1

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**

Please refactor MeasurementRepositoryImpl.kt to comply with the remaining verification rules:

1. Implement an explicit offline check (using a NetworkMonitor or ConnectivityManager) to gate the Firestore write. If the device is offline, it should cleanly skip the Firestore upload block immediately and keep pendingSync = true in Room without waiting for a timeout.
2. The current catch block ignores the exception. We need to handle FirebaseFirestoreException properly. Since this is a Repository, modify the catch block to throw a custom domain exception or pass a result so that the ViewModel can catch it and map it to a user-friendly UiState.error string (instead of silently ignoring it or re-throwing raw Firebase exceptions).

**Identified cause:** Agent didn't implement doc requirements
**Fix:** I have refactored the files as requested. The verification rules have now been implemented. I ran another assembleDebug build and the changes compiled perfectly. Let me know if we are clear to proceed with the next step!
**Did the AI get the cause right?** yes

---

### #p4 — Debugging M4 Firestore heatmap 2

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**

Please refactor NoiseZoneRepositoryImpl.kt to comply with the third verification rule:

1. Remove the Firebase snapshot listener from the init {} block. It causes a memory leak because it is never closed.
2. Implement a dedicated private or internal function (or integrate it directly into the Flow pipeline) using callbackFlow { ... } to listen to the "noise_zones" collection.
3. Store the ListenerRegistration returned by addSnapshotListener, and inside the awaitClose block of the callbackFlow, explicitly call listener.remove() so that the listener is properly cleared when the coroutine scope is cancelled.
4. Let the callbackFlow collect and trigger the Room DAO database insertion safely within its own active lifetime.

**Identified cause:** Agent didn't implement doc requirements
**Fix:** The NoiseZoneRepositoryImpl has been fully refactored, and the build was successful! This enforces strong lifecycle adherence—preventing the memory leak while still automatically bridging remote snapshots cleanly into your Room SSOT architecture whenever the UI collects from this flow.

Let me know if there's anything else you'd like to implement or review for the Heatmap layer!
**Did the AI get the cause right?** yes

---

### #p5 — Debugging M4 Firestore heatmap 3

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**

Please refactor HeatmapViewModel.kt to implement the final verification rule:

1. Inject the connectivity observer/monitor (e.g., NetworkMonitor) into the HeatmapViewModel constructor.
2. Use Kotlin's combine operator to merge the noiseZoneRepository.getNoiseZones() Flow with the connectivity status Flow.
3. Map the combined result into HeatmapUiState, ensuring that the "isOffline" field dynamically reacts to the network state in real time.

**Identified cause:** Agent didn't implement doc requirements
**Fix:** The build has completed successfully!

The HeatmapViewModel is now cleanly injecting the isolated NetworkMonitor, combining the isOnline stream with the getNoiseZones Room database stream. This guarantees that your HeatmapUiState flawlessly and reactively captures both offline/online status transitions and local data snapshots simultaneously.

**Did the AI get the cause right?** yes

---

### #p6 — Debugging M4 Firestore heatmap 4

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**

Please refactor HeatmapScreen.kt to fulfill the final verification requirement:

1. Implement a BottomSheet layout (such as ModalBottomSheet or using a BottomSheetScaffold) inside the HeatmapScreen.
2. Inside this bottom sheet, add a placeholder/stub text or layout representing the "Busy Hours" feature for the selected noise zone, even if the data logic is not fully wired yet.

**Identified cause:** Agent didn't implement doc requirements
**Fix:** The build has completed successfully!

The HeatmapViewModel is now cleanly injecting the isolated NetworkMonitor, combining the isOnline stream with the getNoiseZones Room database stream. This guarantees that your HeatmapUiState flawlessly and reactively captures both offline/online status transitions and local data snapshots simultaneously.

**Did the AI get the cause right?** yes

---

### #p7 — Debugging M4 Firestore heatmap 5

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**

The app is not capturing real GPS coordinates when saving a measurement. Search the codebase for where SaveMeasurementUseCase is called and how latitude and longitude are populated. If they are hardcoded or defaulting to 0.0, implement proper location capture:

Add a LocationRepository interface in domain/repository/ with getCurrentLocation(): Flow<Location?>
Implement it in data/ using FusedLocationProviderClient
Request ACCESS_FINE_LOCATION runtime permission in CaptureScreen alongside the existing RECORD_AUDIO permission request — both triggered on Start button tap
In CaptureViewModel, collect the current location when saving and pass real coordinates to SaveMeasurementUseCase
If location is unavailable or permission denied, show a warning but still allow saving with null coordinates — do not block the save

Do not change any other feature. assembleDebug must stay green.

**Identified cause:** Agent didn't implement doc requirements
**Fix:** The catch operator is now correctly imported and gracefully handles any GPS timeout or security issues natively within Coroutines Flow, guaranteeing that you won't ever see that IllegalStateException: Flow exception transparency is violated crash again when the coroutine is cancelled.

Both assembleDebug and ktlintCheck are perfectly green, and your app is fully stable.

You should be good to test saving a measurement now!

**Did the AI get the cause right?** yes

---

### #p8 — Debugging M5 paywall

- Tool: Antigravity
- Date: 2026-06-10

**Prompt:**
Using docs/09_api_and_external_services.md, docs/04_screens_and_ui.md, and docs/08_state_management.md, implement the Gemini AI consultant screen:

AiRepository interface in domain/repository/ with sendMessage(prompt: String, context: String): Flow<String> — streaming response
AiRepositoryImpl in data/remote/ using the firebase-vertexai SDK with model gemini-2.5-flash. Build the context string from the user's last 10 Measurement records fetched from Room — format as a readable summary of dB levels, times and locations
AiViewModel fully implemented with AiUiState containing messages: List<ChatMessage>, isLoading, isOffline, error, remainingFreePrompts: Int. Wire CheckEntitlementUseCase for the daily limit using ai_daily_count and ai_count_reset_date from DataStore as specified in M5
AiConsultantScreen stateless composable with chat bubble list, text input field, send button, pre-configured chip row with at least 3 suggestion chips, typing indicator during loading, and offline disabled state per wireframe in docs/04
Free users see remaining prompt count displayed (e.g. "2 of 3 free prompts remaining"). When limit is reached emit navigation event to paywall

Rules: Gemini key is accessed via Firebase Vertex AI only, never via BuildConfig directly. Context string passed to Gemini must never include raw audio data. assembleDebug must stay green.

**Identified cause:** Agent didn't implement doc requirements
**Fix:** the background compilation has successfully completed (BUILD SUCCESSFUL in 1m 2s), and both assembleDebug and your stringent ktlintCheck are perfectly green!

Everything related to the Gemini AI Consultant Feature is fully completed and built strictly around the official Google AI Studio client per your instruction.

**Did the AI get the cause right?** yes

<!-- new entries below -->
