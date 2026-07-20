# Nestbox

Android app (Jetpack Compose, Koin DI, Voyager navigation) built on the shared **nl.rhaydus
foundation**, using its **TOAD** presentation architecture.

## Before implementing a feature

Read these first, so you don't have to reverse-engineer the existing code:
- [`docs/architecture.md`](docs/architecture.md) - the data/domain/presentation layering, where each
  type belongs, and the `core/auth` worked example.
- [`docs/toad-architecture.md`](docs/toad-architecture.md) - the TOAD presentation pattern
  (state/event/action/collector/dependencies/screen-model) and a step-by-step checklist for adding
  a feature. The runtime itself ships in `nl.rhaydus:toad`.
- [`docs/rhaydus/0.3.1/CAPABILITIES.md`](docs/rhaydus/0.3.1/CAPABILITIES.md) - what the foundation
  already provides, so you reuse it instead of hand-rolling a second copy.
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
- The foundation version is pinned once in `gradle/libs.versions.toml` (`rhaydusFoundation`), like
  every other dependency; the modules are declared as `libs.rhaydus.*` aliases.
- Foundation constraints on this build: **minSdk 26** (the foundation libraries declare it) and
  **Kotlin >= 2.3.21** (a compiler cannot read metadata from a newer one).

<!-- rhaydus:start -->
## Rhaydus foundation (managed by rhaydus-adopt - do not hand-edit)

This project consumes the nl.rhaydus foundation. Capabilities index:
[`docs/rhaydus/0.3.1/CAPABILITIES.md`](docs/rhaydus/0.3.1/CAPABILITIES.md).

**Consumed modules** (pinned at `0.3.1` via `rhaydusFoundation` in `gradle/libs.versions.toml`):
- `libs.rhaydus.toad` - the entire TOAD runtime, imported from `nl.rhaydus.toad`. Nestbox has no
  local copy; do not recreate one.
- `libs.rhaydus.core.common` - `AppDispatchers`, `runCatchingCancellable`, and `AppLog`, the logging
  facade. `AppLog.install(...)` runs in `NestboxApplication`; log through it, never `android.util.Log`
  or `println`.
- `libs.rhaydus.core.platform` - `SecureStorage` / `AndroidSecureStorage`, which custodies the GitHub
  access token (`nl.rhaydus.platform`).
- `libs.rhaydus.designsystem.core` - `RhaydusTheme`, `BottomBarScaffold`, `LocalBottomBarPadding` /
  `rememberBottomBarPadding` (`nl.rhaydus.designsystem.*`).
- `libs.rhaydus.ktlint.rules` - the 14-rule ktlint ruleset, driven from the root build as
  `ktlintFormat` / `ktlintCheck` (see the style gates below). Not a compile dependency.

Not consumed (yet): `designsystem-editorial` (deliberately - the app's visual design is not settled,
so it keeps its own widgets for now), `designsystem-image`, `offline-sync`, `detekt-rules`. The
foundation's shared
`nl.rhaydus:catalog` is also not consumed -
Nestbox deliberately pins the androidx/Compose stack ahead of the foundation's set, so `libs` stays
the single catalog here (`settings.gradle.kts` records how to add it back).

**Foundation docs** (vendored at the pinned version, do not hand-edit):
[`CAPABILITIES.md`](docs/rhaydus/0.3.1/CAPABILITIES.md) ·
[`architecture.md`](docs/rhaydus/0.3.1/architecture.md) ·
[`toad-architecture.md`](docs/rhaydus/0.3.1/toad-architecture.md) ·
[`code-style.md`](docs/rhaydus/0.3.1/code-style.md) ·
[`design-system-foundations.md`](docs/rhaydus/0.3.1/design-system-foundations.md)

**This app's design system (brand):** [`docs/design-system.md`](docs/design-system.md). Where a
Nestbox doc under `docs/` and a vendored foundation doc differ, the Nestbox one wins - it describes
this app specifically.

**Inner loop:** `foundation.local=true` in `local.properties` composes `../rhaydus-foundation` so
foundation edits land here without a publish cycle. Currently blocked: the foundation's build-logic
pins AGP 9.0.0 and this app is on 9.1.1, and Gradle refuses two AGP versions in one composite build.
Align the AGP versions to switch it on; until then the pinned Maven Central artifacts are the only
path.

**How to develop here:**
- New feature / screen **logic** (state, actions, use cases, data) -> **rhaydus-logic** agent.
- New feature / screen **UI** (Compose render, design system) -> **rhaydus-ui** agent (it reads both
  the foundation design system and this app's design doc above).
- A logic-only or UI-only change uses just that one agent; a full new screen goes logic -> UI
  (rhaydus-logic emits the state/action contract, then rhaydus-ui renders it).
- Review -> **code-reviewer**. Tests -> **unit-test-writer**. Style gates -> the **style-check** skill,
  which drives `./gradlew ktlintFormat` (auto-fix) and `./gradlew ktlintCheck` (gate, also wired into
  `check`). `-Pktlint.root=<dir>` scopes a run to one directory.
- Reuse-first: check the capabilities index before hand-rolling a component, modifier, or util.

_Re-run `rhaydus-adopt` after changing any `nl.rhaydus` dependency or version - it refreshes this
block and re-vendors the docs so both track what the project actually pins._
<!-- rhaydus:end -->
