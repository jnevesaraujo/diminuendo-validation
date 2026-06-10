# Prompts — Debugging

Prompts to diagnose errors. Include the **exact error/stacktrace** in the prompt.

## Good practices

- Paste the full error message / stacktrace and the relevant code.
- State what you have already tried and the expected vs observed behavior.
- Ask for the **root cause**, not just the patch. Be suspicious of "fixes" that silence the symptom.
- Always verify the explanation — the AI may hallucinate plausible but wrong causes
  (record it in `docs/14_ai_usage_log.md`).

---

### #p1 — Debugging screen navigation

- Tool: Antigravity
- Date: 2026-05-19

**Prompt:**
Add a temporary "Skip Auth (Debug)" TextButton to the Auth placeholder screen that navigates to the heatmap route. Mark it with a // TODO: remove before final build comment. Do not touch any other file.

The deep link diminuendo://capture fails with "unable to resolve Intent". The NavGraph may have the deep link declared but the AndroidManifest.xml is missing the corresponding <intent-filter> on the MainActivity. Add the intent filter to the manifest. Do not change any other file. Verify the adb command adb shell am start -W -a android.intent.action.VIEW -d "diminuendo://capture" dam.a50274.diminuendo resolves correctly after the fix.

**Identified cause:** Placeholder auth screen doesn't allow navigation to test remaining ones and deep link is failing.
**Fix:** The assembleDebug background task just finished, and the build remains completely green! The new Android manifest intent filter and the Debug button are working perfectly without causing any compilation issues.

Everything is in place. You should now be able to run that adb shell am start command in your local environment and see it immediately route to the empty Capture screen. Let me know what you'd like to work on next!
**Did the AI get the cause right?** yes

---

<!-- new entries below -->
