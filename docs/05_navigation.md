# 05 — Navigation

> Navigation Compose. Type-safe routes. Define the graph before implementing screens.

## Navigation graph

```
[ AuthScreen (Gateway) ]
                     │
                     ▼
          [ HeatmapScreen (Home) ] ◄───► [ CaptureScreen ]
                     │                           ▲
                     ├───► [ PaywallScreen ]     │ (Deep Link Alert)
                     │                           │
                     ├───► [ DiaryScreen ] ──────┘
                     │
                     └───► [ AiConsultantScreen ]
                                 │
                                 └───► [ PaywallScreen ] (If query limit reached)
```

## Routes

| Route | Arguments | Screen | Access |
|---|---|---|---|
| `auth` | — | AuthScreen | free |
| `heatmap` | — | HeatmapScreen (Home) | free with busy times as premium feature |
| `capture` | — | CaptureScreen | free |
| `diary` | — | DiaryScreen | free |
| `ai_consultant` | — | AiConsultantScreen | Free (Daily limits apply) |
| `paywall` | — | PaywallScreen | free |

## Decisions

- Route definitions: All navigation paths, sealed classes/objects for type-safe routing, and destination arguments are centrally configured in `app/src/main/java/dam/a50274/diminuendo/ui/navigation/NavGraph.kt`.

- *Deep links*: Yes. A custom deep link (`diminuendo://capture`) is configured to handle noise hazard notifications. When a user clicks an automated exposure alert notification, the system bypasses the home view and opens the Capture Screen immediately to display real-time decibel tracking.

- *Back stack* and *single top*: Standard bottom navigation behavior is enforced across the main application shell (`heatmap`, `capture`, `diary`, `ai_consultant`). To prevent the back stack from accumulating duplicate screen states when a user clicks the icons repeatedly, navigation hooks utilize:

```
launchSingleTop = true
restoreState = true
popUpTo(navController.graph.findStartDestination().id) {
    saveState = true
}
```
- Plan-conditional navigation (free vs paid): Subscription validation is decoupled from the UI layers. The choice to display the paywall or grant access to advanced components (like the "Busy Hours" temporal charts or unlimited AI messaging) is evaluated within the `HeatmapViewModel` and `AiViewModel` by monitoring a dedicated reactive state stream coming from a centralized `SubscriptionUseCase`. The Composable functions simply observe this boolean UI state and execute standard conditional layouts (`if (isPremium) { ... } else { navController.navigate("paywall") }`).
