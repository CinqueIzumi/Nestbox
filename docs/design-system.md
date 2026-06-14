# Design System

The visual and interaction language for **Nestbox**, the dedicated reader for **the Doveletter**, a
newsletter about Android development. This document is descriptive: it does not point at code. When a
screen is being designed or redesigned, a contributor should be able to read this and know how the
surface should look and behave without consulting other screens.

> **Maintenance rule.** Any change that introduces, retires, or alters a foundation, component, or
> pattern in this system must update this file in the same change. If the doc and the code disagree,
> the code is wrong, the doc is wrong, or both, and none of those is acceptable to merge.

The one place this doc does name code is §2.2, because the typography scale is implemented and the
roles below map one-to-one onto it.

## 0. Content model

Everything in Nestbox is the Doveletter. There are no other sources, no feeds to add, no
publications or developers to follow, and nothing to filter by source, because there is only one
source. The whole job of the app is to make reading each publication of the Doveletter calm and pleasant.

The content is shaped as **publications**, each of one of three **types**: a **weekly letter** (a
dated, numbered edition that gathers several **entries**, short write-ups and links), an **article**,
or an **interview**. The reader browses publications, opens one to read, marks them read as they go,
and saves publications to return to later. Inside a weekly letter, individual entries can be read and
saved the same way. The word "issue" is deliberately avoided here so it stays free for GitHub Issues,
which the app may later use for community communication, not content.

The Doveletter is published to a private GitHub repository that only subscribers can read, so the
reader connects a GitHub account (OAuth device flow) before any content can load. This is an
authentication gate, not a switch that grants access: signing in proves who the account is, and the
app then checks whether that account actually has access to the private repository. An account that
is not a subscriber stays without access even once connected. The connection is not a personalisation
or sync feature. Everything in the app is local-only: there is no account sync and no reading-state
sync across devices. This shapes a few surfaces, most visibly the Profile screen's connect state and
any locked, no-access, or empty state shown before a reader's content can load.

## 1. Tone

The system is a **reading room for developers**. It borrows from a well-set technical journal and
from the editor the reader spends their day in, not from a social feed or a utility dashboard. The
reading surface is calm and uncluttered. Long-form prose gets room to breathe and a comfortable
measure. Chrome retreats so the writing is the loudest thing on the screen.

The signature is **monospace as voice**. A monospace face, the typeface of code, carries every label
that orients a developer: the publication a piece belongs to, how long it takes to read, when it landed,
the topics on it, the counts on the profile, and of course code itself. Monospace is native to this
audience. It says *this was made for people who read code* without a single illustration. The
reading body, by contrast, is a clean sans, because legibility over a long measure wins for the
paragraphs the reader actually sits and reads.

The reader is here to keep up, calmly. The entry they are reading is the protagonist. The app's
affordances, save and mark-as-read, stay quiet at the edges until reached for.

## 2. Foundations

### 2.1 Color

Color is sourced from a warm Material 3 expressive scheme, a terracotta clay primary over warm
neutrals. This section defines the *roles* those tokens play in the app.

- **Primary (clay).** The single accent. Used for emphasis on text and chrome: the kicker over a
  section, the leading rule of a section header, the unread marker, the active state of a toggle or
  filter chip, the save active state, the reading progress indicator, and a filled primary button.
  It is also, deliberately, the **surface fill of a single hero stat card** per scrollable region,
  for example an "publications read" or reading streak hero. Inside such a card the content colour is
  `onPrimary`, demoted text drops to `onPrimary` at about 0.75 alpha, any divider drops to
  `onPrimary` at about 0.25 alpha, and a call to action inside it steps down a tier (tonal or
  elevated) so it does not vanish into the fill. Beyond that one hero, primary is **not** a
  background colour. Ordinary cards, list rows, and grouped regions use the page background or a
  `surfaceContainer*` shade.
- **On-surface / on-background.** Body and headline copy, and unread entry titles. The default for
  almost all readable type.
- **On-surface-variant.** Demoted copy: metadata, captions, secondary lines, inactive labels, and
  the title of an entry the reader has **already read**. Use it to push information back without
  losing legibility.
- **Surface / background.** The reading canvas. Plain. Long-form prose lives directly on it.
- **Surface-container, -high, -highest.** Elevated tiles, publication and entry cards, code blocks, and
  grouped sections. Step *up* the container shade to express grouping or focus. Elevation is
  communicated by tone, not by drop shadows.
- **Surface-container-lowest.** Reserved for modal sheets and overlays that sit *above* the page.
- **Outline / outline-variant.** Hairline dividers and the rare bordered control. Prefer whitespace
  and a tone change to a dense rule.
- **Secondary container.** The fill for **topic chips**: a selected topic facet swaps to
  `secondaryContainer` / `onSecondaryContainer`, an idle one sits on `surfaceContainerHigh` /
  `onSurface`, and a read-only topic tag uses the idle shade.
- **Inverse-primary.** Small attention markers on chrome, for example a "new publication" dot on a tab.
- **Error / on-error.** Destructive and validation states only.

**Reading specific registers.**

- **Unread vs read.** Unread is the loud state: the entry title sits at full `onSurface` weight with
  a small primary unread dot at its leading edge. Once read, the title demotes to `onSurfaceVariant`
  and the dot disappears, so the list visibly thins out as the reader works through a publication, which
  is the point.
- **Code register.** Inline code is set in the mono `code` role on a faint `surfaceContainerHigh`
  wash. A fenced code block is a full `surfaceContainerHigh` card with the same mono face. Code is
  never recoloured for syntax in chrome contexts such as publication previews. Only the dedicated reading
  view may apply syntax tinting, and even there it leans on the scheme's tones, not a rainbow
  palette.

Dynamic color (Android 12+ Material You) is wired through the theme but **off by default**. The warm
clay scheme is the canonical look. Dynamic color is an opt in personalisation, not the design.

### 2.2 Typography

Two type families carry the system, split by voice. They are implemented in
`core/presentation/theme/`. See the worked mapping below.

- **Reader sans** (`readerFontFamily`). The reading face. Long-form prose, lead paragraphs, page and
  entry titles, and all Material component chrome such as top bar titles and button and navigation
  labels. It is set roman. Legibility over a long measure is its whole job.
- **Mono** (`monoFontFamily`). The developer-native accent. Everything that orients rather than
  reads: kickers and publication labels, the metadata strip (reading time, date, publication number), topics,
  inline and block code, and stat numerals. Mono is monospaced by nature, so counts and aligned
  columns stay steady without a tabular-figures feature.

Both families resolve to **system generic families today** (`SansSerif` and `Monospace`), so the app
ships with zero font assets and works offline immediately. The families are isolated in `Fonts.kt`.
To adopt a bundled or downloadable pairing later, for example a humanist reading sans and a screen
tuned mono, change those two declarations and nothing else in the system moves.

Two parallel scales coexist:

- **Material typography** (`NestboxTypography`). The standard display, headline, title, body, and
  label scale that off-the-shelf Material 3 components consume implicitly. Set in the reading face
  with a touch more body line height for comfort. Do not override it for component internals.
- **Reader typography** (`ReaderTypography`, reached via `MaterialTheme.readerTypography`). The
  project-specific scale used for any text a *screen itself* composes. Roles below.

| Role           | Where it goes                                                                    | Family, character                     |
| -------------- | -------------------------------------------------------------------------------- | ------------------------------------- |
| `kicker`       | All-caps publication or section label over a headline (the publication number, "IN THIS PUBLICATION", a section eyebrow), and the accent over a hero stat | Mono, wide tracking, medium |
| `kickerSmall`  | Same role, packed inside cards and rows                                          | Mono, tighter                         |
| `pageTitle`    | The screen's primary title rendered *inside* the scrolling page, not the top bar slot which uses Material `titleLarge` | Sans, bold, large |
| `headline`     | An entry or publication headline in a hero or featured card                            | Sans, bold, tight leading             |
| `headlineSmall`| Subsection or modal headlines                                                    | Sans, semibold                        |
| `articleTitle` | The title of an entry in a publication list or feed row                               | Sans, semibold                        |
| `feedTitle`    | A smaller card title (compact rows, related entries)                             | Sans, semibold                        |
| `bodyLarge`    | The lead paragraph or standfirst of an entry                                     | Sans, larger, generous leading        |
| `body`         | Running prose, the reading role                                                  | Sans, comfortable leading             |
| `bodySmall`    | Captions, secondary descriptions                                                 | Sans                                  |
| `meta`         | The metadata strip, for example `12 min · Publication 142 · 3d ago`                    | Mono, small                           |
| `metaStrong`   | Emphasised metadata such as an unread count or "NEW"                             | Mono, medium                          |
| `code`         | Inline code and fenced code blocks                                               | Mono                                  |
| `statNumber`   | A numeric stat such as publications read, entries saved, or a day streak              | Mono, oversize                        |
| `pullQuote`    | A highlighted passage pulled from an entry                                        | Sans, italic, medium                  |

Mono is a deliberate signal. It marks the chrome of a reading tool. Reserve it for the roles above
(kicker, meta, code, stat). If a paragraph of prose ever wants mono, that is a sign it is actually
code and belongs in the `code` role. Conversely, never set a publication label or a reading time in the
sans, because the mono voice is half the brand.

Never inline an ad hoc `TextStyle`. Pick the closest reader or Material role and `.copy(...)` only
when a documented exception requires it, for example tinting a `pullQuote` or applying caps to a
`kicker`. Caps on a kicker are applied at the call site (`.uppercase()`), not baked into the style.

### 2.3 Shape & elevation

- **Corners.** Small radii on cards and tiles, medium on buttons (Material defaults via the
  expressive theme), and larger, fully-rounded for chips and the active state of toggle buttons.
  Code blocks take the same small radius as cards so they read as inset panels, not floating objects.
- **Elevation.** Tonal, not shadowed. Surface container shades replace drop shadows for grouping.
  Step the shade up rather than adding a pixel of elevation. There is no card with a drop shadow in
  this system.
- **Accent rule.** A short, primary coloured bar leads a section header, in two sizes picked by the
  role of the kicker it precedes.
  - **Section rule, 28×3 dp.** The "this is a section, this is a sheet" mark. Sits in the page gutter
    (not inside a card) and precedes a full size `kicker` and headline pair. Used by sheet headers,
    settings sections, and any top level region introduction.
  - **Inline rule, 16×1 dp hairline.** The smaller variant for a `kickerSmall` living *inside* a card
    or hero region. Never mix the two: a full `kicker` always gets the 28×3 rule, a `kickerSmall`
    gets the 16×1 hairline or no rule at all.

### 2.4 Spacing

Spacing is rhythmic, not formulaic. The recurring values:

- **4 / 8** dp. Tight inline gaps (dot to title, icon to label, badge offsets).
- **12 / 16** dp. Within component padding, label to control gaps.
- **20 / 24** dp. Page horizontal padding (**24 dp is the page gutter**), and the gap between a
  card's thumbnail and its text block.
- **28 / 32** dp. Gap between distinct blocks inside a sheet or hero region.
- **40 / 48** dp. Gap between major page sections.

The reading view earns extra air: prose paragraphs separate by a full line, and the measure is held
comfortable (generous horizontal padding) rather than running edge to edge. Two adjacent section
headers without a generous gap between them is a layout bug.

### 2.5 Motion

- Use the Material **expressive** motion scheme, already wired into the theme. Do not hand-author
  durations or easings unless animating a custom property the scheme cannot express.
- **Press feedback.** Tappable cards and ripple less surfaces scale to about 0.97 while pressed and
  ease back on release, riding an `InteractionSource` so the scale lines up with the platform's
  pressed state ticks. Press scale is the *only* indication on a ripple less surface. Never strip a
  card's ripple without adding the scale. Material buttons keep their ripple and pressed shape morph
  and do not stack scale on top.
- **Commit choreography.** Marking an entry read fires a single *commit* haptic and lets the row
  settle into its read (demoted) state with a brief cross-fade, never a hard swap. Saving an entry
  pulses the bookmark glyph once. Navigation taps never fire a commit haptic.
- **Progress.** Reading progress and any indeterminate wait use the wavy progress indicator. Flat
  bars are reserved for chrome such as a top bar scrim and never carry semantic progress.
- **List add and remove.** Only user-triggered list mutations animate, for example mark-as-read
  removing an entry from an "unread" filter, or un-saving an entry from "saved". Initial load,
  background refresh, and pagination tail jump in without animation. Removals fade and let neighbours
  slide into the gap.
- **Staggered entry.** Items composed during the initial screen entry window play a brief upward
  translate and fade, delayed per index, a one-shot welcome moment, never an animate on scroll
  effect. Items scrolled in later render statically.
- **Sheets and overlays.** Sheets enter and dismiss from the bottom. Overlays cross-fade.
- All of the above are **suppressed when system animations are disabled** (reduced motion). The
  reduced path is an instant, un-animated swap.

### 2.6 Iconography

- Material **outline** icons, consistent stroke weight across the app. Never mix filled and outlined
  icon families on the same surface. The one earned filled glyph is the *active* bookmark (saved),
  which sits beside its outline idle state on the same control, a deliberate state pair, not a mix.
- Icon only controls always carry a content description.
- Icon size scales with the control's size token. An icon embedded in body type sits on the type
  baseline at a size proportional to the surrounding text.

## 3. Layout primitives

### 3.1 Page scaffold

A page has, top-to-bottom: an optional top app bar, scrolling content with horizontal padding
consistent for the whole page (24 dp gutter), and bottom navigation when on a tab root. The bottom of
scrolling content reserves padding equal to the bottom bar's footprint so the last item is never
occluded.

**Root tabs prefer an in-page title over a chrome title.** On a root tab (Home, Saved, Profile) the
screen's identity is rendered *inside* the scroll using the `pageTitle` role, usually with a `kicker`
above it, so it shares the editorial scale with the section headers below. Reach for a Material top
bar title only on **sub-screens pushed into the stack**, for example an entry's reading view or a
settings sub-page, where back navigation or trailing actions are needed.

### 3.2 Section rhythm

A content section is composed of:

1. **Section rule** (optional). The 28×3 dp primary bar in the gutter.
2. **Kicker.** A short all-caps mono label in primary colour (the section or publication label).
3. **Headline.** The human readable title in the reading sans, on-surface.
4. **Body.** The section's content: a vertical list of entries, a horizontal carousel, or a single
   block.
5. A generous vertical gap before the next section.

This is the canonical way to introduce any region. The kicker gives the orienting label (mono,
clipped, technical), the headline gives the readable title, the body delivers.

### 3.3 Hero region

A hero opens a screen with a single dominant element: the latest publication, a featured entry, or a stat.
A featured hero pairs a `kicker` (the publication label) with the `headline` and a `meta` line, over the
plain-page surface. A stat hero is the one primary filled card per region (§2.1), built around a
`statNumber`. Hero regions sit on the page surface, not on a container. They are part of the page,
not a tile within it.

### 3.4 Publication cards and entry rows

The vertical **list** is the default for a collection of entries. A horizontal carousel is the
exception, used only for a curated strip such as "saved for later". Cards reserve the height of every
optional row they may show (topics, save state) so the list does not reflow as data streams in.

Entry row anatomy (leading-to-trailing, the default compact row):

1. Optional leading thumbnail (the entry's image), small radius, no shadow.
2. **Kicker line.** `kickerSmall` mono, the publication label, for example `WEEKLY LETTER #142`.
3. **Entry title.** `articleTitle`, max two lines, full `onSurface` when unread or demoted when read,
   with a leading primary unread dot while unread.
4. **Meta strip.** `meta` mono: reading time, relative date, joined with `·`.
5. Optional trailing **save** affordance (the bookmark toggle) and an optional topic chip.

An **publication card** promotes the same anatomy for a whole edition: a larger block carrying the publication
`kicker` (`WEEKLY LETTER #142`), its date, the `headline` (the publication title or its first entry), and a `meta`
count of entries and unread items. A featured entry card uses the `headline` role and a larger
thumbnail. Horizontal carousel cards are fixed width and height and never paint an overflow
affordance. The rightmost card clipped against the 24 dp gutter is itself the "there is more" cue. Do
not add an edge fade, chevron, or page indicator.

### 3.5 Reading view

The reading view is the system's reason to exist, so it gets its own rules. It serves both a single
entry and a whole publication (a stack of entries under one publication header).

- It opens with the **publication kicker** (`WEEKLY LETTER #142`), the `headline`, the `meta` strip, and an optional
  hero image, then the **lead paragraph** in `bodyLarge`, then the body in `body`.
- Body prose holds a comfortable measure (24 dp gutters, never edge to edge), separates paragraphs by
  a full line, and renders on the plain-page surface.
- **Code** in the body renders inline (the `code` role on a faint wash) or as a fenced
  `surfaceContainerHigh` block. A block is horizontally scrollable rather than wrapped, because
  wrapping code lies about its structure.
- A **pulled quote** uses the `pullQuote` role set off from the column with vertical air and a leading
  primary rule.
- A thin **reading progress** indicator (wavy, primary) tracks scroll position. It is the only chrome
  that overlays the reading surface.
- An entry that links out to an external write up carries an "Open original" action (see §5, off app
  handoff).
- The reading view is the one place syntax tinting is allowed (§2.1). Nowhere else.

### 3.6 Modal sheets

Modal bottom sheets open fully expanded on `surfaceContainerLowest`. The first element inside is
always the canonical header: a section rule, a `kicker`, a `headlineSmall`, and an optional `body`
description line, followed by the sheet's body. Sheets handle their own dismissal and never need a
custom close button. **No exemptions.** Every modal sheet (topic filter, save to list, sort) opens
with this header. A Material `titleLarge` / `bodyMedium` sheet header reads as system chrome and is a
regression. If a sheet's content is so transient that an editorial header feels theatrical, it should
be a `Dialog`, not a sheet.

## 4. Components

A short catalogue so a contributor knows *which* tool to reach for. Anatomy and styling live with the
components as they are built. This list defines the vocabulary the app grows into. Reusable building
blocks live in `core/presentation/widget/`, theming in `core/presentation/theme/`.

- **Section header.** The §3.2 header: the 28×3 dp primary rule, an all-caps mono `kicker` in
  primary, the reading sans `headline`, and an optional `body` description. The canonical way to
  introduce a region. Implemented as a shared widget.
- **Top app bar.** Page title (Material `titleLarge`), optional subtitle, optional back, optional
  trailing icon actions. Used on sub-screens, not on root tabs, which use an in-page `pageTitle`.
- **Search top app bar.** Same skeleton, the title replaced by a single line search field over the
  Doveletter archive. The leading glyph flips to a wavy circular indicator while loading, and the
  trailing clear glyph lives *inside* the field once the user has typed.
- **Bottom navigation.** The tab destinations for the app's roots. It is app shell code, because it
  enumerates concrete tabs, so it lives at the shell tier alongside the tab host, not as a generic
  `core` widget. The reusable pieces it leans on (icon toggle, color roles) stay in `core`. Provided
  today as `BottomFloatingBar`.
- **Button.** The Material expressive button styles (filled, tonal, outlined, text) across the size
  scale. Filled is the region's one primary action. Never two competing primaries on a surface.
- **Pill chip.** The canonical fully-rounded (`RoundedCornerShape(percent = 50)`) chip: a `Surface`
  carrying a single label. It backs both **topic filter chips** and read-only **topic tags**.
  Selected uses `secondaryContainer` / `onSecondaryContainer` with a semibold label, idle uses
  `surfaceContainerHigh` / `onSurface`. Pass `onClick` for an interactive chip such as a filter, omit
  it for a read-only tag. Reach for it instead of hand-rolling a `Surface` and `Text` pill.
- **Publication card and entry row.** The §3.4 anatomy, the workhorse of every list surface. Reserves its
  optional rows so the list never reflows.
- **Save (bookmark) toggle.** An icon toggle that saves an entry for later. Idle is the outline
  bookmark on `onSurfaceVariant`, active is the filled bookmark on `primary`, with a single pulse on
  commit and a *commit* haptic. The active filled glyph is the one sanctioned filled icon (§2.6).
- **Code block.** A `surfaceContainerHigh` panel in the mono `code` role, horizontally scrollable,
  small radius. The canonical way to show a snippet in the reading view. Never wrap code to fit. In
  the reading view it carries restrained syntax tinting drawn from scheme tones (keywords in
  `primary`, strings in `tertiary`, numbers in `secondary`, comments in `onSurfaceVariant`), never a
  rainbow palette (§2.1).
- **Reading progress indicator.** A thin wavy primary bar tracking scroll in the reading view. The
  only progress chrome allowed to overlay the reading surface.
- **Unread marker.** The small leading primary dot on an unread entry title. Removed on read.
- **Stat tile.** A numeric stat in the mono `statNumber` role with a `kicker` unit label above it,
  never a unit suffix beside the number. One may be promoted to the primary filled stat hero per
  region (§2.1, §3.3).
- **Empty state.** An oversized low-alpha glyph or icon over an italic encouragement headline in the
  `pullQuote` voice. See §5 for the adaptive variant.
- **Pull to refresh.** The Material 3 expressive contained loading indicator inside the standard pull
  to refresh box. This is the one deliberate exception to "wavy progress everywhere", because the
  wavy circle fights the pull arc. Reach for the wavy primitive everywhere else.

## 5. Patterns

Recurring recipes that compose the primitives above.

- **The archive.** A vertical list of publication cards or entry rows (§3.4) under a section header (§3.2).
  The default surface of the app. Sorts newest-first. Supports an optional topic filter strip (below)
  and pull-to-refresh. Unread items lead loud, read items demote in place (§2.1).
- **Latest publication hero.** The Home tab opens on the newest publication as a hero (§3.3): its kicker, its
  headline, a `meta` count of unread entries, and a primary action into the reading view. Past publications
  follow below as the archive.
- **Topic filter strip.** A horizontally scrolling `Row` of pill chips directly above the archive to
  scope it by topic (Compose, Coroutines, Tooling, and so on). The selected chip swaps to
  `secondaryContainer`. Always include a leading "All" chip that clears the filter. A larger topic
  picker lives in a modal sheet (§3.6). The strip shows the quick facets. Do not apply the carousel
  page-edge cue here, because a filter strip is a control, not a peek affordance.
- **Publication and entry reading.** The §3.5 surface, pushed onto the stack from any list row via a
  Material top bar carrying back, the save toggle, and an overflow (share, open original, mark read or
  unread). Marking read here fires the commit choreography (§2.5).
- **Mark as read.** The system's core commit. Triggered by opening an entry (automatic), a swipe
  action on a row, or an overflow item. Fires a single *commit* haptic, demotes the row to its read
  state, and when the current filter is "unread" animates the row out (§2.5). No celebration beyond
  the haptic and the settle. Reading is routine, not a trophy.
- **Save for later.** The bookmark toggle (§4) collects an entry into the **Saved** root. Saving
  pulses the glyph and fires a commit haptic. Un saving from within the Saved list animates the row
  out. Saved is a plain list of the same rows, scopable by topic like the archive.
- **Adaptive empty state.** When a root surface is empty, surface the next best content rather than a
  static "nothing here" panel. An empty Home with nothing unread surfaces a "Catch up" strip of
  recently read or saved entries. An empty Saved surfaces the latest publication. Pair with the empty state
  glyph and an encouragement headline. Do not stack three carousels. The empty state is a moment of
  orientation, not a discovery surface.
- **Editorial section.** Section rule (optional), then kicker, then headline, then body. The default
  way to introduce any region (§3.2).
- **Hero stat.** A single oversize `statNumber` with a `kicker` unit label above it and an italic
  caption beneath. Used for profile counters such as publications read, day streak, and entries saved. The
  unit is named in the kicker, never trailed beside the numeral. Live bound numbers animate between
  values rather than snapping.
- **Tonal grouping.** When two regions must feel distinct on one page, step the container shade rather
  than drawing a divider or adding a card outline.
- **Off app handoff.** "Open original", "Share", and any link out of the app dispatch through a
  `UiEvent` collected at the screen's top level `Content()`, which invokes the platform handler there.
  Never call the browser or share handler from inside a leaf composable.

## 6. Decision rules

Walk this list before reaching for novelty when building a new surface.

- **Need a primary action?** Filled button. One per region.
- **Need to introduce a region of content?** Section header (kicker and headline), §3.2.
- **Need to mark a region as elevated relative to its neighbour?** Step the container shade. Do not
  add a shadow.
- **Need to label a publication, a reading time, a count, or any chrome metadata?** Use the mono scale
  (`kicker`, `meta`, `metaStrong`, `statNumber`). Reading prose never wears mono. Chrome metadata
  never wears the sans.
- **Need to show a snippet of code?** The `code` role, an inline wash or a fenced
  `surfaceContainerHigh` block. Never set code in the reading sans.
- **Need to scope a list?** A topic filter strip of pill chips with a leading "All". Push a larger
  picker to a modal sheet.
- **Need an accent on a piece of text?** Primary colour on the kicker or a leading word. Do not bold
  or recolour body copy.
- **Need a divider?** First try a vertical gap or a tone change. Reach for a hairline only when
  neither will do.
- **Need a custom font weight, size, or family?** Pick a different role from the reader or Material
  scale instead. Never inline a `TextStyle`.
- **Need to show progress?** The wavy progress indicator, except pull-to-refresh (§4).
- **Need a modal interaction?** Bottom sheet first, with its editorial header. Full screen second.
  Dialog only for an unavoidable blocking moment.
