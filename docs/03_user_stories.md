# 03 — User Stories

> Format: **As** \<role\>, **I want** \<action\>, **so that** \<benefit\>.
> Each story has verifiable acceptance criteria (the basis for the tests).

## User roles

- **Visitor / Free Plan** — A standard authenticated user who can monitor real-time decibel levels, record localized metrics, and view a limited regional noise heatmap.
- **Subscriber (simulated paid)** — A premium user who has unlocked unlimited features, allowing access to advanced global geographic map navigation, unlimited AI interaction, and predictive temporal "Busy Hours" charts.

## Stories

### US-01 — Core Navigation and Screens
As an authenticated app user, I want a modern bottom navigation layout so that I can seamlessly switch between the sound meter, the noise map, my exposure history, and the AI advisor.
- Covers: RF-01
- Acceptance criteria:
  - [ ] Given the user is on the Home screen, when they tap any icon on the dark-themed bottom bar, then the app navigates to the respective screen without visual lagging or crashes.
  - [ ] Given a screen change occurs, then the active tab icon becomes highlighted in the neon palette scheme.
- Priority: Must

### US-02 — State sharing between users
As a collaborative citizen, I want my anonymized noise measurements to synchronize with other users' data so that we can collaboratively map out the most polluted noise zones of our city in real-time.
- Covers: RF-02
- Criteria:
  - [ ] When User A submits a valid decibel entry with active coordinates, then User B viewing the map will see the heatmap visualization update to reflect the collective data point.
  - [ ] Conflicts resolved by timestamp ordering (the latest data packet updates the localized Firestore document aggregate).
- Priority: Must

### US-03 — Paid feature
As a premium subscriber, I want to unlock advanced features like "Busy Hours" graphs and unlimited AI consulting so that I can accurately predict sound risks and navigate quieter urban routes.
- Covers: RF-05
- Criteria:
  - [ ] Given a Free Plan user attempts to view the localized hourly bar chart, when they interact with the element, then a simulated dark-mode paywall overlay is presented.
  - [ ] Given a user taps the "Unlock Premium" simulation button, when the database state updates, then the paywall disappears permanently, granting unlimited feature access across app lifecycles.
- Priority: Must

### US-04 — Offline use
As a commuter inside a subway or tunnel, I want to continue tracking my environment sound levels while offline so that my health history logs remain complete even without internet connectivity.
- Covers: RF-06
- Criteria:
  - [ ] Given the device has no network connection, when a sound tracking event occurs, then the dashboard still calculates decibels and permits saving entries into a local encrypted database cache.
  - [ ] When the device network connection is fully restored, then cached offline measurements are automatically synced in the background to Firebase without user intervention.
- Priority: Must

### US-05 — Real-time Decibel Audio Capture
As a user concerned about a noisy room, I want the app to convert background mic inputs into readable metrics so that I can instantly check if my current environment is audibly unsafe.
- Covers: RF-04
- Criteria:
  - [ ] Given the app has received runtime record audio permissions, when the dashboard is visible, then the microphone samples sound waves and translates them dynamically into decibel values ($dB$) on the visual gauge.
  - [ ] Given audio is processed, then raw recording files are never recorded or persistent on internal storage.
- Priority: Must

### US-06 — Gemini AI Consultation
As a user worried about my ear health, I want an integrated AI chatbot to review my monthly exposure statistics so that I can receive actionable advice on avoiding future noise damage.
- Covers: RF-03
- Criteria:
  - [ ] Given a Subscriber opens the Chat tab, when they submit an analytical request, then the Gemini AI model evaluates their specific Firestore diary records and returns a customized response wrapped in a glowing visual interface container.
- Priority: Must
