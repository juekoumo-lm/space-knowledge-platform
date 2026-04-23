#!/usr/bin/env bash
set -euo pipefail

# 一键预检：编译 + 单测 + Jetty烟雾 + 登录接口烟雾
# 用法：
#   bash scripts/full_preflight.sh
#   bash scripts/full_preflight.sh http://127.0.0.1:8081/space-knowledge/api
#   bash scripts/full_preflight.sh --skip-auth

SKIP_AUTH="false"
API_BASE="http://127.0.0.1:8081/space-knowledge/api"

for arg in "$@"; do
  if [[ "$arg" == "--skip-auth" ]]; then
    SKIP_AUTH="true"
  else
    API_BASE="$arg"
  fi
done

echo "[1/4] compile"
mvn -q -DskipTests compile

echo "[2/4] test"
mvn -q test

echo "[3/4] deploy smoke"
bash scripts/deploy_smoke_check.sh

if [[ "$SKIP_AUTH" == "true" ]]; then
  echo "[4/4] auth smoke skipped (--skip-auth)"
else
  echo "[4/4] auth smoke"
  if ! bash scripts/api_auth_smoke.sh "$API_BASE"; then
    echo "Auth smoke failed."
    echo "Hint: initialize demo DB first: bash scripts/init_demo_db.sh -u root -p'你的密码'"
    exit 1
  fi
fi

echo "All preflight checks passed."
