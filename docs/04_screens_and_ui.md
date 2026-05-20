# 04 — Screens and UI

> 3 to 5 main screens. Material 3. Think about states: *loading*, *empty*, *error*, *content*, *offline*.

## Screen inventory

| Screen | Goal | Inputs | Main actions | Shared state? |
|---|---|---|---|---|
| Home | | | | |
| ... | | | | |

## For each screen

### Screen: <Name>
- **User stories:** US-xx
- **Layout (wireframe / sketch):** _link or ASCII_
- **Compose components:** `XxxScreen`, `XxxContent`, `XxxViewModel`
- **UI states:**
  - Loading: ...
  - Empty: ...
  - Error: ... (message + retry action)
  - Offline: ... (banner / degraded mode)
  - Content: ...
- **Paywall?** If so, what changes between free and paid.
- **Multimedia:** which image/audio/video appears and how it is loaded (Coil / player).

## Design system

- Colors / typography / theme (light/dark) — see `app/.../ui/theme/`.
- Reusable components: list in `app/.../ui/components/`.

## Accessibility

- [ ] `contentDescription` on meaningful images/icons
- [ ] Touch targets ≥ 48dp
- [ ] Sufficient contrast / dark theme support
