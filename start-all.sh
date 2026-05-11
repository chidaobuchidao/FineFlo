#!/bin/bash
# ============================================
# 普惠金融管理系统 — 一键启动全部服务
# ============================================
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
[ -f "$PROJECT_DIR/.env" ] && source "$PROJECT_DIR/.env"

echo "========================================="
echo "  普惠金融管理系统 — Starting All Services"
echo "========================================="
echo ""
echo "  Database:  mysql://localhost:${DB_PORT:-3306}/${DB_NAME:-inclusive_finance}"
echo "  Redis:     localhost:${REDIS_PORT:-6379}"
echo "  Eureka:    http://localhost:${EUREKA_PORT:-8761}"
echo "  Loan API:  http://localhost:${LOAN_API_PORT:-8081}"
echo "  Risk Eng:  http://localhost:${RISK_ENGINE_PORT:-5000}"
echo "  Loan UI:   http://localhost:${LOAN_UI_PORT:-5173}"
echo ""

# Step 1: 基础设施
echo "[1/4] Infrastructure..."
cd "$PROJECT_DIR/docker"
docker-compose up -d mysql redis 2>/dev/null || \
    echo "       (Docker not available, assuming MySQL/Redis already running)"
sleep 5

# Step 2: 后端
echo "[2/4] Loan API (SpringBoot)..."
cd "$PROJECT_DIR/loan-api"
export DB_USERNAME="${DB_USERNAME:-root}"
export DB_PASSWORD="${DB_PASSWORD:-2219909857}"
mvn spring-boot:run -Dspring-boot.run.profiles=dev -q 2>&1 &
sleep 15
echo "       Loan API health: $(curl -s http://localhost:${LOAN_API_PORT:-8081}/actuator/health 2>/dev/null || echo 'starting...')"

# Step 3: 风控引擎
echo "[3/4] Risk Engine (Flask)..."
cd "$PROJECT_DIR/risk-engine"
export FLASK_ENV=development
python3 app.py 2>&1 &
sleep 4
echo "       Risk Engine health: $(curl -s http://localhost:${RISK_ENGINE_PORT:-5000}/health 2>/dev/null || echo 'starting...')"

# Step 4: 前端
echo "[4/4] Loan UI (Vue 3)..."
cd "$PROJECT_DIR/loan-ui"
npm run dev 2>&1 &
sleep 4

echo ""
echo "========================================="
echo "  All services started!"
echo "========================================="
echo ""
echo "  前端页面:    http://localhost:5173"
echo "  后端 API:    http://localhost:8081"
echo "  风控引擎:    http://localhost:5000/health"
echo "  Thymeleaf:   http://localhost:8081/enterprise/login"
echo ""
echo "  停止服务:    ./scripts/stop-all.sh"
