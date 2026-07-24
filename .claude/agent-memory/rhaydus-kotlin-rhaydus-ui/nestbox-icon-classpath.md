---
name: nestbox-icon-classpath
description: Nestbox now has material-icons-extended on the classpath, wired 2026-07-24; the canonical glyph empty state is built
metadata:
  type: project
---

As of 2026-07-24, `app/build.gradle.kts` depends on `androidx-compose-material-icons-extended`
(`gradle/libs.versions.toml`, resolves at 1.7.8 via the Compose BOM), alongside `-core`. The
constraint that previously forced a text-only empty state (see design-system.md §4) is gone.
`Icons.Outlined.*` names from the full extended set (e.g. `Inbox`, `Newspaper`, `MarkEmailUnread`,
`Drafts`) resolve now — no need to avoid them or guess-and-check.

Built the canonical empty state as `EmptyState` in
`app/src/main/java/nl/rhaydus/nestbox/core/presentation/widget/EmptyState.kt`: an oversized
(96.dp) outline icon at `onSurfaceVariant` alpha 0.32f over a centred `pullQuote` headline, 24.dp
gap between them, 48.dp vertical breathing room, icon `contentDescription = null` (decorative,
since it always sits directly above a headline already stating the same message in words). Used
on Home's `PublicationFilter.ALL` root-empty case with `Icons.Outlined.Inbox`
("Nothing has synced from the Doveletter yet."). A single-facet miss ("No interview prep yet.") stays
the bare `pullQuote` text line per §4/§5 — that asymmetry is intentional, not a leftover gap to
unify.

**Why:** the user pointed out the icons-core constraint was resolved and asked for the real
component per design-system.md §4, which had been sitting unbuilt.

**How to apply:** reach for `EmptyState` for any genuinely-empty root surface. Don't reach for it
on a narrow filtered/single-facet miss — that stays text-only by design. If a new icon name is
needed, it very likely resolves now, but still worth a quick grep/compile check since extended is
still a large but finite set.
