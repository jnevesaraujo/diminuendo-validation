#!/usr/bin/env bash
#
# setup-ai-context.sh
# Creates LOCAL POINTERS (symlinks) to AGENTS.md, the single source of context.
# The pointers are in .gitignore — they are not versioned and do not duplicate content.
#
# Usage:
#   ./scripts/setup-ai-context.sh            # interactive
#   ./scripts/setup-ai-context.sh claude     # Claude Code only
#   ./scripts/setup-ai-context.sh all        # all
#
set -euo pipefail

cd "$(dirname "$0")/.."   # project root

if [[ ! -f AGENTS.md ]]; then
  echo "ERROR: AGENTS.md not found at the project root." >&2
  exit 1
fi

link() {
  local target="$1"
  mkdir -p "$(dirname "$target")"
  if [[ -e "$target" && ! -L "$target" ]]; then
    echo "WARNING: '$target' already exists and is NOT a symlink. Skipped (not overwriting)."
    return
  fi
  ln -sf "$(python3 -c "import os,sys;print(os.path.relpath('AGENTS.md', os.path.dirname(sys.argv[1])))" "$target")" "$target" 2>/dev/null \
    || ln -sf AGENTS.md "$target"
  echo "OK: $target -> AGENTS.md"
}

choice="${1:-}"
if [[ -z "$choice" ]]; then
  echo "Which tool do you use?"
  echo "  1) Claude Code        (CLAUDE.md)"
  echo "  2) GitHub Copilot     (.github/copilot-instructions.md)"
  echo "  3) Cursor             (.cursorrules)"
  echo "  4) All"
  read -rp "Option [1-4]: " choice
fi

case "$choice" in
  1|claude)  link "CLAUDE.md" ;;
  2|copilot) link ".github/copilot-instructions.md" ;;
  3|cursor)  link ".cursorrules" ;;
  4|all)
    link "CLAUDE.md"
    link ".github/copilot-instructions.md"
    link ".cursorrules"
    ;;
  *) echo "Invalid option." >&2; exit 1 ;;
esac

echo "Done. The source of truth is still AGENTS.md."
