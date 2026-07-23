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
- The publications are read from a private repository named by `DOVELETTER_REPOSITORY`
  (`owner/name`) and `DOVELETTER_BRANCH` (defaults to `main`) in `local.properties`.
- **`DOVELETTER_TOKEN` is a debug-only escape hatch.** The `doveletter` organisation disallows OAuth
  apps, so a device-flow token cannot see the repository; a subscriber's own personal access token
  in `local.properties` is seeded into `SecureStorage` on first read by
  `DebugSeedingTokenLocalDataSource`, which is only wired up when `BuildConfig.DEBUG` is set and the
  field is non-blank. Release builds declare the field empty, so no shipped build carries a
  credential. This exists until a token-entry screen replaces it; it is not the end state.
- The foundation version is pinned once in `gradle/libs.versions.toml` (`rhaydusFoundation`), like
  every other dependency; the modules are declared as `libs.rhaydus.*` aliases.
- Foundation constraints on this build: **minSdk 26** (the foundation libraries declare it) and
  **Kotlin >= 2.3.21** (a compiler cannot read metadata from a newer one).

## Feature logic

ALWAYS delegate feature logic to the `rhaydus-kotlin:rhaydus-logic` agent, regardless of how small the
change appears. "Feature logic" is anything in the TOAD presentation contract (`UiState`, `UiAction`,
`UiEvent`, `LocalVariables`, `ActionDependencies`, `*ScreenModel`, the `flows/` collectors), the domain
layer (use cases, repository interfaces, domain models), the data layer (repository impls, data
sources, mappers, DTOs), or the Koin `di/` module that wires them. A single new `UiAction`, one field
added to a state class, one use case - all of it goes to the agent. Never write or modify these types
directly in the main conversation. This rule has no exceptions.

The agent owns the state/action contract, so its report MUST spell out the resulting `UiState` and
`UiAction` declarations verbatim. That output is the input to the UI step below; do not paraphrase it.

Scope the brief tightly to keep token use down: name the feature package, paste the exact file paths
you already know, and say which docs it can skip. Do the `grep` yourself first rather than making the
agent rediscover call sites. Delegating does not replace reading
[`docs/toad-architecture.md`](docs/toad-architecture.md) yourself - read it, then brief the agent.

## Feature UI

ALWAYS delegate Compose rendering to the `rhaydus-kotlin:rhaydus-ui` agent, regardless of how small the
change appears. "Rendering" is any `*Screen` composable and its sub-composables, any shared component,
and any change to theme, typography, color roles, spacing, or layout. A padding tweak, a swapped
typography role, one extra row in a card - all of it goes to the agent. Never write or modify Compose
UI directly in the main conversation. This rule has no exceptions.

The agent CONSUMES the state/action contract and never invents new state or actions. Paste the
`UiState` / `UiAction` declarations into its brief. If a render genuinely needs state that does not
exist yet, stop and go back to `rhaydus-kotlin:rhaydus-logic` for it rather than letting the UI agent
add it.

A full new screen runs logic first, then UI, carrying the logic agent's contract into the UI brief. A
UI-only change uses this agent alone.

Tell the agent that [`docs/design-system.md`](docs/design-system.md) wins over the vendored foundation
design doc wherever they differ, and that `designsystem-editorial` is deliberately not consumed here,
so it must not reach for editorial components that this build does not have on the classpath.

## Review

**For substantial Kotlin changes, delegate to the `rhaydus-kotlin:code-reviewer` agent before
reporting the work done.** "Substantial" is a new file, a new feature package, a change spanning
multiple files, or any change touching layout, state, or data flow. The reviewer audits against the
full current [`docs/code-style.md`](docs/code-style.md) and catches both new violations and
pre-existing ones in the files you touched. Run it after the build succeeds and before the wrap-up
message, not as a follow-up round after the user has already read the result.

A design-system change without a matching update to [`docs/design-system.md`](docs/design-system.md)
is a blocker, per that doc's maintenance rule. The reviewer is expected to fail the change on it.

## Test writing

ALWAYS delegate test writing to the `rhaydus-kotlin:unit-test-writer` agent, regardless of how small or
simple the task appears. Never write or modify unit tests directly in the main conversation - not for a
single function, a one-line change, or a trivial assertion. This rule has no exceptions.

When the target is a whole package or directory rather than a single file, the brief MUST include:
"audit existing test files in the target for coverage gaps and close them in the same pass." Do not run
a separate audit round; gap-fills belong in the initial delegation.

When several independent files need tests, spawn unit-test-writers in parallel on disjoint file sets
rather than sequentially inside one agent.

**Scope the prompt tightly to keep token and tool usage down.** A loose brief on a large test file can
burn 100K+ tokens on rediscovery and re-reads. For small mechanical changes (adding one field, renaming
a symbol, fixing compile breaks):
- Hand the agent exact file paths and line numbers for the construction sites you want fixed. Do the
  `grep` yourself first and paste the results; do not make the agent rediscover them.
- Skip the package-wide audit ask. List the specific one or two round-trip tests you want and stop
  there. The audit rule above is for genuinely package-wide work, not single-field additions.
- Specify ONE narrow Gradle `--tests` filter in the prompt; do not let the agent pick.
- Tell it explicitly NOT to re-audit, NOT to run the broader suite, and to keep its report concise
  (for example "under 150 words").

The agent is required to run the tests after writing them. Prefer a narrow filter such as
`./gradlew :app:testDebugUnitTest --tests "nl.rhaydus.nestbox.feature.<name>.*"` over the full suite.
When relaying its report:
- If all tests pass, say that the suite was executed and passed.
- If any test fails, surface the failing test names and the agent's diagnosis verbatim, then **stop**
  and wait for the user to approve any fixes. Do not delegate a fix round until the user has reviewed
  and authorized it.

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
