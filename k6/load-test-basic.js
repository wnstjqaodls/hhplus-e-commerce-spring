// 🎯 기본 부하 테스트 스크립트
// 전자상거래 애플리케이션의 다양한 시나리오를 단계적으로 테스트합니다

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 📊 커스텀 메트릭 정의
export const errorRate = new Rate('errors');
export const responseTime = new Trend('response_time');

// ⚙️ 테스트 설정 - 단계적 부하 증가
export const options = {
  stages: [
    { duration: '2m', target: 10 }, // Ramp up to 10 users
    { duration: '5m', target: 10 }, // Stay at 10 users
    { duration: '2m', target: 20 }, // Ramp up to 20 users
    { duration: '5m', target: 20 }, // Stay at 20 users
    { duration: '2m', target: 0 },  // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests under 500ms
    http_req_failed: ['rate<0.1'],    // Error rate under 10%
    errors: ['rate<0.1'],             // Custom error rate under 10%
  },
};

// 🌐 애플리케이션 기본 URL (Docker 환경용)
const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
  // 🎲 전자상거래 애플리케이션 기반 테스트 시나리오 선택
  let testScenario = Math.random();
  
  if (testScenario < 0.4) {
    // 📦 40% - 상품 탐색 시나리오
    testProductBrowsing();
  } else if (testScenario < 0.7) {
    // 💳 30% - 포인트 충전/사용 시나리오
    testPointOperations();
  } else if (testScenario < 0.9) {
    // 🛒 20% - 주문 생성 시나리오
    testOrderOperations();
  } else {
    // 🎫 10% - 쿠폰 발급 시나리오
    testCouponOperations();
  }
  
  sleep(1); // 💤 사용자 행동 간격 시뮬레이션 (Think Time)
}

// 📦 상품 탐색 테스트 함수
function testProductBrowsing() {
  // 상품 목록 조회
  let response = http.get(`${BASE_URL}/api/products`);
  
  let success = check(response, {
    '상품 목록 조회 성공 (200)': (r) => r.status === 200,
    '상품 목록 응답시간 200ms 미만': (r) => r.timings.duration < 200,
  });
  
  errorRate.add(!success);
  responseTime.add(response.timings.duration);
  
  if (response.status === 200) {
    // 🔍 상품 상세 정보 조회
    sleep(0.5);
    let productResponse = http.get(`${BASE_URL}/api/products/1`);
    
    check(productResponse, {
      '상품 상세 조회 성공 (200)': (r) => r.status === 200,
    });
  }
}

// 💳 포인트 충전/조회 테스트 함수
function testPointOperations() {
  const userId = Math.floor(Math.random() * 100) + 1;
  const amount = Math.floor(Math.random() * 10000) + 1000;
  
  // 💰 포인트 충전
  let chargeResponse = http.post(
    `${BASE_URL}/api/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );
  
  let success = check(chargeResponse, {
    '포인트 충전 성공 (200)': (r) => r.status === 200,
    '포인트 충전 응답시간 1초 미만': (r) => r.timings.duration < 1000,
  });
  
  errorRate.add(!success);
  responseTime.add(chargeResponse.timings.duration);
  
  sleep(0.5);
  
  // 💸 포인트 잔액 조회
  let balanceResponse = http.get(`${BASE_URL}/api/points/balance/${userId}`);
  
  check(balanceResponse, {
    '포인트 잔액 조회 성공 (200)': (r) => r.status === 200,
  });
}

// 🛒 주문 생성 테스트 함수
function testOrderOperations() {
  const userId = Math.floor(Math.random() * 100) + 1;
  const productId = Math.floor(Math.random() * 10) + 1;
  const quantity = Math.floor(Math.random() * 3) + 1;
  
  // 📋 주문 생성
  let orderResponse = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );
  
  let success = check(orderResponse, {
    '주문 생성 성공 (200)': (r) => r.status === 200,
    '주문 생성 응답시간 2초 미만': (r) => r.timings.duration < 2000,
  });
  
  errorRate.add(!success);
  responseTime.add(orderResponse.timings.duration);
}

// 🎫 쿠폰 발급 테스트 함수 (선착순 시나리오)
function testCouponOperations() {
  const userId = Math.floor(Math.random() * 100) + 1;
  const couponId = Math.floor(Math.random() * 5) + 1;
  
  // 🏃‍♂️ 쿠폰 발급 (선착순 시나리오)
  let couponResponse = http.post(
    `${BASE_URL}/api/coupons/issue`,
    JSON.stringify({
      userId: userId,
      couponId: couponId
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );
  
  let success = check(couponResponse, {
    '쿠폰 발급 응답시간 1.5초 미만': (r) => r.timings.duration < 1500,
  });
  
  // ⚠️ 쿠폰 재고 부족으로 실패할 수 있으므로 에러로 카운트하지 않음
  responseTime.add(couponResponse.timings.duration);
}

// 📊 테스트 결과 리포트 생성
export function handleSummary(data) {
  return {
    'results/summary.html': htmlReport(data),
    'results/summary.json': JSON.stringify(data),
  };
}

// 📋 HTML 리포트 생성 함수
function htmlReport(data) {
  return `<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>전자상거래 부하 테스트 결과</title>
    <style>
        body { 
            font-family: 'Malgun Gothic', Arial, sans-serif; 
            margin: 20px; 
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
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
        .error { border-left-color: #dc3545; background: #fff5f5; }
        .success { border-left-color: #28a745; background: #f0fff4; }
        h1 { color: #333; text-align: center; }
        h3 { margin-top: 0; color: #555; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🛒 전자상거래 부하 테스트 결과</h1>
        <div class="metric">
            <h3>📈 HTTP 요청 응답시간</h3>
            <p>평균: ${data.metrics.http_req_duration.values.avg.toFixed(2)}ms</p>
            <p>95% 백분위수: ${data.metrics.http_req_duration.values['p(95)'].toFixed(2)}ms</p>
        </div>
        <div class="metric ${data.metrics.http_req_failed.values.rate > 0.1 ? 'error' : 'success'}">
            <h3>❌ 에러율</h3>
            <p>${(data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%</p>
        </div>
        <div class="metric">
            <h3>🚀 초당 요청 수</h3>
            <p>${data.metrics.http_reqs.values.rate.toFixed(2)} req/s</p>
        </div>
        <div class="metric">
            <h3>👥 가상 사용자</h3>
            <p>최대: ${data.metrics.vus_max.values.max}명</p>
        </div>
    </div>
</body>
</html>`;
}