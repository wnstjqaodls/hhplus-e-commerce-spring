import http from 'k6/http';
import { check, sleep } from 'k6';

// Stress test configuration - aggressive load testing
export let options = {
  stages: [
    { duration: '1m', target: 50 },   // Ramp up to 50 users
    { duration: '2m', target: 100 },  // Ramp up to 100 users
    { duration: '3m', target: 200 },  // Ramp up to 200 users
    { duration: '2m', target: 300 },  // Peak load at 300 users
    { duration: '2m', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% under 2s (more lenient for stress test)
    http_req_failed: ['rate<0.25'],    // Error rate under 25%
  },
};

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
  //1. 고동시성 시나리오 집중 테스트
  let scenario = Math.random();
  
  if (scenario < 0.5) {
    //2. 50% - 동시 포인트 충전 (분산 락 테스트)
    concurrentPointCharging();
  } else {
    //3. 50% - 동시 주문 생성 (재고 관리 테스트)
    concurrentOrderCreation();
  }
  
  //4. 스트레스 테스트용 최소 대기시간
  sleep(0.1);
}

function concurrentPointCharging() {
  //5. 경합 증가를 위해 적은 사용자 ID 사용
  const userId = Math.floor(Math.random() * 10) + 1;
  const amount = Math.floor(Math.random() * 5000) + 1000;
  
  //6. 포인트 충전 요청 (타임아웃 30초)
  let response = http.post(
    `${BASE_URL}/api/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '30s',
    }
  );
  
  //7. 포인트 충전 응답 검증 (분산 락 고려)
  check(response, {
    'point charge status is 200 or 409': (r) => r.status === 200 || r.status === 409,
    'point charge response time < 10s': (r) => r.timings.duration < 10000,
  });
}

function concurrentOrderCreation() {
  //8. 재고 경합 생성을 위해 제한된 상품 ID 사용
  const userId = Math.floor(Math.random() * 50) + 1;
  const productId = Math.floor(Math.random() * 5) + 1; // 5개 상품만 사용
  const quantity = 1; // 경합 최대화를 위한 고정 수량
  
  //9. 주문 생성 요청 (타임아웃 30초)
  let response = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '30s',
    }
  );
  
  //10. 주문 생성 응답 검증 (재고 부족 고려)
  check(response, {
    'order creation completed': (r) => r.status === 200 || r.status === 400 || r.status === 409,
    'order creation response time < 15s': (r) => r.timings.duration < 15000,
  });
}