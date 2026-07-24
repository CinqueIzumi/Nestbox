---
name: gates-dont-catch-everything
description: Which documented Nestbox conventions the passing ktlintCheck/lint/test gates do NOT actually enforce, so they need manual review every time.
metadata:
  type: project
---

Passing `./gradlew check` (ktlintCheck + lint + unit tests) does not guarantee compliance with every
rule in `docs/code-style.md`. Confirmed gaps, found during the Home archive/type-facet review
(2026-07-24):

- **Boolean is/has naming** (`docs/code-style.md` "Naming Conventions": "Boolean variables/properties
  are prefixed with is/has") is not caught by the project's ktlint ruleset. A Boolean parameter named
  e.g. `selected` or `needsLeadingGap` passes ktlintCheck cleanly even though every other Boolean in
  the codebase follows `isX`/`hasX`.
- **`@Nested` test grouping** (`docs/code-style.md` "Tests": "Every `@Test` lives inside a `@Nested`
  class — never directly on the outer class") is not enforced by any gate either; a flat test class
  with `@Test` methods on the outer class compiles and runs fine.
- Duplicated literals/business rules across composables (e.g. the same `?: "fallback"` string in three
  files) also pass every gate silently — no lint rule catches semantic duplication.

**Why:** the shared `nl.rhaydus:ktlint-rules` ruleset covers mechanizable formatting (wrapping, trailing
commas, blank lines, sibling-composable spacing) but not naming semantics or test structure, and there's
no detekt/custom rule for either in this repo yet.

**How to apply:** never treat "all gates passed" as a signal that naming conventions or test structure
are compliant — grep for `@Test` methods directly on outer test classes, and read new Boolean parameter/
property names against the is/has rule by hand on every review, regardless of gate status.
