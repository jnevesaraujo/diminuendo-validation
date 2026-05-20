# Prompts — Refactoring

Prompts to improve existing code **without changing behavior**.

## Good practices

- Require: behavior preserved, tests still green, small and cohesive change.
- One refactor at a time (do not mix with a feature — see the commit convention).
- Point to the convention/architecture to preserve (`AGENTS.md`, `docs/06`).
- Ask the AI to explain the *why* of the refactor (candidate for an ADR if structural).

---

### #p1 — (example) Extract logic from the ViewModel

- Tool: <...>
- Date: 2026-05-19
- Context: `docs/06`, `docs/08`, target file

**Prompt:**
> Extract the <X> logic from `XxxViewModel` into a UseCase in `domain/usecase`,
> keeping the behavior and the tests passing. Do not change the screen's public API.
> Explain why it improves testability. Small change, only this refactor.

**Result:** <fill in>
**Did the tests stay green?** <yes/no>
**Assessment:** <...>

---

<!-- new entries below -->
