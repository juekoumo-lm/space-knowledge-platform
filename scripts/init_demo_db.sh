#!/usr/bin/env bash
set -euo pipefail

# 用法:
#   bash scripts/init_demo_db.sh -h 127.0.0.1 -P 3306 -u root -p'pwd'
#   bash scripts/init_demo_db.sh -u root -p'pwd' --no-reset

HOST="127.0.0.1"
PORT="3306"
USER="root"
PASS=""
NO_RESET="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--host) HOST="$2"; shift 2;;
    -P|--port) PORT="$2"; shift 2;;
    -u|--user) USER="$2"; shift 2;;
    -p|--password) PASS="$2"; shift 2;;
    --no-reset) NO_RESET="true"; shift 1;;
    *) echo "Unknown arg: $1"; exit 1;;
  esac
done

MYSQL=(mysql -h"$HOST" -P"$PORT" -u"$USER")
[[ -n "$PASS" ]] && MYSQL+=("-p$PASS")

echo "[1/3] 导入 schema..."
"${MYSQL[@]}" < src/main/resources/db/schema.sql

if [[ "$NO_RESET" != "true" ]]; then
  echo "[2/3] 重置演示数据..."
  "${MYSQL[@]}" < src/main/resources/db/seed/00_reset_demo_data.sql
else
  echo "[2/3] 跳过 reset（--no-reset）"
fi

echo "[3/3] 导入演示数据..."
"${MYSQL[@]}" < src/main/resources/db/seed/99_bootstrap_demo.sql

echo "Done. 演示数据已就绪。"
