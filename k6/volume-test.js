// 볼륨 테스트 스크립트  
// 대용량 데이터 처리 능력과 장시간 안정성 검증

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('volume_errors');
const responseTimeTrend = new Trend('volume_response_time');
const totalOperations = new Counter('volume_total_operations');

// 볼륨 테스트 설정 - 지속적이고 안정적인 부하
export const options = {
  stages: [
    { duration: '5m', target: 20 },   // 1단계: 점진적 증가 (20명)
    { duration: '15m', target: 50 },  // 2단계: 중간 부하 지속 (50명)  
    { duration: '20m', target: 80 },  // 3단계: 높은 부하 지속 (80명)
    { duration: '15m', target: 50 },  // 4단계: 부하 감소 (50명)
    { duration: '5m', target: 0 },    // 5단계: 테스트 종료 (0명)
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95%의 요청이 1초 이하
    http_req_failed: ['rate<0.05'],    // 실패율 5% 이하
    volume_errors: ['rate<0.1'],       // 볼륨 에러율 10% 이하
  },
};

// 애플리케이션 기본 URL
const BASE_URL = 'http://localhost:8080';

// 대용량 테스트 데이터
const LARGE_USER_POOL = Array.from({length: 1000}, (_, i) => i + 1); // 1000명의 사용자
const PRODUCT_POOL = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]; // 10개 상품

export function setup() {
  //1. 볼륨 테스트 시작 알림
  console.log('볼륨 테스트 시작 - 대용량 데이터 처리 검증');
  console.log('목표: 장시간 안정적인 대용량 트랜잭션 처리 능력 검증');
  console.log('예상 소요 시간: 60분');
  
  //2. 사전 애플리케이션 상태 확인
  const healthResponse = http.get(`${BASE_URL}/actuator/health`);
  if (healthResponse.status !== 200) {
    console.error('애플리케이션이 실행되지 않았습니다');
    return null;
  }
  console.log('애플리케이션 준비 완료');
  
  return { 
    baseUrl: BASE_URL,
    startTime: Date.now()
  };
}

export default function(data) {
  //3. Setup 검증 및 데이터 준비
  if (!data) {
    console.error('Setup 실패, 테스트 종료');
    return;
  }

  //4. 현재 실행 시간 기반 시나리오 분배
  const elapsedMinutes = (Date.now() - data.startTime) / (1000 * 60);
  const scenario = Math.random();
  
  if (elapsedMinutes < 25) {
    //5. 초기 25분: 데이터 생성 중심 (60%)
    if (scenario < 0.6) {
      testBulkDataCreation(data.baseUrl);
    } else {
      testDataRetrieval(data.baseUrl);
    }
  } else {
    //6. 후반 35분: 조회 중심 (70%)
    if (scenario < 0.7) {
      testDataRetrieval(data.baseUrl);
    } else {
      testBulkDataCreation(data.baseUrl);
    }
  }
  
  //7. 볼륨 테스트용 짧은 대기시간
  sleep(Math.random() * 1 + 0.5); // 0.5~1.5초 대기
}

// 대용량 데이터 생성 테스트
function testBulkDataCreation(baseUrl) {
  //8. 대용량 사용자 풀에서 랜덤 선택
  const userId = LARGE_USER_POOL[Math.floor(Math.random() * LARGE_USER_POOL.length)];
  
  // 첫 번째 시나리오: 포인트 대량 충전
  //9. 포인트 충전 (다양한 금액대)
  const chargeAmount = Math.floor(Math.random() * 100000) + 10000; // 10,000~110,000원
  const chargeResponse = http.post(
    `${baseUrl}/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: chargeAmount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'volume_point_charge' }
    }
  );

  //10. 포인트 충전 결과 검증
  const chargeSuccess = check(chargeResponse, {
    '볼륨 포인트 충전 성공': (r) => r.status === 200,
    '볼륨 충전 응답시간 < 800ms': (r) => r.timings.duration < 800,
    '충전 후 잔액 정보 확인': (r) => {
      const data = r.json('data');
      return data && data.balance >= chargeAmount;
    }
  });

  recordVolumeMetrics(chargeResponse, chargeSuccess, 'bulk_charge');
  
  sleep(0.3);
  
  // 두 번째 시나리오: 연속 주문 생성
  //11. 여러 상품에 대한 주문 생성
  const productId = PRODUCT_POOL[Math.floor(Math.random() * PRODUCT_POOL.length)];
  const quantity = Math.floor(Math.random() * 5) + 1; // 1~5개
  const orderAmount = Math.floor(Math.random() * 80000) + 20000; // 20,000~100,000원

  const orderResponse = http.post(
    `${baseUrl}/orders/place`,
    JSON.stringify({
      userId: userId,
      productId: productId,
      quantity: quantity,
      amount: orderAmount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'volume_order_creation' }
    }
  );

  //12. 주문 생성 결과 검증
  const orderSuccess = check(orderResponse, {
    '볼륨 주문 생성 성공': (r) => r.status === 200,
    '볼륨 주문 응답시간 < 1000ms': (r) => r.timings.duration < 1000,
    '주문 ID 정상 반환': (r) => {
      const data = r.json('data');
      return data && data.orderId;
    }
  });

  recordVolumeMetrics(orderResponse, orderSuccess, 'bulk_order');
}

// 대용량 데이터 조회 테스트  
function testDataRetrieval(baseUrl) {
  //13. 다양한 사용자의 데이터 조회
  const userId = LARGE_USER_POOL[Math.floor(Math.random() * LARGE_USER_POOL.length)];
  
  // 첫 번째 조회: 포인트 잔액
  //14. 포인트 잔액 조회
  const balanceResponse = http.get(
    `${baseUrl}/points/balance/${userId}`,
    {
      tags: { name: 'volume_balance_check' }
    }
  );

  const balanceSuccess = check(balanceResponse, {
    '볼륨 잔액 조회 성공': (r) => r.status === 200,
    '볼륨 잔액 조회 응답시간 < 300ms': (r) => r.timings.duration < 300,
    '잔액 정보 반환 확인': (r) => {
      const data = r.json('data');
      return data && typeof data.balance === 'number';
    }
  });

  recordVolumeMetrics(balanceResponse, balanceSuccess, 'bulk_balance');

  sleep(0.2);
  
  // 두 번째 조회: 주문 내역
  //15. 사용자 주문 내역 조회
  const historyResponse = http.get(
    `${baseUrl}/orders/history/${userId}`,
    {
      tags: { name: 'volume_order_history' }
    }
  );

  const historySuccess = check(historyResponse, {
    '볼륨 주문내역 조회 성공': (r) => r.status === 200,
    '볼륨 주문내역 응답시간 < 500ms': (r) => r.timings.duration < 500,
    '주문내역 데이터 존재': (r) => r.body && r.body.length > 0
  });

  recordVolumeMetrics(historyResponse, historySuccess, 'bulk_history');

  sleep(0.2);
  
  // 세 번째 조회: 상품 정보
  //16. 랜덤 상품 상세 정보 조회
  const productId = PRODUCT_POOL[Math.floor(Math.random() * PRODUCT_POOL.length)];
  const productResponse = http.get(
    `${baseUrl}/products/${productId}`,
    {
      tags: { name: 'volume_product_detail' }
    }
  );

  const productSuccess = check(productResponse, {
    '볼륨 상품조회 성공': (r) => r.status === 200,
    '볼륨 상품조회 응답시간 < 200ms': (r) => r.timings.duration < 200,
    '상품 정보 완전성': (r) => {
      const data = r.json('data');
      return data && data.name && data.price;
    }
  });

  recordVolumeMetrics(productResponse, productSuccess, 'bulk_product');
}

// 볼륨 테스트 메트릭 기록
function recordVolumeMetrics(response, success, operation) {
  //17. 응답시간 추적
  responseTimeTrend.add(response.timings.duration, { operation: operation });
  totalOperations.add(1, { operation: operation });
  
  //18. 에러율 추적
  if (success) {
    console.log(`${operation} 성공: ${response.timings.duration}ms`);
  } else {
    errorRate.add(1, { operation: operation });
    console.log(`${operation} 실패: Status ${response.status}, Time ${response.timings.duration}ms`);
  }
}

export function teardown(data) {
  //19. 볼륨 테스트 완료 통계
  console.log('볼륨 테스트 완료 (60분 지속 테스트)');
  console.log(`총 실행된 오퍼레이션: ${totalOperations.value}`);
  console.log(`테스트 지속 시간: ${Math.round((Date.now() - data.startTime) / (1000 * 60))}분`);
  
  if (data) {
    //20. 최종 시스템 상태 및 성능 확인
    const finalHealthCheck = http.get(`${data.baseUrl}/actuator/health`);
    if (finalHealthCheck.status === 200) {
      console.log('시스템이 60분 볼륨 테스트 후에도 안정적입니다');
    } else {
      console.log('장시간 테스트 후 시스템 상태를 점검해주세요');
    }
    
    // 추가 메모리 사용량 등 모니터링 로그 출력 권장
    console.log('권장사항: 애플리케이션 메모리, DB 연결 상태 등을 별도로 모니터링해주세요');
  }
}