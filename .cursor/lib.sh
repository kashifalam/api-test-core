#!/usr/bin/env bash
# Shared helpers for Cloud Agent environment setup.
# Sourced by install.sh and start.sh.

log() { echo "[env] $*"; }

MAVEN_VERSION="3.9.9"

# Install the system toolchain (JDK 21, Maven 3.9+, Docker engine + compose,
# fuse-overlayfs) if any piece is missing, and configure the Docker daemon to
# use fuse-overlayfs (the kernel overlay driver cannot mount in this nested VM).
# Idempotent: each step is skipped when already satisfied.
ensure_toolchain() {
  if ! command -v java >/dev/null 2>&1; then
    log "Installing JDK 21"
    sudo apt-get update -qq
    sudo apt-get install -y -qq openjdk-21-jdk-headless
  fi

  if ! command -v mvn >/dev/null 2>&1; then
    log "Installing Maven $MAVEN_VERSION"
    local tmp="/tmp/apache-maven-${MAVEN_VERSION}.tar.gz"
    curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o "$tmp"
    sudo mkdir -p /opt/maven
    sudo tar -xzf "$tmp" -C /opt/maven --strip-components=1
    sudo ln -sf /opt/maven/bin/mvn /usr/local/bin/mvn
    rm -f "$tmp"
  fi

  if ! command -v fuse-overlayfs >/dev/null 2>&1; then
    log "Installing fuse-overlayfs"
    sudo apt-get update -qq
    sudo apt-get install -y -qq fuse-overlayfs || true
  fi

  if ! command -v docker >/dev/null 2>&1; then
    log "Installing Docker engine"
    curl -fsSL https://get.docker.com -o /tmp/get-docker.sh
    sudo sh /tmp/get-docker.sh
    rm -f /tmp/get-docker.sh
    sudo groupadd -f docker
    sudo usermod -aG docker "$(whoami)" || true
  fi

  # Docker must use fuse-overlayfs: the default kernel overlay snapshotter
  # fails to mount inside this nested container.
  if [ ! -f /etc/docker/daemon.json ]; then
    log "Configuring Docker to use fuse-overlayfs"
    sudo mkdir -p /etc/docker
    printf '%s\n' '{' \
      '  "storage-driver": "fuse-overlayfs",' \
      '  "features": { "containerd-snapshotter": false }' \
      '}' | sudo tee /etc/docker/daemon.json >/dev/null
  fi
}

# Resolve the workspace root (the directory that holds all sibling repos).
# The primary repo (api-test-core) lives at <workspace>/api-test-core and the
# other services are checked out as siblings via repositoryDependencies.
workspace_root() {
  local script_dir
  script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  cd "$script_dir/../.." && pwd
}

# Start the Docker daemon if it is not already running and make the socket
# usable without sudo. Idempotent: safe to call from both install and start.
ensure_dockerd() {
  if docker info >/dev/null 2>&1; then
    return 0
  fi
  if sudo docker info >/dev/null 2>&1; then
    sudo chmod 666 /var/run/docker.sock
    return 0
  fi

  log "Starting dockerd"
  sudo bash -c 'nohup dockerd >/var/log/dockerd.log 2>&1 &'
  for _ in $(seq 1 30); do
    if sudo docker info >/dev/null 2>&1; then
      break
    fi
    sleep 2
  done
  if ! sudo docker info >/dev/null 2>&1; then
    log "dockerd failed to start; last log lines:"
    sudo tail -n 40 /var/log/dockerd.log || true
    return 1
  fi
  sudo chmod 666 /var/run/docker.sock
  fix_bridge_netfilter
}

# In this nested VM, Docker's nftables FORWARD rules drop same-bridge
# container-to-container traffic when bridged frames are handed to netfilter.
# Disabling bridge-nf-call-iptables keeps intra-network traffic on the L2 path.
fix_bridge_netfilter() {
  sudo modprobe br_netfilter 2>/dev/null || true
  sudo sysctl -w net.bridge.bridge-nf-call-iptables=0 >/dev/null 2>&1 || true
}

wait_for_health() {
  local name="$1" url="$2"
  for _ in $(seq 1 60); do
    if curl -fsS "$url" 2>/dev/null | grep -q '"status":"UP"'; then
      log "$name is healthy"
      return 0
    fi
    sleep 3
  done
  log "ERROR: $name did not become healthy at $url"
  return 1
}
