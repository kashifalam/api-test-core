#!/usr/bin/env bash
# Cloud Agent install: build the test framework, the service SDKs/test suites,
# and the microservice Docker images. Idempotent and safe to re-run.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

CORE_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ROOT="$(workspace_root)"
SERVICES_DIR="$ROOT/api-test-services"

ensure_toolchain

log "Building api-test-core (Checkstyle + Spotless + Enforcer, install to local .m2)"
( cd "$CORE_DIR" && mvn -B clean install )

if [ -d "$SERVICES_DIR" ]; then
  log "Building api-test-services (SDKs + test suites)"
  ( cd "$SERVICES_DIR" && mvn -B clean install -DskipTests )
else
  log "WARN: api-test-services not found at $SERVICES_DIR (skipping)"
fi

ensure_dockerd
for svc in order-service payment-service; do
  svc_dir="$ROOT/$svc"
  if [ -d "$svc_dir" ]; then
    log "Building Docker image for $svc"
    ( cd "$svc_dir" && docker compose build )
  else
    log "WARN: $svc not found at $svc_dir (skipping)"
  fi
done

log "install complete"
