# 09 — APIs and External Services

> Includes the AI integration (mandatory) and the state-sharing backend.

## Services used

| Service | Purpose | Authentication | Free tier? |
|---|---|---|---|
| Google AI (Gemini Pro / Flash) | RF-03: AI personalized acoustic recommendations and chatbot consultation. | Firebase Vertex AI authenticated secure configuration. | Yes (Google AI Studio Spark / Free Tier limits). |
| Firebase Cloud Firestore | RF-02: Multi-user real-time state sharing backend to build the global noise heatmap. | Firebase Auth App-secured tokens. | Yes (Firestore free daily usage quotas). |
| On-Device Audio Pipeline | RF-04: Sound wave sample streaming and local decibel calculation (No cloud storage used). | Standard Android system hardware permissions runtime check. | Yes (Local hardware utility). |

## AI integration

- **Mode:** Remote API via the Vertex AI for Firebase SDK. This prevents embedding raw backend developer keys inside the client build.
- Endpoint / SDK: `com.google.firebase:firebase-vertexai` library dependency using the `gemini-2.5-flash` model structure configuration.
- Input/output: Text-to-Text interaction layer. Input consists of a structured text payload combining the user's historical decibel logs and map trends; output consists of a string parsing safety suggestions and silent routes.
- **Offline degradation:** The chat input text bar field becomes completely disabled, changing the quick-action chips into a passive error message card container: "The Gemini Advisor requires an active network channel to evaluate data safely."
- **Cost / limits:** Enforces a rigid query access limit restriction of 3 requests per calendar day for Free Plan accounts. Unrestricted interactions are simulated as unlocked exclusively via the paid tier overlay layer (linked directly to RF-05).

## API contracts (summary)

| Method / Call Type | Target Node / Path | Request Body / Payload | Response / Output | Errors handled |
|---|---|---|---|---|
| Firestore Document Set | `users/{userId}/measurements/{id}` | `MeasurementDto` data mapping schema object. | Async confirmation payload validation task success. | `FirebaseFirestoreException` (Permission Denied, Timeout) |
| Firestore Snapshots Realtime Listener | `noise_zones` collection query filter. | Coordinates bounding viewport parameters. | Real-time continuous stream emitting collections of `NoiseZoneDto`. | `FirebaseNetworkException` |
| Vertex AI Compute Flow | Vertex AI remote model execution hook. | Structured text string prompt payload parameter wrapper. | `GenerateContentResponse` wrapper processing generated text output tokens. | `IllegalArgumentException`, `QuotaExceededException` (429) |

## Network error handling

- Timeouts and Retries: Firebase infrastructure components automatically apply an underlying native exponential backoff retry routine configuration whenever communication disruptions occur.
- UiState Exception Mapping: Out-of-network parsing or query timeout exceptions are caught safely in the repository abstraction layer blocks, mapping technical exceptions into clean localized user-facing failure error variables inside the active screen `UiState`.
- Offline Mode Fallback: In cases of severe network dropouts, transaction blocks are rerouted directly to fire read operations from the local Room persistence cache database layers (in strict compliance with 06 — Architecture design rules).

## Secrets

- Repository Safety Rules: Application system connection keys, private Google Services tokens, and backend mapping parameters are strictly prohibited from being pushed to public source version control code repositories. See `docs/10_security_and_permissions.md`.
- BuildConfig Injections: Values are defined securely inside the local, git-ignored `local.properties` environment text properties file at the root architecture framework path. During build compilation, they are processed through the Gradle Secrets Plugin architecture to expose them safely to the Kotlin data layer files via automated `BuildConfig` parameter strings.



