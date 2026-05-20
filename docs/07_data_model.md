# 07 — Data Model

> Domain model + persistence (Room) + DTOs (network) + DataStore. Includes mappings.

## Domain model

```
<Entity>
  - id: String
  - ...
```

Entity-relationship (ER) diagram — _link or ASCII_.

## Local persistence — Room

| Entity | Table | Key | Notes (indexes, relations) |
|---|---|---|---|
| `XxxEntity` | `xxx` | `id` | |

- Migrations: strategy (`fallbackToDestructiveMigration` only in dev; real migrations later).
- Synchronization fields: `updatedAt`, `pendingSync`, `deleted` (soft delete) — as per offline.

## DTOs — network

| DTO | Endpoint | Maps to |
|---|---|---|
| `XxxDto` | `GET /xxx` | `XxxEntity` / `Xxx` (domain) |

## DataStore (preferences / lightweight state)

| Key | Type | Purpose |
|---|---|---|
| `is_premium` | Boolean | Simulated subscription state |
| `onboarding_done` | Boolean | |
| `user_id` | String | Local identity |

## Mappings

- `XxxDto → XxxEntity → Xxx (domain)` in `data/mapper/`.
- Rule: the UI only sees domain models.

## Multimedia data

- Images: URL + Coil cache; audio/video: <where stored, streaming vs offline download>.
