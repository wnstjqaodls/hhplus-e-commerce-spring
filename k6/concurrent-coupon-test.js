// 🎫 동시 쿠폰 발급 부하 테스트 스크립트
// 선착순 쿠폰 발급 시나리오에서 동시성 제어 및 카프카 메시지 처리 성능 검증

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 📊 커스텀 메트릭 정의
export const errorRate = new Rate('coupon_errors');
export const successRate = new Rate('coupon_success');
export const responseTime = new Trend('coupon_response_time');
export const concurrentIssues = new Counter('concurrent_coupon_issues');

// ⚙️ 테스트 설정 - 동시 쿠폰 발급 시뮬레이션
export const options = {
  scenarios: {
    // 🏃‍♂️ 급격한 쿠폰 발급 요청 (플래시 세일 상황)
    spike_coupon_issue: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 100 }, // 빠른 증가
        { duration: '30s', target: 100 }, // 고부하 유지
        { duration: '10s', target: 0 },   // 빠른 감소
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% 요청이 1초 이내
    http_req_failed: ['rate<0.05'],    // 에러율 5% 미만 (쿠폰 소진 고려)
    coupon_success: ['rate>0.1'],      // 최소 10% 성공률
    coupon_response_time: ['p(95)<800'], // 쿠폰 발급 응답시간
  },
};

// 🌐 애플리케이션 기본 URL
const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
  // 🎫 동시 쿠폰 발급 테스트
  testConcurrentCouponIssuance();

  sleep(0.1); // 짧은 대기시간으로 동시성 극대화
}

// 🎫 동시 쿠폰 발급 테스트 함수
function testConcurrentCouponIssuance() {
  const userId = Math.floor(Math.random() * 1000) + 1; // 더 많은 사용자 ID 범위
  const couponId = Math.floor(Math.random() * 3) + 1;   // 3종류 쿠폰

  const startTime = Date.now();

  // 🏃‍♂️ 쿠폰 발급 요청 (카프카 비동기 처리)
  let couponResponse = http.post(
    `${BASE_URL}/coupons/${couponId}/issue/${userId}`,
    null,
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '5s', // 타임아웃 설정
    }
  );

  const endTime = Date.now();
  const duration = endTime - startTime;

  // 📊 응답 검증 및 메트릭 수집
  let isSuccess = check(couponResponse, {
    '쿠폰 발급 요청 접수 성공': (r) => r.status === 200,
    '쿠폰 발급 응답시간 1초 미만': (r) => r.timings.duration < 1000,
    '응답 본문 존재': (r) => r.body && r.body.length > 0,
  });

  // 📈 메트릭 기록
  responseTime.add(duration);

  if (isSuccess && couponResponse.status === 200) {
    successRate.add(1);
    concurrentIssues.add(1);

    // 📝 성공 응답 로깅 (샘플링)
    if (Math.random() < 0.1) { // 10% 확률로 로깅
      console.log(`✅ 쿠폰 발급 성공: User ${userId}, Coupon ${couponId}, Response: ${couponResponse.body}`);
    }
  } else {
    successRate.add(0);
    errorRate.add(1);

    // 🚨 실패 응답 분석
    if (couponResponse.status === 409) {
      console.log(`⚠️ 쿠폰 소진: User ${userId}, Coupon ${couponId}`);
    } else if (couponResponse.status === 500) {
      console.log(`❌ 서버 오류: User ${userId}, Coupon ${couponId}, Status: ${couponResponse.status}`);
    }
  }
}

// 📊 상세한 테스트 결과 리포트 생성
export function handleSummary(data) {
  const successCount = data.metrics.concurrent_coupon_issues.values.count || 0;
  const totalRequests = data.metrics.http_reqs.values.count || 0;
  const successRatio = totalRequests > 0 ? (successCount / totalRequests * 100).toFixed(2) : 0;

  return {
    'results/concurrent-coupon-report.html': htmlReport(data, successCount, successRatio),
    'results/concurrent-coupon-metrics.json': JSON.stringify(data),
  };
}

// 📋 HTML 리포트 생성 함수
function htmlReport(data, successCount, successRatio) {
  const avgResponseTime = data.metrics.http_req_duration.values.avg.toFixed(2);
  const p95ResponseTime = data.metrics.http_req_duration.values['p(95)'].toFixed(2);
  const errorRate = data.metrics.http_req_failed.values.rate * 100;
  const requestRate = data.metrics.http_reqs.values.rate.toFixed(2);

  return `<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🎫 동시 쿠폰 발급 부하 테스트 결과</title>
    <style>
        body {
            font-family: 'Malgun Gothic', Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .metric {
            margin: 15px 0;
            padding: 15px;
            border-left: 4px solid #007cba;
            background: #f8f9fa;
            border-radius: 4px;
        }
        .success { border-left-color: #28a745; background: #f0fff4; }
        .warning { border-left-color: #ffc107; background: #fffdf0; }
        .error { border-left-color: #dc3545; background: #fff5f5; }
        .highlight { font-weight: bold; color: #0056b3; }
        h1 { color: #333; text-align: center; }
        h3 { margin-top: 0; color: #555; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎫 동시 쿠폰 발급 부하 테스트 결과</h1>

        <div class="grid">
            <div class="metric ${successRatio > 50 ? 'success' : successRatio > 20 ? 'warning' : 'error'}">
                <h3>🏆 쿠폰 발급 성공률</h3>
                <p>성공한 발급 수: <span class="highlight">${successCount}건</span></p>
                <p>전체 요청 대비: <span class="highlight">${successRatio}%</span></p>
            </div>

            <div class="metric">
                <h3>📈 응답시간 분석</h3>
                <p>평균: <span class="highlight">${avgResponseTime}ms</span></p>
                <p>95% 백분위수: <span class="highlight">${p95ResponseTime}ms</span></p>
            </div>
        </div>

        <div class="grid">
            <div class="metric ${errorRate < 5 ? 'success' : errorRate < 20 ? 'warning' : 'error'}">
                <h3>❌ 에러율 분석</h3>
                <p>HTTP 에러율: <span class="highlight">${errorRate.toFixed(2)}%</span></p>
                <p>※ 쿠폰 소진으로 인한 실패 포함</p>
            </div>

            <div class="metric">
                <h3>🚀 처리량</h3>
                <p>초당 요청 수: <span class="highlight">${requestRate} req/s</span></p>
                <p>최대 동시 사용자: <span class="highlight">${data.metrics.vus_max.values.max}명</span></p>
            </div>
        </div>

        <div class="metric">
            <h3>📋 동시성 분석 결과</h3>
            <ul>
                <li><strong>카프카 메시지 처리:</strong> ${successCount > 0 ? '정상 작동' : '처리 실패'}</li>
                <li><strong>응답시간 안정성:</strong> ${p95ResponseTime < 1000 ? '우수 (1초 이내)' : '개선 필요'}</li>
                <li><strong>동시성 제어:</strong> ${errorRate < 10 ? '적절함' : '병목 존재 가능성'}</li>
                <li><strong>시스템 안정성:</strong> ${errorRate < 30 ? '안정적' : '불안정'}</li>
            </ul>
        </div>

        <div class="metric warning">
            <h3>⚠️ 주의사항</h3>
            <p>이 테스트는 선착순 쿠폰 발급 특성상 높은 실패율이 예상되는 시나리오입니다.</p>
            <p>중요한 것은 <strong>시스템 안정성</strong>과 <strong>적절한 응답시간</strong> 유지입니다.</p>
        </div>
    </div>
</body>
</html>`;
}