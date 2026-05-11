#!/bin/bash
# 停止全部服务 → 调用 scripts/stop-all.sh
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
bash "$PROJECT_DIR/scripts/stop-all.sh"
