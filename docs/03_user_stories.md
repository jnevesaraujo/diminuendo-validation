# 03 — User Stories

> Format: **As** \<role\>, **I want** \<action\>, **so that** \<benefit\>.
> Each story has verifiable acceptance criteria (the basis for the tests).

## User roles

- **Visitor / Free Plan** — ...
- **Subscriber (simulated paid)** — ...

## Stories

### US-01 — <title>
**As** ... **I want** ... **so that** ...
- Covers: RF-01
- Acceptance criteria:
  - [ ] Given ... when ... then ...
  - [ ] ...
- Priority: Must

### US-02 — State sharing between users
**As** ... **I want** changes from another user to appear ... **so that** ...
- Covers: RF-02
- Criteria:
  - [ ] When user A changes X, user B sees the update.
  - [ ] Conflicts resolved by <strategy>.

### US-03 — Paid feature
**As** a subscriber **I want** ... **so that** ...
- Covers: RF-05
- Criteria:
  - [ ] Free user sees a *paywall* when trying X.
  - [ ] After "subscribing" (simulated), X becomes available and persists.

### US-04 — Offline use
**As** a user without network **I want** ... **so that** ...
- Covers: RF-06
- Criteria:
  - [ ] Main flow completable without network.
  - [ ] Changes sync when the connection is restored.

> Add the remaining ones. Keep the story → RF → test mapping.
