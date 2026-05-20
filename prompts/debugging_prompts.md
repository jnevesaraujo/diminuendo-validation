# Prompts — Debugging

Prompts to diagnose errors. Include the **exact error/stacktrace** in the prompt.

## Good practices

- Paste the full error message / stacktrace and the relevant code.
- State what you have already tried and the expected vs observed behavior.
- Ask for the **root cause**, not just the patch. Be suspicious of "fixes" that silence the symptom.
- Always verify the explanation — the AI may hallucinate plausible but wrong causes
  (record it in `docs/14_ai_usage_log.md`).

---

### #p1 — (example) Crash when opening a screen

- Tool: <...>
- Date: 2026-05-19

**Prompt:**
> The app crashes when navigating to DetailScreen. Stacktrace:
> ```
> <paste stacktrace>
> ```
> Relevant code: `<paste>`. Expected: open the detail. I already tried <...>.
> Identify the root cause and propose the minimal fix, without changing the architecture.

**Identified cause:** <fill in>
**Fix:** <fill in>
**Did the AI get the cause right?** <yes/no — if not, what it was>

---

<!-- new entries below -->
