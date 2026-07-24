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
or a piece of **interview prep**. The reader browses publications, opens one to read, marks them read
as they go, and saves publications to return to later. Inside a weekly letter, individual entries can
be read and saved the same way. The word "issue" is deliberately avoided here so it stays free for GitHub Issues,
which the app may later use for community communication, not content.

**Interview prep is preparation material for a job interview**, not a conversation with someone. The
repository names that directory `interview`, which is easy to misread, and the app must never echo
that misreading back at the reader: the type is labelled "Interview prep" everywhere it surfaces, and
its section headline is "Get ready". Copy that calls these pieces interviews, or files them under a
heading like "Conversations", is a bug in the content model, not a wording preference.

The types differ in more than a label, and the app is expected to keep them visibly apart. A weekly
letter has a **cadence**: it is dated and numbered, it is read to keep up, and it goes stale. An
article or a piece of interview prep is **evergreen**: it is a standalone titled piece read whenever
its topic is what the reader wants. That difference drives how each one is identified. An article or
a piece of interview prep carries **its own title**, so that title is its headline everywhere it
appears. A weekly letter has
none, and is identified by its number and date instead, so it wears the newsletter's own name, "The
Doveletter", as its headline. Blending the three into one undifferentiated chronological stream is a
regression: see the type facet strip and the sectioned archive in §5.

The Doveletter is published to a private GitHub repository that only subscribers can read, so the
reader connects a GitHub account (OAuth device flow) before any content can load. This is an
authentication gate, not a switch that grants access: signing in proves who the account is, and the
app then checks whether that account actually has access to the private repository. An account that
is not a subscriber stays without access even once connected. The connection is not a personalisation
or sync feature. Everything in the app is local-only: there is no account sync and no reading-state
sync across devices. This shapes a few surfaces, most visibly the Profile screen's connect state and
any locked, no-access, or empty state shown before a reader's content can load.

## 1. Tone

The system is a **reading room for developers**, staged as a printed thing: a warm paper canvas, an
ink/rust palette, and set type rather than screen-native chrome. It borrows from a well-set technical
journal and from the editor the reader spends their day in, not from a social feed or a utility
dashboard. The reading surface is calm and uncluttered. Long-form prose gets room to breathe and a
comfortable measure. Chrome retreats so the writing is the loudest thing on the screen.

The signature is **monospace as voice**. A monospace face, the typeface of code, carries every label
that orients a developer: the publication a piece belongs to, the metadata strip, the type facet chips,
the counts and status on the profile, the floating tab bar's labels, and of course code itself. Mono is
native to this audience. It says *this was made for people who read code* without a single
illustration. The reading body, by contrast, is a serif set for a comfortable long measure, while
titles and headlines take a second, larger display serif — a deliberate three-way split, never a
generic system sans (§2.2).

The reader is here to keep up, calmly. The entry they are reading is the protagonist. The app's
affordances stay quiet at the edges until reached for.

## 2. Foundations

### 2.1 Color

Color is a warm **paper / ink / rust** scheme, not a Material-generated tonal palette: a paper canvas,
near-black ink for text and one dark "ink card" surface family, and a single rust accent. This section
defines the *roles* those tokens play in the app, mapped onto Material's `ColorScheme` slots so
ordinary Material components stay coherent, plus a small extension for the handful of values that have
no honest Material slot.

- **Primary (rust accent).** The single accent. Used for emphasis on text and chrome: kickers over a
  region, the weekly-letter type label in a ledger row's meta line, the active state of a filter chip,
  the reading-progress fill, and a filled primary button. Primary is **not** a background colour for
  ordinary cards or rows — those sit on the page canvas or a `surfaceContainer*` shade.
- **On-surface.** Ink. Titles, headlines, and the default for readable type.
- **On-surface-variant.** Body prose colour — a dark, slightly lighter warm brown than ink, used for
  running paragraph text so headings read a shade darker than the copy beneath them.
- **Outline.** The meta/muted tone. Every mono metadata line (dates, the ledger meta row, the reading
  chrome's `READING · NN%` label, group header kickers) reads in this colour, not `onSurfaceVariant`.
- **Outline-variant.** Hairline rules — the thin dividers between ledger rows.
- **Surface / background.** The paper canvas.
- **Surface-bright.** The reader's canvas, a hair lighter than the page canvas so the reading view
  reads as its own room.
- **Surface-container-low.** Card surfaces — the source-access card, section panels.
- **Surface-container-high.** The inline-code wash behind a `code` span.
- **Tertiary.** The positive/access-ok tone (a connected GitHub account, read access confirmed).
- **Inverse-surface / inverse-on-surface.** The **ink card** family: a dark, ink-filled surface with
  paper content, used for the locked-state card, the floating tab bar, and any other "printed on dark
  stock" treatment. Both values hold constant across light and dusk — an ink card looks the same in
  either mode, which is why the floating tab bar never changes look when the theme does.
- **Error / on-error.** Destructive and validation states only, left at the Material-standard tones —
  the redesign doesn't touch error.

**`NestboxColors` extension.** A handful of values have no honest Material role — they're pinned to one
call site, not a role reused across the system — and are provided the same way as the reader typography
scale (a `staticCompositionLocalOf` plus a `MaterialTheme.nestboxColors` extension, distinct light and
dusk instances):

| Token | Where it goes |
| --- | --- |
| `idleChipStroke` | The unselected type-facet chip's 1dp stroke |
| `chipIdleText` | The unselected type-facet chip's label |
| `readRowTitle` | A ledger row's title once read, demoted from ink |
| `noteRule` | The reader chrome's circle-button stroke (back / overflow) |
| `listFooterMuted` | The end-of-list footer caption, the quietest tone in the system |
| `codeSurface` | The fenced code block's fill — ink in light mode, darker-than-canvas in dusk |

No raw hex at a call site, ever — a screen reads `MaterialTheme.colorScheme.*` or
`MaterialTheme.nestboxColors.*`, never an inline `Color(0x...)`.

**Dusk mode.** Nestbox's dark theme is called **Dusk**, and it is warm dark, never neutral grey. Ink
and body invert to light warm tones (contrast ≥ 7:1); the meta/outline tone stays roughly the same
lightness in both modes, because it has to read at a consistent whisper against either canvas; the
accent lifts a step brighter for contrast against the dark canvas; and the ink-card family
(`inverseSurface`/`inverseOnSurface`) is pinned to the same value in both schemes, by design (see
above). Dynamic color (Android 12+ Material You) stays wired through the theme but off by default —
paper/ink/rust and its Dusk remap are the canonical look, not a personalisation opt-out.

### 2.2 Typography

Three type families carry the system, split by voice, implemented in `core/presentation/theme/Fonts.kt`
as bundled TTFs (no system-generic fallback):

- **Display serif** (`displaySerifFontFamily`, Instrument Serif). Titles, headlines, and the reader's
  drop cap. Set roman, with an italic cut reserved for the pull quote role. It is the paper's largest,
  most editorial voice.
- **Body serif** (`bodySerifFontFamily`, Newsreader, a variable family at 400/500/600). The reading
  face. Long-form prose, list rows, and the reader's italic note voice.
- **Mono** (`monoFontFamily`, Spline Sans Mono, a variable family at 400/500/600). The developer-native
  accent: kickers, metadata, chips, code, and — because Material's label scale is routed through it too
  — every stock Material button and navigation label. Monospace is native to this audience; it marks
  the chrome of a reading tool.

Two parallel scales coexist:

- **Material typography** (`NestboxTypography`). The standard display/headline/title/body/label scale
  that off-the-stock Material 3 components consume implicitly (button labels, any Material top bar).
  Display/headline/title roles are set in the display serif, body roles in the body serif, and label
  roles — the voice of every stock button and nav label — in mono. Do not override it for component
  internals.
- **Reader typography** (`ReaderTypography`, reached via `MaterialTheme.readerTypography`). The
  project-specific scale used for any text a *screen itself* composes. Roles below.

| Role | Where it goes | Family · size |
| --- | --- | --- |
| `masthead` | Home's own "Nestbox" title | Display serif, 34, lh 1.0 |
| `kicker` | A prominent all-caps mono label — the masthead's overline, the reader's `ARTICLE · DOVE LETTER` kicker | Mono medium, 10, ls 0.22em |
| `kickerSmall` | A packed mono label — the ledger group header, the reader chrome's `READING · NN%` | Mono medium, 9.5, ls 0.20em |
| `pageTitle` | A root tab's in-page title (Profile's "Profile") | Display serif, 30 |
| `headline` | The reader's H1 | Display serif, 31, lh 1.14 |
| `headlineSmall` | A subsection or locked-state headline | Display serif, 24, lh 1.2 |
| `articleTitle` | A ledger row's title; the reader's H2 | Display serif, 17.5, lh 1.25 |
| `feedTitle` | A compact card title | Display serif, 15 |
| `identityName` | The Profile identity card's name | Display serif, 18 |
| `avatarInitial` | An avatar circle's initial glyph | Display serif, 22 |
| `dropCap` | The reader's first-paragraph drop cap | Display serif, 54, lh 0.82 |
| `bodyLarge` / `body` | Running prose — identical today; the reader distinguishes its lead paragraph with the drop cap, not a size step | Body serif, 17, lh 1.72 |
| `bodySmall` | List rows, the reader's bullet items | Body serif, 15.5 |
| `rowTitle` | A card row's title — the source-access card's "GitHub connected" / "Not connected" line | Body serif medium, 15.5 |
| `readerNote` | An italic reader-margin note voice (foundational per this section; no in-scope surface consumes it yet) | Body serif italic, 14.5, lh 1.5 |
| `meta` | The metadata strip — a ledger row's meta line, the reader's byline rule | Mono, 9, ls 0.14em |
| `metaStrong` | Emphasised metadata — a status label such as `ACTIVE` | Mono medium, 9, ls 0.12em |
| `identifierSmall` | A compact mono identifier rendered as data, not a caps label — the Profile `@handle` | Mono, 9.5, ls 0.08em |
| `identifier` | A longer mono identifier rendered as data — the repo slug | Mono, 11, lh 15 |
| `badgeGlyph` | A short, untracked mono glyph inside a circular badge — the `GH` badge | Mono, 10, lh 14 |
| `chipLabel` | The mono caps control-label voice, chip size — the type facet chip | Mono, 10, ls 0.14em |
| `tabLabel` | The mono caps control-label voice, tab size — the floating tab bar's item text | Mono medium, 10, ls 0.12em |
| `controlLabel` | The mono caps control-label voice, standalone size — `SIGN OUT`; the same voice as the redline's `CONNECT GITHUB` button (11, ls 0.16em), not yet a call site in this build | Mono medium, 10.5, ls 0.16em |
| `code` | Fenced code blocks | Mono, 12, lh 1.75 |
| `statNumber` | A large numeral display (the GitHub device code) | Mono medium, 40 |
| `pullQuote` | The reader's block quote; the empty-state headline | Display serif italic, 20, lh 1.4 |

Mono is a deliberate signal. It marks the chrome of a reading tool. Reserve it for the roles above.
Reading prose never wears mono; conversely, never set a publication label, a chip, or the metadata
strip in a serif.

Never inline an ad hoc `TextStyle`, and never reach for `.copy(fontSize = ...)` /
`.copy(letterSpacing = ...)` on a role either — `ReaderTypography` *is* the app's typography theme, so
a call site setting type metrics by hand means the role table is missing a role, not that an exception
is warranted. Add the role instead. The one legitimate `.copy(...)` left in the system is applying a
theme *colour* to a role's `SpanStyle` (`code.toSpanStyle().copy(background = ...)` for inline code),
which a static role table can't carry. Caps on a kicker or chip are applied at the call site
(`.uppercase()`), not baked into the style.

### 2.3 Shape & elevation

- **Corners.** `3` a code-inline chip, `12` the fenced code block, `16` cards, `18` a feature card,
  fully-rounded (`RoundedCornerShape(percent = 50)`) for pill chips, the floating tab bar, and buttons.
- **Elevation.** Tonal, not shadowed, with **one sanctioned exception**: the floating tab bar (§4) keeps
  a drop shadow, because it is a physically floating object over the page, not a grouped region — the
  one deliberate brand carve-out this system allows itself. Everywhere else, step the container shade
  rather than adding a pixel of elevation.
- **Ledger group header.** The system's section-introduction mark is a mono `kickerSmall` label in the
  meta colour over a full-bleed 1dp ink rule (`onSurface`), not a coloured bar. This replaces the prior
  system's 28×3 dp primary accent bar entirely — there is no accent-bar variant left in the system, in
  a card or otherwise.

### 2.4 Spacing

Values in dp, drawn from the redline sheet rather than computed:

- **4.** Micro — label to value.
- **8.** Chip gap, grid gap.
- **16.** Row gap, a card's internal row padding.
- **18.** A card's inset from the gutter.
- **22.** The page gutter. **The reader uses 26** instead, for its slightly wider measure.
- **26.** Spacing between distinct sections on a page.

Two adjacent section headers without a generous gap between them is a layout bug. The reading view
holds its measure comfortable — never edge to edge — and separates paragraphs by a full line.

### 2.5 Motion

- Use the Material **expressive** motion scheme, already wired into the theme. Do not hand-author
  durations or easings unless animating a custom property the scheme cannot express.
- **Press feedback.** Tappable rows and ripple-less surfaces scale to about 0.97 while pressed and ease
  back on release, riding an `InteractionSource` so the scale lines up with the platform's pressed
  state ticks. Press scale is the *only* indication on a ripple-less surface. Material buttons and the
  floating tab bar's items keep their own ripple and do not stack scale on top.
- **Reading progress is a flat bar, not the wavy indicator.** The reader's scroll-progress bar (§3.5)
  is a flat 3dp track (ink at 8% alpha) with an accent fill tracking scroll position — chrome, not the
  wavy primitive. Indeterminate waits elsewhere (a loading list, a pending GitHub check) still use the
  Material 3 expressive wavy indicator; the flat/wavy split is by determinacy, and the reader's
  progress is the one determinate exception living in chrome.
- **Staggered entry.** Items composed during the initial screen-entry window play a brief upward
  translate and fade, delayed per index, a one-shot welcome moment, never an animate-on-scroll effect.
  Items scrolled in later render statically.
- **Sheets and overlays.** Sheets enter and dismiss from the bottom. Overlays cross-fade.
- All of the above are **suppressed when system animations are disabled** (reduced motion). The
  reduced path is an instant, un-animated swap.

### 2.6 Iconography

- Material **outline** icons, consistent stroke weight across the app, drawn from
  `material-icons-extended`. Never mix filled and outlined icon families on the same surface.
- Icon-only controls always carry a content description.
- Icon size scales with the control's size token. The floating tab bar and the type facet chips carry
  **no icons at all** — they are text-only, mono-labelled controls (§4); an icon on either is a
  regression.

## 3. Layout primitives

### 3.1 Page scaffold

A page has, top-to-bottom: an optional top app bar, scrolling content with a consistent 22dp
horizontal gutter (the reader uses 26dp instead, §2.4), and the floating tab bar when on a root tab.
The bottom of scrolling content reserves padding equal to the bar's footprint so the last item is never
occluded.

**Root tabs prefer an in-page title over a chrome title.** On a root tab (Nest, Profile) the screen's
identity is rendered *inside* the scroll — Home's `masthead` role over a mono overline kicker, Profile's
`pageTitle` — rather than a Material top bar title. Reach for a Material top bar only on **sub-screens
pushed into the stack**, where back navigation or trailing actions are needed.

### 3.2 Section rhythm

A content section is composed of:

1. **The ledger group header** (§2.3): a mono `kickerSmall` label in the meta colour, then a full-bleed
   1dp ink rule.
2. **Body.** The section's content — the ledger list, a card, or a single block.
3. A generous vertical gap (26dp) before the next section.

This is the canonical way to introduce any region, on Home (one header per publication type, in fixed
order) and on Profile (the "Source access" group label) alike.

### 3.3 The type facet strip

A horizontally scrolling row of pill chips (§4) scoping the archive by publication type: `ALL`,
`LETTERS`, `ARTICLES`, `PREP`, mapped 1:1 onto the app's `PublicationFilter`. It sits directly under
the masthead, above the sectioned ledger, because the facet scopes the whole page below it. Like any
control, it takes no edge fade, chevron, or page indicator, and stays inside the page gutter.

### 3.4 The ledger row

The vertical **ledger** is the archive's only anatomy now — there is no card-based publication tile
left in the system (see §4, "Ledger row" and the retired publication card). A ledger row is a
baseline-aligned flex row:

1. **Date column.** Fixed 30dp width, right-aligned, two lines (month over day) in `meta` mono, meta
   colour.
2. **Title.** `articleTitle` (display serif, 17.5), ink while unread.
3. **Meta line**, offset 5dp below the title: `meta` mono. The type label — `WEEKLY LETTER № 42`,
   `ARTICLE`, `INTERVIEW PREP` — tints accent for a weekly letter and stays meta-coloured otherwise;
   it's followed by other short mono fields on the same line.

Rows are separated by a hairline (`outlineVariant`), 15dp vertical padding, 16dp gap between the date
column and the text block. The archive stays **sectioned by publication type**, one ledger group header
per type in the fixed order weekly letters → articles → interview prep (§5); a section with nothing in
it is omitted.

### 3.5 Reading view

The reader is the system's reason to exist and gets its own canvas — `surfaceBright`, 26dp gutter.

- **Scroll progress**, full-bleed at the very top, above content: a flat 3dp bar, `onSurface` at 8%
  alpha for the track, accent for the fill, tracking `LazyListState` scroll fraction (§2.5).
- **Top chrome**, over a canvas-to-transparent scrim: a leading back circle (34dp, `noteRule`-stroked,
  `surface`-filled, ink glyph), a centred `kickerSmall` "READING · NN%" label live-bound to scroll
  position, and a trailing circle in the same treatment for an overflow action — only rendered when
  there's an actual action behind it.
- **Article head.** A `kicker` composed from the publication's type and number (e.g.
  `ARTICLE · DOVE LETTER`), the `headline` role for the H1, then a `meta` line (date + type) under a
  **full ink rule** — not a hairline — closing off the head.
- **Body**, mapped from the parsed markdown blocks: `body` for paragraphs (the first carries the
  `dropCap` treatment and nothing else distinguishes it — no separate lead size), `headline`/`articleTitle`-family
  sizes for headings with H2s preceded by a mono numbered index (`01`, `02`, …) in accent, `pullQuote`
  for a block quote (an accent rule to its left), `code` on a `codeSurface` fill for a fenced block
  (horizontally scrollable, never wrapped), and an inline-code wash (`surfaceContainerHigh`) for inline
  code. Syntax tinting inside a code block is the one place restrained color leans on scheme tones
  rather than the mono-monochrome default elsewhere.
- **Footer.** A full ink rule as the article's closing mark, margin-top 36dp.

### 3.6 Modal sheets

Modal bottom sheets open fully expanded on `surfaceContainerLowest`. The first element inside is
always the canonical header: the ledger group header (a `kickerSmall` label over a full ink rule),
a `headlineSmall`, and an optional `body` description line, followed by the sheet's body. Sheets handle
their own dismissal and never need a custom close button. If a sheet's content is so transient that an
editorial header feels theatrical, it should be a `Dialog`, not a sheet.

## 4. Components

A short catalogue so a contributor knows *which* tool to reach for. Anatomy and styling live with the
components as they are built. Reusable building blocks live in `core/presentation/widget/`, theming in
`core/presentation/theme/`.

- **Ledger group header.** The §3.2 header. A shared widget (`SectionHeader`): a mono `kickerSmall`
  label in the meta colour, a full-bleed 1dp ink rule, and an optional headline/description trailer for
  callers that still want one. Replaces the prior system's 28×3 dp primary accent bar entirely.
- **Top app bar.** Page title (Material `titleLarge`, now set in the display serif), optional subtitle,
  optional back, optional trailing icon actions. Used on sub-screens, not on root tabs (§3.1).
- **Floating tab bar.** The root-tab destination switcher, `NEST` / `PROFILE`, a shared widget
  (`BottomFloatingBar`). A translucent ink pill (ink at 92% alpha, both light and dusk — the ink-card
  family never changes with the theme), padding 5dp, 2dp gap between items; each item is a pill with
  paper text, active on a paper-at-12%-alpha fill, inactive at 65% alpha paper, the mono `tabLabel`
  role, M3 ripple retained. Text labels, never icons (§2.6). This is the one component in the system
  that keeps a drop shadow (§2.3) — the sanctioned brand exception.
- **Button.** The Material expressive button styles (filled, tonal, outlined, text) across the size
  scale. Filled is a region's one primary action. Button labels render in the mono voice (§2.2,
  `NestboxTypography.labelLarge`), matching the rest of the chrome.
- **Pill chip.** The canonical fully-rounded chip (`PillChip`), h30, padded 7×13, the mono `chipLabel`
  role. Backs the type facet strip (§3.3). Selected fills the ink-card colours
  (`inverseSurface`/`inverseOnSurface`); idle stays transparent with a `NestboxColors.idleChipStroke`
  stroke and `NestboxColors.chipIdleText` label. Pass `onClick` for an interactive chip, omit it for a
  read-only tag.
- **Ledger row.** The §3.4 anatomy, the workhorse of the archive. The prior card-shaped publication tile
  (`PublicationCard`) is **retired** — there is no elevated-tile treatment left for a publication in the
  list; every entry is a ledger row.
- **Code block.** A `codeSurface`-filled panel in the mono `code` role, horizontally scrollable, 12dp
  radius. The canonical way to show a snippet in the reading view. Never wrap code to fit. Carries
  restrained syntax tinting drawn from scheme tones, the one place in the system this is allowed.
- **Reading progress indicator.** The reader's flat 3dp scroll-progress bar (§2.5, §3.5). The one
  determinate-progress exception to "wavy everywhere."
- **Empty state.** An oversized low-alpha outline glyph (96dp, `outline` at 0.35 alpha) over an italic
  encouragement headline in the `pullQuote` voice, centred, 24dp between the two and 48dp of vertical
  air around the pair so it reads as a page moment rather than a cramped error row. The glyph is
  decorative and carries no content description. A **text-only variant**, the `pullQuote` line with no
  glyph, is the right tool for a narrow miss such as a type facet with nothing behind it.
- **Pull to refresh.** The Material 3 expressive contained loading indicator inside the standard pull to
  refresh box, the one other deliberate exception to "wavy everywhere," because the wavy circle fights
  the pull arc.

## 5. Patterns

Recurring recipes that compose the primitives above.

- **The archive.** A vertical ledger of rows (§3.4) under a group header (§3.2) per publication type,
  sectioned in the fixed order weekly letters → articles → interview prep, a section with nothing in it
  omitted. Sorts newest-first. The header copy is fixed so the three regions stay recognisable across
  surfaces:

  | Type           | Kicker            | Headline      |
  | -------------- | ----------------- | ------------- |
  | Weekly letter  | `WEEKLY LETTERS`  | Past editions |
  | Article        | `ARTICLES`        | Long reads    |
  | Interview prep | `INTERVIEW PREP`  | Get ready     |

- **Type facet strip.** §3.3. Scopes the whole archive below it, including whichever publication
  otherwise leads it, so a facet change never leaves a stale leading item behind.
- **The newest publication.** Home's selection logic separates the newest matching publication from the
  sectioned ledger below it so nothing is drawn twice on one page; how it's set apart visually — if at
  all, beyond simply leading the ledger — is a screen-level decision within the ledger's own visual
  language now, not a dedicated hero-card treatment. The prior system's filled "continue reading" hero
  card is retired along with the reading-progress state it depended on.
- **Publication reading.** The §3.5 surface, pushed onto the stack from any ledger row. The reader's
  trailing chrome circle (an overflow action) is only rendered when there's a real action behind it.
- **Off app handoff.** Any link out of the app — opening a browser for GitHub device-flow verification,
  for instance — dispatches through a `UiEvent` collected at the screen's top-level `Content()`, which
  invokes the platform handler there. Never call the browser handler from inside a leaf composable.
- **Tonal grouping.** When two regions must feel distinct on one page, step the container shade rather
  than drawing a divider or adding a card outline.

## 6. Decision rules

Walk this list before reaching for novelty when building a new surface.

- **Need a primary action?** Filled button. One per region.
- **Need to introduce a region of content?** The ledger group header (§3.2, §4) — a mono label over a
  full ink rule, never a coloured accent bar.
- **Need to mark a region as elevated relative to its neighbour?** Step the container shade. Do not add
  a shadow — the floating tab bar is the system's one carved-out exception (§2.3), not a precedent.
- **Need to label a publication, a reading time, a count, or any chrome metadata?** Use the mono scale
  (`kicker`, `kickerSmall`, `meta`, `metaStrong`, `statNumber`). Reading prose never wears mono; chrome
  metadata never wears a serif.
- **Need to show a snippet of code?** The `code` role, an inline wash or a fenced `codeSurface` block.
  Never set code in a serif.
- **Need to scope the archive?** The type facet strip (§3.3), mapped 1:1 onto `PublicationFilter`. There
  is no separate topic-based filter in this system.
- **Need to keep publication types apart?** Both at once: a ledger group header per type (§5, fixed
  order and fixed copy) and the type facet strip above it. Never one blended stream.
- **Need an accent on a piece of text?** The rust accent on a kicker, a type label, or a leading rule.
  Do not bold or recolour body copy.
- **Need a divider?** A hairline (`outlineVariant`) between ledger rows, or a full ink rule for a
  heavier break (a group header, the reader's byline rule). Prefer whitespace and a tone change over
  either where neither structural role applies.
- **Need a custom font weight, size, or family?** Pick a different role from the reader or Material
  scale instead. Never inline a `TextStyle`.
- **Need to show progress?** The reader's flat progress bar for determinate scroll (§2.5); the Material
  3 expressive wavy indicator for everything indeterminate, except pull-to-refresh (§4).
- **Need a modal interaction?** Bottom sheet first, with its editorial header (§3.6). Full screen
  second. Dialog only for an unavoidable blocking moment.
