# ADR 0002 — Infrastructure Gateway: Firebase Native SDKs vs Traditional REST API

- Status: Accepted
- Date: 2026-06-09
- Deciders: Project team
- AI assistance: Gemini

## Context
The application requires a robust infrastructure stack capable of managing secure user registration and login, multi-user real-time state sharing (for the global noise heatmap), persistent remote data synchronization (for the exposure diary), and advanced AI execution (for the Gemini advisor). While the architecture baseline template assumes a traditional REST API ecosystem using Retrofit or Ktor, building and hosting a custom standalone backend database server within the strict academic timeline introduces a high implementation risk.

## Options considered

1. **Traditional REST API (Retrofit/Ktor) + Custom Backend Server**
   - **Pros:** Full control over the hosting server, custom query filtering optimization, and database schema; leverages standard HTTP methods taught in foundational web networking modules.
   - **Cons:** Requires developing, testing, and maintaining a separate backend application (e.g., Node.js or Python FastAPI); requires manual custom integration of WebSockets or Polling frameworks to implement the real-time heatmap updates; requires spinning up a custom endpoint proxy to handle and sign Gemini API keys securely without client-side exposure.
2. **Firebase Native Android SDKs (Auth, Firestore, Vertex AI)**
   - **Pros:** Zero backend application code to write or maintain (Serverless architecture); Firestore provides native realtime Snapshot Listeners for instant multi-user synchronization; native out-of-the-box local data caching mechanisms that satisfy offline requirements natively; Vertex AI for Firebase manages the Gemini API key securely using Firebase App Check integration.
   - **Cons:** Creates a hard architectural dependency and vendor lock-in to the Google Cloud/Firebase ecosystem; bypasses the implementation of manual HTTP network mapping layers using Retrofit or Ktor.

## Decision
We adopt the **Firebase Native Android SDK suite** as the core infrastructure gateway for the Diminuendo project. This selection eliminates backend infrastructure overhead, shifting all development energy toward refining the Jetpack Compose user interface and mathematical raw microphone decibel calculations. Furthermore, Firestore's built-in real-time stream synchronization satisfies the multi-user state sharing requirements cleanly and reliably.

## Consequences
- **Positive:** Project velocity is significantly accelerated; data persistence rules, local-first syncing, and token identity tracking are natively provided by the SDK lifecycle; Gemini chat configurations are secured natively without server proxies.
- **Negative:** Standard HTTP networking components (Retrofit/Ktor) are bypassed; unit testing patterns must isolate logic by mocking or wrapping Firebase SDK task responses inside repository layer boundaries.
- **Impact on other docs:** Solidifies the infrastructure strategy declared in `docs/06_architecture.md`, establishes the collection structures in `docs/07_data_model.md`, and sets up the communication pipeline specified in `docs/09_apis_and_external_services.md`.