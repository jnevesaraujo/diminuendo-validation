# 10 — Security and Permissions

## Secrets management (mandatory)

- **No** key/secret committed. `.gitignore` already ignores `local.properties` and `secrets.properties`.
- Flow: `local.properties` (not versioned) → read in `build.gradle.kts` → `BuildConfig.XXX`.
- Provide `local.properties.example` (without real values) so the team knows which keys to fill in.

```properties
# local.properties.example
AI_API_KEY=put_your_key_here
BACKEND_URL=https://...
```

> If a key is committed by mistake: **revoke and rotate** the key; deleting the file is not enough.

## Android permissions

| Permission | Why | Runtime? |
|---|---|---|
| `INTERNET` | Network / AI | no |
| `ACCESS_NETWORK_STATE` | Detect offline | no |
| `RECORD_AUDIO` | If using audio | yes |
| `CAMERA` | If using image/video | yes |

- Request runtime permissions at the moment of use, with rationale and handling of denial.

## User data

- Which personal data is stored and where (Room/DataStore/backend).
- Encryption if applicable (EncryptedDataStore / SQLCipher) — justify in an ADR if used.

## Authentication / authorization

- How the user is identified (anonymous? account?).
- How access to paid features is validated (server-side vs only a local flag — limitations).

## Checklist

- [ ] No secrets in git history
- [ ] `local.properties.example` present and up to date
- [ ] Minimum necessary permissions
- [ ] Handling of permission denial
