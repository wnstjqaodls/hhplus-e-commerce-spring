// 🔐 분산 락 경합 부하 테스트 스크립트
// 포인트 충전과 주문 결제의 분산 락 동시성 제어 성능 검증

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 📊 커스텀 메트릭 정의
export const lockContentionRate = new Rate('lock_contention_errors');
export const lockAcquisitionTime = new Trend('lock_acquisition_time');
export const pointChargeSuccess = new Counter('point_charge_success');
export const orderPaymentSuccess = new Counter('order_payment_success');
export const deadlockErrors = new Counter('deadlock_errors');

// ⚙️ 테스트 설정 - 분산 락 경합 시뮬레이션
export const options = {
  scenarios: {
    // 💰 포인트 충전 동시성 테스트 (같은 사용자 ID로 경합 발생)
    point_charging_contention: {
      executor: 'constant-vus',
      vus: 50,
      duration: '2m',
      tags: { scenario: 'point_charging' },
    },

    // 🛒 주문 결제 동시성 테스트
    order_payment_contention: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '30s', target: 30 },
        { duration: '1m', target: 30 },
        { duration: '30s', target: 0 },
      ],
      tags: { scenario: 'order_payment' },
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<2000'],     // 95% 요청이 2초 이내
    http_req_failed: ['rate<0.15'],        // 에러율 15% 미만 (락 대기 고려)
    lock_contention_errors: ['rate<0.3'],  // 락 경합 에러 30% 미만
    lock_acquisition_time: ['p(95)<1500'], // 락 획득 시간
  },
};

// 🌐 애플리케이션 기본 URL
const BASE_URL = 'http://host.docker.internal:8080';

// 🎲 제한된 사용자 ID 풀 (락 경합을 유도하기 위해)
const USER_ID_POOL = [1, 2, 3, 4, 5, 10, 15, 20, 25, 30];

export default function () {
  const scenario = __ENV.K6_SCENARIO || 'mixed';

  // 시나리오에 따라 다른 테스트 실행
  if (__ITER % 3 === 0) {
    // 33% - 포인트 충전 테스트
    testDistributedLockForPointCharge();
  } else if (__ITER % 3 === 1) {
    // 33% - 주문 결제 테스트
    testDistributedLockForOrderPayment();
  } else {
    // 34% - 혼합 시나리오 (동일 사용자의 포인트 충전 + 주문)
    testMixedLockScenario();
  }

  sleep(Math.random() * 0.5); // 0-500ms 랜덤 대기
}

// 💰 포인트 충전 분산 락 테스트
function testDistributedLockForPointCharge() {
  // 제한된 사용자 ID로 락 경합 유도
  const userId = USER_ID_POOL[Math.floor(Math.random() * USER_ID_POOL.length)];
  const amount = Math.floor(Math.random() * 5000) + 1000; // 1,000-6,000 포인트

  const startTime = Date.now();

  let chargeResponse = http.post(
    `${BASE_URL}/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '5s',
      tags: { operation: 'point_charge', user_id: userId },
    }
  );

  const endTime = Date.now();
  const duration = endTime - startTime;

  // 📊 응답 분석
  let success = check(chargeResponse, {
    '포인트 충전 응답 수신': (r) => r.status !== 0,
    '포인트 충전 성공': (r) => r.status === 200,
    '응답시간 3초 이내': (r) => r.timings.duration < 3000,
  });

  // 🔐 락 관련 에러 분석
  if (chargeResponse.status === 409) {
    lockContentionRate.add(1);
    console.log(`🔒 락 경합 감지: User ${userId}, Duration: ${duration}ms`);
  } else if (chargeResponse.status === 500) {
    // 로그에서 데드락 패턴 확인
    if (chargeResponse.body && chargeResponse.body.includes('deadlock')) {
      deadlockErrors.add(1);
      console.log(`💀 데드락 발생: User ${userId}`);
    }
  } else if (success) {
    pointChargeSuccess.add(1);
    lockAcquisitionTime.add(duration);
  }
}

// 🛒 주문 결제 분산 락 테스트
function testDistributedLockForOrderPayment() {
  const userId = USER_ID_POOL[Math.floor(Math.random() * USER_ID_POOL.length)];
  const productId = Math.floor(Math.random() * 10) + 1;
  const quantity = Math.floor(Math.random() * 2) + 1;
  const amount = quantity * 10000; // 상품 가격

  const startTime = Date.now();

  let orderResponse = http.post(
    `${BASE_URL}/orders/order-and-pay`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '8s', // 주문은 더 복잡한 프로세스
      tags: { operation: 'order_payment', user_id: userId },
    }
  );

  const endTime = Date.now();
  const duration = endTime - startTime;

  // 📊 응답 분석
  let success = check(orderResponse, {
    '주문 결제 응답 수신': (r) => r.status !== 0,
    '주문 결제 성공': (r) => r.status === 200,
    '응답시간 5초 이내': (r) => r.timings.duration < 5000,
  });

  // 🔐 락 및 비즈니스 로직 에러 분석
  if (orderResponse.status === 409) {
    lockContentionRate.add(1);
  } else if (orderResponse.status === 400) {
    // 포인트 부족, 재고 부족 등
    console.log(`⚠️ 비즈니스 규칙 위반: User ${userId}, Response: ${orderResponse.body}`);
  } else if (success) {
    orderPaymentSuccess.add(1);
    lockAcquisitionTime.add(duration);
  }
}

// 🔄 혼합 시나리오 테스트 (동일 사용자의 포인트 충전 + 주문)
function testMixedLockScenario() {
  const userId = USER_ID_POOL[Math.floor(Math.random() * 3)]; // 더 적은 사용자로 경합 극대화

  // 1. 포인트 충전 시도
  let chargeResponse = http.post(
    `${BASE_URL}/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: 10000
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '3s',
      tags: { operation: 'mixed_charge', user_id: userId },
    }
  );

  sleep(0.1); // 짧은 간격

  // 2. 바로 주문 시도 (동일 사용자)
  let orderResponse = http.post(
    `${BASE_URL}/orders/order-and-pay`,
    JSON.stringify({
      userId: userId,
      productId: 1,
      quantity: 1,
      amount: 5000
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '5s',
      tags: { operation: 'mixed_order', user_id: userId },
    }
  );

  // 📊 혼합 시나리오 결과 분석
  if (chargeResponse.status === 200 && orderResponse.status === 200) {
    console.log(`✅ 혼합 시나리오 성공: User ${userId}`);
  } else if (chargeResponse.status === 409 || orderResponse.status === 409) {
    lockContentionRate.add(1);
    console.log(`🔒 혼합 시나리오 락 경합: User ${userId}`);
  }
}

// 📊 분산 락 성능 리포트 생성
export function handleSummary(data) {
  const pointChargeCount = data.metrics.point_charge_success.values.count || 0;
  const orderPaymentCount = data.metrics.order_payment_success.values.count || 0;
  const lockContentionCount = data.metrics.lock_contention_errors.values.count || 0;
  const deadlockCount = data.metrics.deadlock_errors.values.count || 0;
  const totalRequests = data.metrics.http_reqs.values.count || 0;

  return {
    'results/distributed-lock-report.html': distributedLockHtmlReport(data, {
      pointChargeCount,
      orderPaymentCount,
      lockContentionCount,
      deadlockCount,
      totalRequests
    }),
    'results/distributed-lock-metrics.json': JSON.stringify(data),
  };
}

// 📋 분산 락 전용 HTML 리포트
function distributedLockHtmlReport(data, metrics) {
  const avgResponseTime = data.metrics.http_req_duration.values.avg.toFixed(2);
  const p95ResponseTime = data.metrics.http_req_duration.values['p(95)'].toFixed(2);
  const errorRate = data.metrics.http_req_failed.values.rate * 100;
  const lockContentionRate = metrics.totalRequests > 0 ?
    (metrics.lockContentionCount / metrics.totalRequests * 100).toFixed(2) : 0;

  return `<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🔐 분산 락 경합 부하 테스트 결과</title>
    <style>
        body {
            font-family: 'Malgun Gothic', Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1000px;
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
        .grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; }
        .wide { grid-column: span 3; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔐 분산 락 경합 부하 테스트 결과</h1>

        <div class="grid">
            <div class="metric success">
                <h3>💰 포인트 충전</h3>
                <p>성공: <span class="highlight">${metrics.pointChargeCount}건</span></p>
            </div>

            <div class="metric success">
                <h3>🛒 주문 결제</h3>
                <p>성공: <span class="highlight">${metrics.orderPaymentCount}건</span></p>
            </div>

            <div class="metric ${lockContentionRate < 20 ? 'success' : lockContentionRate < 40 ? 'warning' : 'error'}">
                <h3>🔒 락 경합률</h3>
                <p><span class="highlight">${lockContentionRate}%</span></p>
            </div>
        </div>

        <div class="grid">
            <div class="metric">
                <h3>📈 응답시간</h3>
                <p>평균: <span class="highlight">${avgResponseTime}ms</span></p>
                <p>95%: <span class="highlight">${p95ResponseTime}ms</span></p>
            </div>

            <div class="metric ${errorRate < 10 ? 'success' : errorRate < 20 ? 'warning' : 'error'}">
                <h3>❌ 에러율</h3>
                <p><span class="highlight">${errorRate.toFixed(2)}%</span></p>
            </div>

            <div class="metric ${metrics.deadlockCount === 0 ? 'success' : 'error'}">
                <h3>💀 데드락</h3>
                <p>발생: <span class="highlight">${metrics.deadlockCount}건</span></p>
            </div>
        </div>

        <div class="metric wide">
            <h3>🔍 분산 락 성능 분석</h3>
            <ul>
                <li><strong>락 획득 성능:</strong> ${p95ResponseTime < 1500 ? '우수 (1.5초 이내)' : '개선 필요'}</li>
                <li><strong>동시성 제어:</strong> ${metrics.deadlockCount === 0 ? '안정적 (데드락 없음)' : '위험 (데드락 발생)'}</li>
                <li><strong>Redis 락 안정성:</strong> ${lockContentionRate < 30 ? '정상' : '병목 의심'}</li>
                <li><strong>사용자 경험:</strong> ${errorRate < 15 ? '양호' : '불량 (높은 실패율)'}</li>
            </ul>
        </div>

        <div class="metric warning wide">
            <h3>⚠️ 분산 락 최적화 권장사항</h3>
            <ul>
                <li><strong>락 타임아웃:</strong> 현재 설정이 적절한지 검토 필요</li>
                <li><strong>Redis 성능:</strong> 락 경합률이 높을 경우 Redis 스케일링 고려</li>
                <li><strong>비즈니스 로직:</strong> 락 보유 시간 최소화 필요</li>
                <li><strong>에러 핸들링:</strong> 락 획득 실패 시 적절한 재시도 로직</li>
            </ul>
        </div>
    </div>
</body>
</html>`;
}