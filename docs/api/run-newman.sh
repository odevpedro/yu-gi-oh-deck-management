#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

newman run \
  "$ROOT_DIR/api/duel-service.postman_collection.json" \
  -e "$ROOT_DIR/api/yu-gi-oh-deck-management.postman_environment.json" \
  "$@"
