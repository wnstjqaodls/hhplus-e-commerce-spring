// k6 학습용 기본 스크립트
// 단계별로 복잡성을 늘려가며 k6의 핵심 개념을 익혀보세요

import http from 'k6/http';
import { check, sleep } from 'k6';

// Step 1: 가장 기본적인 설정
export const options = {
  // 가상 사용자(Virtual Users) 1명으로 10번 반복
  vus: 1,
  iterations: 10,
};

const BASE_URL = 'http://localhost:8080';

export default function() {
  //1. 테스트 실행 시작 알림
  console.log('테스트 실행 중...');
  
  //2. HTTP GET 요청 보내기
  const response = http.get(`${BASE_URL}/actuator/health`);
  
  //3. 응답 검증하기
  const result = check(response, {
    '상태 코드가 200인가?': (r) => r.status === 200,
    '응답 시간이 500ms 이하인가?': (r) => r.timings.duration < 500,
    '응답에 "UP"이 포함되어 있는가?': (r) => r.body.includes('UP'),
  });
  
  //4. 결과 출력
  console.log(`응답 시간: ${response.timings.duration}ms`);
  console.log(`상태 코드: ${response.status}`);
  
  //5. 1초 대기 (실제 사용자 행동 시뮬레이션)
  sleep(1);
}