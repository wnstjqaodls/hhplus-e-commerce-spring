# 항해플러스 9기 복습 학습계획 — 최종판

> 기준: Notion 챕터 원문 + 코드 분석 + 코치 피드백 (2026-04-05)

## 핵심 원칙

챕터별 목표에서 반복되는 공통 원칙:

1. **테스트 가능한 구조를 먼저 설계한다** — 모든 챕터에서 "테스트 가능한 구조 및 테스트 코드 작성에 집중"이 반복됨. 구현보다 테스트 가능성을 먼저 고려
2. **설계가 명확하면 코드는 수단이 된다** — "설계가 명확하지 않으면 코드를 치는 행위는 불필요한 노동이 된다" (Chapter 2-1 원문)
3. **요구사항 분석 → 아키텍처 → 구현 → 테스트 검증 순서를 지킨다** — "Understand → Plan → Execute → Validate"
4. **견고하지만 유연한 서버를 지향한다** — Chapter 2-1, 2-3, 2-4 목표에서 동일하게 등장
5. **유지보수, 확장 가능한 코드에 대해 끊임없이 고민한다** — Chapter 2-1, 2-3, 2-4 원문 목표 그대로

---

## 우선순위 결정 근거

### 코치 피드백 요약 (3명)

**로이 코치 (TDD 1주차 — CRITICAL 2건)**

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| 객체지향 | "잔액 증가 역할과 책임은 누구에게?" — OOP 기본기 부족 | 🔴 |
| 테스트 방법론 | `@WebMvcTest` + MockMvc로 API 테스트해야 함 | 🔴 |
| JUnit5 | `assertAll`, `ParameterizedTest`, `hasSize()` 학습 필요 | 🟡 |
| 예외 처리 | catch 후 re-throw 하지 말고 ExceptionHandler까지 직행 | 🟡 |
| Lombok | 기본 활용 학습 필요 | 🟡 |
| 불필요한 코드 | TestLogger는 IDE가 이미 해주므로 삭제 | 🟢 |
| 주석 | 의미있는 주석이란 무엇인지 고민 | 🟢 |

**석범 코치 (STEP03 설계)**

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| ERD | FK 없어도 관계는 설정해야 함 | 🟡 |
| 플로우 설계 | 포인트 결제만 유효 → 불필요한 플로우 분리 제거 | 🟡 |
| DB 설계 | ALTER MODIFY는 비용이 큰 작업 → 변경 가능성 고려한 데이터 타입 | 🟡 |
| 상태 vs 이력 | 트레이드오프가 아닌 서로 다른 역할 | 🟢 |

**제이 코치 (STEP15-16 이벤트)**

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| Fault Tolerance | warn 로그만으로 부족 → retry + DLQ 패턴 필요 | 🔴 |
| Idempotency | 외부 API timeout 시 실제 성공 가능성 미고려 | 🔴 |
| Compensating TX | 설계에만 머물러 있음 → 실제 구현 필요 | 🟡 |
| Event Ordering | concurrent handler 간 순서 보장 전략 필요 | 🟡 |
| 칭찬 | EDA 핵심 구현, SAGA 패턴 분석, 로깅 전략 체계적 | ✅ |

### Hall of Fame 기준 (내가 못한 것들)

Chapter 2-1 우수사례 기준으로 내가 달성하지 못한 항목들:

**안은솔 (로이 코치)**
- 올바른 인터페이스의 활용 및 객체에게 적당한 역할과 책임 할당 — 미달 (Anemic Domain Model 문제)
- 경계값 위주의 테스트 범위 설정 및 ParameterizedTest와 Steps를 통한 중복 제거 — 미달 (`chargeAmountLimitBoundary()` 주석 처리 상태)

**박서희 (이석범 코치)**
- 객체지향적인 코드 및 클래스의 책임 검증 탁월 — 미달 (PointService가 잔액 계산, 검증, 이력 기록 모두 담당)
- 작업단위의 명확한 커밋 분리로 작업 내용을 파악하기 쉬움 — 확인 불가

**이세호 (한상진 코치)**
- 동시성 제어 기법에 대한 분석 — 미달 (STEP09-10 "못함, 못함, 못함..")
- 깔끔한 요구사항 구현 및 테스트 작성 — 미달 (테스트 11개, 추정 커버리지 40-50%)

---

## 4주 학습계획

### 1주차: Chapter 1-1 TDD + Chapter 2-1/2-2 아키텍처 설계 — 목표: "TDD를 이해하고 경험한다. 테스트 가능한 구조 및 테스트 코드 작성에 집중한다."

이 두 챕터를 먼저 해야 하는 이유: TDD와 객체지향 기본기가 없으면 이후 DB 락, Redis, Kafka를 배워도 코드 설계 방식이 바뀌지 않는다. 로이 코치의 CRITICAL 피드백 2건이 모두 이 챕터에 해당한다.

#### 학습할 내용 (원문 기반)

**TDD 방법론 핵심**
- TDD는 소프트웨어 설계 방법론이다. 엄격하게 적용하는 것이 목적이 아니라 이해하고 경험하는 것이 목표
- TDD 개발 순서: 빨강(실패하는 테스트 작성) → 초록(빨리 통과하게 만들기, 가짜 구현도 허용) → 리팩토링(중복 제거)
- 테스트 코드에서는 실패 케이스가 우선되어야 한다
- FIRST principle: Fast, Independent, Repeatable, Self-Validating, Timely

**Test Double**
- Mock: 어떤 행동을 할지에 초점 (행동 검증)
- Stub: 어떤 상태를 반영하는지에 초점 (상태 검증)
- 고전파 vs 런던파 트레이드오프 이해

**아키텍처**
- 계층별로 코드를 나누는 이유: 테스트 가능성, 유지보수 용이성
- 의존성 방향을 상위 → 하위 단방향으로 관리
- 헥사고날 아키텍처에서 Port-Adapter 패턴의 역할

#### Hall of Fame 기준 맞추기

- 안은솔 기준: 경계값 위주의 테스트 범위 설정 + ParameterizedTest로 중복 제거
- 안은솔 기준: 객체에게 적당한 역할과 책임 할당 (Service가 아닌 Domain Entity가 상태 변경 책임)
- 박서희 기준: 객체지향적인 코드 및 클래스의 책임 검증

#### 실습 (내 코드 수정)

**1-1. Arrange-Act-Assert 패턴 적용 (Chapter 1-1 미구현)**
- 대상: `PointControllerTest.java`
- 방법: 기존 테스트 메서드를 `// Arrange`, `// Act`, `// Assert` 주석으로 명확히 구분
- 확인: Act와 Assert가 한 줄로 섞인 패턴 찾아서 분리

**1-2. 경계값 테스트 완성 + ParameterizedTest 적용 (Chapter 1-1 미구현)**
- 대상: `PointControllerTest.java`의 주석 처리된 `chargeAmountLimitBoundary()` 테스트
- 방법: 주석 해제 후 `@ParameterizedTest` + `@ValueSource`로 변환
- 추가: 최대 잔고(500만원) 경계값, 최소 충전액 경계값 테스트 케이스 작성

**1-3. Rich Domain Model 적용 (로이 코치 CRITICAL 피드백)**
- 대상: TDD 프로젝트의 `PointService.java`
- 문제: 잔액 계산, 검증, 이력 기록이 Service에 집중됨
- 방법: `Point` 도메인 객체에 `charge(amount)`, `use(amount)` 메서드를 만들어 잔액 변경 책임을 옮김
- 검증: PointService는 Point 객체를 호출만 하고, 잔액 계산 로직이 Service 내에 없는 상태

**1-4. @WebMvcTest 기반 API 테스트 추가 (로이 코치 CRITICAL 피드백)**
- 방법: `@WebMvcTest(PointController.class)` + `MockMvc`로 HTTP 레이어 테스트 작성
- 테스트 대상: `/point/{id}/charge`, `/point/{id}/use`, `/point/{id}` 엔드포인트 각각 1개

#### 성공 기준

- `PointService`에 잔액 계산 로직(+, -, 비교 연산)이 없다
- `PointControllerTest`에 `@ParameterizedTest`가 1개 이상 존재한다
- `@WebMvcTest` 기반 테스트가 통과한다
- 모든 테스트 메서드에 `// Arrange`, `// Act`, `// Assert` 구분이 있다

#### 이번 주 검증 방법

```bash
# TDD 프로젝트 테스트 전체 실행
cd /d/dev/workspace/hhplus-tdd-jvm-java/hhplus-tdd-java
./gradlew test

# 특정 테스트 클래스만 실행
./gradlew test --tests "io.hhplus.tdd.point.PointControllerTest"

# 테스트 결과 HTML 리포트 확인
# build/reports/tests/test/index.html
```

---

### 2주차: Chapter 2-3/2-4 DB 설계 + 동시성 제어 — 목표: "나의 서비스에서 발생하는 동시성 문제의 DB를 활용한 적절한 해결 방법을 선정한다."

이 챕터를 2주차에 하는 이유: Chapter 1-1/2-1 기본기가 있어야 "왜 이 락이 필요한가"를 이해할 수 있다. DB 락 → 분산 락(3주차) 순서가 원문 과제 의존관계이기도 하다.

#### 학습할 내용 (원문 기반)

**트랜잭션과 동시성**
- 트랜잭션 크기 결정 기준: 원자성 보장 최소 단위, 너무 큰 트랜잭션의 문제(DB 성능 저하, Lock 경합)
- Deadlock 방지: 여러 트랜잭션이 동시에 여러 리소스를 점유하는 상황
- DB 커넥션 풀 고갈 원인 분석 (단순히 풀을 늘리는 것은 해결책이 아님)

**Database Lock**
- 낙관적 락 vs 비관적 락 적용 기준
- `@Lock(LockModeType.PESSIMISTIC_WRITE)` JPA 비관적 락
- 격리수준(Isolation Level)이 동시성 문제에 미치는 영향

**DB 인덱스 최적화**
- 조회 성능 저하 구간 식별 방법
- Query Plan 분석
- 인덱스 설계 원칙

#### Hall of Fame 기준 맞추기

- 이세호 기준: 동시성 제어 기법에 대한 분석 — 어떤 상황에서 낙관적/비관적/분산 락을 선택하는지 직접 정리
- 이세호 기준: 깔끔한 요구사항 구현 및 테스트 작성 — DB 락 통합 테스트 작성

#### 실습 (내 코드 수정)

**2-1. DB Lock 기반 동시성 해결 구현 (Chapter 2-4 미구현)**
- 문제: 현재 코드는 DB Lock이 아닌 Redis 분산락만 있음. STEP09에서는 DB 활용 해결 방안을 요구했음
- 방법: `ProductRepository`에 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 추가, 재고 차감 시 비관적 락 적용
- 또는: `@Version` 필드 추가 후 낙관적 락으로 재시도 로직 구현
- 선택 기준 문서화: 재고 차감에는 충돌 빈도가 높으므로 비관적 락이 적절한 이유 주석으로 기록

**2-2. MySQL 통합 테스트 작성 (Chapter 2-3 미구현)**
- 현재: Redis Testcontainers 테스트만 있고 MySQL 통합 테스트 없음
- 방법: `@SpringBootTest` + `@Testcontainers`로 MySQL 컨테이너 사용, `CountDownLatch`로 동시성 시뮬레이션
- 테스트 대상: 재고 차감 동시성 (50개 요청, 재고 10개 → 정확히 10개만 성공)

**2-3. DB 인덱스 설계 (Chapter 2-3 미구현, STEP08)**
- 조회 성능 저하 가능 구간 식별:
  - 인기 상품 조회 (`ORDER BY order_count DESC`, 3일 기간 필터)
  - 쿠폰 유효성 확인 (`WHERE coupon_id = ? AND user_id = ?`)
- 방법: `EXPLAIN` 쿼리로 Full Table Scan 구간 확인 후 복합 인덱스 추가
- 결과: 적용 전/후 `EXPLAIN` 결과 비교 메모로 남기기

**2-4. 예외 처리 체계 정비 (로이 코치 피드백)**
- 방법: `InsufficientStockException`, `InsufficientPointException` 도메인 전용 예외 클래스 정의
- `@RestControllerAdvice` + `@ExceptionHandler`로 catch-rethrow 패턴 제거
- HTTP 상태코드 매핑: 재고 부족 → 409, 입력 오류 → 400, 인증 오류 → 401

#### 성공 기준

- `ProductRepository`에 락 어노테이션이 적용되어 있다
- MySQL Testcontainers 기반 동시성 통합 테스트가 1개 이상 통과한다
- 모든 예외가 `@RestControllerAdvice`를 통해 처리된다 (Service 내 catch-rethrow 없음)
- 인기 상품 조회 쿼리에 인덱스가 적용되었고 EXPLAIN 결과가 확인된다

#### 이번 주 검증 방법

```bash
# 이커머스 전체 테스트 (Docker Compose 실행 후)
cd /d/dev/workspace/hhplus-e-commerce-spring
docker compose up -d mysql redis
./gradlew test

# MySQL 통합 테스트만 실행
./gradlew test --tests "*.integration.*"

# EXPLAIN 실행 (MySQL 직접 접속)
docker exec -it hhplus-mysql mysql -u root -p
EXPLAIN SELECT * FROM order_item GROUP BY product_id ORDER BY COUNT(*) DESC LIMIT 5;
```

---

### 3주차: Chapter 3-1 분산 락 + Chapter 3-2 Redis 활용 — 목표: "분산 환경에서 Lock을 적용할 수 있는 방법을 고민하고, 캐시 레이어 적용을 통해 DB I/O를 줄인다."

이 챕터를 3주차에 하는 이유: 2주차에서 DB 락의 한계(단일 서버 범위)를 이해한 후에야 분산 락이 필요한 이유를 납득할 수 있다. DB Lock → Distributed Lock → Redis Cache 순서가 원문 과제 흐름이다.

#### 학습할 내용 (원문 기반)

**분산 락**
- DB 트랜잭션 범위를 초과하는 상황에서 락이 필요한 이유
- Redis 기반 Redisson 분산락: `tryLock(waitTime, leaseTime, timeUnit)`
- 락 키 설계: `(1) 적절한 키 (2) 적절한 범위` 선정 (STEP11 요구사항)
- AOP 기반 락 분리: 락 로직과 비즈니스 로직을 분리하는 이유

**Redis 자료구조와 활용**
- Redis Sorted Set(ZSET) 기반 랭킹 시스템 설계
- 캐싱 전략 선택 기준: Cache-Aside, Write-Through, Write-Behind
- 자주 변하지 않는 데이터 vs 변하는 데이터 구분 기준

#### Hall of Fame 기준 맞추기

- 이세호 기준: 동시성 제어 기법에 대한 분석 — DB Lock vs 분산 Lock 선택 기준을 직접 정리하여 코드 주석 또는 README에 기록

#### 실습 (내 코드 수정)

**3-1. 분산 락 키 설계 보완 (Chapter 3-1 미구현 — 캐싱 보고서)**
- 현재: `DistributedLockAop.java`에 `REDISSON_LOCK_PREFIX = "MY_LOCK:"` 고정 접두사만 있음
- 문제: 사용자별 락 키 격리 전략이 코드 주석에 없음
- 방법: 포인트 충전은 `POINT:CHARGE:{userId}`, 재고 차감은 `STOCK:REDUCE:{productId}` 등 도메인별 키 패턴 정의, 주석으로 선택 이유 기록

**3-2. Redis 인기 상품 랭킹 구현 (Chapter 3-2 미구현, STEP13)**
- 현재: STEP13 Redis Sorted Set 기반 랭킹 코드 확인 안됨
- 방법: 주문 완료 시 `ZSET`의 `productId` score에 주문 수량만큼 `zincrby` 실행
- 조회: `zrevrangebyscore`로 상위 5개 상품 반환
- 캐시 만료: `EXPIRE` 또는 일 단위 키 분리 (`ranking:2026-04-05`) 방식 중 선택 후 이유 기록

**3-3. 캐싱 전략 적용 (Chapter 3-1 미구현, STEP12)**
- 현재: `@Cacheable`, `@CacheEvict` 코드 없음
- 대상 구간: 상품 목록 조회 (`ProductQueryService.findAll()`) — 자주 변하지 않고 조회가 많은 구간
- 방법: `@Cacheable(value = "products", key = "#pageable")` 적용, `@CacheEvict`는 재고 변경 시 실행
- 캐시 TTL: 5분 설정, 이유 주석으로 기록

**3-4. Redis 기반 선착순 쿠폰 발급 구조 점검 (Chapter 3-2 미구현, STEP14)**
- 현재: `CouponIssueConsumer`가 Kafka 기반으로 구현되어 있으나, STEP14는 Redis 기반 설계가 출발점
- 방법: Redis `SETNX` 또는 Lua script로 중복 발급 방지 로직 추가 (Kafka 이전 단계 보완)
- 목표: 동일 사용자가 같은 쿠폰을 동시에 두 번 요청해도 한 번만 발급되는 것 검증

#### 성공 기준

- 분산 락 키 패턴이 도메인별로 명확히 구분되고 주석으로 설명되어 있다
- Redis ZSET 기반 인기 상품 랭킹 엔드포인트가 동작한다
- 상품 목록 조회 2회 시 2번째 호출은 Redis hit이다 (로그 또는 테스트로 확인)
- 쿠폰 중복 발급 방지 테스트가 통과한다

#### 이번 주 검증 방법

```bash
# Docker Compose로 Redis 포함 실행
cd /d/dev/workspace/hhplus-e-commerce-spring
docker compose up -d mysql redis

# Redis 직접 확인 (랭킹 ZSET)
docker exec -it hhplus-redis redis-cli
ZREVRANGE ranking:2026-04-05 0 4 WITHSCORES

# 캐시 히트 확인
# application.log에서 "Cache hit" 또는 "Cache miss" 로그 확인
# 또는 Redis CLI에서 KEYS products* 확인

# 분산 락 통합 테스트
./gradlew test --tests "*.DistributedLockIntegrationTest"
```

---

### 4주차: Chapter 3-3/3-4 이벤트 + Kafka + Chapter 4 장애 대응 — 목표: "이벤트를 활용해 관심사와 트랜잭션을 분리하고, Fault Tolerance를 구현한다."

이 챕터를 4주차에 하는 이유: 분산 락과 캐시로 단일 서비스 내 문제를 해결한 후에야, 서비스 간 트랜잭션 분리(이벤트)와 Kafka 기반 비동기 처리가 왜 필요한지 이해된다. 제이 코치의 CRITICAL 피드백 2건이 이 챕터에 해당한다.

#### 학습할 내용 (원문 기반)

**이벤트 기반 설계**
- 핵심 비즈니스 로직은 동기, 부가 기능은 비동기 처리: "유저는 주문 즉시 성공 여부 확인 가능해야 하므로, 외부 데이터 플랫폼 전송은 비동기가 적합"
- `@TransactionalEventListener` 기반 ApplicationEvent 흐름
- Transactional Outbox Pattern: 이벤트 유실 대응
- Orchestrator vs Choreography 패턴 트레이드오프

**Kafka**
- Kafka가 대용량 처리에 사용되는 이유: 분산 메세징 시스템, 고가용성과 유연함
- Partition과 처리량 관계: N개 파티션 = N개 병렬 처리 가능
- Retention 정책: 일반적으로 7~14일, 고빈도 이벤트는 1일
- Consumer 오프셋 커밋 순서와 순서 보장 전략
- Redis vs Kafka 선택 기준 (원문: 둘 다 사용 가능, 선착순 쿠폰은 Kafka가 안정성 측면에서 유리)

**장애 대응**
- Fault Tolerance: retry + DLQ 패턴
- Idempotency: 외부 API timeout 시 실제 성공 가능성 처리 방법
- 부하 테스트 지표 분석 및 병목 탐색

#### Hall of Fame 기준 맞추기

- 제이 코치가 칭찬한 것을 유지: EDA 핵심 구현, SAGA 패턴 분석, 로깅 전략 체계적
- 제이 코치가 지적한 것 보완: retry + DLQ, Idempotency Key 실제 구현

#### 실습 (내 코드 수정)

**4-1. Kafka Consumer에 retry + DLQ 패턴 추가 (제이 코치 CRITICAL 피드백)**
- 현재: `CouponIssueConsumer.java`의 실패 처리가 `log.error()`만 있음
- 방법:
  - `@RetryableTopic(attempts = 3)` 어노테이션 추가 (Spring Kafka 제공)
  - DLQ 토픽 설정: `coupon-issue-request.DLT`
  - DLQ 소비자 클래스 추가: 실패한 메시지 영구 저장 또는 알림
- 검증: 의도적 예외 발생 시 3회 재시도 후 DLQ로 이동하는 것 확인

**4-2. Idempotency Key 구현 (제이 코치 CRITICAL 피드백)**
- 문제: 외부 API timeout 시 실제 성공했는데 재시도하면 중복 처리 발생
- 방법: `order_id`를 멱등성 키로 활용, `processed_events` 테이블에 처리 여부 저장
- 조회: Consumer 실행 전 해당 키가 이미 처리됐는지 확인, 중복이면 skip

**4-3. ApplicationEvent 레이어 명확히 분리 (Chapter 3-3 미구현)**
- 현재: ApplicationEvent → Kafka Consumer 사이 레이어가 명확하지 않음
- 방법: `@TransactionalEventListener(phase = AFTER_COMMIT)`으로 주문 완료 이벤트 발행, Kafka Producer는 이 이벤트를 수신해서 메시지 발행
- 이유: 트랜잭션 커밋 후에만 이벤트가 발행되어 이벤트 유실 방지

**4-4. Kafka Producer 코드 확인 및 보완 (Chapter 3-4 미구현)**
- 현재: `CouponIssueConsumer`는 있으나 Producer 코드 확인 필요
- 방법: `coupon-issue-request` 토픽에 메시지를 발행하는 `CouponIssueProducer` 클래스 존재 여부 확인 후 미존재 시 작성
- Kafka 설정: `docker-compose.yml`의 Kafka 설정 확인, 파티션 수 및 Consumer Group 주석으로 기록

**4-5. 보상 트랜잭션 최소 구현 (제이 코치 피드백)**
- 현재: SAGA 패턴 분석 문서는 있으나 실제 구현 없음
- 방법: 결제 실패 시 재고 복구(`Product.restoreStock()`) 메서드 추가, 주문 취소 이벤트 발행
- 범위 제한: Orchestration SAGA 전체가 아니라, PayOrderUseCase 실패 시 재고 복구 1개 경로만 구현

#### 성공 기준

- 쿠폰 발급 Consumer가 3회 실패 후 DLQ 토픽으로 메시지를 이동시킨다
- 동일 `order_id`로 두 번 요청 시 두 번째 요청이 처리되지 않는다 (멱등성 보장)
- 주문 트랜잭션 커밋 후에만 이벤트가 발행된다 (`@TransactionalEventListener AFTER_COMMIT`)
- 결제 실패 시 재고가 원복된다

#### 이번 주 검증 방법

```bash
# 전체 인프라 실행
cd /d/dev/workspace/hhplus-e-commerce-spring
docker compose up -d

# DLQ 확인 (Kafka Consumer 실패 시)
docker exec -it hhplus-kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic coupon-issue-request.DLT \
  --from-beginning

# Kafka 토픽 목록 확인
docker exec -it hhplus-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 --list

# 멱등성 테스트 (동일 order_id로 두 번 HTTP 요청)
curl -X POST http://localhost:8080/orders/{orderId}/pay
curl -X POST http://localhost:8080/orders/{orderId}/pay  # 두 번째는 처리 안 됨

# 통합 테스트 전체 실행
./gradlew test
```

---

## VM 학습 환경 사용법

### 실습 흐름

1. `plink -ssh root@192.168.20.50 -pw "dnjsvudwnd@PR" -batch` 로 AI VM 접속
2. 또는 로컬에서 직접 `/d/dev/workspace/hhplus-e-commerce-spring` 경로에서 작업
3. Docker Compose로 MySQL, Redis, Kafka 실행 후 `./gradlew test`

### 코드 수정 → 테스트 사이클

```bash
# 1. 인프라 실행
cd /d/dev/workspace/hhplus-e-commerce-spring
docker compose up -d mysql redis kafka zookeeper

# 2. 인프라 헬스 확인
docker compose ps

# 3. 코드 수정 (IntelliJ 또는 편집기)

# 4. 테스트 실행
./gradlew test

# 5. 특정 테스트만 실행
./gradlew test --tests "패키지.클래스명"

# 6. 서버 실행 후 수동 테스트
./gradlew bootRun
curl -X POST http://localhost:8080/point/1/charge -H "Content-Type: application/json" -d '{"amount": 10000}'

# 7. TDD 프로젝트
cd /d/dev/workspace/hhplus-tdd-jvm-java/hhplus-tdd-java
./gradlew test
```

---

## 자료 추천 (챕터별)

챕터 노트 원문에 명시된 참고 자료:

**Chapter 1-1 TDD**
- 원문에 참고 자료 URL 명시 없음. 원문 코드 예시(Point 클래스 TDD 스텝별 예시)가 주 학습 자료

**Chapter 2-1 서버구축 설계**
- 원문에 참고 자료 URL 명시 없음

**Chapter 2-2 소프트웨어 설계**
- 원문에 참고 자료 URL 명시 없음. 레이어드/헥사고날/클린 아키텍처 비교는 원문 챕터 내 설명이 주 자료

**Chapter 2-3/2-4 DB**
- 원문에 참고 자료 URL 명시 없음

**Chapter 3-1 ~ 3-4 대용량 처리**
- 원문에 참고 자료 URL 명시 없음

**Chapter 4 장애 대응**
- 원문에 참고 자료 URL 명시 없음

> 원문 챕터 노트에 외부 참고 자료 링크가 명시된 항목이 없다. 각 챕터의 학습 근거는 Notion 원문 본문, 과제 명세, 원문 QnA로 한정된다.
