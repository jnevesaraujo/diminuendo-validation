# 07 — Data Model

> Domain model + persistence (Room) + DTOs (network) + DataStore. Includes mappings.

## Domain model

```
<User>
	- `id`: String
	- `name`: String
	- `email`: String
	
<Measurement>
	- `id`: String
	- `userId`: String
	- `dbLevel`: Double
	- `timestamp`: Long
	- `latitude`: Double
	- `longitude`: Double
	- `contextTag`: String (e.g., "Loud", "Moderate", "Quiet")
	- `locationName`: String (e.g., "Metro Line A")

<NoiseZone>
	- `locationId`: String (Geohash identifier)
	- `centerLatitude`: Double
	- `centerLongitude`: Double
	- `hourlyAverages`: List<Double> (24 decimal slots representing 0h to 23h)
	- `totalContributions`: Int
```

### Entity-relationship (ER) diagram
```
+---------------+             +---------------------+
|     USER      |             |     MEASUREMENT     |
+---------------+             +---------------------+
| id (PK)       |1       0..* | id (PK)             |
| name          |------------>| userId (FK)         |
| email         |             | dbLevel             |
+---------------+             | timestamp           |
                              | latitude            |
                              | longitude           |
+---------------+             | contextTag          |
|   NOISEZONE   |             | locationName        |
+---------------+             +---------------------+
| locationId(PK)|                       |
| centerLat     |                       | (Implicitly linked via
| centerLng     |                       v  Geohash coordinates)
| hourlyAverages|         [ Heatmap Calculation ]
| totalContrib  |
+---------------+
```

## Local persistence — Room

| Entity | Table | Key | Notes (indexes, relations) |
|---|---|---|---|
| `MeasurementEntity` | `measurements` | `id` | Index on `userId` for faster diary lookups. Index on `pendingSync` for offline scanning. |
| `NoiseZoneEntity` | `noise_zones` | `locationId` | Local cache of the map zones to allow offline map viewing of recently visited areas. |

- Migrations: The framework utilizes `fallbackToDestructiveMigration()` purely during early sandbox development phases. Proper structural schema migration scripts will be explicitly declared before shipping production release variants.
- Synchronization fields: To manage background network updates seamlessly, `MeasurementEntity` incorporates an `updatedAt` (Long timestamp) token, a `pendingSync` (Boolean) indicator flag, and an `isDeleted` (Boolean) soft-delete flag.

## DTOs — network

Since the application uses the Firebase Android SDK, remote network operations interact directly with Cloud Firestore Document Snapshots rather than traditional REST raw JSON bodies. The Data Transfer Objects model the data layout structure exactly as written on the Firestore cloud collections.

| DTO | Firestore Collection Path | Maps to |
|---|---|---|
| `MeasurementDto` | `users/{userId}/measurements` | `MeasurementEntity` / `Measurement` (domain) |
| `NoiseZoneDto` | `noise_zones` | `NoiseZoneEntity` / `NoiseZone` (domain) |

## DataStore (preferences / lightweight state)

| Key | Type | Purpose |
|---|---|---|
| `is_premium` | Boolean | Tracks the simulated premium subscription flag status to unlock specific app layouts. |
| `onboarding_done` | Boolean | Checks if the application introductory slideshow has already been completed by the client. |
| `user_id` | String | Caches the currently authenticated user identifier string locally for low-latency session checks. |

## Mappings

Transformations are strictly decoupled using explicit Extension Functions placed inside the `data/mapper/` layer directory module.

- Data Isolation Rule: The data storage structures (`Dto, Entity`) are parsed safely into pure Kotlin memory data components (`Domain`) before being emitted upwards. UI Layer Composables never gain direct access to raw persistence database formats.
- Example Flow: `MeasurementDto.toEntity(): MeasurementEntity` -> `MeasurementEntity.toDomain(): Measurement`

## Multimedia data

- Audio Inputs: In strict compliance with privacy rule RN-03, raw audio record files or microphone sound clips are never stored locally or remotely on any infrastructure node. Sound pressures are captured directly in volatile RAM cache memory buffers, computed instantly into a singular decibel ($dB$) metric float, and immediately dumped from hardware allocations.
- Waveform Vector Arrays: The dynamic frequency wave layout rendered at the base of the capture tracking screen is saved entirely as a compressed array list of 20 normalized integers within the `Measurement` database record string row to populate the diary thumbnail view efficiently without disk overhead.
