# 04 — Screens and UI

> 3 to 5 main screens. Material 3. Think about states: *loading*, *empty*, *error*, *content*, *offline*.

## Screen inventory

| Screen | Goal | Inputs | Main actions | Shared state? |
|---|---|---|---|---|
| Auth Screen | Secure user identification and access matrix management. | User credentials (Email, Password), Alternative Matrix OAuth tokens. | Authenticate, toggle between login/registration, trigger password reset. | No. |
| Home / Heatmap Screen | Spatial visualization of shared urban acoustic pollution. | GPS location data, global Firestore map coordinate datasets. | Filter map viewport, select specific noise zone markers, trigger simulated subscription. | Yes (Reads collective user submissions). |
| Capture Screen | Live audio analysis and environment acoustic tracking. | Real-time audio stream buffers from device microphone. | Toggle live recording stream, log instant measurement snapshot to personal diary. | No (Local state). |
| Diary Screen | Chronological and descriptive history of personal exposure. | Stored local/remote database history records. | Filter history logs by severity level, delete specific measurement data rows. | Yes (Synchronizes with user identity database). |
| AI Consultant | Interactive chatbot recommendations for audiological health. | Text input prompt strings, background exposure diary history context. | Dispatch text prompt, trigger pre-configured chip queries. | Yes (Maintains active conversation log). |

## For each screen

### Screen: Auth Screen
- **User stories:** US-01
- **Layout (wireframe / sketch):**
```
+-----------------------------------+
|            DIMINUENDO             |
|   [Light / Dark Mode Toggle]      |
|                                   |
|   +---------------------------+   |
|   |   Unified Access Matrix   |   |
|   |                           |   |
|   |  [ Email Address Input ]  |   |
|   |  [ Password Input      ]  |   |
|   |                           |   |
|   |     (( LOGIN BUTTON ))    |   |
|   +---------------------------+   |
|                                   |
|    - OR - Alternative Matrix -    |
|       [G] Google   [A] Apple      |
+-----------------------------------+
```
- **Compose components:** `AuthScreen`, `AuthCardContent`, `AuthViewModel`
- **UI states:**
  - Loading: Disables entry input fields; overlays a centered Material 3 indeterminate circular progress indicator.
  - Empty: Fields clear; login execution button remains disabled until structural validations (valid email string format and password character size minimums) are met.
  - Error: Highlights problematic text field boundaries in warning red tones and produces a validation failure snackbar notification showing an option to retry or execute credential recovery.
  - Offline: Renders a global connection status warning bar; disables alternative third-party OAuth provider entry mechanisms.
  - Content: Standard layout presentation highlighting inputs inside a glassmorphic layout container with neon highlighting.
- **Paywall?** No.
- **Multimedia:** Static decorative branding logo loaded synchronously using vector resources (Image).

### Screen: Home / Heatmap Screen
- **User stories:** US-01, US-02, US-03
- **Layout (wireframe / sketch):** 

+-----------------------------------+
| [ Search Location...            ] |
+-----------------------------------+
| . . . . . . . . . . . . . . . . . |
| . . . . [HEATMAP OVERLAY] . . . . |
| . . . . . . ( User) . . . . . . . |
| . . . . . . . . . . . . . . . . . |
+-----------------------------------+
| ========== BOTTOM SHEET ==========|
| Location Name Display             |
| Noise peak hours (Bar Chart):     |
| █ █ █ ▄ ▄ ▄ █ █ █ █ [♕ PREMIUM]  |
+-----------------------------------+


- **Compose components:** `HeatmapScreen`, `GoogleMapViewContainer`, `ZoneInsightsBottomSheet`, `HeatmapViewModel`
- **UI states:**
  - Loading: Loads remote mapping network modules and downloads global geospatial noise coordinate documents from Firestore.
  - Empty: Renders base layout map grids with zero overlay points if selected geographical bounds contain no submitted user contributions.
  - Error: Displays a network parsing connection failure message overlay with an option to retry data retrieval.
  - Offline: Renders a degraded static layout view fetching regional data entirely from the internal local database cache layers.
  - Content: Interactive display using Google Maps API presenting regional heat gradients based on localized multi-user inputs.
- **Paywall?** Yes. Free-tier accounts can view live heat layouts surrounding their active coordinates. Interacting with historical, predictive hourly acoustic metrics ("Busy Hours" bar chart charts) triggers a dark-themed paywall blocker to prompt a premium simulation upgrade.
- **Multimedia:** Incorporates geographical map layouts from the Google Maps SDK layer.

### Screen: Capture Screen
- **User stories:** US-01, US-04, US-05
- **Layout (wireframe / sketch):**

+-----------------------------------+
| Diminuendo               [Profile]|
+-----------------------------------+
|                                   |
|             /-------\             |
|            /         \            |
|           |   65 dB   |           |
|            \         /            |
|             \-------/             |
|              SAFE LOG             |
|                                   |
|   Avg: 58dB | Peak: 82dB | 0h45m  |
|                                   |
|  ~~~~~~~~~~~ WAVEFORM ~~~~~~~~~~  |
|                                   |
|       (( SAVE MEASUREMENT ))      |
+-----------------------------------+

- **Compose components:** `CaptureScreen`, `DecibelMeterGauge`, `WaveformVisualizer`, `CaptureViewModel` 
- **UI states:**
  - Loading: System initializations while requesting raw hardware audio subsystem access pointers.
  - Empty: Waiting for microphone audio buffer access approval; requests Android system permission access handlers.
  - Error: Triggers an explanatory text card warning detailing a hardware allocation error with a functional button path link to the system configuration panel.
  - Offline: Displays a persistent local logging status banner indicating that metrics will safely cache locally before remote syncing.
  - Content: Animates active decibel calculations dynamically inside the primary gauge while drawing a real-time live canvas waveform tracking frame frequency fluctuations at the base.
- **Paywall?** No.
- **Multimedia:** Uses the device microphone stream continuously converted mathematically to acoustic metric variables ($dB = 20 \cdot \log_{10}(\text{Amplitude})$). Real-time animation generated procedurally on a Jetpack Compose Canvas.

### Screen: Diary Screen
- **User stories:** US-01, US-04
- **Layout (wireframe / sketch):**

+-----------------------------------+
| Exposure History        [Filters] |
+-----------------------------------+
|  [LOUD] Metro Line A              |
|  14:32 - 88 dB   ~-~~ (Wave)  [X] |
|                                   |
|  [MODERATE] Office Space          |
|  09:15 - 62 dB   ~~-~ (Wave)  [X] |
|                                   |
|  [QUIET] Public Library           |
|  08:00 - 40 dB   ~-~~ (Wave)  [X] |
+-----------------------------------+

- **Compose components:** `DiaryScreen`, `HistoryLogCardItem`, `DiaryViewModel`
- **UI states:**
  - Loading: Queries regional SQLite/Room system database tables and cloud server record history indices.
  - Empty: Displays an illustrative graphic element and helpful instruction detailing how to perform an environment audio scan.
  - Error: Renders data formatting exception warnings along with an interactive historical database refresh action button.
  - Offline: Displays historical elements with a warning icon on items that are locally cached and awaiting remote cloud server sync.
  - Content: Renders a structural lazy list element arranging logged acoustic logs filtered cleanly by severity tags ("Loud", "Moderate", "Quiet").
- **Paywall?** No.
- **Multimedia:** Recreates mini-waveform graphical vector representations drawn on a layout Canvas matching stored measurement snapshots.

### Screen: AI Consultant
- **User stories:** US-01, US-03, US-06
- **Layout (wireframe / sketch):** 

+-----------------------------------+
| Gemini Advisor                    |
+-----------------------------------+
|  [IA]: Your noise exposure today  |
|  is elevated. Consider visiting   |
|  the Botanical Gardens nearby.    |
|                                   |
|              [Route suggestions?] |
|                                   |
| +-------------------------------+ |
| | Ask Diminuendo...          [^]| |
| +-------------------------------+ |
+-----------------------------------+

- **Compose components:** `AiConsultantScreen`, `ChatBubbleLayout`, `QueryChipRow`, `AiViewModel`
- **UI states:**
  - Loading: Displays an animated bouncing ellipsis typing indicator while waiting for the Gemini API server response token stream.
  - Empty: Renders a welcoming layout view with multiple shortcut chip options to help initiate historical logs assessment.
  - Error: Informs the user of a prompt timeout exception with an interactive text resubmit link option.
  - Offline: Disables conversation inputs completely; displays an explicit warning message stating the AI agent requires server connectivity.
  - Content: Renders a clean chat timeline displaying conversational bubble components surrounded by distinct theme highlights.
- **Paywall?** Yes. Free-tier usage enforces a restricted count of 3 daily inference evaluation prompts. Premium subscription parameters grant unrestricted chat sessions.
- **Multimedia:** Interactive AI responses fetched dynamically via remote system network integration streams using the Google AI SDK.

## Design system

The application switches natively between two contrast variants depending on system settings or user preference toggles, following Material 3 specifications.

### 1. Dark Mode Theme ("The Neon Nocturne")
- Background Palette: Deep Navy (`#0F131F`), Surface Midnight (`#161C2E`).

- Accent Tones: Neon Pink (`#FF007F`) and Royal Purple (`#7B2CBF`).

- Typographic Layout: Primary headings set in Space Grotesk (`#FFFFFF`), body texts tracking across clean Inter configurations (`#E2E8F0`).

### 2. Light Mode Theme ("The Acoustic Clarity")
- Background Palette: Clean Soft White (`#F8F9FA`), Surface Light Gray (`#FFFFFF`).

- Accent Tones: Deep Slate Blue (`#1E293B`) for body components, Soft Rose Coral (`#FF4A8D`), and Light Amethyst (`#9D4EDD`).

- Typographic Layout: Clear high-contrast Space Grotesk labels in Slate (`#0F172A`), body text paragraphs formatted cleanly through dark Inter parameters (`#334155`).

### Reusable Components
`DiminuendoGlassCard`: Standardized container executing semi-transparent blur backdrops.

`AcousticButton`: Accent gradient interactive button supporting internal text vectors.

`StatusBadge`: Dynamic category label tagging environment risk states ("Loud", "Moderate", "Quiet").

`LoadingOverlay`: Centered layout management element blocking inputs while showing progress.

## Accessibility

- [x] `contentDescription` on meaningful images/icons (e.g., sound meters, map status indicators, and chat bubbles).
- [x] Touch targets ≥ 48dp on all interactive elements, chips, and navigation components.
- [x] Sufficient contrast support matching WCAG AA standards across both the high-contrast Dark Mode ("The Neon Nocturne") and Light Mode ("The Acoustic Clarity") profiles.
