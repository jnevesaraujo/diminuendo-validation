# 13 — Extensions / Future Features

> Ideas beyond the minimum scope. Useful to differentiate the project and for the final discussion.
> Do not implement without closing the mandatory scope first.

## Candidates

| Idea | User value | Effort | Depends on |
|---|---|---|---|
| **Auditory Safe Routing** | Calculates walking navigation routes based not only on distance, but on noise minimisation, helping users avoid highly polluted sound corridors. | **L** | `M4` (Global Heatmap), `M2` (Gemini API integrations) |
| **Wear OS Companion App** | Allows users to check live decibel tracking, log quick entries into their diary, and receive environmental hazard alerts directly on their smartwatch. | **L** | `M6` (Acoustic Capture Pipeline), `M1` (Local Storage) |
| **Acoustic Geofencing Alerts** | Triggers proactive push notifications when a user enters an urban area classified as a high-intensity sound hazard by the community map. | **M** | `M4` (Global Heatmap Tracking Framework) |
| **Social Shared Leaderboards** | Gamifies environmental tracking by creating local leaderboards for "Quiet Zone Explorers" or top crowdsourcing contributors to incentivize mapping. | **M** | `M4` (Cloud Firestore Multi-user Infrastructure) |

---

## Known technical debt

| Item | Where | Why it ended up like this | How to fix |
|---|---|---|---|
| **Microphone Structural Calibration Variance** | `data/repository/AudioCaptureRepositoryImpl.kt` | Different Android smartphone models possess native physical hardware microphone sensitivity properties, leading to uncalibrated variance in calculated decibel numbers. Left as default calculation algorithms for the MVP due to the lack of an acoustic laboratory testing setup. | Integrate an internal user-facing calibration slider overlay panel allowing custom dB offset adjustments, or maintain an online database matrix mapping model corrections. |
| **Local Freemium Verification Security** | `domain/usecase/CheckEntitlementUseCase.kt` | Tracking subscription states (`isPremium`) directly through Jetpack DataStore parameters is susceptible to manipulation or tampering on custom rooted Android devices. Settled upon local verification layers to reduce backend infrastructure overhead for the initial development cycle. | Migrating subscription status checking blocks to be executed entirely server-side utilizing protected **Firebase Cloud Functions** bound directly to Google Play Billing validation webhooks. |
| **Synchronous In-Memory Waveform Processing** | `ui/feature/capture/WaveformVisualizer.kt` | Rendering real-time UI canvas canvas elements forces heavy main thread layout redraw cycles during long audio polling intervals. Built directly on volatile canvas threads to guarantee basic delivery speed benchmarks. | Refactoring the capture audio visualizer pipeline loop to run calculations within asynchronous high-performance low-level **RenderThread** pipelines or dedicated hardware shaders. |

---

## Discarded ideas (and why)

- **Continuous 24/7 Background Environmental Recording:** Initially planned to gather continuous urban data blocks automatically. Discarded due to severe Android OS battery sandbox execution constraints, intensive thread management system drains, and massive user privacy trust friction regarding active background microphones.
- **Laboratory-grade Acoustic Frequency Spectrum Analysis:** Considered offering advanced Hz-level spectrum categorization. Discarded because built-in smartphone voice communication microphones are hardware-equalized for vocal capture, lacking the linear frequency response required for professional scientific acoustic evaluation.
- **Real-time Peer-to-Peer Bluetooth Local Audio Mesh:** Evaluated using local device networks to swap live ambient levels directly without network connectivity. Discarded due to high battery usage, structural hardware disconnection drops, and because the Firebase offline queue sync pipeline provides a far superior user experience.