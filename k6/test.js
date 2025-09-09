import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const responseTimeTrend = new Trend('response_time', true);
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');

// 테스트 설정
export const options = {
  scenarios: {
    // 1. 기본 API 부하 테스트
    api_load_test: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '1m', target: 10 }, // 1분 동안 10명까지 증가
        { duration: '2m', target: 10 }, // 2분 동안 10명 유지
        { duration: '1m', target: 20 }, // 1분 동안 20명까지 증가
        { duration: '2m', target: 20 }, // 2분 동안 20명 유지
        { duration: '1m', target: 0 },  // 1분 동안 0명으로 감소
      ],
    },
    
    // 2. 스파이크 테스트 (갑작스러운 부하)
    spike_test: {
      executor: 'ramping-vus',
      startTime: '8m',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 50 }, // 10초 동안 50명으로 급증
        { duration: '30s', target: 50 }, // 30초 동안 50명 유지
        { duration: '10s', target: 0 },  // 10초 동안 0명으로 감소
      ],
    },
    
    // 3. 지속 부하 테스트 (소량 장기간)
    endurance_test: {
      executor: 'constant-vus',
      startTime: '9m',
      vus: 5,
      duration: '5m',
    }
  },
  
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이하
    http_req_failed: ['rate<0.05'],   // 실패율 5% 이하
    errors: ['rate<0.1'],             // 에러율 10% 이하
  },
};

const BASE_URL = 'http://localhost:8080';

// 테스트 데이터
const TEST_USERS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
const TEST_PRODUCTS = [1, 2, 3];

export function setup() {
  console.log('🚀 부하 테스트 시작');
  console.log('📊 목표:');
  console.log('  - API 응답 시간 95% < 500ms');
  console.log('  - 실패율 < 5%');
  console.log('  - 에러율 < 10%');
  
  // 애플리케이션 health check
  const healthCheck = http.get(`${BASE_URL}/actuator/health`);
  if (healthCheck.status !== 200) {
    console.error('애플리케이션이 실행되지 않았습니다');
    return null;
  }
  console.log('애플리케이션 health check 완료');
  
  return { baseUrl: BASE_URL };
}

export default function(data) {
  if (!data) {
    console.error('Setup failed, skipping test');
    return;
  }

  const userId = TEST_USERS[Math.floor(Math.random() * TEST_USERS.length)];
  const productId = TEST_PRODUCTS[Math.floor(Math.random() * TEST_PRODUCTS.length)];
  
  // 시나리오별 가중치 (각 시나리오가 실행될 확률)
  const scenario = Math.random();
  
  if (scenario < 0.4) {
    // 40% - 상품 조회 시나리오
    productBrowsingScenario(data.baseUrl);
  } else if (scenario < 0.7) {
    // 30% - 포인트 관련 시나리오  
    pointScenario(data.baseUrl, userId);
  } else if (scenario < 0.9) {
    // 20% - 주문 시나리오
    orderScenario(data.baseUrl, userId, productId);
  } else {
    // 10% - 쿠폰 발급 시나리오
    couponScenario(data.baseUrl, userId);
  }
  
  sleep(Math.random() * 2 + 1); // 1~3초 랜덤 대기
}

function productBrowsingScenario(baseUrl) {
  console.log('상품 브라우징 시나리오 실행');
  
  // 1. 상품 목록 조회
  const listResponse = http.get(`${baseUrl}/products`, {
    tags: { name: 'get_product_list' }
  });
  
  const listSuccess = check(listResponse, {
    '상품 목록 조회 성공': (r) => r.status === 200,
    '상품 목록 응답 시간 < 300ms': (r) => r.timings.duration < 300,
  });
  
  recordMetrics(listResponse, listSuccess, 'product_list');
  
  if (listSuccess && listResponse.json('data')) {
    sleep(0.5);
    
    // 2. 개별 상품 상세 조회
    const productId = Math.floor(Math.random() * 3) + 1;
    const detailResponse = http.get(`${baseUrl}/products/${productId}`, {
      tags: { name: 'get_product_detail' }
    });
    
    const detailSuccess = check(detailResponse, {
      '상품 상세 조회 성공': (r) => r.status === 200,
      '상품 상세 응답 시간 < 200ms': (r) => r.timings.duration < 200,
    });
    
    recordMetrics(detailResponse, detailSuccess, 'product_detail');
  }
}

function pointScenario(baseUrl, userId) {
  console.log('💰 포인트 시나리오 실행');
  
  // 1. 포인트 잔액 조회
  const balanceResponse = http.get(`${baseUrl}/points/balance/${userId}`, {
    tags: { name: 'get_point_balance' }
  });
  
  const balanceSuccess = check(balanceResponse, {
    '포인트 조회 성공': (r) => r.status === 200,
    '포인트 조회 응답 시간 < 200ms': (r) => r.timings.duration < 200,
  });
  
  recordMetrics(balanceResponse, balanceSuccess, 'point_balance');
  
  sleep(0.3);
  
  // 2. 포인트 충전 (50% 확률)
  if (Math.random() < 0.5) {
    const chargeAmount = Math.floor(Math.random() * 50000) + 10000; // 10,000 ~ 60,000
    
    const chargeResponse = http.post(
      `${baseUrl}/points/charge`,
      JSON.stringify({
        userId: userId,
        amount: chargeAmount
      }),
      {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'charge_point' }
      }
    );
    
    const chargeSuccess = check(chargeResponse, {
      '포인트 충전 성공': (r) => r.status === 200,
      '포인트 충전 응답 시간 < 400ms': (r) => r.timings.duration < 400,
      '충전 후 잔액 증가 확인': (r) => {
        const data = r.json('data');
        return data && data.balance >= chargeAmount;
      }
    });
    
    recordMetrics(chargeResponse, chargeSuccess, 'point_charge');
  }
}

function orderScenario(baseUrl, userId, productId) {
  console.log('🛒 주문 시나리오 실행');
  
  const quantity = Math.floor(Math.random() * 3) + 1; // 1~3개
  const amount = Math.floor(Math.random() * 30000) + 10000; // 10,000 ~ 40,000
  
  // 1. 주문 생성
  const orderResponse = http.post(
    `${baseUrl}/orders/place`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'place_order' }
    }
  );
  
  const orderSuccess = check(orderResponse, {
    '주문 생성 성공': (r) => r.status === 200,
    '주문 생성 응답 시간 < 500ms': (r) => r.timings.duration < 500,
    '주문 ID 반환 확인': (r) => {
      const data = r.json('data');
      return data && data.orderId;
    }
  });
  
  recordMetrics(orderResponse, orderSuccess, 'place_order');
  
  if (orderSuccess) {
    const orderId = orderResponse.json('data.orderId');
    sleep(0.5);
    
    // 2. 주문 결제 (70% 확률)
    if (Math.random() < 0.7) {
      const paymentResponse = http.post(
        `${baseUrl}/orders/${orderId}/pay`,
        JSON.stringify({
          userId: userId,
          usePoint: Math.random() < 0.6 // 60% 확률로 포인트 사용
        }),
        {
          headers: { 'Content-Type': 'application/json' },
          tags: { name: 'pay_order' }
        }
      );
      
      const paymentSuccess = check(paymentResponse, {
        '주문 결제 처리': (r) => r.status === 200 || r.status === 400, // 400도 정상 (잔액부족 등)
        '결제 응답 시간 < 600ms': (r) => r.timings.duration < 600,
      });
      
      recordMetrics(paymentResponse, paymentSuccess, 'pay_order');
    }
    
    sleep(0.3);
    
    // 3. 주문 내역 조회
    const historyResponse = http.get(`${baseUrl}/orders/history/${userId}`, {
      tags: { name: 'get_order_history' }
    });
    
    const historySuccess = check(historyResponse, {
      '주문 내역 조회 성공': (r) => r.status === 200,
      '주문 내역 응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });
    
    recordMetrics(historyResponse, historySuccess, 'order_history');
  }
}

function couponScenario(baseUrl, userId) {
  console.log('🎫 쿠폰 발급 시나리오 실행');
  
  const couponId = Math.floor(Math.random() * 3) + 1; // 1~3
  
  const couponResponse = http.post(
    `${baseUrl}/coupons/${couponId}/issue/${userId}`,
    null,
    {
      tags: { name: 'issue_coupon' }
    }
  );
  
  const couponSuccess = check(couponResponse, {
    '쿠폰 발급 요청 성공': (r) => r.status === 200,
    '쿠폰 발급 응답 시간 < 300ms': (r) => r.timings.duration < 300,
    '요청 ID 반환 확인': (r) => {
      const data = r.json('data');
      return data && data.requestId;
    }
  });
  
  recordMetrics(couponResponse, couponSuccess, 'issue_coupon');
}

function recordMetrics(response, success, operation) {
  responseTimeTrend.add(response.timings.duration, { operation: operation });
  
  if (success) {
    successfulRequests.add(1, { operation: operation });
  } else {
    failedRequests.add(1, { operation: operation });
    errorRate.add(1);
    console.log(`${operation} 실패: Status ${response.status}`);
  }
}

export function teardown(data) {
  console.log('부하 테스트 완료');
  console.log('결과 요약:');
  console.log(`  - 성공한 요청: ${successfulRequests.value}`);
  console.log(`  - 실패한 요청: ${failedRequests.value}`);
  
  if (data) {
    // 최종 health check
    const finalHealthCheck = http.get(`${data.baseUrl}/actuator/health`);
    if (finalHealthCheck.status === 200) {
      console.log('애플리케이션이 여전히 정상 상태입니다');
    } else {
      console.log('애플리케이션 상태를 확인해주세요');
    }
  }
}
