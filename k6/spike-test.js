// 스파이크 테스트 스크립트
// 갑작스러운 트래픽 급증 상황에서의 시스템 안정성 테스트

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('spike_errors');
const successfulRequests = new Counter('spike_successful_requests');

// 스파이크 테스트 설정 - 급격한 부하 증가/감소 패턴
export const options = {
  stages: [
    { duration: '2m', target: 5 },    // 1단계: 평상시 부하 (5명)
    { duration: '30s', target: 100 }, // 2단계: 급격한 스파이크 (100명)
    { duration: '1m', target: 100 },  // 3단계: 스파이크 지속 (100명)
    { duration: '30s', target: 5 },   // 4단계: 급격한 감소 (5명)
    { duration: '1m', target: 5 },    // 5단계: 안정화 (5명)
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이하
    http_req_failed: ['rate<0.2'],     // 실패율 20% 이하
    spike_errors: ['rate<0.25'],       // 스파이크 에러율 25% 이하
  },
};

// 애플리케이션 기본 URL
const BASE_URL = 'http://localhost:8080';

// 테스트 데이터
const TEST_USER_IDS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
const TEST_PRODUCT_IDS = [1, 2, 3];

export function setup() {
  //1. 테스트 시작 전 애플리케이션 상태 확인
  console.log('스파이크 테스트 시작');
  console.log('목표: 급격한 트래픽 변화에 대한 시스템 안정성 검증');
  
  //2. Health check 수행
  const healthResponse = http.get(`${BASE_URL}/actuator/health`);
  if (healthResponse.status !== 200) {
    console.error('애플리케이션이 실행되지 않았습니다');
    return null;
  }
  console.log('애플리케이션 준비 완료');
  
  return { baseUrl: BASE_URL };
}

export default function(data) {
  //3. Setup 데이터 검증
  if (!data) {
    console.error('Setup 실패, 테스트 종료');
    return;
  }

  //4. 랜덤 시나리오 선택 (가중치 적용)
  const scenario = Math.random();
  
  if (scenario < 0.5) {
    //5. 50% - 고부하 주문 시나리오
    testHighLoadOrders(data.baseUrl);
  } else if (scenario < 0.8) {
    //6. 30% - 동시 포인트 충전 시나리오  
    testConcurrentPointCharge(data.baseUrl);
  } else {
    //7. 20% - 쿠폰 발급 대란 시나리오
    testCouponRush(data.baseUrl);
  }
  
  //8. 사용자 행동 간격 시뮬레이션
  sleep(Math.random() * 0.5 + 0.1); // 0.1~0.6초 대기
}

// 고부하 주문 처리 테스트
function testHighLoadOrders(baseUrl) {
  //9. 랜덤 테스트 데이터 생성
  const userId = TEST_USER_IDS[Math.floor(Math.random() * TEST_USER_IDS.length)];
  const productId = TEST_PRODUCT_IDS[Math.floor(Math.random() * TEST_PRODUCT_IDS.length)];
  const quantity = Math.floor(Math.random() * 3) + 1; // 1~3개
  
  //10. 주문 생성 요청
  const orderResponse = http.post(
    `${baseUrl}/orders/place`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity,
      amount: Math.floor(Math.random() * 50000) + 10000 // 10,000~60,000원
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'spike_order_creation' },
      timeout: '10s'
    }
  );

  //11. 응답 검증 및 메트릭 기록
  const orderSuccess = check(orderResponse, {
    '스파이크 주문 생성 처리': (r) => r.status === 200 || r.status === 400,
    '스파이크 주문 응답시간 < 5초': (r) => r.timings.duration < 5000,
    '주문 응답 데이터 존재': (r) => r.body && r.body.length > 0,
  });

  recordMetrics(orderResponse, orderSuccess, 'spike_order');
}

// 동시 포인트 충전 테스트
function testConcurrentPointCharge(baseUrl) {
  //12. 동일한 사용자 ID로 충돌 유발 (분산 락 테스트)
  const userId = Math.floor(Math.random() * 3) + 1; // 1~3번 사용자만 사용
  const amount = Math.floor(Math.random() * 30000) + 10000; // 10,000~40,000원

  //13. 포인트 충전 요청
  const chargeResponse = http.post(
    `${baseUrl}/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'spike_point_charge' },
      timeout: '15s'
    }
  );

  //14. 분산 락 동작 검증
  const chargeSuccess = check(chargeResponse, {
    '스파이크 포인트 충전 처리': (r) => r.status === 200,
    '스파이크 충전 응답시간 < 10초': (r) => r.timings.duration < 10000,
    '충전 후 잔액 정보 반환': (r) => {
      const data = r.json('data');
      return data && typeof data.balance === 'number';
    },
  });

  recordMetrics(chargeResponse, chargeSuccess, 'spike_charge');
}

// 쿠폰 발급 대란 시나리오
function testCouponRush(baseUrl) {
  //15. 한정 쿠폰으로 경쟁 상황 유발
  const userId = Math.floor(Math.random() * 100) + 1; // 1~100번 사용자
  const couponId = 1; // 모든 사용자가 같은 쿠폰 요청

  //16. 쿠폰 발급 요청
  const couponResponse = http.post(
    `${baseUrl}/coupons/${couponId}/issue/${userId}`,
    null,
    {
      tags: { name: 'spike_coupon_issue' },
      timeout: '10s'
    }
  );

  //17. 선착순 발급 로직 검증
  const couponSuccess = check(couponResponse, {
    '스파이크 쿠폰 발급 시도': (r) => r.status === 200 || r.status === 409 || r.status === 400,
    '스파이크 쿠폰 응답시간 < 8초': (r) => r.timings.duration < 8000,
    '쿠폰 발급 응답 형식 검증': (r) => r.body && r.body.length > 0,
  });

  recordMetrics(couponResponse, couponSuccess, 'spike_coupon');
}

// 메트릭 기록 함수
function recordMetrics(response, success, operation) {
  //18. 커스텀 메트릭 업데이트
  if (success) {
    successfulRequests.add(1, { operation: operation });
  } else {
    errorRate.add(1, { operation: operation });
    console.log(`${operation} 실패: Status ${response.status}, Time ${response.timings.duration}ms`);
  }
}

export function teardown(data) {
  //19. 테스트 종료 후 정리 작업
  console.log('스파이크 테스트 완료');
  console.log(`성공한 요청: ${successfulRequests.value}`);
  
  if (data) {
    //20. 최종 시스템 상태 확인
    const finalHealthCheck = http.get(`${data.baseUrl}/actuator/health`);
    if (finalHealthCheck.status === 200) {
      console.log('시스템이 스파이크 테스트 후에도 정상 상태입니다');
    } else {
      console.log('시스템 상태를 확인해주세요');
    }
  }
}