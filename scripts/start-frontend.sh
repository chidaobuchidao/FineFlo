#!/bin/bash
# 启动前端 Vue 3 开发服务器
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "[Frontend] Starting Vue 3 dev server..."
cd "$PROJECT_DIR/loan-ui"
npm run dev 2>&1 &
sleep 4
echo "[Frontend] Dev server at http://localhost:5173"
