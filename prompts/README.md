# prompts/

Record of the **prompts** used with AI. It is not decoration: it is assessed (see `docs/16`).

## Rules

- Save **non-trivial** prompts (the ones that produced real code/decisions).
- Give an **id** to each entry (`#p1`, `#p2`, …) and reference it in `docs/14_ai_usage_log.md`
  and, if applicable, in the commit/PR.
- Also record what did **not** work — prompts that failed teach as much as those that worked.

## Format of each entry

```
### #pN — <short title>
- Tool: <Claude Code | Antigravity | Codex | Copilot>
- Date: YYYY-MM-DD
- Context given to the AI: <files/docs referenced>

**Prompt:**
> (exact text of the prompt)

**Result:** <summary>
**Assessment:** Accepted / Edited / Rejected — why
```

Files: `compose_prompts.md`, `architecture_prompts.md`, `debugging_prompts.md`, `refactoring_prompts.md`.
