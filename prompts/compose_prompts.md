# Prompts — Jetpack Compose / UI

Format: see `prompts/README.md`. Record UI prompts (Composables, state, theme, navigation).

## Good prompt practices for Compose

- State the `UiState` and the `Screen(state, onAction)` contract — see `docs/08`.
- Ask for *stateless composables* + preview; state in the ViewModel.
- Mention Material 3 and the states (loading/empty/error/offline/content).
- Point the tool to `docs/04_screens_and_ui.md` and to the existing component to reuse.

---

### #p1 — (example) Stateless list screen

- Tool: <...>
- Date: 2026-05-19
- Context given to the AI: `docs/04`, `docs/08`, `ui/components/`, `SampleScreen.kt`

**Prompt:**
> Create a Composable `XxxScreen(state: XxxUiState, onAction: (XxxAction) -> Unit)` in
> Material 3, stateless, with loading/empty/error/content states, reusing
> `LoadingIndicator` and `ErrorView` from `ui/components`. Include `@Preview`. Follow the
> pattern of `SampleScreen.kt`. Do not access repositories directly.

**Result:** <fill in>
**Assessment:** <Accepted/Edited/Rejected — why>

---

<!-- new entries below -->
