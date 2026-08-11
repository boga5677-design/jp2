#!/usr/bin/env bash
set -euo pipefail
ROOT="app/src/main/java"
COUNT=$(grep -R --include='*.kt' -hE '^data class Word\b' "$ROOT" | wc -l | tr -d ' ')
if [ "$COUNT" -ne 1 ]; then
  echo "Expected exactly one data class Word declaration, found $COUNT"
  grep -R --include='*.kt' -nE '^data class Word\b' "$ROOT" || true
  exit 1
fi
echo "Model declaration check passed."
