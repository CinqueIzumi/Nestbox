## #1 2026-06-12 Weekly

This is a mock publication used to visually test the Doveletter reader. All links point at
example.com, the article is original placeholder prose, and the author is fictional. It
exists only to exercise markdown rendering (headings, lists, nested lists, links,
blockquotes, inline code, and fenced code blocks) before real content is wired in.

## 📚 Article & References

- [Test the rendering of a long link title that wraps across more than one line](https://example.com/articles/long-title): A reference entry with a sentence of commentary after the colon, so the reader can check how a link title and its trailing description sit together on the row. The point here is line length and how a `body` paragraph follows an `articleTitle`.

- [A short one](https://example.com/short): Short commentary.

- [Inline code inside a description](https://example.com/inline-code): Some entries mention an API like `remember`, a type like `StateFlow`, or a call such as `collectAsStateWithLifecycle()` in the middle of a sentence, and the reader should set those in the mono `code` role on a faint wash without breaking the line rhythm.

- [An entry with no trailing description](https://example.com/no-description)

## 🎤 Conference & Speaking & Videos

- [A talk with a colon: in the title](https://example.com/talk): Commentary that follows a title which itself contains a colon, to confirm the parser splits on the right one and the rest renders as prose.

- [Another session](https://example.com/session): One more row so the list has rhythm and the gap between rows can be eyeballed.

## 🛠️ Releases & Open-Source

- [Sample Library 2.0.0](https://example.com/releases/2.0.0): A release note entry. Stable release with bug fixes and a few new APIs.

- [Tooling Preview 1](https://example.com/tooling): A preview build entry to vary the list.

## 🔎 Nested lists

- [A parent entry that owns a sub-list](https://example.com/parent): This release groups several sub-releases, so the entry has a nested bullet list under it to test indentation and the second-level marker.
  - [Child One 1.0.0-alpha01](https://example.com/child-1): First nested item.
  - [Child Two 1.0.0-alpha02](https://example.com/child-2): Second nested item, slightly longer so it can wrap and the hanging indent under a nested bullet is visible.
  - [Child Three 1.0.0](https://example.com/child-3): A third nested item to give the indented group some height.

## 🕊️ Dove Letter Article

### How Compose Decides What to Redraw

State in Jetpack Compose is not a variable you read once. It is a value the runtime watches.
When you call a composable, the runtime records which pieces of state that function touched,
and it remembers the position in the tree where the reading happened. Later, when one of those
values changes, the runtime does not redraw the screen. It redraws only the composables that
read the value that changed. This is the whole idea behind recomposition, and most performance
questions in Compose come down to understanding which functions get invited back and which do
not.

> A composable that reads no changing state recomposes only when its inputs change. A composable
> that reads a `MutableState` recomposes whenever that state is written, wherever the write
> happens.

#### Reading state is what creates the dependency

Consider a counter. The dependency between the button and the label is not declared anywhere.
It is created the moment the label reads `count`:

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Count: $count")          // reads count, so it recomposes
        Button(onClick = { count++ }) { // writes count, never reads it
            Text("Increment")           // reads nothing that changes
        }
    }
}
```

When `count` changes, the runtime walks its list of readers. The `Text` that interpolates
`count` is on that list, so it recomposes. The `Button`'s label reads a constant string, so it
is skipped even though it sits right next to the write. Proximity in the source means nothing.
Only the read matters.

#### remember is about position, not just caching

A common mistake is to treat `remember` as a cache keyed by value. It is keyed by *position in
the composition*. The same line of code, reached at the same slot in the tree, returns the same
remembered value across recompositions. Move it, and the slot changes:

```kotlin
// Stable: one slot, survives recomposition
val controller = remember { ExpensiveController() }

// Bug: a new instance every time the branch flips,
// because the two calls occupy two different slots
val controller = if (isWide) {
    remember { ExpensiveController() }
} else {
    remember { ExpensiveController() }
}
```

If you need a remembered value to be recreated when an input changes, pass that input as a key
argument. The runtime compares keys and throws away the cached value when any of them differ:

```kotlin
val pager = remember(userId) { Pager(userId) }
```

This reads as "remember a `Pager`, but forget it and build a new one whenever `userId`
changes." Keys are how you connect the lifecycle of a remembered object to the data it depends
on.

#### Stability decides whether a skip is allowed

The runtime will skip a composable whose inputs have not changed, but only if it can prove the
inputs are *stable*. A type is stable when the runtime can trust that two equal instances behave
the same and that a change to the instance will notify the composition. Primitives, `String`,
and types built only from stable types qualify. A plain `List` does not, because the runtime
cannot tell whether the list was mutated in place.

> If a composable takes an unstable parameter, the runtime cannot skip it, so it recomposes
> every time its parent does. One unstable parameter on a hot path can quietly recompose a whole
> subtree.

The shell command to see which of your composables are restartable and skippable is the
compiler metrics report:

```shell
./gradlew assembleRelease \
  -Pandroidx.enableComposeCompilerMetrics=true \
  -Pandroidx.enableComposeCompilerReports=true
```

Read the generated report, find the composables marked `restartable` but not `skippable`, and
look at their parameters. An unstable parameter there is the thing to fix, usually by wrapping a
collection in an immutable type or marking a class as stable.

#### Conclusion

The model is small once it clicks. Reading state creates a dependency. Writing state notifies
the readers, and only the readers. `remember` ties a value to a position and, optionally, to a
set of keys. Stability decides whether an unchanged input earns a skip. Hold those four ideas
together and the recomposition behaviour of any screen stops being a mystery and becomes
something you can read straight off the source.

As always, happy reading!

— [Test Author](https://example.com/author)
