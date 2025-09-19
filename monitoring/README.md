# E-commerce 부하 테스트 및 모니터링 가이드

## 📋 개요

이 가이드는 HangHae Plus E-commerce Spring 애플리케이션의 부하 테스트와 모니터링 환경 구성 및 사용법을 설명합니다.

## 🛠 구성 요소

### 부하 테스트 도구
- **k6**: JavaScript 기반 부하 테스트 도구
- 3가지 테스트 시나리오: Load Test, Stress Test, Peak Test

### 모니터링 스택
- **Prometheus**: 메트릭 수집 및 저장
- **Grafana**: 메트릭 시각화 대시보드
- **Spring Boot Actuator**: 애플리케이션 메트릭 노출

## 🚀 시작하기

### 1. 전체 환경 시작

```bash
# 1. 기본 인프라 시작 (MySQL, Redis, Kafka)
docker-compose up -d

# 2. 모니터링 스택 시작
docker-compose --profile monitoring up -d

# 3. Spring Boot 애플리케이션 시작
./gradlew bootRun
```

### 2. 접속 URL

- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator Health**: http://localhost:8080/actuator/health
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 📊 부하 테스트 실행

### Load Test (기본 부하 테스트)
```bash
docker-compose --profile testing run --rm k6 run /scripts/load-test-basic.js
```
- **목적**: 일반적인 부하에서의 성능 측정
- **시나리오**: 점진적 사용자 증가 (10명 → 20명)
- **테스트 비율**: 상품 조회(40%) + 포인트 충전(30%) + 주문(20%) + 쿠폰(10%)

### Stress Test (스트레스 테스트)
```bash
docker-compose --profile testing run --rm k6 run /scripts/stress-test.js
```
- **목적**: 높은 부하에서의 시스템 한계 측정
- **시나리오**: 300명까지 사용자 증가
- **중점**: 분산 락 성능, 재고 관리 동시성

### Peak Test (최대 부하 테스트)
```bash
docker-compose --profile testing run --rm k6 run /scripts/peak-test.js
```
- **목적**: 갑작스러운 트래픽 급증 상황 시뮬레이션
- **시나리오**: 플래시 세일, 한정 쿠폰 발급
- **급증**: 10명 → 200명 (10초 내)

## 📈 모니터링 설정

### Grafana 대시보드 설정

1. **Grafana 접속**: http://localhost:3000 (admin/admin)
2. **데이터소스 확인**: Prometheus가 자동으로 설정됨
3. **대시보드 생성** 또는 기존 템플릿 사용

### 주요 모니터링 메트릭

#### HTTP 요청 관련
```promql
# 초당 요청 수 (RPS)
rate(http_server_requests_seconds_count[1m])

# 평균 응답 시간
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])

# 95% 응답 시간
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[1m]))

# 에러율
rate(http_server_requests_seconds_count{status=~"4..|5.."}[1m]) / rate(http_server_requests_seconds_count[1m])
```

#### JVM 메트릭
```promql
# 힙 메모리 사용률
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}

# GC 횟수
rate(jvm_gc_pause_seconds_count[1m])

# 스레드 수
jvm_threads_live_threads
```

#### 애플리케이션 메트릭
```promql
# 데이터베이스 연결 풀
hikaricp_connections_active
hikaricp_connections_idle

# 캐시 히트율
cache_gets_total{result="hit"} / cache_gets_total
```

## 📋 테스트 시나리오별 주의사항

### Load Test
- **목표**: 정상 운영 환경에서의 성능 벤치마크
- **성공 기준**: 
  - 95% 응답시간 < 500ms
  - 에러율 < 10%
  - 모든 API 정상 동작

### Stress Test  
- **목표**: 시스템 한계점 파악
- **관찰 포인트**:
  - 분산 락 대기 시간
  - 데이터베이스 커넥션 풀 고갈
  - 메모리 사용량 증가

### Peak Test
- **목표**: 갑작스러운 트래픽 급증 대응
- **시나리오**:
  - 플래시 세일 (동일 상품 집중 접근)
  - 한정 쿠폰 발급 (선착순 경쟁)
- **관찰 포인트**:
  - 큐잉 시스템 동작
  - 장애 전파 방지

## 🔧 문제 해결

### 일반적인 문제들

1. **Docker 컨테이너 시작 실패**
   ```bash
   docker-compose down
   docker system prune -f
   docker-compose up -d
   ```

2. **k6 테스트 실패**
   - 애플리케이션이 실행 중인지 확인
   - `host.docker.internal` 연결 확인

3. **Prometheus 메트릭 수집 안됨**
   - Spring Boot 애플리케이션의 `/actuator/prometheus` 엔드포인트 확인
   - Prometheus 설정의 target 주소 확인

4. **Grafana 대시보드 데이터 없음**
   - Prometheus 데이터소스 연결 상태 확인
   - 쿼리 문법 확인

### 로그 확인
```bash
# 애플리케이션 로그
./gradlew bootRun

# 컨테이너 로그
docker-compose logs prometheus
docker-compose logs grafana
docker-compose logs k6
```

## 📊 결과 분석

### 테스트 결과 파일
- **k6 결과**: `./data/k6-results/` 디렉터리
- **HTML 리포트**: `summary.html`
- **JSON 데이터**: `summary.json`

### 주요 분석 지표

1. **Performance**:
   - Average/95%/99% 응답 시간
   - Requests per second (RPS)
   - Throughput

2. **Reliability**:
   - Error rate
   - Success rate
   - Timeout 발생률

3. **Scalability**:
   - 사용자 증가에 따른 성능 변화
   - 리소스 사용량 증가 패턴

## 🎯 성능 목표

### Chapter 4 과제 기준

- **TPS 목표**: API별 적절한 TPS 설정
- **응답시간**: 95% 응답시간 < 2초
- **에러율**: < 5%
- **가용성**: 99.9% 이상

### 시나리오별 기대값

| 테스트 유형 | 사용자 수 | TPS | 응답시간(95%) | 에러율 |
|-------------|-----------|-----|---------------|--------|
| Load Test   | 20명      | 50+ | < 500ms       | < 5%   |
| Stress Test | 200명     | 100+| < 2s          | < 15%  |
| Peak Test   | 200명     | 150+| < 3s          | < 20%  |

## 📝 보고서 작성 가이드

과제 제출을 위한 보고서에는 다음 내용을 포함하세요:

1. **테스트 계획**:
   - 테스트 대상 API
   - 시나리오 설계 근거
   - 성능 목표 설정

2. **실행 결과**:
   - 각 테스트별 상세 결과
   - 그래프 및 차트
   - 병목 지점 분석

3. **문제점 및 개선사항**:
   - 발견된 성능 이슈
   - 해결 방안
   - 코드/설정 개선사항

4. **장애 시나리오**:
   - 가상 장애 상황 정의
   - 대응 방안
   - 모니터링 개선사항