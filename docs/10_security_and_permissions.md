# 10 — Security and Permissions

## Secrets management (mandatory)

- **No** sensitive keys or infrastructure credentials will be committed to the public version control repository. The project `.gitignore` file is explicitly configured to exclude `local.properties` and any secret configuration files.
- **Credential Flow:** Sensitive parameters are stored locally in `local.properties` -> extracted during build runtime compilation by `build.gradle.kts` -> injected securely as immutable fields inside the accessible application metadata object `BuildConfig.XXX`.
- **Team Configuration Template:** The following template acts as a blueprint for configuring local developer environments safely:

```properties
# local.properties.example
# Copy this file to 'local.properties' and fill in your real project credentials.
MAPS_API_KEY=your_google_maps_android_api_key_here
GEMINI_API_KEY=your_optional_fallback_ai_studio_key_here
```

> If a production API key or security file is accidentally pushed to git history, the credential must be revoked immediately and rotated on the Google Cloud / Firebase console. Simply erasing the file via a new commit does not clear the asset from git history snapshots.

## Android permissions

| Permission | Why | Runtime? |
|---|---|---|
| `android.permission.INTERNET` | Establishes connections with Firebase services and remote Gemini model endpoints. | no |
| `android.permission.ACCESS_NETWORK_STATE` | Monitored by the connectivity observer pipeline to toggle offline/degraded UI flows. | no |
| `android.permission.RECORD_AUDIO` | Samples raw environmental sound wave amplitudes from the microphone to compute decibel metrics. | yes |
| `android.permission.ACCESS_FINE_LOCATION` | Collects geographical coordinates to populate the map and verify proximity to known noise zones. | yes |
| `android.permission.POST_NOTIFICATIONS` | Allows background tasks or geofencing alarms to trigger audible and visual risk warning alerts (Android 13+). | yes |

- Runtime Execution Strategy: Runtime system permission verification prompts are deferred until the precise moment the target hardware interaction occurs (e.g., clicking "Start Meter" triggers the audio permission dialog). If a permission request is explicitly dismissed by the user, the application presents a clear educational rationale overlay explaining the necessity of the sensor, providing a seamless operational path loop back to the system settings pane if rejected a second time.

## User data

- Identity Information (Name, Email): Managed directly within the secure silo of Firebase Authentication infrastructure and cached locally within an encrypted `DataStore` key-value property container.
- Personal Exposure Diary History: Logged locally within individual Room database tables and systematically backed up to user-scoped private Firestore collection branches protected behind explicit access verification constraints.
- Heatmap Location Submissions: Coordinates shared globally to compute community sound maps are entirely anonymized before transmit. Specific identifiers linking a measurement point back to an explicit user account profile string are completely stripped out to protect personal movement habits.
- Audio Privacy Safeguard: No verbal conversations or environmental sounds are captured or written to physical disk sectors. Stream samples are processed purely within transient memory layers to derive a single statistical float integer and instantly recycled.

## Authentication / authorization

- Session Tracking: Users access application functions by setting up an account profile verified using Firebase Auth (Email access matrix or alternative OAuth mechanisms). An active secure session token is required to read or edit personal exposure histories.
- Premium Plan Validation: Access validation checking for features like advanced "Busy Hours" graphs is evaluated by the centralized `SubscriptionUseCase`. This checks a client-side premium state flag mirrored down to the local Jetpack DataStore preferences container directly from the user's Firestore profile properties.
- Limitation Context: Managing verification flags entirely on a client-side device introduces risks if a deployment runs on specialized rooted hardware. However, for the scope of this implementation MVP model, isolating logic within decoupled UseCase boundaries establishes clean architectural foundations that can be easily migrated to rigid cloud server-side verification pipelines later.

## Checklist

- [x] No secrets or private project credential tokens present within git history files
- [x] local.properties.example present and up to date at the project architecture root path
- [x] Minimum necessary permissions configured inside the system manifest files (Camera and Video hooks omitted)
- [x] Clean architectural handling logic designed for permission denial and user rationale rendering
