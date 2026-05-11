#!/bin/bash
# 启动基础设施：MySQL + Redis（Docker Compose）
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "[Infra] Starting MySQL + Redis..."
cd "$PROJECT_DIR/docker"
docker-compose up -d mysql redis
echo "[Infra] Waiting for MySQL health check..."
sleep 6
echo "[Infra] MySQL + Redis ready."
