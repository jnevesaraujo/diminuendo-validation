# 05 — Navigation

> Navigation Compose. Type-safe routes. Define the graph before implementing screens.

## Navigation graph

```
start = Home
Home ──> Detail/{id}
Home ──> Profile
Detail ──> Paywall   (if paid action and free user)
Profile ──> Settings
```

(Replace with your real graph — diagram or list.)

## Routes

| Route | Arguments | Screen | Access |
|---|---|---|---|
| `home` | — | HomeScreen | free |
| `detail/{id}` | `id: String` | DetailScreen | free |
| `paywall` | — | PaywallScreen | — |
| `settings` | — | SettingsScreen | free |

## Decisions

- Route definitions: see `app/.../ui/navigation/`.
- *Deep links*? <yes/no, which>
- *Back stack* and *single top*: <rules>
- Plan-conditional navigation (free vs paid): where it is decided (ViewModel/UseCase),
  **not** scattered across the Composables.
