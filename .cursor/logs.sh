#!/usr/bin/env bash
# Tail logs for a service container in a persistent terminal.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

SERVICE="${1:?usage: logs.sh <container-name>}"

ensure_dockerd
# Wait for the container to exist, then follow its logs.
for _ in $(seq 1 60); do
  if docker ps -a --format '{{.Names}}' | grep -qx "$SERVICE"; then
    break
  fi
  sleep 2
done
exec docker logs -f "$SERVICE"
