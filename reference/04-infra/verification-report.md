# LXC 112 HHPlus 프로젝트 검증 보고서

**검증 일시**: 2026-04-05  
**대상**: LXC 112 (`192.168.20.112`) — HHPlus E-Commerce + TDD 프로젝트

---

## 검증 결과 요약

| # | 항목 | 결과 | 비고 |
|---|------|------|------|
| 1 | 인프라 컨테이너 상태 | ✅ PASS | mysql/redis/zookeeper/kafka 모두 Running (11시간 이상 안정) |
| 2 | E-Commerce API 헬스체크 | ✅ PASS | status:UP, DB/Redis 모두 UP |
| 3 | Swagger UI 접근 | ✅ PASS | HTTP 200 |
| 4-1 | 상품 목록 조회 | ✅ PASS | 기존 상품 정상 조회 |
| 4-2 | 상품 생성 | ✅ PASS | `{"id":2,"productName":"TestProduct","amount":10000,"quantity":100}` |
| 4-3 | 포인트 충전 | ✅ PASS | 50,000원 충전 성공 |
| 4-4 | 주문 생성 | ❌ FAIL | 500 Internal Server Error — DB 데이터 오염 (상세 아래) |
| 4-5 | 주문내역 조회 | ❌ FAIL | 400 Bad Request — 라우팅 오류 (상세 아래) |
| 5-1 | TDD 포인트 조회 | ✅ PASS | `{"id":1,"point":4000,...}` |
| 5-2 | TDD 포인트 충전 | ✅ PASS | 충전 후 14,000원 정상 반영 |
| 5-3 | TDD 포인트 이력 | ✅ PASS | CHARGE/USE 이력 3건 정상 조회 |
| 6 | nginx 메인 페이지 | ✅ PASS | HTTP 200 |
| 7 | nginx 프록시 헬스체크 | ✅ PASS | `/api/ecommerce/actuator/health` 정상 통과 |
| 8 | Kafka 토픽 확인 | ✅ PASS | `__consumer_offsets`, `coupon-issue-request` 토픽 존재 |
| 9 | Java 프로세스 생존 | ✅ PASS | hhplus-tdd (8081), hhplus-ecommerce (8080), Kafka, Zookeeper 모두 가동 중 |
| 10 | 메모리 사용량 | ⚠️ WARN | 8GiB 중 3.1GiB 사용 (정상), 단 E-Commerce 앱이 1.3GiB 점유 |
| 11 | systemd 서비스 활성화 | ✅ PASS | hhplus-ecommerce, hhplus-tdd 모두 enabled |

---

## 실패 항목 상세 분석

### 4-4. 주문 생성 — ❌ FAIL

**에러**: `500 Internal Server Error` / `{"success":false,"error":"서버 오류 발생"}`

**근본 원인**: `users_points` 테이블에 `user_id=1` 레코드가 **2개** 존재  

```
id  amount   last_charged_at              user_id
1   0        2026-04-04 18:16:08          1       ← 초기화 중 생성된 빈 레코드
2   50000    2026-04-05 04:39:12          1       ← 4-3 포인트 충전으로 생성된 레코드
```

`PlaceOrderService.placeOrder()`에서 `loadPointPort.loadPoint(userId=1)` 호출 →  
`PointRepository.findByUserId(1)` 실행 → `Optional<PointJpaEntity>` 반환 예상이지만  
JPA가 2건 조회하여 `NonUniqueResultException` 발생.

**재현 스택 트레이스 (로그 원문)**:
```
org.springframework.dao.IncorrectResultSizeDataAccessException:
  Query did not return a unique result: 2 results were returned
  at ecommerce.order.application.service.PlaceOrderService.placeOrder(PlaceOrderService.java:38)
Caused by: org.hibernate.NonUniqueResultException:
  Query did not return a unique result: 2 results were returned
```

**발생 경위**: 4-3 포인트 충전 API(`POST /points/charge`)가 중복 INSERT 방어 없이  
새 레코드를 생성하는 구조여서, 이미 존재하는 userId=1에 대해 두 번째 레코드를 추가함.

---

### 4-5. 주문내역 조회 — ❌ FAIL

**에러**: `400 Bad Request`

**근본 원인**: 라우팅 설계 불일치  
- 요청: `GET /orders/history?userId=1`  
- 실제 컨트롤러: `@GetMapping("/{orderId}")` — 경로 변수로 orderId(Long) 기대  
- `"history"` 문자열이 `Long` 변환에 실패 → `MethodArgumentTypeMismatchException`

**로그 원문**:
```
Resolved [org.springframework.web.method.annotation.MethodArgumentTypeMismatchException:
  Method parameter 'orderId': Failed to convert value of type 'java.lang.String'
  to required type 'java.lang.Long'; For input string: "history"]
```

**올바른 호출 형식**: `GET /orders/{orderId}` (예: `GET /orders/1`)  
쿼리 파라미터(`?userId=1`) 기반 사용자별 주문목록 조회 기능은 현재 구현되지 않음.

---

## 시스템 리소스 상태

```
메모리 (8GiB 기준):
  - 사용 중: 3.1GiB  
  - 여유: 1.3GiB  
  - buff/cache: 3.6GiB (실질 여유: 4.9GiB)

Docker 컨테이너 메모리:
  - kafka-7:       923.2 MiB
  - mysql:         444.2 MiB
  - zookeeper-7:   157.7 MiB
  - redis:          15.4 MiB

JVM 프로세스 (컨테이너 외부):
  - hhplus-ecommerce: 약 1.3GiB RSS (Xmx 미지정, JVM 기본 할당)
  - hhplus-tdd:       약 218MB RSS (-Xmx512M 제한 없음)
```

⚠️ E-Commerce 앱(`hhplus-e-commerce-spring-20b2908.jar`)에 `-Xmx` 옵션이 없음.  
현재 1.3GiB를 점유하고 있으며, 트래픽 증가 시 OOM 가능성 있음.

---

## 정상 동작 확인 항목

- **Docker Compose 인프라** (mysql, redis, zookeeper, kafka): 11시간 이상 안정 운용
- **E-Commerce 헬스체크** (`/actuator/health`): MySQL + Redis 모두 UP
- **TDD API** (포인트 조회/충전/이력): 전 항목 정상
- **Swagger UI**: 정상 접근
- **nginx 프록시**: `/api/ecommerce/` 경로로 정상 포워딩
- **Kafka 토픽**: `coupon-issue-request` 토픽 정상 생성
- **systemd**: 부팅 자동시작 설정 완료

---

## 발견된 버그 목록

| 번호 | 심각도 | 위치 | 내용 |
|------|--------|------|------|
| BUG-1 | 🔴 HIGH | `PointRepository.findByUserId` | userId 중복 레코드 존재 시 NonUniqueResultException 미처리. UPSERT 또는 findFirstByUserId 전환 필요 |
| BUG-2 | 🟡 MED | `POST /points/charge` | 동일 userId에 대해 중복 INSERT 방어 로직 없음 |
| BUG-3 | 🟡 MED | `GET /orders/history` | 엔드포인트 미존재. 컨트롤러는 `GET /orders/{orderId}` 형식만 지원 |
| BUG-4 | 🟢 LOW | `OrderPersistenceAdapter.saveOrder` | productId를 항상 1L로 하드코딩 (`// productId는 Order 도메인에서 가져올 수 없으므로 임시로 1L 설정`) |
| BUG-5 | 🟢 LOW | systemd hhplus-ecommerce | `-Xmx` 미설정으로 메모리 무제한 할당 가능 |

---

## 수정 없이 정상 사용 가능한 API

```bash
# E-Commerce (8080)
GET  /actuator/health           # 헬스체크
GET  /swagger-ui/index.html     # Swagger UI
GET  /products                  # 상품 목록
POST /products                  # 상품 생성
POST /points/charge             # 포인트 충전 (단, 신규 userId에 한함)
GET  /orders/{orderId}          # 단건 주문 조회 (orderId가 Long인 경우만)

# TDD (8081)
GET   /point/{userId}           # 포인트 조회
PATCH /point/{userId}/charge    # 포인트 충전
GET   /point/{userId}/histories # 포인트 이력
```
