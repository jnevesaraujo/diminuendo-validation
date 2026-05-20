# ADR 0001 — Dependency Injection: Hilt vs Koin

> Example ADR, **already filled in**, to serve as a model. The team can keep,
> change or replace it — but the final decision must be recorded here.

- **Status:** Accepted
- **Date:** 2026-05-19
- **Deciders:** Project team
- **AI assistance:** none

## Context

The mandatory stack requires a DI framework (**Hilt or Koin**). We need to decide
on one and follow it consistently across the whole application (data/domain/ui layers).

## Options considered

1. **Hilt**
   - Pros: pattern recommended by Google for Android, integration with ViewModel/Compose,
     compile-time verification, lots of learning material.
   - Cons: relies on annotations + KSP/kapt; error messages sometimes dense.
2. **Koin**
   - Pros: simple API in pure Kotlin, no code generation, fast startup.
   - Cons: runtime resolution (errors only at execution), less "idiomatic Android" for some.

## Decision

We adopt **Hilt**. The skeleton in `app/` is already configured with Hilt
(`@HiltAndroidApp`, `@HiltViewModel`, modules in `di/`). Compile-time verification
reduces wiring errors that are hard to diagnose — relevant in a project
with AI-assisted generation.

## Consequences

- Positive: setup already done; consistency guaranteed; DI errors caught at compile time.
- Negative: dependency on KSP; the team must understand the module graph.
- Impact: `docs/06_architecture.md` (DI section) reflects this choice. Switching to Koin
  would require a new ADR (superseding this one) and refactoring the `di/` modules.
