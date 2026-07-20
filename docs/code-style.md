# Code Style Guide

This project follows the [official Kotlin code style](https://kotlinlang.org/docs/coding-conventions.html)
as declared in `gradle.properties` (`kotlin.code.style=official`). Conventions specific to the
TOAD presentation architecture live in [`toad-architecture.md`](toad-architecture.md); this guide
covers everything else.

The mechanizable half is **enforced**, not just documented: the shared `nl.rhaydus:ktlint-rules`
ruleset runs from the root build. `./gradlew ktlintFormat` auto-fixes what it can (multi-argument
one-per-line wrapping, trailing commas, blank-line rules, sibling-composable spacing, boolean
`.not()`); `./gradlew ktlintCheck` gates on everything including the structural rules that have no
auto-fix, and `check` depends on it. Scope a run with `-Pktlint.root=<dir>`. The full rule list is in
the foundation's [`code-style.md`](rhaydus/0.3.1/code-style.md).

## Naming Conventions

Files are named in **PascalCase**, matching their primary class.

| Type | Convention | Example |
|------|-----------|---------|
| Domain models | Plain nouns | `AccountState`, `DeviceCodeResponse` |
| Data entities | `*Entity` suffix | `BookEntity` |
| Data sources | `*DataSource` / `*DataSourceImpl` (both in one file, named after the interface) | `GitHubRemoteDataSource` |
| Repositories | `*Repository` / `*RepositoryImpl` | `AccountRepository` |
| Use cases | `*UseCase` | `MarkContentAsReadUseCase` |
| Screens | `*Screen` | `ProfileScreen`, `HomeScreen` |
| Screen models | `*ScreenModel` | `ProfileScreenModel` |
| Actions | `*Action` (one per file) | `StartGitHubLinkAction`, `DisconnectGitHubAction` |
| Events | `*Event` | `ProfileEvent` |
| UI state | `*UiState` | `ProfileUiState` |
| Local variables | `*LocalVariables` | `ProfileLocalVariables` |
| Dependencies | `*Dependencies` | `ProfileDependencies` |
| Mappers | `*Mapper` | `AccountMapper` |
| DI modules | `*Module` | `ProfileModule`, `authModule` |

- Functions and variables use **camelCase**; functions are action-oriented (`startDeviceFlow`,
  `getToken`).
- Boolean variables/properties are prefixed with `is`/`has` (`isLoading`, `isActive`).
- **Injected use cases keep their full `*UseCase` name** as the property/parameter — e.g.
  `val startGitHubLinkUseCase: StartGitHubLinkUseCase`, never abbreviated to `startGitHubLink`.

## One declaration per file

- **One data class per file**, named after the class — models, DTOs, result holders, wrappers.
  Never declare a `data class` locally inside a function/`init`, and never colocate unrelated data
  classes in a shared file. A single-caller data class still gets its own file next to that caller.
- **One enum per file**, named after the enum. Attach labels/icons/colors as constructor properties
  on the enum (`status.label`) rather than re-deriving them in a `when` at each call site.
- **One action per file** — `<Name>Action.kt` holds only the `sealed interface`; each concrete
  `data object`/`data class` action lives in its own file implementing it and carries the `Action`
  suffix (`StartGitHubLinkAction`). Concrete events likewise carry the `Event` suffix; the same
  per-suffix rule applies across the table above (`*UiState`, `*UseCase`, `*Dependencies`, …).
- **Exception — sealed hierarchy variants.** The variants of a `sealed interface`/`sealed class`
  MAY be co-located in the sealed type's own file (for `SealedType.Variant` namespacing), or split
  one-per-file. Both keep exhaustive `when`. `AccountState` co-locates its variants; that is not a
  violation of the one-data-class-per-file rule.

## Comments

Keep code self-documenting; prefer descriptive names over comments. Only write a comment when it
earns its place — a non-obvious edge case, a workaround, a spec requirement, or a name that genuinely
can't be made clearer. Never write a comment that restates what the code or a descriptive name
already says; comments that aren't necessary are noise.

## Boolean Negation

Never use the `!` prefix operator — always use `.not()`. Applies to locals, properties, call
results, and complex expressions. The non-null assertion `!!` is unaffected (but should be rare).

```kotlin
// Good
if (isLoading.not()) { ... }

// Bad
if (!isLoading) { ... }
```

## If / Else

- A single-line `if`/`else` expression may omit braces: `val x = if (a) b else c`.
- The moment it breaks across multiple lines, **every branch uses an explicit `{ ... }` block**.
  Never mix a braced branch with a brace-less one, and never span a brace-less branch over multiple
  lines.

## Visibility

Nestbox is a **single module** today, but it follows internal-by-default anyway, and `ktlintCheck`
gates it. Module-private declarations are `internal`: TOAD plumbing (`*ScreenModel`, `*Action`,
`*UiState`, `*Event`, `*Dependencies`, `*LocalVariables`, collectors), `*Impl` classes, the stateless
render composables a `*Screen` object hosts, and module-private helpers. `public` is reserved for the
deliberate cross-module surface — domain contracts, `*UseCase`, `*Screen`/`*Tab`, the aggregated
`*Module`, shared design-system components. `private` still covers genuinely local helpers.

Visibility cascades: marking a `*UiState` internal forces its collector and its render composable
internal too, since a public declaration cannot expose an internal type. That is the intended
direction — follow it rather than widening the state back to public.

## Compose

- Screens are Voyager `Screen` objects; their composables are members of the object — the core
  `XScreen(state, runAction)` render function is `public` (so `@Preview` can drive it), every
  sub-component composable is `private`. See [`toad-architecture.md`](toad-architecture.md).
- Reusable UI components live in `core/presentation/widget/`; theming in `core/presentation/theme/`.
- **Blank line between sibling composables.** Inside any layout scope (`Column`, `Row`, `Box`),
  leave a blank line between each child composable call, including `Spacer`. Never stack two
  composable calls back-to-back.
- **Multi-argument composables break across lines** — two or more arguments means each on its own
  line with a trailing comma, even for short calls. Single-argument composables stay inline.

## Argument and Property Layout

Whenever a function declaration, function call, constructor invocation, or data-class instantiation
passes **more than one** argument/property, each goes on its own line: opening `(` on the name's
line, one argument per line, **trailing comma on the last**, closing `)` on its own line.
Single-argument forms stay inline. A trailing lambda doesn't count as an argument (`runTest(d) { }`
stays on one line).

**Property/parameter annotations go on their own line**, above the declaration — never inline. When
a class's properties carry such annotations, separate **every** property in that list with a blank
line (each is now a multi-line construct), including any unannotated ones mixed in.

```kotlin
@Serializable
data class DeviceCodeResponse(
    @SerialName("device_code")
    val deviceCode: String,

    @SerialName("user_code")
    val userCode: String,

    val interval: Int,
)
```

```kotlin
// Single argument — inline.
tokenStore.getToken()

// Two or more — one per line, trailing comma.
AccountRepository(
    authService = get(),
    tokenStore = get(),
)
```

## Code Block Whitespace

Every multi-line construct acts as a *paragraph* — a blank line before and after it.

- No blank line after an opening `{` (exception: `sealed` bodies get a blank line after `{`), and
  no blank line before a closing `}`. Leave a blank line after a block's closing `}` unless it's the
  last line of its enclosing block.
- Multi-line `val`/`var` assignments, multi-line calls, and `when`/`if` expressions each get a blank
  line before and after.
- **Guard clauses** are each their own paragraph: blank line after an extraction before the first
  guard, between guards, and after the last guard before the main logic — for every form
  (`?: return`, `if (x) return`, `throw`, `continue`).
- One blank line between functions, between interface members, and between sealed variants. No blank
  lines between data-class properties (but `@SerialName`-annotated DTO properties get a blank line
  between each, as each is a 2-line construct).

## Import Ordering

1. Android / AndroidX
2. Third-party (Kotlin stdlib, Coroutines, Ktor, Koin, Voyager, Compose)
3. Project (`nl.rhaydus.nestbox.*`)

Remove unused imports. **Never reference anything by fully-qualified name inline** — add an import
and use the short name (types, return types, generic args, lambda receivers, top-level functions).
This is strictest for project-own code.

## Data Flow

- UI state is exposed as `StateFlow` (immutable from the UI's perspective).
- One-time events (navigation, toasts) are sent via `Channel`.
- Repository data is exposed as `Flow` and collected in the screen model's collectors.
- Actions execute on the `Main` dispatcher; network/IO runs on `IO`.

## Error Handling & Logging

The error model is layered — see [`architecture.md`](architecture.md#error-handling-across-layers)
for the full rationale. In short:

- **Data sources and repositories throw freely.** They never assume they can't crash and never
  swallow a genuine failure into a sentinel return; if something goes wrong (network, IO, parsing),
  the exception propagates. Repository-level `try`/`catch`/`runCatching` is reserved for *deliberate
  business decisions* (e.g. falling back to a placeholder account), never for hiding errors the
  caller should see or for running failure-policy side effects.
- **Every use case returns `Result<T>` and owns failure policy,** wrapping its repository call in
  `runCatchingCancellable` (`nl.rhaydus.common`) — `runCatching` that rethrows `CancellationException` so
  a cancelled coroutine never becomes a `Result.failure`. Never use bare `runCatching` around a
  suspend call. Reactions to a failure that span operations belong here, not in the repository (e.g.
  `GetGitHubAccountUseCase` drops the stored token via `signOut()` when the fetch fails, then
  returns the original failure).
- **Screen-model actions unpack the `Result` with `.onSuccess { }` / `.onFailure { }`** and fold the
  outcome into UI state via `scope`. **Never use `.fold()`** — `.onSuccess`/`.onFailure` only.
  Actions therefore carry no `try`/`catch` of their own; the use case already converted the throw
  into a `Result`.

```kotlin
// Use case — domain layer.
class StartGitHubAuthorizationUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(): Result<DeviceAuthorization> =
        runCatchingCancellable { accountRepository.startDeviceAuthorization() }
}

// Action — presentation layer.
dependencies.startGitHubAuthorizationUseCase()
    .onSuccess { authorization -> scope.setState { it.copy(link = Connecting(...)) } }
    .onFailure { scope.setState { it.copy(link = Disconnected(error = "Couldn't reach GitHub.")) } }
```

Plain `try`/`catch` remains acceptable inside data/repository code where control flow needs it
(retry loops, or rethrowing `CancellationException` before catching `Exception` so a deliberate
recovery doesn't fire on cancellation).

- Network calls use the shared Ktor client (`createHttpClient`).
- Logging uses `android.util.Log` with a per-class `TAG`. (No `Timber` dependency in this project;
  if one is added later, prefer it and never `println`.)

## Dependencies

- Versions are managed centrally in `gradle/libs.versions.toml`; reference them via `libs.<alias>`
  in `build.gradle.kts`. Never hard-code a version in a module build file.

## Tests

Tests follow **Arrange–Act–Assert**, with each section marked exactly as `// ----- Arrange -----`,
`// ----- Act -----`, `// ----- Assert -----` (collapse to `// ----- Act & Assert -----` when the
act and assertion are one expression). Group the tests for a single function of the unit under test
in a `@Nested inner class` named in PascalCase after that function; shared mocks, `@BeforeEach`, and
helpers live on the outer class. Every `@Test` lives inside a `@Nested` class — never directly on the
outer class.
