# 14 — AI Usage Log (ongoing)

> Update **as you use AI**, not at the end. It is the proof of *how* the work evolved.
> The full prompt goes to `prompts/`; here goes the summary and the critical decision.
> The `AI-Assisted:` *trailer* in commits links to this log.

## How to fill in

One line per relevant interaction (non-trivial ones). Be honest about what you **rejected**.

| Date | Tool | Goal | Prompt (ref. in `prompts/`) | Result | Accepted / Rejected / Edited | Why |
|---|---|---|---|---|---|---|
| 2026-06-09 | Antigravity | M0 scaffold | `architecture_prompts.md#p1` | Full skeleton compiled | Edited | Rename missing packages and added TypeConverters |
| 2026-06-09 | Antigravity | M1 navigation shell | `compose_prompts.md#p1` | Auth screen compiled: unable to verify rest and deep link failed | Edited | Made debug prompt |
| 2026-06-09 | Antigravity | Debug Navigation | `debugging_prompts.md#p1` | All screens compiled and deep link returned ok | Edited | Made debug prompt |
| 2026-06-10 | Antigravity | M3 mock user | `architecture_prompts.md#p2` | Hardcoded mock userId for local testing before Firebase Auth | Accepted | Temporary; will be replaced in M2 with real Firebase Auth uid |
| | | | | | | |

## AI errors / hallucinations detected

| Date | What the AI got wrong | How it was noticed | Fix |
|---|---|---|---|
| | | | |

> The final reflection (what went well/badly overall) goes to `docs/15_postmortem.md`.
