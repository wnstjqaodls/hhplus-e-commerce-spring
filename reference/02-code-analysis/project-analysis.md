# 항해플러스 프로젝트 종합 분석 리포트

> 작성일: 2026-04-05
> 대상: hhplus-e-commerce-spring, hhplus-tdd-jvm-java

---

## 1. 프로젝트 개요

### 1-1. hhplus-tdd-java (TDD 1주차)

| 항목 | 내용 |
|------|------|
| 위치 | `/d/dev/workspace/hhplus-tdd-jvm-java/hhplus-tdd-java` |
| GitHub | https://github.com/wnstjqaodls/hhplus-tdd-java |
| 스택 | Spring Boot 3.2.0, Java 17, Gradle 8.4 |
| 도메인 | 포인트 충전/사용 시스템 |
| 테스트 | 18개 단위 테스트, 1개 미완성 통합 테스트 |
| 커밋 | ~20개, `dev` 브랜치 |
| PR | 1개 (코치 피드백 있음) |

**핵심**: TDD 방법론 학습 프로젝트. 인메모리 mock DB 사용, 포인트 정책 10개 정의 중 5개 구현.

### 1-2. hhplus-e-commerce-spring (이커머스 전체 과정)

| 항목 | 내용 |
|------|------|
| 위치 | `/d/dev/workspace/hhplus-e-commerce-spring` |
| GitHub | https://github.com/wnstjqaodls/hhplus-e-commerce-spring |
| 스택 | Spring Boot 3.4.1, Java 17, MySQL 8.0, Redis 7.4, Kafka 7.4 |
| 아키텍처 | Clean Architecture (Hexagonal, Port-Adapter) |
| 도메인 | Product, Order, Point, Coupon (4개) |
| 파일 | Java 100개, 테스트 11개 |
| 커밋 | 159개, `step19` 브랜치 |
| PR | 12개 (코치 피드백 3건) |
| 인프라 | Docker Compose (MySQL, Redis, Kafka, Prometheus, Grafana, K6) |

**핵심**: 실무 수준의 이커머스 시스템. 분산 락, 이벤트 드리븐, 부하 테스트까지 포함.

---

## 2. 코치 피드백 종합 (3명)

### 로이 코치 (TDD 1주차)

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| 객체지향 | "잔액 증가 역할과 책임은 누구에게?" — OOP 기본기 부족 | 🔴 |
| 테스트 방법론 | `@WebMvcTest` + MockMvc로 API 테스트해야 함 | 🔴 |
| JUnit5 | `assertAll`, `ParameterizedTest`, `hasSize()` 학습 필요 | 🟡 |
| 예외 처리 | catch 후 re-throw 하지 말고 ExceptionHandler까지 직행 | 🟡 |
| Lombok | 기본 활용 학습 필요 | 🟡 |
| 불필요한 코드 | TestLogger는 IDE가 이미 해주므로 삭제 | 🟢 |
| 주석 | 의미있는 주석이란 무엇인지 고민 | 🟢 |

### 석범 코치 (STEP03 설계)

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| ERD | FK 없어도 관계는 설정해야 함 | 🟡 |
| 플로우 설계 | 포인트 결제만 유효 → 불필요한 플로우 분리 제거 | 🟡 |
| DB 설계 | ALTER MODIFY는 비용이 큰 작업 → 변경 가능성 고려한 데이터 타입 | 🟡 |
| 상태 vs 이력 | 트레이드오프가 아닌 서로 다른 역할 | 🟢 |

### 제이 코치 (STEP15-16 이벤트)

| 영역 | 피드백 | 심각도 |
|------|--------|--------|
| Fault Tolerance | warn 로그만으로 부족 → retry + DLQ 패턴 필요 | 🔴 |
| Idempotency | 외부 API timeout 시 실제 성공 가능성 미고려 | 🔴 |
| Compensating TX | 설계에만 머물러 있음 → 실제 구현 필요 | 🟡 |
| Event Ordering | concurrent handler 간 순서 보장 전략 필요 | 🟡 |
| 칭찬 | EDA 핵심 구현, SAGA 패턴 분석, 로깅 전략 체계적 | ✅ |

---

## 3. 과제 완성도 타임라인

```
STEP03 설계      ████████████ 100% — 요구사항 분석, ERD, 시퀀스 ✅
STEP05 MVP       ████████░░░░  70% — 헥사고날 아키텍처 구축 ✅
STEP06 추가기능  ████░░░░░░░░  35% — "채점 나중으로 부탁" ⚠️
STEP07-08 DB     ████░░░░░░░░  35% — 체크리스트 대부분 미체크 ⚠️
STEP09-10 동시성 ██░░░░░░░░░░  15% — "못함, 못함, 못함.." ❌
STEP11-12 분산락 ██████░░░░░░  50% — 인프라 구축 OK, 테스트 미완 ⚠️
STEP13-14 Redis  ░░░░░░░░░░░░   0% — FAILED 판정 ❌
STEP15-16 이벤트 ██████████░░  85% — 회복! 코치 긍정 피드백 ✅
STEP17-18 Kafka  ████░░░░░░░░  35% — 다시 미완성 ⚠️
STEP19-20 부하   ████████░░░░  70% — 부하테스트 수행 ✅
```

**패턴**: 초반 성실 → 중반 급격 하락 (동시성/DB 부분) → 이벤트에서 회복 → 다시 하락 → 마지막 회복

---

## 4. 잘한 점

1. **Clean Architecture 채택**: 헥사고날 아키텍처를 도입하고 Port-Adapter 패턴 일관 적용
2. **분산 시스템 인프라**: Redisson 분산 락, Kafka 이벤트 드리븐, Redis 랭킹 시스템 구축
3. **AOP 기반 분산 락**: `@DistributedLock` 커스텀 어노테이션 + AOP로 인프라 관심사 분리
4. **SAGA 패턴 분석**: Choreography/Orchestration 비교 분석 + compensating transaction 설계
5. **모니터링 스택**: Prometheus + Grafana + K6 부하 테스트 환경 구성
6. **Docker Compose 구성**: 8개 서비스 오케스트레이션 (MySQL, Redis, Kafka, Zookeeper 등)
7. **정책 문서화**: TDD 프로젝트에서 포인트 정책 정의서 v0.1 작성 (실무 감각)
8. **KPT 회고 습관**: PR마다 Keep-Problem-Try 회고 작성 시도

---

## 5. 부족한 점 & 약점 분석

### 🔴 Critical (반드시 보완)

#### C1. 객체지향 프로그래밍 기본기
- **증거**: 로이 코치 "잔액 증가 역할과 책임은 누구에게?"
- **문제**: Service에 비즈니스 로직 집중, Domain Entity가 빈약 (Anemic Domain Model)
- **현상**: PointService가 잔액 계산, 검증, 이력 기록 모두 담당
- **목표**: Rich Domain Model — Entity가 자신의 상태 변경 책임

#### C2. 동시성 제어 이해 부족
- **증거**: STEP09-10 "못함, 못함, 못함.." / 다른 수강생 코드 참고 고백
- **문제**: 낙관적 락 vs 비관적 락 vs 분산 락의 적용 기준 모호
- **현상**: TDD 프로젝트의 HashMap/ArrayList 동시성 미처리, TODO 코멘트만 존재
- **목표**: 각 락 전략의 트레이드오프 이해 + 실제 구현

#### C3. 테스트 전략 미숙
- **증거**: Mock 위주 단위 테스트만, @WebMvcTest 미사용, 통합 테스트 미완성
- **문제**: 테스트 피라미드 (Unit → Integration → E2E) 전략 부재
- **현상**: 이커머스 100개 파일 중 테스트 11개 (추정 커버리지 40-50%)
- **목표**: 테스트 더블 전략 + 통합 테스트 + TestContainers 활용

#### C4. Fault Tolerance & Resilience
- **증거**: 제이 코치 "warn 로그만으로 부족 → retry + DLQ 필요"
- **문제**: 실패 시나리오 처리 부재, 멱등성 미보장
- **현상**: Kafka consumer 에러 시 로그만 남김, 보상 트랜잭션 설계만 존재
- **목표**: Retry + DLQ + Circuit Breaker + Idempotency Key 실제 구현

### 🟡 Important (개선 필요)

#### I1. 예외 처리 체계
- catch-all Exception → generic error response
- 도메인별 Custom Exception 미정의 (InsufficientStockException 등)
- HTTP 상태코드 미활용 (모든 에러가 500)

#### I2. DB 설계 & JPA 활용
- FK 관계 미설정, 인덱스 미지정
- Hibernate DDL auto 의존 (production 위험)
- Flyway/Liquibase 마이그레이션 도구 미사용

#### I3. API 설계
- Request DTO에 Jakarta Validation 미적용 (@NotNull, @Positive 등)
- ResponseEntity 미사용 (raw domain object 반환)
- 멱등성 키 미구현

#### I4. 코드 완성도
- 30+ TODO 코멘트 방치
- PayOrderService 테스트 부재
- 일부 테스트 메서드명 의미 불명확 (negative_prance, balance_promaxy_postFailure)

### 🟢 Recommended (추후 개선)

- Lombok 활용 미숙
- JUnit5 심화 기능 (assertAll, ParameterizedTest) 미활용
- API Rate Limiting 부재
- Connection Pool 최적화 (현재 max 3)
- Structured Logging (correlation ID 등)

---

## 6. 프로젝트별 아키텍처 요약

### TDD 프로젝트 — 3-Layer

```
PointController (REST)
       ↓
PointService (Business Logic + Validation)
       ↓
UserPointTable / PointHistoryTable (In-Memory Mock)
```

### 이커머스 프로젝트 — Hexagonal Architecture

```
┌─ Adapter (In) ──────────────────────────────┐
│  PlaceOrderController                       │
│  PayOrderController                         │
│  ChargePointController                      │
│  IssueCouponController                      │
└──────────────┬──────────────────────────────┘
               ↓
┌─ Application (Use Cases) ───────────────────┐
│  PlaceOrderUseCase                          │
│  PayOrderUseCase → OrderEventPublisher      │
│  ChargePointUseCase                         │
│  ReduceStockUseCase (@DistributedLock)      │
│  IssueCouponUseCase → CouponIssueProducer   │
└──────────────┬──────────────────────────────┘
               ↓
┌─ Domain ────────────────────────────────────┐
│  Product (stock management)                 │
│  Order + Payment (order lifecycle)          │
│  Point (charge/use with limits)             │
│  Coupon (FCFS issuing)                      │
└──────────────┬──────────────────────────────┘
               ↓
┌─ Adapter (Out) ─────────────────────────────┐
│  JPA Repositories (MySQL)                   │
│  Redis Adapter (ranking ZSET)               │
│  Kafka Producer (coupon-issue, order-event)  │
│  Redisson (distributed lock)                │
└─────────────────────────────────────────────┘
```

---

## 7. 핵심 기술 스택 정리

| 기술 | TDD | E-Commerce | 학습 수준 |
|------|-----|------------|-----------|
| Spring Boot | 3.2.0 | 3.4.1 | 기초 활용 |
| JPA/Hibernate | - | ✅ | 기본 CRUD |
| MySQL | - | ✅ | 기초 |
| Redis | - | ✅ (랭킹, 캐시) | 기본 활용 |
| Kafka | - | ✅ (이벤트) | 기초 개념 |
| Redisson | - | ✅ (분산 락) | AOP 적용 |
| Docker Compose | - | ✅ (8 서비스) | 구성 가능 |
| JUnit5/Mockito | 기초 | 기초 | Mock 위주 |
| TestContainers | 선언만 | 선언만 | 미활용 |
| Prometheus/Grafana | - | ✅ | 구성 수준 |

---

## 8. GitHub PR 전체 목록

### hhplus-tdd-java

| # | 제목 | 상태 | 코치 피드백 |
|---|------|------|------------|
| 1 | [INIT] 포인트 정책, 초기 테스트 코드 PR 요청 | Merged | ✅ 로이 코치 (11개 항목) |

### hhplus-e-commerce-spring

| # | STEP | 제목 | 상태 | 코치 피드백 |
|---|------|------|------|------------|
| 1 | 03 | 이커머스 시스템 설계 | Merged | ✅ 석범 코치 |
| 2 | 05 | 아키텍처 구현 - 필수 기능 | Merged | - |
| 3 | 06 | 아키텍처 구현 - 추가 기능 | Merged | - |
| 4 | 07-08 | DB 설계 + 통합테스트 | Merged | - |
| 5 | - | 브랜치 최신화 | Merged | - |
| 6 | - | 상품 도메인 개발 | Merged | - |
| 7 | 09-10 | 동시성 제어 (DB) | Merged | - |
| 8 | 11-12 | Distributed Lock | Merged | - |
| 9 | 13-14 | Redis 활용 | FAILED | - |
| 10 | 15-16 | Event + TX Diagnosis | Merged | ✅ 제이 코치 |
| 11 | 17-18 | Kafka 도입 | Merged | - |
| 12 | 19-20 | 부하 테스트 | Merged | - |
