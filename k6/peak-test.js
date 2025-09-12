// 🚀 피크 트래픽 테스트 스크립트
// 플래시 세일이나 갑작스런 트래픽 급증 상황을 시뮬레이션합니다

import http from 'k6/http';
import { check, sleep } from 'k6';

// ⚡ 피크 테스트 설정 - 갑작스런 트래픽 급증 시뮬레이션
export const options = {
  stages: [
    { duration: '30s', target: 10 },   // 📈 평상시 부하
    { duration: '10s', target: 200 },  // ⚡ 갑작스런 급증!
    { duration: '1m', target: 200 },   // 🔥 급증 상태 유지
    { duration: '30s', target: 10 },   // 📉 평상시로 복귀
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'], // 📊 급증 시 높은 응답시간 허용 (3초)
    http_req_failed: ['rate<0.3'],     // ⚠️ 급증 시 에러율 허용 (30%)
  },
};

// 🌐 애플리케이션 기본 URL (Docker 환경용)
const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
  // 🛍️ 플래시 세일 시나리오 시뮬레이션
  let scenario = Math.random();
  
  if (scenario < 0.6) {
    // 🔥 60% - 인기 상품 접근 (플래시 세일)
    flashSaleProduct();
  } else if (scenario < 0.8) {
    // 🏃‍♂️ 20% - 쿠폰 대란
    flashCouponIssue();
  } else {
    // 🩺 20% - 시스템 상태 확인
    healthCheck();
  }
  
  sleep(Math.random() * 0.5); // 💭 랜덤 대기 시간 (급박한 상황 시뮬레이션)
}

// 🔥 플래시 세일 상품 접근 테스트 함수
function flashSaleProduct() {
  // 👥 모든 사용자가 동일한 "플래시 세일" 상품에 접근
  const flashSaleProductId = 1; // 💎 고정 상품 ID (집중 공격)
  
  let response = http.get(`${BASE_URL}/api/products/${flashSaleProductId}`);
  
  check(response, {
    '플래시 세일 상품 접근 가능': (r) => r.status === 200,
    '플래시 세일 응답시간 5초 미만': (r) => r.timings.duration < 5000,
  });
  
  // 🛒 상품이 이용 가능하면 즉시 주문 시도
  if (response.status === 200) {
    sleep(0.1); // ⚡ 플래시 세일을 위한 매우 짧은 대기시간
    
    const userId = Math.floor(Math.random() * 1000) + 1;
    let orderResponse = http.post(
      `${BASE_URL}/api/orders`,
      JSON.stringify({
        userId: userId,
        productId: flashSaleProductId,
        quantity: 1
      }),
      {
        headers: { 'Content-Type': 'application/json' },
        timeout: '10s',
      }
    );
    
    check(orderResponse, {
      '플래시 세일 주문 시도': (r) => r.status !== 500, // 서버 에러가 아닌 모든 응답 허용
    });
  }
}

// 🎫 플래시 쿠폰 발급 테스트 함수
function flashCouponIssue() {
  // 🕐 한정 시간, 한정 수량 쿠폰
  const flashCouponId = 1;
  const userId = Math.floor(Math.random() * 1000) + 1;
  
  let response = http.post(
    `${BASE_URL}/api/coupons/issue`,
    JSON.stringify({
      userId: userId,
      couponId: flashCouponId
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '10s',
    }
  );
  
  check(response, {
    '쿠폰 발급 시도': (r) => r.status !== 500, // 🛡️ 서버가 죽지 않으면 성공
    '쿠폰 발급 응답시간 8초 미만': (r) => r.timings.duration < 8000,
  });
}

// 🩺 시스템 헬스 체크 함수
function healthCheck() {
  // 💊 피크 부하 상황에서의 시스템 상태 확인
  let response = http.get(`${BASE_URL}/actuator/health`);
  
  check(response, {
    '시스템 헬스 체크 성공': (r) => r.status === 200,
    '헬스 체크 빠른 응답': (r) => r.timings.duration < 1000,
  });
}