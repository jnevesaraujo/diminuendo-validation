# 16 — Evaluation Rubric

> How the project is evaluated. In an **AI Engineering** project the **process** counts
> as much as the product. Indicative weights — the instructor confirms the final values.

## Criteria

| # | Criterion | Weight | What is evaluated |
|---|---|---:|---|
| 1 | **Mandatory requirements** | 20% | 3–5 screens, shared state, AI, multimedia, simulated freemium, offline |
| 2 | **Architecture & code quality** | 20% | MVVM + Repository, layers, no duplicated logic, conventions, clean lint |
| 3 | **Correct stack** | 10% | Compose/M3, Navigation, ViewModel/StateFlow, Hilt/Koin, Retrofit/Ktor, Room, DataStore, Coil |
| 4 | **Tests & CI** | 10% | Relevant tests, CI green, Definition of Done met |
| 5 | **Context engineering & AI usage** | 15% | `AGENTS.md` followed, prompts logged (`prompts/`), `14_ai_usage_log.md` honest |
| 6 | **Documentation (`docs/`, ADRs)** | 10% | Coherent, up to date, justified decisions |
| 7 | **Critical reflection (`15_postmortem.md`)** | 10% | Honest analysis of working with AI, lessons |
| 8 | **Functioning & UX** | 5% | Stable app, states handled, basic accessibility |

## Levels (per criterion)

- **Insufficient** — absent or non-functional.
- **Sufficient** — meets the minimum.
- **Good** — meets it well, with care and coherence.
- **Excellent** — meets it, justifies decisions and demonstrates critical thinking.

## Penalties

- Committed secrets.
- Git history without convention / without AI usage log.
- AI-generated code **not understood** by the team (checked in the discussion).
- `main` does not build / CI red at delivery.

## Discussion / defense

The team must be able to **explain any line** of the code, including AI-generated code.
Not understanding your own generated code is penalized regardless of whether it works.
