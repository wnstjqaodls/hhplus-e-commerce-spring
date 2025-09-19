// 🚀 성능 개선 전후 벤치마크 테스트
// 최적화된 시스템 성능을 측정하고 개선 효과를 검증

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 📊 개선 효과 측정용 커스텀 메트릭
export const cacheHitRate = new Rate('cache_hits');
export const optimizedResponseTime = new Trend('optimized_response_time');
export const lockAcquisitionImprovement = new Trend('lock_acquisition_improvement');
export const throughputImprovement = new Counter('throughput_improvement');

// ⚙️ 벤치마크 테스트 설정
export const options = {
  scenarios: {
    // 🔄 캐시 성능 테스트 (상품 조회)
    cache_performance_test: {
      executor: 'constant-vus',
      vus: 30,
      duration: '3m',
      tags: { scenario: 'cache_test' },
    },

    // 🔐 개선된 분산락 성능 테스트
    improved_lock_test: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 50 },
        { duration: '1m', target: 0 },
      ],
      tags: { scenario: 'improved_lock_test' },
    },
  },
  thresholds: {
    // 🎯 개선 목표 설정
    http_req_duration: ['p(95)<400'],      // 95% 요청이 400ms 이내 (기존: 680ms)
    http_req_failed: ['rate<0.03'],        // 에러율 3% 미만 (기존: 8.5%)
    optimized_response_time: ['p(95)<300'], // 최적화된 응답시간
    cache_hits: ['rate>0.8'],              // 캐시 히트율 80% 이상
  },
};

// 🌐 애플리케이션 기본 URL
const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
  const scenario = __ENV.K6_SCENARIO || __VU % 3;

  switch (scenario) {
    case 0:
    case 'cache_test':
      testCachePerformance();
      break;
    case 1:
    case 'improved_lock_test':
      testImprovedDistributedLock();
      break;
    case 2:
    default:
      testOverallPerformance();
      break;
  }

  sleep(Math.random() * 0.3); // 0-300ms 랜덤 대기
}

// 📦 캐시 성능 테스트
function testCachePerformance() {
  const productId = Math.floor(Math.random() * 10) + 1;
  const startTime = Date.now();

  // 첫 번째 요청 (캐시 미스 예상)
  let firstResponse = http.get(`${BASE_URL}/products/${productId}`, {
    tags: { request_type: 'cache_miss_candidate' },
  });

  const firstDuration = Date.now() - startTime;

  sleep(0.1);

  // 두 번째 요청 (캐시 히트 예상)
  const secondStartTime = Date.now();
  let secondResponse = http.get(`${BASE_URL}/products/${productId}`, {
    tags: { request_type: 'cache_hit_candidate' },
  });

  const secondDuration = Date.now() - secondStartTime;

  // 📊 캐시 효과 분석
  let cacheHit = secondDuration < firstDuration * 0.5; // 50% 이상 빠르면 캐시 히트로 판단

  check(firstResponse, {
    '첫 번째 상품 조회 성공': (r) => r.status === 200,
    '첫 번째 응답시간 양호': (r) => r.timings.duration < 500,
  });

  check(secondResponse, {
    '두 번째 상품 조회 성공': (r) => r.status === 200,
    '캐시 효과로 응답 개선': (r) => secondDuration < firstDuration,
  });

  // 메트릭 기록
  cacheHitRate.add(cacheHit ? 1 : 0);
  optimizedResponseTime.add(secondDuration);

  if (cacheHit) {
    console.log(`🎯 캐시 히트 감지: Product ${productId}, First: ${firstDuration}ms, Second: ${secondDuration}ms`);
  }
}

// 🔐 개선된 분산락 성능 테스트
function testImprovedDistributedLock() {
  // 제한된 사용자 풀로 락 경합 유도 (기존보다 더 많은 사용자)
  const userId = Math.floor(Math.random() * 20) + 1; // 10 → 20으로 확장
  const amount = Math.floor(Math.random() * 3000) + 1000;

  const lockStartTime = Date.now();

  let chargeResponse = http.post(
    `${BASE_URL}/points/charge`,
    JSON.stringify({
      userId: userId,
      amount: amount
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '3s', // 기존 5s → 3s 단축
      tags: { operation: 'improved_lock_charge', user_id: userId },
    }
  );

  const lockDuration = Date.now() - lockStartTime;

  let success = check(chargeResponse, {
    '개선된 포인트 충전 성공': (r) => r.status === 200,
    '개선된 응답시간 (3초 이내)': (r) => r.timings.duration < 3000,
    '분산락 최적화 효과': (r) => lockDuration < 2000, // 2초 이내 목표
  });

  // 개선된 락 성능 메트릭
  lockAcquisitionImprovement.add(lockDuration);

  if (success) {
    throughputImprovement.add(1);

    // 🎯 성능 개선 감지 로깅
    if (lockDuration < 1000) {
      console.log(`⚡ 락 성능 개선 우수: User ${userId}, Duration: ${lockDuration}ms`);
    }
  } else if (chargeResponse.status === 409) {
    console.log(`🔒 개선된 락 경합 처리: User ${userId}, Duration: ${lockDuration}ms`);
  }
}

// 🚀 종합 성능 테스트
function testOverallPerformance() {
  const startTime = Date.now();

  // 1. 상품 목록 조회 (캐시 적용)
  let productListResponse = http.get(`${BASE_URL}/products`, {
    tags: { operation: 'cached_product_list' },
  });

  sleep(0.1);

  // 2. 특정 상품 조회 (캐시 적용)
  let productResponse = http.get(`${BASE_URL}/products/1`, {
    tags: { operation: 'cached_product_detail' },
  });

  sleep(0.1);

  // 3. 포인트 잔액 조회 (캐시 적용)
  const userId = Math.floor(Math.random() * 50) + 1;
  let balanceResponse = http.get(`${BASE_URL}/points/balance/${userId}`, {
    tags: { operation: 'cached_balance' },
  });

  const totalDuration = Date.now() - startTime;

  let overallSuccess = check(null, {
    '상품 목록 조회 성공': () => productListResponse.status === 200,
    '상품 상세 조회 성공': () => productResponse.status === 200,
    '포인트 잔액 조회 성공': () => balanceResponse.status === 200,
    '전체 시나리오 응답시간 우수': () => totalDuration < 1000, // 1초 이내 목표
  });

  if (overallSuccess) {
    console.log(`✅ 종합 성능 우수: Total ${totalDuration}ms, User ${userId}`);
  }
}

// 📊 성능 개선 벤치마크 리포트
export function handleSummary(data) {
  const avgResponseTime = data.metrics.http_req_duration.values.avg.toFixed(2);
  const p95ResponseTime = data.metrics.http_req_duration.values['p(95)'].toFixed(2);
  const errorRate = data.metrics.http_req_failed.values.rate * 100;
  const cacheHitRatio = data.metrics.cache_hits?.values.rate * 100 || 0;
  const avgLockTime = data.metrics.lock_acquisition_improvement?.values.avg || 0;
  const throughputCount = data.metrics.throughput_improvement?.values.count || 0;

  return {
    'results/performance-benchmark-report.html': benchmarkHtmlReport(data, {
      avgResponseTime,
      p95ResponseTime,
      errorRate,
      cacheHitRatio,
      avgLockTime,
      throughputCount
    }),
    'results/performance-benchmark.json': JSON.stringify(data),
  };
}

// 📋 성능 개선 전용 HTML 리포트
function benchmarkHtmlReport(data, metrics) {
  const improvementScore = calculateImprovementScore(metrics);

  return `<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🚀 성능 개선 벤치마크 결과</title>
    <style>
        body {
            font-family: 'Malgun Gothic', Arial, sans-serif;
            margin: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #333;
        }
        .container {
            max-width: 1100px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
            padding: 20px;
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            border-radius: 8px;
        }
        .improvement-score {
            font-size: 3em;
            font-weight: bold;
            margin: 10px 0;
        }
        .metric {
            margin: 15px 0;
            padding: 20px;
            border-left: 4px solid #007cba;
            background: #f8f9fa;
            border-radius: 8px;
            transition: transform 0.2s;
        }
        .metric:hover { transform: translateY(-2px); }
        .success { border-left-color: #28a745; background: #f0fff4; }
        .warning { border-left-color: #ffc107; background: #fffdf0; }
        .excellent { border-left-color: #6f42c1; background: #f8f5ff; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .wide { grid-column: span 2; }
        .highlight { font-weight: bold; color: #0056b3; font-size: 1.2em; }
        h1 { margin: 0; }
        h3 { margin-top: 0; color: #555; }
        .comparison {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin: 10px 0;
        }
        .before { color: #dc3545; }
        .after { color: #28a745; font-weight: bold; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 성능 개선 벤치마크 결과</h1>
            <div class="improvement-score">${improvementScore}점</div>
            <p>성능 개선 종합 점수 (100점 만점)</p>
        </div>

        <div class="grid">
            <div class="metric ${metrics.p95ResponseTime < 400 ? 'success' : metrics.p95ResponseTime < 500 ? 'warning' : 'error'}">
                <h3>📈 응답시간 개선</h3>
                <div class="comparison">
                    <span class="before">개선 전: 680ms</span>
                    <span class="after">개선 후: ${metrics.p95ResponseTime}ms</span>
                </div>
                <p>개선율: <span class="highlight">${((680 - metrics.p95ResponseTime) / 680 * 100).toFixed(1)}%</span></p>
            </div>

            <div class="metric ${metrics.errorRate < 3 ? 'success' : 'error'}">
                <h3>❌ 에러율 개선</h3>
                <div class="comparison">
                    <span class="before">개선 전: 8.5%</span>
                    <span class="after">개선 후: ${metrics.errorRate.toFixed(2)}%</span>
                </div>
                <p>개선율: <span class="highlight">${((8.5 - metrics.errorRate) / 8.5 * 100).toFixed(1)}%</span></p>
            </div>
        </div>

        <div class="grid">
            <div class="metric ${metrics.cacheHitRatio > 80 ? 'excellent' : metrics.cacheHitRatio > 60 ? 'success' : 'warning'}">
                <h3>⚡ 캐시 성능</h3>
                <p>캐시 히트율: <span class="highlight">${metrics.cacheHitRatio.toFixed(1)}%</span></p>
                <p>상품 조회 성능이 <strong>${metrics.cacheHitRatio > 80 ? '크게' : '어느 정도'}</strong> 개선되었습니다.</p>
            </div>

            <div class="metric ${metrics.avgLockTime < 1000 ? 'success' : 'warning'}">
                <h3>🔐 분산락 성능</h3>
                <p>평균 락 획득 시간: <span class="highlight">${metrics.avgLockTime.toFixed(0)}ms</span></p>
                <p>목표 (1초 이내): ${metrics.avgLockTime < 1000 ? '✅ 달성' : '❌ 미달성'}</p>
            </div>
        </div>

        <div class="metric wide excellent">
            <h3>🎯 주요 성능 개선 사항</h3>
            <ul>
                <li><strong>분산락 타임아웃 최적화:</strong> 5초 → 1초 (80% 단축)</li>
                <li><strong>데이터베이스 커넥션 풀:</strong> 3개 → 50개 (1,567% 증가)</li>
                <li><strong>Redis 캐싱 도입:</strong> 상품/포인트 조회 성능 대폭 향상</li>
                <li><strong>트랜잭션 최적화:</strong> 락 보유 시간 3초 → 2초 (33% 단축)</li>
            </ul>
        </div>

        <div class="grid">
            <div class="metric">
                <h3>📊 성능 지표 요약</h3>
                <p>평균 응답시간: <span class="highlight">${metrics.avgResponseTime}ms</span></p>
                <p>성공한 처리량: <span class="highlight">${metrics.throughputCount}건</span></p>
                <p>초당 요청 수: <span class="highlight">${data.metrics.http_reqs.values.rate.toFixed(2)} req/s</span></p>
            </div>

            <div class="metric ${improvementScore > 80 ? 'excellent' : improvementScore > 60 ? 'success' : 'warning'}">
                <h3>🏆 개선 평가</h3>
                <p><strong>성능 등급:</strong>
                ${improvementScore > 90 ? '🥇 Excellent' :
                  improvementScore > 80 ? '🥈 Very Good' :
                  improvementScore > 70 ? '🥉 Good' : '📈 Needs Improvement'}
                </p>
                <p><strong>권장사항:</strong></p>
                <ul>
                    ${improvementScore < 80 ? '<li>추가 캐시 전략 검토</li>' : ''}
                    ${metrics.avgLockTime > 1000 ? '<li>분산락 성능 추가 튜닝</li>' : ''}
                    ${metrics.cacheHitRatio < 70 ? '<li>캐시 TTL 및 전략 재검토</li>' : ''}
                </ul>
            </div>
        </div>
    </div>
</body>
</html>`;
}

function calculateImprovementScore(metrics) {
  let score = 0;

  // 응답시간 개선 (40점 만점)
  const responseImprovement = (680 - metrics.p95ResponseTime) / 680 * 100;
  score += Math.min(40, responseImprovement * 0.8);

  // 에러율 개선 (25점 만점)
  const errorImprovement = (8.5 - metrics.errorRate) / 8.5 * 100;
  score += Math.min(25, errorImprovement * 0.5);

  // 캐시 성능 (20점 만점)
  score += Math.min(20, metrics.cacheHitRatio * 0.25);

  // 분산락 성능 (15점 만점)
  if (metrics.avgLockTime < 1000) score += 15;
  else if (metrics.avgLockTime < 1500) score += 10;
  else if (metrics.avgLockTime < 2000) score += 5;

  return Math.round(Math.max(0, Math.min(100, score)));
}