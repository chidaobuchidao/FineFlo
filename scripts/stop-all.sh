#!/bin/bash
# 停止所有本地运行的服务
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Stopping all services..."

# 杀 Java 进程（Eureka / loan-api）
echo "  - Killing Java processes..."
taskkill /F /IM java.exe 2>/dev/null || true

# 杀 Python 进程（Risk Engine）
echo "  - Killing Python Flask..."
taskkill /F /IM python3.exe 2>/dev/null || true
taskkill /F /IM python.exe 2>/dev/null || true

# 杀 Node 进程（Vite dev server）
echo "  - Killing Node dev server..."
taskkill /F /IM node.exe 2>/dev/null || true

# 停止 Docker 容器
echo "  - Stopping Docker containers..."
cd "$PROJECT_DIR/docker"
docker-compose down 2>/dev/null || true

echo "All services stopped."
