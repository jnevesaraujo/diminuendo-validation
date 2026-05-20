# 02 — Requirements

> Each requirement has a stable ID (`RF-xx` / `RNF-xx`). The IDs are referenced
> in user stories, tests and commits.

## Functional Requirements

| ID | Requirement | Priority (MoSCoW) | Mandatory requirement covered |
|---|---|---|---|
| RF-01 | | Must | 3–5 screens |
| RF-02 | | Must | State sharing between users |
| RF-03 | | Must | AI |
| RF-04 | | Must | Multimedia |
| RF-05 | | Must | Freemium / simulated subscription |
| RF-06 | | Must | Offline |
| RF-07 | | Should | |

## Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| RNF-01 | Performance | E.g.: main screen ready in < 1s with cache. |
| RNF-02 | Offline | E.g.: flow X works without network; syncs on recovery. |
| RNF-03 | Security | No secrets in the repository (see doc 10). |
| RNF-04 | Accessibility | Contrast, *content descriptions*, touch target sizes. |
| RNF-05 | Compatibility | minSdk / targetSdk defined. |

## Freemium Model (detail)

| Feature | Free Plan | Paid Plan (simulated) |
|---|---|---|
| | | |

> The subscription is **simulated** (no real payment). How it is simulated and where the state
> is stored: see `docs/06_architecture.md` and `docs/08_state_management.md`.

## Business rules

- RN-01: ...
