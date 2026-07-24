# Redesign: outstanding work

Handoff note for the visual redesign committed as `8aa52be`
(`wip: Redesign the screens to the new Nestbox design system`) on `release/1.0.0`.

That commit compiles (`:app:compileDebugKotlin` passes) but is **not finished**. This file
records what is left, so the work can be picked up without re-deriving it.

## Where the design lives

The redlines are in a claude.ai design project, not in this repo:

- Project: <https://claude.ai/design/p/06b959ea-6dd5-4b9d-b4e0-8909cba3a900>
- `Nestbox Spec.dc.html` is the redline sheet, REV B (Jul 24 2026), sections A to E
- `Nestbox.dc.html` is the interactive mockup and the ground truth for structure and ordering

Reach them with the `DesignSync` tool (load it via `ToolSearch` with `select:DesignSync`,
then `get_file` against that project id). The distilled brief used to drive the build lived
in a session scratchpad and is gone; the two files above are the durable source.

## Scope that was agreed, and is not a gap

The redesign was deliberately constrained to what the existing TOAD contracts already carry.
The following appear in the design and were **intentionally left out** because no state backs
them. They should not be re-reported as defects:

reading progress and the "continue reading" card · read/unread state, unread dots, unread
counts · reading-time estimates · highlights and notes · the `Margins` tab · profile stat
tiles · last-sync and sync-now · the reader text-size preference.

The one derived exception that **was** built: the reader's scroll-progress bar and its
`READING · NN%` label, both computed from `LazyListState`, which needs no contract change.

The only contract addition in the commit is `HomeEvent.NavigateToProfileEvent` plus
`ConnectGithubAction`, so the signed-out Home has a route to the Profile tab where GitHub
auth actually lives.

## Outstanding

### 1. The code review never ran (highest priority)

Nothing in this change has been audited against `docs/code-style.md`. The review was about to
start when work stopped. Run the `rhaydus-kotlin:code-reviewer` agent over the working tree
and have it check, beyond its normal pass:

- window insets on all three screens (see item 2)
- `RootScreen`'s inset handling (see item 3)
- that the rewritten `docs/design-system.md` matches the code, and does not describe any of
  the out-of-scope features above as if they exist
- type-role discipline: a dedicated pass added seven roles (`rowTitle`, `identifier`,
  `identifierSmall`, `badgeGlyph`, `chipLabel`, `tabLabel`, `controlLabel`) and swept eight
  `.copy()` call sites. Confirm none survive or were reintroduced. Note that `.copy(alpha = ...)`
  on a `Color`, and `MarkdownDocument.kt`'s `.toSpanStyle().copy(background = ...)`, are
  intentional and are not violations.
- `MarkdownDocument.kt`'s reworked inline-code wash, which now draws from
  `TextLayoutResult.getPathForRange` inside a `drawBehind`, for correctness and for
  performance (it runs per text block inside a `LazyColumn`)
- that nothing still references the deleted `PublicationCard.kt`, in code or in docs
- content descriptions on icon-only controls (the reader's back and overflow circles, the tab bar)

### 2. Insets on Home and Profile are unverified

The reader was rendering under the status bar because it used the design frame's raw `72dp` /
`120dp` padding with no inset handling. Those numbers are measured from inside a 412x892 design
frame that draws its own status bar, so they are relative to the system bars, not to the
physical screen edge. That is fixed in `PublicationDetailScreen.kt`.

`HomeScreen.kt` and `ProfileScreen.kt` took their padding from the same frame and have **not**
been checked. Verify both, in both themes.

### 3. `RootScreen` reserves inset space without consuming it

`RootScreen`'s `Scaffold` applies `.padding(it)` rather than `Modifier.consumeWindowInsets(it)`,
so system-bar space is reserved visually but never marked consumed for descendants. This was
flagged during the build and deliberately left alone as out of scope. It is the likely root
cause behind item 2, so fix it before patching individual screens, or the screens will end up
double-compensating.

### 4. Deferred from the theme foundation pass

- **Tab bar does not hide on scroll.** The design calls for hide-on-scroll-down and
  reveal-on-scroll-up. No scroll signal is currently plumbed from the tab content up to
  `BottomBarScreen`, so wiring it reaches into the screens. The bar is always visible today.
- **No backdrop blur** behind the floating tab bar. It uses a translucent ink fill plus a
  shadow only. There is no blur primitive in the foundation's modifier catalogue and the
  effect is platform-version gated.

Both are recorded as deliberate simplifications in the widget KDoc and in `docs/design-system.md`.

### 5. Only the reader has been seen on a device

`HomeScreen` and `ProfileScreen` have not been run at all, in either theme. The reader was
checked once, in Dusk mode, and that single screenshot surfaced five defects (detached inline
code backgrounds, a colliding drop cap, a duplicated title, an occluded kicker, and a raw ISO
date), all since fixed. Assume the other two screens carry a similar yield until they have
actually been looked at.

Worth checking specifically: the ledger row's baseline alignment across the date column and
the title column, the locked (signed-out) Home state, and all four `GitHubLinkState` branches
on Profile, since the mockup only ever drew the connected one.

## Known deviations from the design

Deliberate, but worth revisiting if they read badly on device:

- **The drop cap was removed.** Compose has no CSS float. An `InlineTextContent`
  approximation was built first, but it overprinted the following line on a real device, so it
  was taken out. The `dropCap` role still exists in `ReaderTypography` and is currently unused.
- **The reader's scroll-progress bar is a flat bar**, not the wavy indicator the design system
  otherwise mandates, and it sits above the status bar so it stays full bleed.
- **The tab bar keeps a drop shadow**, which is the one sanctioned exception to the system's
  otherwise tonal-only elevation rule.
- **One em dash was replaced with a comma** in the `WHAT'S INSIDE` copy on the locked Home
  state, to match the house preference against em dashes in UI copy.
