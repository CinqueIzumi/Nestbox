# Architecture

Nestbox is a native Android client (Kotlin, Jetpack Compose, Koin, Voyager) that follows **Clean
Architecture** principles with the custom **TOAD** presentation framework. This doc covers the
layering; TOAD itself is documented in [`toad-architecture.md`](toad-architecture.md) and code style
in [`code-style.md`](code-style.md).

Nestbox is currently a **single Gradle module** (`:app`). The package tree under
`nl.rhaydus.nestbox` plays the role the module graph would in a larger build:

```
nl/rhaydus/nestbox/
├── core/            # Shared infrastructure + operation services consumed by features
│   ├── network/         # Ktor client factory
│   ├── auth/            # GitHub account/link operation service (data + domain)
│   └── presentation/    # Theme, shared widgets, markdown rendering, nav shell
└── feature/         # Leaf features, one folder each (home, profile, …)
```

## The nl.rhaydus foundation

Nestbox builds on the shared `nl.rhaydus` foundation rather than carrying its own copies of the
cross-app primitives. Pinned in `gradle/libs.versions.toml` (`rhaydusFoundation`), consumed from
Maven Central, docs vendored at the pinned version under [`rhaydus/`](rhaydus/):

| Artifact | What Nestbox takes from it |
|---|---|
| `nl.rhaydus:toad` | The whole TOAD runtime — `ToadScreenModel`, `UiState`/`UiAction`/`UiEvent`, `Collector`, `ActionDependencies`, `ActionScope`, `LocalVariables` (package `nl.rhaydus.toad`). |
| `nl.rhaydus:core-common` | `AppDispatchers`, `runCatchingCancellable`, the `AppLog` logging facade — installed once in `NestboxApplication` (package `nl.rhaydus.common`). |
| `nl.rhaydus:core-platform` | `SecureStorage` / `AndroidSecureStorage`, the Keystore-backed store the GitHub token lives in (package `nl.rhaydus.platform`). |
| `nl.rhaydus:designsystem-core` | `RhaydusTheme` (the Material 3 Expressive scaffold `NestboxTheme` wraps), `BottomBarScaffold` + `LocalBottomBarPadding` / `rememberBottomBarPadding` (package `nl.rhaydus.designsystem.*`). |

**Reuse-first:** before hand-rolling a component, modifier, util, or layout primitive, check
[`rhaydus/0.3.1/CAPABILITIES.md`](rhaydus/0.3.1/CAPABILITIES.md) — the foundation may already have it,
and reinventing something listed there is a defect rather than a style nit. Nestbox's own brand
language (colors, reader typography, editorial widgets) stays in this repo; the foundation is
deliberately brand-agnostic.

## Layered Architecture

Every feature and every `core/` operation service is organised into **data / domain / presentation**
layers. A layer holds its own sub-folders for its component types; create only the layers and
folders a given area actually needs (a presentation-only feature has just `presentation/`; an
operation service with no UI, like `core/auth`, has just `data/` + `domain/`).

```
<feature-or-core-area>/
├── domain/                 # Pure business layer — depends on nothing
│   ├── model/                  # Domain models, state classifications, value types
│   ├── repository/             # Repository interfaces (contracts)
│   └── usecase/                # Business-logic use cases (added when there's real logic)
├── data/                   # Implements the domain contracts
│   ├── model/                  # DTOs (*Response), entities (*Entity)
│   ├── datasource/             # *RemoteDataSource / *LocalDataSource — interface + Impl colocated
│   ├── repository/             # Repository implementations (*RepositoryImpl)
│   └── mapper/                 # Data <-> domain mappers
└── presentation/           # TOAD — see toad-architecture.md
    ├── state/  event/  action/  collector/  screenmodel/  screen/
```

Both repositories **and** data sources are contract-first: the interface is the swappable seam, the
`*Impl` is one realisation. A data source's interface and its `*Impl` live in the **same file**, named
after the interface (`GitHubAuthRemoteDataSource.kt` holds both `GitHubAuthRemoteDataSource` and
`GitHubAuthRemoteDataSourceImpl`); the repository interface (domain) and `*RepositoryImpl` (data) are
split across layers. DI always binds the interface (`single<TokenLocalDataSource> { TokenLocalDataSourceImpl(...) }`),
so swapping a remote/local source is a one-line module change with no consumer edits.

### Layer rules

- **Domain** depends on nothing. It owns domain models and the repository interfaces.
- **Data** implements the domain interfaces; it owns DTOs, data sources, mappers, and
  `*RepositoryImpl`. Data may reference domain, never the reverse.
- **Presentation** depends on **domain** only, and reaches it through **use cases** — never a
  repository or data-source type directly. A use case may be a thin pass-through to a repository
  today and grow logic later without touching its callers.

### Error handling across layers

Nestbox does **not** assume repositories and data sources can't crash. The error model is fixed by
the layer a type sits in:

- **Data sources / repositories throw.** When a network, IO, or parsing operation fails, the
  exception propagates — they don't degrade a real failure into a sentinel return value, and they
  don't run failure-policy side effects (clearing a bad token, retry-and-give-up). The only
  `try`/`catch`/`runCatching` allowed inside this layer encodes a *deliberate business decision*
  (e.g. `AccountRepositoryImpl` falls back to a placeholder account when the profile fetch succeeds-
  but-profile-fetch-fails, so a valid link isn't dropped). Catching purely to hide an error from the
  caller is a smell.
- **Use cases return `Result<T>` and own failure policy.** Every use case wraps its repository call
  in `runCatchingCancellable` (`nl.rhaydus.common`, from the `nl.rhaydus:core-common` foundation
  artifact) and returns `Result<T>` — never the bare domain type.
  `runCatchingCancellable` is `runCatching` that rethrows `CancellationException`, so structured-
  concurrency cancellation is never captured as a `Result.failure`. This is the seam where "repos
  throw" becomes "callers get a value-typed outcome". Cross-operation reactions to a failure live
  here, not in the repository: `GetGitHubAccountUseCase` signs out (drops the unusable token) when
  the account fetch fails, then still returns the original failure.
- **Presentation (TOAD actions) folds the `Result`.** Actions call the use case and unpack with
  `.onSuccess { }` / `.onFailure { }` (**never `.fold()`**), translating success and failure into UI
  state through `scope`. Because the use case already turned the throw into a `Result`, actions hold
  no `try`/`catch` of their own. See `StartGitHubLinkAction` / `CheckGitHubAuthorizationAction` for
  the worked pattern, and [`code-style.md`](code-style.md#error-handling--logging) for the rules.

### Placing a new type in the correct layer

Classify the type before choosing a package — don't default to where the first consumer lives:

1. **Encodes a business rule or a state classification derived from domain data?** → `domain/model/`.
   Example: `AccountState` (Loading/Disconnected/Connecting/Connected) is domain, even though only a
   `@Composable` renders it today.
2. **A persisted row, DTO, or remote payload?** → `data/model/` (`*Entity`, `*Response`/`*Dto`).
3. **Display-only — colors, icons, composable argument shapes, routes, tabs?** → `presentation/`.

**Heuristic:** if a headless use case or a non-Compose client could consume the type without losing
meaning, it belongs in `domain`. Filing a domain concept under `presentation/` forces downstream code
to invert the dependency or duplicate the concept — and relocating it later is a cascading import
refactor.

### Worked example — `core/auth`

The GitHub linking service is the reference layout for a UI-less operation service:

```
core/auth/
├── domain/
│   ├── model/{GitHubAccount,DeviceAuthorization,AuthorizationResult}.kt   # domain models
│   ├── repository/AccountRepository.kt        # interface — stateless suspend operations
│   └── usecase/
│       ├── StartGitHubAuthorizationUseCase.kt
│       ├── CheckGitHubAuthorizationUseCase.kt
│       ├── GetGitHubAccountUseCase.kt
│       └── SignOutGitHubUseCase.kt
├── data/
│   ├── model/{DeviceCodeResponse,AccessTokenResponse,GitHubUserResponse}.kt   # DTOs
│   ├── datasource/GitHubAuthRemoteDataSource.kt   # interface + Impl: GitHub device-flow endpoints
│   ├── datasource/TokenLocalDataSource.kt         # interface + Impl: the token, over SecureStorage
│   └── repository/AccountRepositoryImpl.kt        # implements the domain interface
└── di/AuthModule.kt                           # binds interfaces to impls, exposes use cases
```

The repository is **stateless**: suspend operations only (start authorization, check once, get
account, sign out) — no `StateFlow`, no background work, no app-scope. The device flow's link
**state** lives in the `profile` presentation layer (`GitHubLinkState` inside `ProfileUiState`), and
the screen drives it from **lifecycle**, not a background loop: it dispatches a single
`CheckGitHubAuthorizationAction` on `Lifecycle.Event.ON_RESUME` — one poll when the user returns
from approving in the browser, or a one-time restore on first show. The `profile` feature injects
the **use cases** through its `ProfileDependencies` — actions call
`dependencies.startGitHubAuthorizationUseCase()` etc.; it never sees a repository, data source, or
data-layer type. Injected use-case properties keep the full `*UseCase` name.

## Dependency Injection

Koin. Each feature and each `core/` operation service owns one `module { }` (in its `di/` folder),
binding implementations to domain interfaces (`single<AccountRepository> { AccountRepositoryImpl(...) }`).
All modules are aggregated in `NestboxModule.kt` (`nestboxModules`) and started in
`NestboxApplication`. Repositories/data sources are `single`; screen models are `factory`.

## Navigation

Voyager. `TabNavigator` drives the bottom-bar tabs; `Navigator` handles push/pop stacks. The nav
shell (`RootScreen`, `BottomBarScreen`, `BottomFloatingBar`) lives in `core/presentation/`. When the
project grows, cross-feature navigation should go through a navigation contract rather than features
importing each other's `Screen`/`Tab` classes directly.

## Dispatchers

`AppDispatchers` (`nl.rhaydus.common`, from the `nl.rhaydus:core-common` foundation artifact)
provides `Main`/`IO`/`Default` via DI for testability. Actions run on `Main`; data sources/
repositories switch to `IO` for network and disk work.
