# How to Contribute

This document describes the **human workflow**. The rules for AI are in
[`AGENTS.md`](AGENTS.md).

## 1. Set up your AI tool

The **single source of context is `AGENTS.md`**. Do not duplicate its content.

Some tools read `AGENTS.md` natively (Codex, Cursor, Aider...). Others
need a file with a specific name. Instead of copying the content (which then
diverges), create a **local pointer** (symlink) — it is in `.gitignore`, not versioned:

```bash
# Automatic (recommended)
./scripts/setup-ai-context.sh        # Linux/macOS
./scripts/setup-ai-context.ps1       # Windows (PowerShell)

# Or manually:
ln -s AGENTS.md CLAUDE.md                          # Claude Code
ln -s AGENTS.md .github/copilot-instructions.md    # GitHub Copilot
```

> **Auto-load vs explicit instruction.** A file that the tool *auto-loads*
> (e.g. `CLAUDE.md` in Claude Code) enters the context in every session — robust.
> Just *telling* it "follow AGENTS.md" works but is fragile (it gets lost in long sessions
> or with context compaction). That is why we use auto-loaded pointers.
> **Never run `mv AGENTS.md CLAUDE.md`** — that fragments the class, reintroduces a
> *vendor* file into the history and breaks template updates.

## 2. Branches

- `main` — always compilable and with passing tests.
- `feature/<short-description>` — one feature.
- `fix/<short-description>` — a fix.

Do not *push* directly to `main`; use a Pull Request.

## 3. Commit convention

We use **Conventional Commits** with a mandatory AI-assistance *trailer*:

```
<type>(<scope>): <imperative summary, ≤ 72 chars>

<optional body: explain the WHY, not the what>

AI-Assisted: <Claude Code | Antigravity | Codex | Copilot | none>
```

**Types:** `feat` · `fix` · `refactor` · `test` · `docs` · `chore` · `perf` · `build` · `ci`

Examples:

```
feat(auth): add anonymous login with Firebase

AI-Assisted: Claude Code
```

```
refactor(player): extract PlaybackController from the ViewModel

Reduces the ViewModel's responsibilities and isolates audio logic
to allow unit testing.

AI-Assisted: none
```

The `AI-Assisted:` *trailer* links the commit to the log in `docs/14_ai_usage_log.md`
without duplicated work.

## 4. AI usage logging

Whenever you use AI to produce non-trivial code:

1. Save the relevant prompt in the appropriate file in [`prompts/`](prompts/).
2. Add a line in [`docs/14_ai_usage_log.md`](docs/14_ai_usage_log.md).
3. Indicate the tool in the commit's `AI-Assisted:` *trailer*.

## 5. Pull Requests

- The PR uses the template (`.github/pull_request_template.md`), which **is the Definition of Done**.
- The CI (build + tests + lint) must pass.
- Check **all** the checklist items before requesting a review.

## 6. Code style (ktlint)

The style is **checked automatically** by ktlint (`./gradlew ktlintCheck`, also in CI)
and auto-fixable with `./gradlew ktlintFormat`.

The canonical configuration is in **`.editorconfig`** (line `ktlint_code_style = intellij_idea`):

- We deliberately use the **`intellij_idea`** style, not `ktlint_official`.
  `ktlint_official` (the plugin default) is too aggressive with idiomatic
  Jetpack Compose and Gradle KTS code — it forces line breaks and reindentations that make
  the code *less* readable and generate constant noise in CI. `intellij_idea` aligns
  with the Android Studio formatter, which is what you will use day to day.
- **Do not switch to `ktlint_official`** without recording an ADR — it would change the formatting
  of practically every file and break the team's history/PRs.
- `function-naming` and `filename` are disabled on purpose: `@Composable` uses
  PascalCase and tests use names with backticks/spaces — both legitimate here.

## 7. Definition of Done

See section 3 of [`AGENTS.md`](AGENTS.md). Summary: compiles · tests pass · lint passes ·
UI works · docs updated · prompts recorded · architecture preserved · decisions recorded.
