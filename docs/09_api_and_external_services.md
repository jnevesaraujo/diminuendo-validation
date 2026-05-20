# 09 — APIs and External Services

> Includes the AI integration (mandatory) and the state-sharing backend.

## Services used

| Service | Purpose | Authentication | Free tier? |
|---|---|---|---|
| AI (<OpenAI/Gemini/local …>) | RF-03 | API key | |
| Shared state backend (<Firebase/Supabase/API>) | RF-02 | | |
| Multimedia (<storage/CDN>) | RF-04 | | |

## AI integration

- **Mode:** remote API _or_ installed local model (e.g.: on-device / Gemini Nano / TFLite).
- Endpoint / SDK: <...>
- Input/output: <text, image, audio…>
- **Offline degradation:** what happens without network (cache responses? disable the feature?).
- **Cost / limits:** *rate limits*, *quota*; is it a paid feature? (link to RF-05).

## API contracts (summary)

| Method | Endpoint | Request | Response | Errors |
|---|---|---|---|---|
| GET | `/xxx` | — | `XxxDto[]` | 401, 5xx |
| POST | `/xxx` | `XxxDto` | `XxxDto` | |

## Network error handling

- Timeouts, retry/backoff, mapping to `error` in the UiState.
- No network → serve Room (see doc 06/08).

## Secrets

- Keys **never** in the repository. See `docs/10_security_and_permissions.md`.
- Defined in `local.properties` → exposed via `BuildConfig`.
