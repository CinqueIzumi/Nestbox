#!/usr/bin/env bash
# PreToolUse hook: before fanning out code-exploration agents in Nestbox, remind to read the docs
# first. Fires once per session, and only for Explore / general-purpose subagents (the ones used to
# map the codebase). Other agent types (claude-code-guide, Plan, etc.) pass through untouched.
#
# additionalContext is not honored on PreToolUse, so the only way to surface a model-visible note is
# exit 2 (which blocks). To avoid blocking every spawn forever, this blocks just the first matching
# spawn per session, drops a marker, then stays out of the way.

input=$(cat)

subagent=$(printf '%s' "$input" | jq -r '.tool_input.subagent_type // empty' 2>/dev/null)
session=$(printf '%s' "$input" | jq -r '.session_id // "nosession"' 2>/dev/null)

case "$subagent" in
  Explore | general-purpose) ;;
  *) exit 0 ;;
esac

marker="${TMPDIR:-/tmp}/nestbox-docs-reminder-${session}"

if [ -f "$marker" ]; then
  exit 0
fi

touch "$marker"

echo "Nestbox docs-first reminder: read docs/architecture.md, docs/toad-architecture.md, docs/code-style.md, and docs/design-system.md before exploring the code with agents. They document the data/domain/presentation layering, the TOAD pattern and the add-a-feature checklist, the code style, and the design system, so most architecture questions are answered there. Reserve Explore/general-purpose agents for what the docs genuinely do not cover (exact dependency versions, current assets state, verbatim base-class signatures). If you have already read them, re-issue this call to proceed. This reminder fires once per session." >&2

exit 2
