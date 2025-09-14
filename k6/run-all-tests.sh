#!/bin/bash
# 🚀 전체 부하 테스트 실행 스크립트
# 모든 테스트 시나리오를 순차적으로 실행하고 결과를 정리합니다.

set -e  # 에러 발생 시 스크립트 중단

echo "🎯 HH+ E-commerce 부하 테스트 시작"
echo "======================================"

# 📁 결과 디렉토리 생성
mkdir -p results
rm -f results/*.html results/*.json

# 📊 테스트 시작 시간 기록
START_TIME=$(date +%s)
echo "시작 시간: $(date)"

echo ""
echo "1. 🚀 기본 부하 테스트 (Load Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/load-test.json load-test-basic.js

echo ""
echo "2. 💨 연기 테스트 (Smoke Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/smoke-test.json smoke-test.js

echo ""
echo "3. 🔥 스트레스 테스트 (Stress Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/stress-test.json stress-test.js

echo ""
echo "4. ⚡ 스파이크 테스트 (Spike Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/spike-test.json spike-test.js

echo ""
echo "5. 🏔️ 피크 테스트 (Peak Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/peak-test.json peak-test.js

echo ""
echo "6. 📦 대용량 테스트 (Volume Test) 실행..."
echo "--------------------------------------"
k6 run --out json=results/volume-test.json volume-test.js

echo ""
echo "7. 🎫 동시 쿠폰 발급 테스트 실행..."
echo "--------------------------------------"
k6 run --out json=results/concurrent-coupon.json concurrent-coupon-test.js

echo ""
echo "8. 🔐 분산 락 경합 테스트 실행..."
echo "--------------------------------------"
k6 run --out json=results/distributed-lock.json distributed-lock-test.js

# 📊 테스트 종료 시간 및 소요 시간 계산
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))
MINUTES=$((DURATION / 60))
SECONDS=$((DURATION % 60))

echo ""
echo "✅ 모든 부하 테스트 완료!"
echo "========================="
echo "종료 시간: $(date)"
echo "총 소요 시간: ${MINUTES}분 ${SECONDS}초"

echo ""
echo "📋 생성된 결과 파일:"
echo "-------------------"
ls -la results/

echo ""
echo "🌐 HTML 리포트 확인:"
echo "-------------------"
echo "- 기본 부하 테스트: results/summary.html"
echo "- 동시 쿠폰 발급: results/concurrent-coupon-report.html"
echo "- 분산 락 경합: results/distributed-lock-report.html"

echo ""
echo "📊 JSON 메트릭 파일:"
echo "-------------------"
find results/ -name "*.json" -type f

echo ""
echo "🎯 다음 단계: STEP 20 성능 분석 및 개선"
echo "======================================="
echo "1. HTML 리포트를 브라우저에서 확인"
echo "2. 병목점 식별 및 개선 방안 수립"
echo "3. 성능 최적화 구현"
echo "4. 개선 후 재테스트 수행"