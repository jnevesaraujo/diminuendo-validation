# 14 — AI Usage Log (ongoing)

> Update **as you use AI**, not at the end. It is the proof of *how* the work evolved.
> The full prompt goes to `prompts/`; here goes the summary and the critical decision.
> The `AI-Assisted:` *trailer* in commits links to this log.

## How to fill in

One line per relevant interaction (non-trivial ones). Be honest about what you **rejected**.

| Date | Tool | Goal | Prompt (ref. in `prompts/`) | Result | Accepted / Rejected / Edited | Why |
|---|---|---|---|---|---|---|
| 2026-06-09 | Antigravity | M0 scaffold | `architecture_prompts.md#p1` | Full skeleton compiled | Edited | Rename missing packages and added TypeConverters |
| 2026-06-09 | Antigravity | M1 navigation shell | `compose_prompts.md#p1` | Auth screen compiled: unable to verify rest and deep link failed | Edited | Made debug prompt |
| 2026-06-09 | Antigravity | Debug Navigation | `debugging_prompts.md#p1` | All screens compiled and deep link returned ok | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M3 mock user | `architecture_prompts.md#p2` | Hardcoded mock userId for local testing before Firebase Auth | Accepted | Temporary; will be replaced in M2 with real Firebase Auth uid |
| 2026-06-10 | Antigravity | M2 Firebase Auth | `architecture_prompts.md#p3` | Created classes for Firebase Auth | Edited | Included debug prompt |
| 2026-06-09 | Antigravity | Debug Firebase Auth | `debugging_prompts.md#p2` | Missed some requirements | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M4 Firestore heatmap | `architecture_prompts.md#p5` | Firestore snapshot listener, Room SSOT, heatmap overlay, weighted average update | Edited | Included Debug prompt |
| 2026-06-09 | Antigravity | Debug Firestore heatmap | `debugging_prompts.md#p3&p4&p5&p6&p7` | Missed some requirements | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M5 paywall | `architecture_prompts.md#p8` | Paywall and Busy Hours working. Gemini daily limit not wired — AiViewModel was a stub. Location never implemented in capture pipeline | Edited | Two follow-up prompts required: location fix and Gemini implementation |
| 2026-06-11 | Antigravity | M7 test suite — CheckEntitlementUseCase, HeatmapViewModel, MeasurementMapper, MeasurementRepository | `architecture_prompts.md#p9` | All test cases generated, ./gradlew test green | Edited | Added debug prompt |
| 2026-06-11 | Antigravity | CI fix | `debugging_prompts.md#p9&p10` | CI Node.js update, lint reduction | Edited | Corrected some aspects manually, like minSDK and gradle version |
| 2026-06-11 | Antigravity | UI polish pass + Profile screen | `compose_prompts.md#p3` | Layout fixes, TopAppBar, Profile screen | Edited | Included debug prompt |
| 2026-06-11 | Antigravity | Bug fixes — reverse geocoding, Busy Hours data, TopAppBar size, search bar styling | `debugging_prompts.md#p10&p11&p12` | Four targeted fixes, no business logic changes | Edited | Agent had hardcoded placeholder strings in ViewModel and bottom sheet; required explicit fix prompts |
| 2026-06-11 | Antigravity | Bug fix batch — TopAppBar insets, bottom sheet clipping, map selection, dB calculation, location default | `debugging_prompts.md#p9-p12` | Five targeted fixes across UI layout and audio pipeline | Accepted | dB bug was a fundamental amplitude normalisation error — agent used raw PCM values instead of normalised RMS. Map interactions were stubs never wired to ViewModel. |
| 2026-06-11 | dB formula applied to raw PCM values (range 0–32767) instead of normalised RMS (range 0–1), producing readings 40–60dB too high | `debugging_prompts.md#p9-p12` | Noticed during emulator testing — silent room showing 60–100dB | Accepted | Fixed by implementing proper RMS calculation across full buffer and normalising against 32767.0 before applying log10 |
| 2026-06-12 | Antigravity | HeatmapScreen cascading breakage — multiple patches conflicting | `debugging_prompts.md#p15` | Required full screen rewrite after incremental fixes broke pins and insets | Edited | Agent accumulated conflicting inset and marker logic across multiple prompts. Lesson: one file, one fix, read before write |
| 2026-06-12 | Antigravity | UI Refinements | `compose_prompts.md#p4` | Added Classification Badges to diary entries | Accepted | - |
| 2026-06-12 | Antigravity | Offline UI re-wiring after HeatmapScreen rewrites broke connectivity banners | `debugging_prompts.md#p16` | Offline banners and disabled states restored across all screens | Accepted | UI offline indicators lost during cascading HeatmapScreen patches. Data layer was intact. Lesson: UI-only patches can silently drop state wiring |
| 2026-06-12 | Antigravity | Offline completion — UI banners on remaining screens + WorkManager sync | `compose_prompts.md#p7, architecture_prompts.md#p10` | Offline banners added to HeatmapScreen, CaptureScreen, DiaryScreen. WorkManager implemented for pending sync queue | <fill after> | WorkManager was completely absent despite being referenced in M3 prompt — agent left a comment placeholder instead of implementing it |
| 2026-06-12 | Antigravity | Fix WorkManager test crash — function injection pattern | `debugging_prompts.md#p18` | Introduced workScheduler lambda to isolate WorkManager from unit tests | Accepted | Cleaner than WorkManager test init. Verified production default still enqueues real work. |
| | | | | | | |

## AI errors / hallucinations detected

| Date | What the AI got wrong | How it was noticed | Fix |
|---|---|---|---|
| 2026-06-10 | dB formula applied to raw PCM values (range 0–32767) instead of normalised RMS (range 0–1), producing readings 40–60dB too high | Noticed during emulator testing — silent room showing 60–100dB | Fixed by implementing proper RMS calculation across full buffer and normalising against 32767.0 before applying log10 |
| 2026-06-11 | WorkManager referenced in M3 prompt but never implemented — only a comment placeholder left in MeasurementRepositoryImpl | Noticed during offline audit before documentation phase | Implemented SyncMeasurementsWorker with function injection pattern for testability |
| 2026-06-11 | Agent left hardcoded "Captured Location" string in CaptureViewModel despite coordinates being available | Noticed during diary review in emulator | Implemented Geocoder reverse lookup on Dispatchers.IO |
| 2026-06-12 | Multiple HeatmapScreen patches accumulated conflicting window inset modifiers causing TopAppBar padding regression and map pin loss | Noticed after unknown location fix broke previously working pins | Required full single-file rewrite with explicit structural constraints |
| 2026-06-12 | WorkManager explicitly disabled in manifest, never manually initialised — caused test crash | Noticed when running ./gradlew test after WorkManager implementation | Fixed via function injection pattern isolating scheduler from test environment |
| | | | |

> The final reflection (what went well/badly overall) goes to `docs/15_postmortem.md`.
