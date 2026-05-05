#!/bin/bash
# 3단계 벤치마크 실행 스크립트
# k6는 로컬 PC에서 실행 (실제 사용자 관점, 인터넷 RTT 포함)
# 사전 요구사항: k6 설치 (https://k6.io/docs/getting-started/installation/)

set -e

BASE_URL="${BASE_URL:-http://evil55.cloud/api/user-service}"
SCRIPT="$(dirname "$0")/deck-search.js"
RESULTS_DIR="docs/perf/results"
mkdir -p "$RESULTS_DIR"

echo "======================================"
echo " Deck 조회 성능 벤치마크"
echo " 대상: $BASE_URL"
echo " 주의: 인터넷 RTT 포함 측정값"
echo "======================================"

# Stage 1: Baseline (인덱스 없음)
echo ""
echo "[1/3] Baseline 측정 시작 (인덱스 없음, PK만 존재)"
echo "  → 현재 상태 그대로 측정합니다."
read -p "  준비되면 Enter..."
k6 run -e BASE_URL="$BASE_URL" \
  --out json="$RESULTS_DIR/baseline.json" \
  "$SCRIPT"
cp "$RESULTS_DIR/summary.json" "$RESULTS_DIR/baseline-summary.json"
echo "  ✓ Baseline 측정 완료 → $RESULTS_DIR/baseline-summary.json"

# Stage 2: Index 적용
echo ""
echo "[2/3] Index 적용 후 측정"
echo "  → 다음 SQL을 lbs-server MySQL에 실행하세요:"
echo "     ALTER TABLE decks ADD INDEX idx_category_created_date (category, created_date DESC);"
read -p "  인덱스 추가 완료 후 Enter..."
k6 run -e BASE_URL="$BASE_URL" \
  --out json="$RESULTS_DIR/index.json" \
  "$SCRIPT"
cp "$RESULTS_DIR/summary.json" "$RESULTS_DIR/index-summary.json"
echo "  ✓ Index 측정 완료 → $RESULTS_DIR/index-summary.json"

# Stage 3: Cache 적용
echo ""
echo "[3/3] Cache 적용 후 측정"
echo "  → UserService에 CacheConfig + @Cacheable 추가 후 배포하세요."
echo "  → redis-cli FLUSHDB 실행 후 진행하세요."
read -p "  캐시 적용 및 배포 완료 후 Enter..."
k6 run -e BASE_URL="$BASE_URL" \
  --out json="$RESULTS_DIR/cache.json" \
  "$SCRIPT"
cp "$RESULTS_DIR/summary.json" "$RESULTS_DIR/cache-summary.json"
echo "  ✓ Cache 측정 완료 → $RESULTS_DIR/cache-summary.json"

echo ""
echo "======================================"
echo " 전체 측정 완료"
echo " 결과 파일: $RESULTS_DIR/"
echo "======================================"
