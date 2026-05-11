#!/bin/bash
# 启动后端 Loan-API（dev 模式，无需 Redis/Eureka）
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 读取根目录 .env
[ -f "$PROJECT_DIR/.env" ] && source "$PROJECT_DIR/.env"

echo "[Backend] Starting loan-api (dev profile)..."
cd "$PROJECT_DIR/loan-api"
export DB_USERNAME="${DB_USERNAME:-root}"
export DB_PASSWORD="${DB_PASSWORD:-2219909857}"
mvn spring-boot:run -Dspring-boot.run.profiles=dev -q 2>&1 &
sleep 12
echo "[Backend] API ready at http://localhost:${LOAN_API_PORT:-8081}"
echo "[Backend] Health: http://localhost:${LOAN_API_PORT:-8081}/actuator/health"
