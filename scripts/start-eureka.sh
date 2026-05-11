#!/bin/bash
# 启动 Eureka 注册中心
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "[Eureka] Building and starting..."
cd "$PROJECT_DIR/eureka-server"
mvn spring-boot:run -q 2>&1 &
sleep 8
echo "[Eureka] Running at http://localhost:8761"
