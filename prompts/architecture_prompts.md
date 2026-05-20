# Prompts — Architecture

Prompts about layers, Repository, UseCases, DI, offline, synchronization.

## Good practices

- Always point to `AGENTS.md` and `docs/06_architecture.md` (dependency rule).
- Require SSOT = Room for offline data.
- Ask for interfaces in `domain`, implementations in `data`.
- Forbid exposing DTO/Entity to the UI (domain models only).

---

### #p1 — (example) Repository with offline cache

- Tool: <...>
- Date: 2026-05-19
- Context given to the AI: `docs/06`, `docs/07`, `docs/08`, `data/`

**Prompt:**
> Implement `XxxRepositoryImpl` that fulfills the `XxxRepository` interface (domain).
> Single Source of Truth = Room: expose `Flow<List<Xxx>>` from the DB; a `refresh()` fetches
> from the API and updates the DB. Without network, serve the DB. Map DTO→Entity→domain in `data/mapper`.
> Follow `docs/06_architecture.md`. Do not access Retrofit from the UI.

**Result:** <fill in>
**Assessment:** <...>

---

<!-- new entries below -->
