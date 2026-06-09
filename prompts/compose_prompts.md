# Prompts — Jetpack Compose / UI

Format: see `prompts/README.md`. Record UI prompts (Composables, state, theme, navigation).

## Good prompt practices for Compose

- State the `UiState` and the `Screen(state, onAction)` contract — see `docs/08`.
- Ask for *stateless composables* + preview; state in the ViewModel.
- Mention Material 3 and the states (loading/empty/error/offline/content).
- Point the tool to `docs/04_screens_and_ui.md` and to the existing component to reuse.

---

### #p1 — M1, navigation shell

- Tool: Antigravity
- Date: 2026-06-9
- Context given to the AI: `docs/04`, `docs/08`, `ui/components/`, `SampleScreen.kt`

**Prompt:**
Using docs/05_navigation.md and docs/04_screens_and_ui.md, implement the navigation shell for Diminuendo:

NavGraph.kt in ui/navigation/ with type-safe routes: auth, heatmap, capture, diary, ai_consultant, paywall
Bottom navigation bar with 4 icons (heatmap, capture, diary, ai_consultant) using launchSingleTop = true, restoreState = true, and popUpTo as specified in docs/05
Each destination as a completely empty placeholder Composable — no business logic, just a Text("ScreenName") centered on screen
Deep link diminuendo://capture registered on the capture route
AuthScreen as the start destination, outside the bottom bar
Dark/Light theme scaffold from docs/04 design system (color tokens only, no full implementation yet)

The app must navigate between all screens without crashes. Do not implement any ViewModel or state yet. assembleDebug must stay green.

**Result:** <fill in>
**Assessment:** <Accepted/Edited/Rejected — why>

---

<!-- new entries below -->
