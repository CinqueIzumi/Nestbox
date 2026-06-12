# Nestbox

Android app (Jetpack Compose, Koin DI, Voyager navigation) using the home-grown **TOAD**
presentation architecture.

## Before implementing a feature

Read these first, so you don't have to reverse-engineer the existing code:
- [`docs/architecture.md`](docs/architecture.md) - the data/domain/presentation layering, where each
  type belongs, and the `core/auth` worked example.
- [`docs/toad-architecture.md`](docs/toad-architecture.md) - the TOAD presentation pattern
  (state/event/action/collector/dependencies/screen-model) and a step-by-step checklist for adding
  a feature.
- [`docs/code-style.md`](docs/code-style.md) - naming, file layout (one declaration per file),
  comments, Compose formatting, whitespace, visibility, and test structure.
- [`docs/design-system.md`](docs/design-system.md). The visual and interaction language for the
  Doveletter reader: the "reading room for developers" tone, color roles, the reader and mono
  typography scales (`MaterialTheme.readerTypography`), layout primitives, components, and patterns.
  Carries a maintenance rule: a change to a foundation, component, or pattern updates the doc in the
  same change.

## Build config

- GitHub auth uses the OAuth **device flow**; it needs `GITHUB_CLIENT_ID` in `local.properties`
  (surfaced via `BuildConfig.GITHUB_CLIENT_ID`). The client id is not a secret but is kept out of
  version control.
