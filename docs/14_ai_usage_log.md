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
| 2026-06-09 | Antigravity | Debug Navigation | `debugging_prompts.md#p1` | All screens compiled and deep link returned ok | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M3 mock user | `architecture_prompts.md#p2` | Hardcoded mock userId for local testing before Firebase Auth | Accepted | Temporary; will be replaced in M2 with real Firebase Auth uid |
| 2026-06-10 | Antigravity | M2 Firebase Auth | `architecture_prompts.md#p3` | Created classes for Firebase Auth | Edited | Included debug prompt |
| 2026-06-09 | Antigravity | Debug Firebase Auth | `debugging_prompts.md#p2` | Missed some requirements | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M4 Firestore heatmap | `architecture_prompts.md#p5` | Firestore snapshot listener, Room SSOT, heatmap overlay, weighted average update | Edited | Included Debug prompt |
| 2026-06-09 | Antigravity | Debug Firestore heatmap | `debugging_prompts.md#p3&p4&p5&p6` | Missed some requirements | Accepted | Completed debug prompt |
| 2026-06-10 | Antigravity | M5 paywall | architecture_prompts.md#p7 | Paywall and Busy Hours working. Gemini daily limit not wired — AiViewModel was a stub. Location never implemented in capture pipeline | Edited | Two follow-up prompts required: location fix and Gemini implementation |
| 2026-06-11 | Antigravity | M7 test suite — CheckEntitlementUseCase, HeatmapViewModel, MeasurementMapper, MeasurementRepository | architecture_prompts.md#p9 | All test cases generated, ./gradlew test green | Accepted / Edited | <note anything the agent got wrong or any production code it changed> |
| | | | | | | |

## AI errors / hallucinations detected

| Date | What the AI got wrong | How it was noticed | Fix |
|---|---|---|---|
| | | | |

> The final reflection (what went well/badly overall) goes to `docs/15_postmortem.md`.
