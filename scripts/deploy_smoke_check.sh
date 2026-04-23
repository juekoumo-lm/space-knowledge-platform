#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://127.0.0.1:8081/space-knowledge}"
LOG_FILE="/tmp/space_knowledge_jetty.log"

cleanup() {
  pkill -f "jetty:run" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "[1/4] Build check..."
# 确保构建产物完整（含资源文件）
mvn -q -DskipTests package

echo "[2/4] Starting Jetty..."
# 后台启动，日志写入临时文件便于排障
(mvn -DskipTests jetty:run >"$LOG_FILE" 2>&1 &) >/dev/null 2>&1

for i in $(seq 1 60); do
  if curl -fsS "$BASE_URL/index.html" >/dev/null 2>&1; then
    break
  fi
  sleep 1
  if [[ "$i" == "60" ]]; then
    echo "Jetty startup timeout. Last log lines:"
    tail -n 80 "$LOG_FILE" || true
    exit 1
  fi
done

echo "[3/4] Endpoint smoke checks..."
# 静态资源可访问性 + 受保护 API 匿名访问行为
ROOT_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/index.html")
LOGIN_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/login.html")
API_PAYLOAD=$(curl -s "$BASE_URL/api/student/dashboard")

if [[ "$ROOT_STATUS" != "200" || "$LOGIN_STATUS" != "200" ]]; then
  echo "Static page check failed: index=$ROOT_STATUS login=$LOGIN_STATUS"
  exit 1
fi

if [[ "$API_PAYLOAD" != *"用户未登录"* ]]; then
  echo "Unexpected anonymous API response: $API_PAYLOAD"
  exit 1
fi

echo "[4/4] OK"
echo "- index.html: $ROOT_STATUS"
echo "- login.html: $LOGIN_STATUS"
echo "- /api/student/dashboard (anonymous): $API_PAYLOAD"
