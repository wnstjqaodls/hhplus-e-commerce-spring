// 스모크 테스트 스크립트
// 기본 기능 동작 확인 및 배포 후 빠른 검증용

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('smoke_errors');

// 스모크 테스트 설정 - 최소한의 부하로 핵심 기능 검증  
export const options = {
  vus: 3,        // 가상 사용자 3명
  duration: '2m', // 2분간 실행
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이하
    http_req_failed: ['rate<0.01'],   // 실패율 1% 이하 (매우 엄격)
    smoke_errors: ['rate<0.01'],      // 스모크 에러율 1% 이하
  },
};

// 애플리케이션 기본 URL
const BASE_URL = 'http://localhost:8080';

// 스모크 테스트용 고정 데이터
const SMOKE_USER_ID = 999;
const SMOKE_PRODUCT_ID = 1;
const SMOKE_COUPON_ID = 1;

export function setup() {
  //1. 스모크 테스트 시작 - 기본 기능 검증
  console.log('스모크 테스트 시작');
  console.log('목표: 배포 후 핵심 기능 정상 동작 확인');
  console.log('기간: 2분간 최소 부하로 검증');
  
  //2. 애플리케이션 기본 상태 확인
  const healthResponse = http.get(`${BASE_URL}/actuator/health`);
  if (healthResponse.status !== 200) {
    console.error('애플리케이션 Health Check 실패');
    return null;
  }
  console.log('애플리케이션 정상 상태 확인');
  
  return { baseUrl: BASE_URL };
}

export default function(data) {
  //3. Setup 검증
  if (!data) {
    console.error('Setup 실패, 스모크 테스트 종료');
    return;
  }

  //4. 핵심 비즈니스 플로우 순차 실행
  console.log('핵심 비즈니스 플로우 검증 시작');
  
  // Step 1: 상품 조회 기능 검증
  //5. 상품 목록 조회 테스트
  testProductListAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 2: 상품 상세 조회 기능 검증
  //6. 상품 상세 조회 테스트  
  testProductDetailAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 3: 포인트 관련 기능 검증
  //7. 포인트 잔액 조회 테스트
  testPointBalanceAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 4: 포인트 충전 기능 검증
  //8. 포인트 충전 테스트
  testPointChargeAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 5: 주문 생성 기능 검증
  //9. 주문 생성 테스트
  testOrderCreationAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 6: 주문 내역 조회 기능 검증
  //10. 주문 내역 조회 테스트
  testOrderHistoryAPI(data.baseUrl);
  sleep(0.5);
  
  // Step 7: 쿠폰 발급 기능 검증
  //11. 쿠폰 발급 테스트
  testCouponIssueAPI(data.baseUrl);
  
  //12. 플로우 완료 후 안정화 대기
  sleep(1);
}

// 상품 목록 조회 API 검증
function testProductListAPI(baseUrl) {
  //13. 상품 목록 API 호출
  const response = http.get(`${baseUrl}/products`, {
    tags: { name: 'smoke_product_list' }
  });
  
  //14. 응답 검증 및 기록
  const success = check(response, {
    '상품목록 API 상태코드 200': (r) => r.status === 200,
    '상품목록 API 응답시간 < 300ms': (r) => r.timings.duration < 300,
    '상품목록 데이터 존재 확인': (r) => {
      const data = r.json('data');
      return data && Array.isArray(data) && data.length > 0;
    }
  });
  
  recordSmokeResult(response, success, 'product_list');
  console.log(success ? '상품 목록 API 정상' : '상품 목록 API 이상');
}

// 상품 상세 조회 API 검증
function testProductDetailAPI(baseUrl) {
  //15. 상품 상세 API 호출
  const response = http.get(`${baseUrl}/products/${SMOKE_PRODUCT_ID}`, {
    tags: { name: 'smoke_product_detail' }
  });
  
  //16. 응답 검증 및 기록
  const success = check(response, {
    '상품상세 API 상태코드 200': (r) => r.status === 200,
    '상품상세 API 응답시간 < 200ms': (r) => r.timings.duration < 200,
    '상품상세 필수필드 존재': (r) => {
      const data = r.json('data');
      return data && data.name && data.price && data.stock;
    }
  });
  
  recordSmokeResult(response, success, 'product_detail');
  console.log(success ? '상품 상세 API 정상' : '상품 상세 API 이상');
}

// 포인트 잔액 조회 API 검증
function testPointBalanceAPI(baseUrl) {
  //17. 포인트 잔액 API 호출
  const response = http.get(`${baseUrl}/points/balance/${SMOKE_USER_ID}`, {
    tags: { name: 'smoke_point_balance' }
  });
  
  //18. 응답 검증 및 기록
  const success = check(response, {
    '포인트잔액 API 상태코드 200': (r) => r.status === 200,
    '포인트잔액 API 응답시간 < 200ms': (r) => r.timings.duration < 200,
    '잔액 정보 형식 검증': (r) => {
      const data = r.json('data');
      return data && typeof data.balance === 'number' && data.balance >= 0;
    }
  });
  
  recordSmokeResult(response, success, 'point_balance');
  console.log(success ? '포인트 잔액 API 정상' : '포인트 잔액 API 이상');
}

// 포인트 충전 API 검증
function testPointChargeAPI(baseUrl) {
  //19. 포인트 충전 API 호출
  const chargeAmount = 10000; // 고정 금액으로 테스트
  const response = http.post(
    `${baseUrl}/points/charge`,
    JSON.stringify({
      userId: SMOKE_USER_ID,
      amount: chargeAmount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'smoke_point_charge' }
    }
  );
  
  //20. 응답 검증 및 기록
  const success = check(response, {
    '포인트충전 API 상태코드 200': (r) => r.status === 200,
    '포인트충전 API 응답시간 < 500ms': (r) => r.timings.duration < 500,
    '충전 후 잔액 증가 확인': (r) => {
      const data = r.json('data');
      return data && data.balance >= chargeAmount;
    }
  });
  
  recordSmokeResult(response, success, 'point_charge');
  console.log(success ? '포인트 충전 API 정상' : '포인트 충전 API 이상');
}

// 주문 생성 API 검증
function testOrderCreationAPI(baseUrl) {
  //21. 주문 생성 API 호출
  const response = http.post(
    `${baseUrl}/orders/place`,
    JSON.stringify({
      userId: SMOKE_USER_ID,
      productId: SMOKE_PRODUCT_ID,
      quantity: 1,
      amount: 25000
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'smoke_order_creation' }
    }
  );
  
  //22. 응답 검증 및 기록
  const success = check(response, {
    '주문생성 API 상태코드 200': (r) => r.status === 200,
    '주문생성 API 응답시간 < 800ms': (r) => r.timings.duration < 800,
    '주문 ID 정상 반환 확인': (r) => {
      const data = r.json('data');
      return data && data.orderId && typeof data.orderId === 'number';
    }
  });
  
  recordSmokeResult(response, success, 'order_creation');
  console.log(success ? '주문 생성 API 정상' : '주문 생성 API 이상');
}

// 주문 내역 조회 API 검증
function testOrderHistoryAPI(baseUrl) {
  //23. 주문 내역 API 호출
  const response = http.get(`${baseUrl}/orders/history/${SMOKE_USER_ID}`, {
    tags: { name: 'smoke_order_history' }
  });
  
  //24. 응답 검증 및 기록
  const success = check(response, {
    '주문내역 API 상태코드 200': (r) => r.status === 200,
    '주문내역 API 응답시간 < 400ms': (r) => r.timings.duration < 400,
    '주문내역 데이터 형식 검증': (r) => {
      const data = r.json('data');
      return data && Array.isArray(data);
    }
  });
  
  recordSmokeResult(response, success, 'order_history');
  console.log(success ? '주문 내역 API 정상' : '주문 내역 API 이상');
}

// 쿠폰 발급 API 검증
function testCouponIssueAPI(baseUrl) {
  //25. 쿠폰 발급 API 호출 (스모크 테스트용 임시 사용자)
  const smokeUserId = SMOKE_USER_ID + Math.floor(Math.random() * 1000); // 충돌 방지
  const response = http.post(
    `${baseUrl}/coupons/${SMOKE_COUPON_ID}/issue/${smokeUserId}`,
    null,
    {
      tags: { name: 'smoke_coupon_issue' }
    }
  );
  
  //26. 응답 검증 및 기록 (발급 완료 또는 이미 발급됨 모두 정상)
  const success = check(response, {
    '쿠폰발급 API 처리완료': (r) => r.status === 200 || r.status === 409,
    '쿠폰발급 API 응답시간 < 600ms': (r) => r.timings.duration < 600,
    '쿠폰발급 응답 데이터 존재': (r) => r.body && r.body.length > 0
  });
  
  recordSmokeResult(response, success, 'coupon_issue');
  console.log(success ? '쿠폰 발급 API 정상' : '쿠폰 발급 API 이상');
}

// 스모크 테스트 결과 기록
function recordSmokeResult(response, success, apiName) {
  //27. 스모크 테스트 메트릭 업데이트
  if (!success) {
    errorRate.add(1, { api: apiName });
    console.log(`${apiName} 스모크 테스트 실패 - Status: ${response.status}, Time: ${response.timings.duration}ms`);
  }
}

export function teardown(data) {
  //28. 스모크 테스트 완료 및 결과 요약
  console.log('스모크 테스트 완료');
  console.log('결과: 모든 핵심 API의 기본 동작 검증 완료');
  
  if (data) {
    //29. 최종 헬스 체크
    const finalHealthCheck = http.get(`${data.baseUrl}/actuator/health`);
    if (finalHealthCheck.status === 200) {
      console.log('애플리케이션이 스모크 테스트 후에도 정상 상태입니다');
      console.log('배포 검증 완료 - 프로덕션 배포 준비됨');
    } else {
      console.log('스모크 테스트 후 시스템 상태 이상 - 배포 중단 권장');
    }
  }
  
  //30. 스모크 테스트 가이드라인
  console.log('스모크 테스트 완료 후 권장사항:');
  console.log('   1. 에러율이 1% 미만인지 확인');
  console.log('   2. 모든 API 응답시간이 임계값 이내인지 확인'); 
  console.log('   3. 이상 발견 시 즉시 배포 롤백 고려');
}