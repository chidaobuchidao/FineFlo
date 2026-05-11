#!/bin/bash
# 启动 AI 风控引擎（Flask，跳过 Eureka 注册）
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "[RiskEngine] Starting Flask service..."
cd "$PROJECT_DIR/risk-engine"
export FLASK_ENV=development
export EUREKA_SERVER=http://localhost:1/nonexistent
python3 app.py 2>&1 &
sleep 3
echo "[RiskEngine] Service at http://localhost:5000"
echo "[RiskEngine] Health: http://localhost:5000/health"
