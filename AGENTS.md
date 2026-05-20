# AGENTS.md — Single Source of Context for AI Agents

> **This is the single, canonical source of context for this project.**
> Read this file **in full before generating or changing any code**.
> It is tool-neutral: it serves Claude Code, Antigravity, Codex, Cursor, Copilot or any other.
> Do not duplicate this content in other files. Tools that require a different name should
> use a *pointer* (symlink) — see `CONTRIBUTING.md` and `scripts/setup-ai-context.sh`.

---

## 1. What this project is

A **native Android application (Kotlin + Jetpack Compose)**, the final project of the
course unit. The topic was agreed in advance with the instructor and is described in
`docs/01_project_vision.md`. **Read the documentation in `docs/` first, before writing code.**

### Mandatory product requirements

The application **must**:

1. Have **3 to 5 main screens**.
2. Support **state sharing between users** (data that more than one user sees/edits).
3. Use **AI** — via a remote API **or** a locally installed model.
4. Use **image, audio or video** (at least one).
5. Have **paid features** with a *freemium* model: free usage + paid subscription **(simulated)**.
6. Support **offline features**.

### Mandatory technology stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel · **MVVM** ·
StateFlow · **Repository Pattern** · Hilt **or** Koin · Retrofit **or** Ktor · Coil ·
Room · DataStore.

---

## 2. Context Engineering — rules for the agent

Before producing code, **the agent must**:

- **Read the documentation first.** Consult the relevant files in `docs/` (especially
  `06_architecture.md`, `07_data_model.md`, `08_state_management.md`) before proposing code.
- **Follow the existing architecture.** Respect the `data / domain / ui` layers and the
  MVVM pattern already present in `app/`. Do not introduce an alternative architecture without an approved ADR.
- **Do not duplicate logic.** Look for existing code that solves the problem before creating new code.
  Reuse Repositories, UseCases, UI components and utilities.
- **Preserve naming conventions.** Follow the names, packages and style already in use.
  The style is checked by `ktlint`/`.editorconfig` — do not go against it. The style is
  `ktlint_code_style = intellij_idea` (defined in `.editorconfig`, do **not** change to
  `ktlint_official`). Run `./gradlew ktlintFormat` before finishing. See `CONTRIBUTING.md` §6.
- **Generate small, cohesive changes.** One change = one purpose. Avoid giant *commits*
  that mix refactor + feature + formatting.
- **Explain non-trivial decisions.** When there is a relevant design choice (e.g. cache,
  threading, offline strategy), explain why and record it in an ADR (`docs/adr/`).
- **Do not invent requirements.** If something is ambiguous, ask or flag it as a TODO — do not
  assume features that are not in the documentation.
- **Do not introduce new dependencies** without registering them in `gradle/libs.versions.toml` and
  justifying them (ADR or PR description).
- **Security:** never put API keys, secrets or tokens in versioned code.
  Use `local.properties` / `BuildConfig` / variables — see `docs/10_security_and_permissions.md`.

---

## 3. Definition of Done

A feature is only **complete** when **all** of the following hold:

- [ ] The code **compiles** (`./gradlew assembleDebug`).
- [ ] The **tests pass** (`./gradlew test`) and there is a test for the relevant new logic.
- [ ] The **lint passes** (`./gradlew ktlintCheck` / `detekt`).
- [ ] The **UI works** correctly (verified at runtime/preview).
- [ ] The affected **documentation** in `docs/` was updated.
- [ ] The **prompts** used were recorded in `prompts/` and the AI usage in `docs/14_ai_usage_log.md`.
- [ ] The **architecture was preserved** (layers, MVVM, no duplicated logic).
- [ ] Non-trivial decisions were recorded (ADR or commit/PR body).

The same checklist is in the Pull Request template — it is a real gate, not decoration.

---

## 4. Commit convention

Conventional Commits + an indication of AI assistance. See `CONTRIBUTING.md`.

```
<type>(<optional scope>): <imperative summary>

<optional body: the why>

AI-Assisted: <Claude Code | Antigravity | Codex | Copilot | none>
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `perf`, `build`, `ci`.

---

## 5. Repository map

| Path | What for |
|---|---|
| `app/` | Compilable Android skeleton, with 1 example *feature* end-to-end (follows this architecture). |
| `docs/01..16` | Engineering design. **Read before coding.** |
| `docs/adr/` | Architecture Decision Records — non-trivial decisions. |
| `prompts/` | Record of the prompts used (Compose, architecture, debugging, refactoring). |
| `AGENTS.md` | **This file.** Single source of context. |
| `README.md` | Public face of the project (filled in by the student). Not for AI instructions. |
| `CONTRIBUTING.md` | Human workflow: branches, commits, AI logging, PRs. |
| `.github/` | CI (build+test+lint) and the PR template (= Definition of Done). |
| `scripts/` | `setup-ai-context.*` creates the local pointers for your tool. |

---

## 6. Expected workflow for a task

1. Read the issue/goal and the relevant documentation in `docs/`.
2. Plan: which layers you touch, which files, which decisions.
3. Implement in small, cohesive changes, following the existing architecture.
4. Write/update tests.
5. Run `./gradlew assembleDebug test ktlintCheck`.
6. Update the affected `docs/`; record prompts in `prompts/` and AI usage in `docs/14_ai_usage_log.md`.
7. Check the Definition of Done.
8. Commit (Conventional Commits + `AI-Assisted:`), then PR with the checklist.
