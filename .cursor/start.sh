#!/usr/bin/env bash
# Cloud Agent start: bring up the order-service and payment-service stacks
# (each with its own MongoDB) and wire them onto a shared network so the
# payment-service can reach the order-service by name. Idempotent.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

ROOT="$(workspace_root)"
ORDER_DIR="$ROOT/order-service"
PAYMENT_DIR="$ROOT/payment-service"
SHARED_NET="apitest-net"

ensure_toolchain
ensure_dockerd
fix_bridge_netfilter

docker network create "$SHARED_NET" 2>/dev/null || true

if [ -d "$ORDER_DIR" ]; then
  log "Starting order-service (port 8081)"
  ( cd "$ORDER_DIR" && docker compose up -d )
  docker network connect "$SHARED_NET" order-service 2>/dev/null || true
  wait_for_health "order-service" "http://localhost:8081/actuator/health"
else
  log "WARN: order-service not found at $ORDER_DIR (skipping)"
fi

if [ -d "$PAYMENT_DIR" ]; then
  log "Starting payment-service (port 8082)"
  # Reach the order-service by container name over the shared network instead
  # of the cross-bridge host.docker.internal path, which is blocked here.
  ( cd "$PAYMENT_DIR" && ORDER_SERVICE_URL="http://order-service:8081" docker compose up -d )
  docker network connect "$SHARED_NET" payment-service 2>/dev/null || true
  wait_for_health "payment-service" "http://localhost:8082/actuator/health"
else
  log "WARN: payment-service not found at $PAYMENT_DIR (skipping)"
fi

log "start complete"
docker ps --format '{{.Names}}\t{{.Status}}\t{{.Ports}}'
