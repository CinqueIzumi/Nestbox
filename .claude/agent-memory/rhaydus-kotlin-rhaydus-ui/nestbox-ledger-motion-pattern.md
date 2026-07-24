---
name: nestbox-ledger-motion-pattern
description: Nestbox's first use of pressScaleClickable + staggeredEntry, established on Home's ledger rows during the 2026-07-24 ledger redesign
metadata:
  type: project
---

During the 2026-07-24 Home redesign (paper/ink/rust "ledger" system, see `docs/design-system.md`),
`HomeScreen.kt`'s `LedgerRow` became the first place in Nestbox to consume
`nl.rhaydus.designsystem.modifier.pressScaleClickable` and
`nl.rhaydus.designsystem.component.{rememberStaggeredEntryCoordinator,staggeredEntry}` from
`designsystem-core`. Until then the app only used stock Material `Surface(onClick = ...)` (which
keeps its own ripple, e.g. `PillChip`, `BottomFloatingBar`).

Pattern established:
- A tappable, hairline-divided list row (no card, no elevation) is **ripple-less**, using
  `.pressScaleClickable(onClick = ...)` for the press feedback, per design-system.md §2.5
  ("Press scale is the only indication on a ripple-less surface"). Material components that already
  carry their own ripple (buttons, the floating tab bar, `PillChip`'s `Surface`) are left alone —
  scale is not stacked on top of a ripple.
- The screen-entry stagger coordinator is created with `rememberStaggeredEntryCoordinator(key = this)`
  inside the screen's own composable member function, where `this` is the enclosing `Screen` singleton
  object (not a re-typed reference to the object's own same-named render function) — reads cleanly and
  sidesteps any ambiguity with a member composable that shares the object's name (`HomeScreen(state,
  runAction)` inside `object HomeScreen`).
- Per-row stagger `index` is threaded as a running counter across a hero item + multiple
  `LazyListScope.itemsIndexed` sections; the running total must be captured into a `val` **before**
  each `itemsIndexed` call (not read live inside the item lambda), since the lambda closes over
  whatever the enclosing `var` holds at composition time, not at closure-creation time.

**Why:** the app's design-system.md §2.5 documents both primitives as system-wide motion foundations
(not opt-in flourishes), and the ledger row is exactly the kind of tappable-row/list-entry surface they
were written for.

**How to apply:** the next screen with a tappable, ripple-less row (e.g. Profile's source-access card
rows, if they get list-item semantics) should follow the same `pressScaleClickable` +
`staggeredEntry(key = this)` pattern for consistency, not reinvent ripple or skip the stagger.
