# 01 — Project Vision

*> Fill in before writing code. 1 page is enough. Update if the vision changes.*

## Topic agreed with the instructor

Diminuendo is an Android application dedicated to crowdsourced urban noise pollution monitoring and auditory health preservation. It utilizes the device's microphone to measure environmental decibel levels in real-time, feeding a collaborative global heatmap. Furthermore, it incorporates temporal analysis to predict peak noise hours for specific urban zones, empowering users to make informed decisions about their surroundings.

## Problem / opportunity

*- What problem does it solve?*

High exposure to urban noise pollution causes long-term hearing degradation, sleep disorders, and elevated stress levels, yet citizens lack a tool to visualize real-time or historical noise data in specific city areas before visiting them.

*- For whom (target user)?*

City dwellers, daily commuters, remote workers seeking quiet spaces, individuals with noise sensitivity, and health-conscious citizens.

## Value proposition (1 sentence)

"An app that lets health-conscious urban citizens monitor, predict, and avoid hazardous city noise levels in a collaborative, real-time, and AI-assisted way."

## How it meets the mandatory requirements

| Requirement | How it will be met |
|---|---|
| 3–5 main screens | The app will feature 5 main views: Auth (Login/Register), Capture (Real-time dB Meter), Heatmap (Google Maps view), Exposure Diary (History log), and Gemini AI Chatbot Consultant. |
| State sharing between users | Anonymized decibel measurements from individual users are continuously synchronized via Firebase Firestore to dynamically update a shared global noise heatmap for all active users. |
| AI (remote or local API) | Integration with the Gemini API (via Firebase Vertex AI SDK) to analyze user exposure logs and provide personalized safety advice and quieter route recommendations. |
| Image / audio / video | Accesses the device's microphone via the Android Audio framework to capture sound amplitude data, which is mathematically processed into decibel metrics. |
| Freemium (free + simulated paid subscription) | Free tier offers basic real-time decibel metering and global heatmap visualization. The simulated Premium tier unlocks detailed "Busy Hours" temporal graphs and unlimited interactions with the Gemini AI Consultant. |
| Offline | Core decibel metering and the personal Exposure Diary remain functional offline by caching data locally. Pending measurements are automatically queued and pushed to the cloud once network connectivity returns. |

## Out of scope (what we will NOT do)

- Continuous 24/7 background audio recording (omitted due to Android privacy constraints and high battery consumption).

- High-fidelity, laboratory-grade frequency spectrum analysis or professional calibration for audio hardware.

- Integration with external wearable hardware accessories or IoT decibel meters.

## Success metric

We know it worked if a user can open the app offline, successfully record a real-time decibel measurement, view it locally in their historical diary, and see their anonymized contribution accurately merge into the shared global heatmap once they reconnect to the internet.
