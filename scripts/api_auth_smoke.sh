#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://127.0.0.1:8081/space-knowledge/api}"
USERNAME="${2:-student01}"
PASSWORD="${3:-student123}"

request_json() {
  local method="$1"
  local url="$2"
  local auth="${3:-}"
  local data="${4:-}"
  if [[ -n "$auth" ]]; then
    curl -s -X "$method" "$url" -H "Authorization: Bearer $auth" -H 'Content-Type: application/json' -d "$data"
  else
    curl -s -X "$method" "$url" -H 'Content-Type: application/json' -d "$data"
  fi
}

echo "[1/3] 登录获取 token..."
LOGIN_RESP=$(request_json POST "$BASE_URL/auth/login" "" "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

TOKEN=$(python - <<'PY' "$LOGIN_RESP"
import json,sys
try:
    data=json.loads(sys.argv[1])
    print(((data.get('data') or {}).get('token')) or '')
except Exception:
    print('')
PY
)

if [[ -z "$TOKEN" ]]; then
  echo "登录失败，响应：$LOGIN_RESP"
  exit 1
fi

echo "[2/3] 访问学生首页数据接口..."
DASH=$(curl -s "$BASE_URL/student/dashboard" -H "Authorization: Bearer $TOKEN")
if [[ "$DASH" != *"\"code\":200"* ]]; then
  echo "dashboard 接口异常：$DASH"
  exit 1
fi

echo "[3/3] 访问推荐接口..."
REC=$(curl -s "$BASE_URL/student/recommend?limit=5" -H "Authorization: Bearer $TOKEN")
if [[ "$REC" != *"\"code\":200"* ]]; then
  echo "recommend 接口异常：$REC"
  exit 1
fi

# 额外校验：匿名访问应被拒绝，避免接口误开放
ANON=$(curl -s "$BASE_URL/student/dashboard")
if [[ "$ANON" != *"用户未登录"* ]]; then
  echo "匿名鉴权校验异常：$ANON"
  exit 1
fi

echo "OK: 登录、dashboard、recommend、匿名鉴权检查全部通过。"
