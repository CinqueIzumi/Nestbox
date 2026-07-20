# Vendored foundation docs

Version-stamped copies of the `nl.rhaydus` foundation's canonical docs, taken from the tag the app
pins in `gradle/libs.versions.toml` (`rhaydusFoundation`). They are vendored rather than read from
a sibling checkout so they always describe the **pinned** artifacts, not whatever `main` has moved on
to, and so they stay readable when no local foundation checkout exists.

Do not hand-edit these files - they are copies. Change them upstream in `rhaydus-foundation`, release,
then bump `rhaydusFoundation` and re-vendor the matching tag into a new `docs/rhaydus/<version>/`
directory (deleting the superseded one).

The app's own conventions live in `docs/` one level up and take precedence where they differ, since
they describe Nestbox specifically. The foundation's `design-system-foundations.md` is the
brand-agnostic skeleton; Nestbox's brand language is [`docs/design-system.md`](../design-system.md).
