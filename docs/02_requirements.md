# 02 — Requirements

> Each requirement has a stable ID (`RF-xx` / `RNF-xx`). The IDs are referenced
> in user stories, tests and commits.

## Functional Requirements

| ID | Requirement | Priority (MoSCoW) | Mandatory requirement covered |
|---|---|---|---|
| RF-01 | The application must feature 5 core screens (Auth, Dashboard/Meter, Heatmap, Exposure Diary, and Gemini AI Chatbot) with fluid navigation using Jetpack Compose. | Must | 3–5 screens |
| RF-02 | The application must automatically transmit anonymized decibel measurements along with geographic coordinates to Firebase Firestore to build a shared global heatmap. | Must | State sharing between users |
| RF-03 | The application must integrate an AI chatbot using the Google AI/Gemini SDK to analyze the user's noise exposure history and provide health recommendations. | Must | AI |
| RF-04 | The application must access the device's microphone to capture environmental sound amplitude and compute real-time decibel metrics. | Must | Multimedia |
| RF-05 | The application must restrict access to the advanced "Busy Hours" temporal analysis charts and unlimited AI interactions behind a simulated paid subscription wall. | Must | Freemium / simulated subscription |
| RF-06 | The application must allow real-time decibel monitoring and local data logging when offline, automatically syncing queued records upon network reconnection. | Must | Offline |
| RF-07 | The application should allow users to edit profile details or delete specific entries from their personal exposure history diary. | Should | |

## Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| RNF-01 | Performance | The main dashboard screen with the real-time decibel meter must load and become operational in less than 1.5 seconds using cached local data. |
| RNF-02 | Offline | The core decibel calculator and personal exposure logs must remain operational without network; state synchronization must resolve silently in the background on recovery. |
| RNF-03 | Security | Sensitive API credentials (such as Gemini and Firebase keys) must never be hardcoded in the public repository; secure environment variables or Firebase App Check must be used. (see doc 10). |
| RNF-04 | Accessibility | The user interface must comply with Material Design 3 accessibility standards, providing distinct contrast ratios in Dark Mode, interactive touch targets of at least 48x48dp, and explicit content descriptions for charts and components. |
| RNF-05 | Compatibility | The application must support devices starting from Android 8.0 (minSdk 26) to leverage necessary audio management and background APIs, targeting Android 15 (targetSdk 35). |

## Freemium Model (detail)

| Feature | Free Plan | Paid Plan (simulated) |
|---|---|---|
| Real-time dB Meter | Full access to real-time sound tracking and risk level display. | Full access to real-time sound tracking and risk level display. |
| Global Noise Heatmap | Restricted to viewing the heatmap overlay surrounding the user's current physical location. | Unlimited navigation, zooming, and location searches across the entire global map. |
| "Busy Hours" Analysis | Not available. | Complete access to historical bar charts detailing peak noise hours for selected urban zones. |
| Gemini AI Consultant | Limited to 3 advice requests or chatbot messages per day. | Unlimited chatbot interactions with personalized safe-route suggestions. |

> The subscription is **simulated** (no real payment). How it is simulated and where the state
> is stored: see `docs/06_architecture.md` and `docs/08_state_management.md`.

## Business rules

- RN-01 (Data Integrity): A sound measurement can only be submitted to the global noise heatmap if it contains valid geographical coordinates and a corresponding timestamp.

- RN-02 (State Persistence): The user's simulated premium subscription status must be persistently stored in local preferences or database state and checked upon every application launch.

- RN-03 (Privacy Safeguard): Audio data captured via the microphone must be processed solely in volatile memory for decibel mathematical derivation; raw audio recording files must never be saved or stored anywhere on the device.
