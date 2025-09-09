# 🧭 Navigation Guide - HH+ E-commerce Spring

빠른 탐색과 효율적인 개발을 위한 프로젝트 네비게이션 가이드

---

## 🚀 Quick Access Dashboard

### 👤 Role-Based Navigation

#### 🧑‍💻 **개발자 (Developer)**
```
┌─ 🎯 시작하기
│  ├─ 📋 [Project Index](./PROJECT_INDEX.md) - 전체 프로젝트 개요
│  ├─ 🏗️ [CLAUDE.md](../CLAUDE.md) - 개발 컨텍스트 
│  └─ ⚙️ [Quick Start](../README.md#quick-start) - 환경 설정
│
├─ 🏛️ 아키텍처 이해  
│  ├─ 📐 [Clean Architecture](./references/chap2-1_design/) - 설계 원칙
│  ├─ 🔄 [Sequence Diagrams](./architecture/sequenceDiagram.md) - 플로우
│  └─ 🗃️ [ERD](./architecture/erd.md) - 데이터 모델
│
└─ 💻 구현 가이드
   ├─ 🧪 [TDD Guide](./references/chap1-1_tdd/) - 테스트 방법론  
   ├─ 📊 [Database Guide](./references/chap2-3_server-construction_database/) - DB 설계
   └─ 🔄 [Kafka Integration](./쿠폰_시스템개선_카프카도입.md) - 메시징
```

#### 🏗️ **아키텍트 (Architect)**
```
┌─ 📋 요구사항 분석
│  ├─ 🎯 [Functional Requirements](./requirements/functional-requirements.md) - 기능 요구사항
│  ├─ ⚡ [Non-Functional Requirements](./requirements/non-functional-requirements.md) - 품질 속성
│  └─ 📜 [Policy Definition](./requirements/policy-definition.md) - 비즈니스 규칙
│
├─ 🏗️ 시스템 설계
│  ├─ 🧩 [Architecture Overview](./PROJECT_INDEX.md#-project-architecture-overview) - 구조 개요
│  ├─ 🔄 [Flow Chart](./architecture/flowChart.md) - 비즈니스 플로우  
│  └─ 📊 [Technology Stack](./PROJECT_INDEX.md#-technology-stack-reference) - 기술 스택
│
└─ 📈 확장성 계획
   ├─ 🚦 [Traffic Handling](./references/chap3-1_trafic/) - 대용량 처리
   ├─ 🔧 [Advanced Database](./references/chap2-4_server-construction_advance/) - DB 최적화
   └─ 🏢 [MSA Guide](./references/chap3-4_msa/) - 마이크로서비스
```

#### 🧪 **테스터 (QA)**
```
┌─ 🧪 테스트 전략
│  ├─ 📋 [Test Categories](./PROJECT_INDEX.md#-test-categories) - 테스트 분류
│  ├─ 🎯 [TDD Methodology](./references/chap1-1_tdd/) - 테스트 방법론
│  └─ 🔧 [Test Commands](./PROJECT_INDEX.md#-test-execution-commands) - 실행 명령어
│
├─ 🏃 테스트 실행  
│  ├─ 🔬 [Unit Tests](../src/test/java/) - 단위 테스트
│  ├─ 🔗 [Integration Tests](../src/test/java/ecommerce/integration/) - 통합 테스트
│  └─ 📊 [Performance Tests](./PROJECT_INDEX.md#current-focus-areas) - 성능 테스트
│
└─ 📊 품질 관리
   ├─ ✅ [Completion Status](./PROJECT_INDEX.md#-completion-status) - 진행 상황
   └─ 🔧 [Technical Debt](./PROJECT_INDEX.md#-technical-debt) - 기술 부채
```

#### 🚀 **데브옵스 (DevOps)**
```
┌─ 🐳 인프라 설정
│  ├─ 🐳 [Docker Compose](../docker-compose.yml) - 로컬 환경
│  ├─ ⚙️ [Application Config](../src/main/resources/application.yml) - 앱 설정
│  └─ 🔧 [Build Configuration](../build.gradle.kts) - 빌드 설정
│
├─ 📊 모니터링
│  ├─ 📈 [Actuator Endpoints](http://localhost:8080/actuator) - 헬스체크
│  ├─ 📋 [Swagger UI](http://localhost:8080/swagger-ui.html) - API 문서
│  └─ 🔍 [Logging Guide](./PROJECT_INDEX.md#environment-profiles) - 로그 설정
│
└─ 🚀 배포 가이드
   └─ 🔄 [Fault Tolerance](./references/chap4-1_disability/) - 장애 대응
```

---

## 🏗️ Domain-Driven Navigation

### 🛒 Product Domain Deep Dive
```
📦 Product Domain
├─ 🏛️ Core Components
│  ├─ 🧩 [Product.java](../src/main/java/ecommerce/product/domain/Product.java) - 도메인 엔티티
│  ├─ 🎯 [GetProductUseCase](../src/main/java/ecommerce/product/application/port/in/GetProductUseCase.java) - 조회 유즈케이스
│  └─ 📊 [ProductRankingService](../src/main/java/ecommerce/product/application/service/ProductRankingService.java) - 랭킹 서비스
│
├─ 🌐 Web Layer
│  ├─ 🎮 [GetProductController](../src/main/java/ecommerce/product/adapter/in/web/GetProductController.java) - 상품 조회 API
│  ├─ 📋 [GetProductListController](../src/main/java/ecommerce/product/adapter/in/web/GetProductListController.java) - 목록 조회 API
│  └─ ✨ [CreateProductController](../src/main/java/ecommerce/product/adapter/in/web/CreateProductController.java) - 상품 생성 API
│
├─ 💾 Persistence Layer  
│  ├─ 🗃️ [ProductJpaEntity](../src/main/java/ecommerce/product/adapter/out/persistence/ProductJpaEntity.java) - JPA 엔티티
│  ├─ 🔍 [ProductRepository](../src/main/java/ecommerce/product/adapter/out/persistence/ProductRepository.java) - 저장소 인터페이스
│  └─ 🔗 [ProductPersistenceAdapter](../src/main/java/ecommerce/product/adapter/out/persistence/ProductPersistenceAdapter.java) - 영속성 어댑터
│
└─ 🧪 Testing
   ├─ 🔬 [ProductTest](../src/test/java/ecommerce/product/) - 도메인 테스트
   ├─ 🔗 [ProductPersistenceAdapterTest](../src/test/java/ecommerce/product/adapter/out/persistence/ProductPersistenceAdapterTest.java) - 영속성 테스트
   └─ 📊 [ProductRankingIntegrationTest](../src/test/java/ecommerce/integration/ProductRankingIntegrationTest.java) - 랭킹 통합 테스트
```

### 📋 Order Domain Deep Dive  
```
📦 Order Domain
├─ 🏛️ Core Components
│  ├─ 🧩 [Order.java](../src/main/java/ecommerce/order/domain/Order.java) - 주문 도메인 엔티티
│  ├─ 💳 [Payment.java](../src/main/java/ecommerce/order/domain/payment/Payment.java) - 결제 도메인 엔티티
│  └─ 📊 [PaymentStatus.java](../src/main/java/ecommerce/order/domain/payment/PaymentStatus.java) - 결제 상태
│
├─ 🎯 Use Cases
│  ├─ 📋 [PlaceOrderUseCase](../src/main/java/ecommerce/order/application/port/in/PlaceOrderUseCase.java) - 주문 생성
│  ├─ 💳 [PayOrderUseCase](../src/main/java/ecommerce/order/application/port/in/PayOrderUseCase.java) - 주문 결제  
│  ├─ 🔗 [OrderAndPayUseCase](../src/main/java/ecommerce/order/application/port/in/OrderAndPayUseCase.java) - 주문+결제 통합
│  └─ 📜 [GetOrderHistoryUseCase](../src/main/java/ecommerce/order/application/port/in/GetOrderHistoryUseCase.java) - 주문 내역
│
├─ 🌐 Web Layer
│  ├─ 📋 [PlaceOrderController](../src/main/java/ecommerce/order/adapter/in/web/PlaceOrderController.java) - 주문 생성 API
│  ├─ 💳 [PayOrderController](../src/main/java/ecommerce/order/adapter/in/web/PayOrderController.java) - 결제 API
│  ├─ 🔗 [OrderAndPayController](../src/main/java/ecommerce/order/adapter/in/web/OrderAndPayController.java) - 통합 API
│  └─ 📜 [GetOrderHistoryController](../src/main/java/ecommerce/order/adapter/in/web/GetOrderHistoryController.java) - 내역 API
│
└─ 🧪 Testing
   ├─ 🔬 [OrderTest](../src/test/java/ecommerce/order/domain/OrderTest.java) - 도메인 테스트
   ├─ 🎮 [PlaceOrderControllerTest](../src/test/java/ecommerce/order/adapter/in/web/PlaceOrderControllerTest.java) - 컨트롤러 테스트
   └─ 🔧 [PlaceOrderServiceTest](../src/test/java/ecommerce/order/application/service/PlaceOrderServiceTest.java) - 서비스 테스트
```

### 💰 Point Domain Deep Dive
```
📦 Point Domain  
├─ 🏛️ Core Components
│  └─ 🧩 [Point.java](../src/main/java/ecommerce/point/domain/Point.java) - 포인트 도메인 엔티티
│
├─ 🎯 Use Cases
│  ├─ ⚡ [ChargePointUseCase](../src/main/java/ecommerce/point/application/port/in/ChargePointUseCase.java) - 포인트 충전
│  └─ 💰 [GetPointBalanceUseCase](../src/main/java/ecommerce/point/application/port/in/GetPointBalanceUseCase.java) - 잔액 조회
│
├─ 🔧 Services  
│  ├─ ⚡ [ChargePointService](../src/main/java/ecommerce/point/application/service/ChargePointService.java) - 충전 서비스
│  └─ 💰 [GetPointBalanceService](../src/main/java/ecommerce/point/application/service/GetPointBalanceService.java) - 조회 서비스
│
├─ 🌐 Web Layer
│  └─ 🎮 [ChargePointController](../src/main/java/ecommerce/point/adapter/in/web/ChargePointController.java) - 포인트 API
│
├─ 💾 Persistence Layer
│  ├─ 🗃️ [PointJpaEntity](../src/main/java/ecommerce/point/adapter/out/persistence/PointJpaEntity.java) - JPA 엔티티  
│  ├─ 🔍 [PointRepository](../src/main/java/ecommerce/point/adapter/out/persistence/PointRepository.java) - 저장소
│  └─ 🔗 [PointPersistenceAdapter](../src/main/java/ecommerce/point/adapter/out/persistence/PointPersistenceAdapter.java) - 어댑터
│
└─ 🧪 Testing
   ├─ 🔬 [PointTest](../src/test/java/ecommerce/point/domain/PointTest.java) - 도메인 테스트
   ├─ 🎮 [ChargePointControllerTest](../src/test/java/ecommerce/point/adapter/in/web/ChargePointControllerTest.java) - API 테스트  
   ├─ 🔧 [ChargePointServiceTest](../src/test/java/ecommerce/point/application/service/ChargePointServiceTest.java) - 서비스 테스트
   └─ 🎯 [ChargePointUseCaseTest](../src/test/java/ecommerce/point/application/port/in/ChargePointUseCaseTest.java) - 유즈케이스 테스트
```

### 🎫 Coupon Domain Deep Dive (🔄 Kafka Integration)
```  
📦 Coupon Domain
├─ 🏛️ Core Components
│  └─ 🧩 [Coupon.java](../src/main/java/ecommerce/coupon/domain/Coupon.java) - 쿠폰 도메인 엔티티
│
├─ 🎯 Use Cases
│  └─ 🎫 [IssueCouponUseCase](../src/main/java/ecommerce/coupon/application/port/in/IssueCouponUseCase.java) - 쿠폰 발급
│
├─ 🔧 Services
│  └─ 🎫 [IssueCouponService](../src/main/java/ecommerce/coupon/application/service/IssueCouponService.java) - 발급 서비스
│
├─ 🌐 Web Layer
│  └─ 🎮 [IssueCouponController](../src/main/java/ecommerce/coupon/adapter/in/web/IssueCouponController.java) - 쿠폰 발급 API
│
├─ 💾 Persistence Layer
│  ├─ 🗃️ [CouponJpaEntity](../src/main/java/ecommerce/coupon/adapter/out/persistence/CouponJpaEntity.java) - 쿠폰 JPA 엔티티
│  ├─ 👤 [UserCouponJpaEntity](../src/main/java/ecommerce/coupon/adapter/out/persistence/UserCouponJpaEntity.java) - 사용자 쿠폰 엔티티  
│  ├─ 🔍 [CouponRepository](../src/main/java/ecommerce/coupon/adapter/out/persistence/CouponRepository.java) - 쿠폰 저장소
│  ├─ 👤 [UserCouponRepository](../src/main/java/ecommerce/coupon/adapter/out/persistence/UserCouponRepository.java) - 사용자 쿠폰 저장소
│  └─ 🔗 [CouponPersistenceAdapter](../src/main/java/ecommerce/coupon/adapter/out/persistence/CouponPersistenceAdapter.java) - 어댑터
│
├─ 🔄 Event Processing (Kafka)  
│  ├─ 📨 [CouponIssueEvent](../src/main/java/ecommerce/coupon/application/event/CouponIssueEvent.java) - 발급 이벤트
│  └─ 📋 [Kafka Integration Guide](./쿠폰_시스템개선_카프카도입.md) - 카프카 도입 계획
│
└─ 📖 References
   └─ 🔄 [Kafka Technical Guide](./카프카에%20대해서.md) - 카프카 기술 가이드
```

---

## ⚙️ Configuration Navigation Map

### 🔧 Core Configuration Hub
```
⚙️ Configuration Center
├─ 🏠 Main Config
│  ├─ 🎛️ [application.yml](../src/main/resources/application.yml) - 메인 애플리케이션 설정
│  ├─ 🧪 [application-test.yml](../src/test/resources/application-test.yml) - 테스트 프로파일 설정
│  └─ 🏗️ [build.gradle.kts](../build.gradle.kts) - 빌드 및 의존성 설정
│
├─ 🔌 Infrastructure Setup
│  ├─ 🐳 [docker-compose.yml](../docker-compose.yml) - 로컬 인프라 (MySQL, Redis)  
│  └─ 📦 [Docker Configuration Guide](./PROJECT_INDEX.md#-environment-profiles) - 컨테이너 설정
│
├─ 🏗️ Spring Configuration
│  ├─ 🗃️ [JpaConfig.java](../src/main/java/ecommerce/JpaConfig.java) - JPA 설정
│  ├─ 📖 [SwaggerConfiguration.java](../src/main/java/ecommerce/config/SwaggerConfiguration.java) - API 문서 설정
│  ├─ 💾 [CacheConfiguration.java](../src/main/java/ecommerce/config/CacheConfiguration.java) - 캐시 설정  
│  └─ 🔄 [AopForTransaction.java](../src/main/java/ecommerce/config/AopForTransaction.java) - 트랜잭션 AOP
│
├─ 🔒 Distributed Systems
│  ├─ 🔐 [DistributedLock.java](../src/main/java/ecommerce/config/DistributedLock.java) - 분산 락 어노테이션
│  ├─ 🔧 [DistributedLockAop.java](../src/main/java/ecommerce/config/DistributedLockAop.java) - 분산 락 AOP 구현
│  ├─ 🔴 [RedissonConfiguration.java](../src/main/java/ecommerce/config/RedissonConfiguration.java) - Redis 분산 락 설정
│  └─ 🔍 [CustomSpringELParser.java](../src/main/java/ecommerce/config/CustomSpringELParser.java) - SpEL 파서
│
└─ 🔄 Messaging  
   ├─ ⚡ [KafkaConfig.java](../src/main/java/ecommerce/config/KafkaConfig.java) - Kafka 설정
   └─ 📛 [CacheNames.java](../src/main/java/ecommerce/config/CacheNames.java) - 캐시 명명 규칙
```

---

## 🧪 Testing Navigation Matrix

### 🔬 Test Organization Structure
```
🧪 Testing Hierarchy
├─ 📊 Test Categories
│  ├─ 🔬 Unit Tests - `src/test/java/ecommerce/**/domain/`
│  │  ├─ [PointTest](../src/test/java/ecommerce/point/domain/PointTest.java) - 포인트 도메인 로직
│  │  └─ [OrderTest](../src/test/java/ecommerce/order/domain/OrderTest.java) - 주문 도메인 로직
│  │
│  ├─ 🔧 Service Tests - `src/test/java/ecommerce/**/service/`  
│  │  ├─ [ChargePointServiceTest](../src/test/java/ecommerce/point/application/service/ChargePointServiceTest.java) - 포인트 충전 서비스
│  │  └─ [PlaceOrderServiceTest](../src/test/java/ecommerce/order/application/service/PlaceOrderServiceTest.java) - 주문 생성 서비스
│  │
│  ├─ 🌐 Controller Tests - `src/test/java/ecommerce/**/web/`
│  │  ├─ [ChargePointControllerTest](../src/test/java/ecommerce/point/adapter/in/web/ChargePointControllerTest.java) - 포인트 API
│  │  └─ [PlaceOrderControllerTest](../src/test/java/ecommerce/order/adapter/in/web/PlaceOrderControllerTest.java) - 주문 API
│  │
│  ├─ 🔗 Integration Tests - `src/test/java/ecommerce/integration/`
│  │  ├─ [DistributedLockIntegrationTest](../src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java) - 분산 락 통합
│  │  └─ [ProductRankingIntegrationTest](../src/test/java/ecommerce/integration/ProductRankingIntegrationTest.java) - 상품 랭킹 통합
│  │
│  └─ 💾 Persistence Tests - `src/test/java/ecommerce/**/persistence/`
│     └─ [ProductPersistenceAdapterTest](../src/test/java/ecommerce/product/adapter/out/persistence/ProductPersistenceAdapterTest.java) - 영속성 어댑터
│
├─ 🏃 Test Execution Paths  
│  ├─ 🎯 Domain-Specific Tests
│  │  ├─ `./gradlew test --tests "ecommerce.product.*"` - 상품 도메인 전체
│  │  ├─ `./gradlew test --tests "ecommerce.order.*"` - 주문 도메인 전체  
│  │  ├─ `./gradlew test --tests "ecommerce.point.*"` - 포인트 도메인 전체
│  │  └─ `./gradlew test --tests "ecommerce.coupon.*"` - 쿠폰 도메인 전체
│  │
│  ├─ 📊 Layer-Specific Tests
│  │  ├─ `./gradlew test --tests "**.*Test"` - 단위 테스트만
│  │  ├─ `./gradlew test --tests "**.*ServiceTest"` - 서비스 계층만  
│  │  ├─ `./gradlew test --tests "**.*ControllerTest"` - 컨트롤러 계층만
│  │  └─ `./gradlew test --tests "**.*IntegrationTest"` - 통합 테스트만
│  │
│  └─ 🏗️ Infrastructure Tests
│     ├─ `./gradlew test --tests "*DistributedLock*"` - 분산 락 테스트
│     └─ `./gradlew test --tests "*Persistence*"` - 영속성 테스트
│
└─ 📖 Testing Guides
   ├─ 🎯 [TDD Methodology](./references/chap1-1_tdd/Chapter%201-1%20TDD.md) - TDD 방법론
   └─ 🔧 [Test Configuration Guide](./PROJECT_INDEX.md#test-execution-commands) - 테스트 설정 및 실행
```

---

## 📚 Reference Documentation Network

### 📖 Knowledge Base Hub  
```
📚 Reference Network
├─ 🎓 Development Methodology
│  ├─ 🧪 [TDD Guide](./references/chap1-1_tdd/Chapter%201-1%20TDD.md) - 테스트 주도 개발
│  └─ 🏗️ [Design Patterns](./references/chap2-1_design/Chapter%202-1%20서버구축%20-%20설계.md) - 설계 패턴 가이드
│
├─ 🏗️ Architecture References
│  ├─ 🗃️ [Database Fundamentals](./references/chap2-3_server-construction_database/Chapter%202-3%20서버구축-데이터베이스%20기본.md) - DB 기본 개념
│  ├─ 📊 [Advanced Database](./references/chap2-4_server-construction_advance/Chapter%202-4%20서버구축-데이터베이스%20심화.md) - DB 성능 최적화
│  └─ 🏛️ [Software Architecture](./references/chap2-2_architecture/Chapter%202-2%20서버구축%20-%20소프트웨어%20설계%202352dc3ef51480659cdcf91aebb7775b.md) - 소프트웨어 설계
│
├─ 📈 Scalability & Performance  
│  ├─ 🚦 [Traffic Handling 3-1](./references/chap3-1_trafic/Chapter%203-1%20대용량%20트래픽&데이터%20처리.md) - 대용량 트래픽 처리
│  ├─ 🔧 [Traffic Handling 3-2](./references/chap3-2_trafic/Chapter%203-2%20대용량%20트래픽&데이터%20처리.md) - 트래픽 처리 심화
│  ├─ ⚡ [Traffic Handling 3-3](./references/chap3-3_trafic/Chapter%203-3%20대용량%20트래픽&데이터%20처리.md) - 성능 최적화
│  └─ 🏢 [MSA Guide](./references/chap3-4_msa/Chapter%203-4%20대용량%20트래픽&데이터%20처리.md) - 마이크로서비스 아키텍처
│
├─ 🔄 Messaging & Events
│  ├─ ⚡ [Kafka Integration Plan](./쿠폰_시스템개선_카프카도입.md) - 쿠폰 시스템 Kafka 도입
│  └─ 📋 [Kafka Technical Guide](./카프카에%20대해서.md) - Kafka 기술 상세 가이드
│
└─ 🚨 Operations & Monitoring
   └─ ⚠️ [Fault Tolerance](./references/chap4-1_disability/Chapter%204%20장애대응.md) - 장애 대응 및 복구
```

---

## 🔍 Search & Discovery Tools

### 🔎 Quick Search Patterns
```bash
# 🔍 Code Pattern Search  
find src -name "*.java" -exec grep -l "UseCase" {} \;          # 모든 UseCase 파일 찾기
find src -name "*.java" -exec grep -l "@DistributedLock" {} \; # 분산 락 사용 위치
find src -name "*Test.java"                                   # 모든 테스트 파일

# 📊 Domain Boundary Analysis
find src -path "*/domain/*" -name "*.java"                    # 모든 도메인 엔티티
find src -path "*/application/port/*" -name "*.java"          # 모든 포트 인터페이스  
find src -path "*/adapter/*" -name "*.java"                   # 모든 어댑터 구현

# 🔧 Configuration Discovery
find . -name "*.yml" -o -name "*.yaml" -o -name "*.properties" # 모든 설정 파일
grep -r "spring.kafka" src/                                    # Kafka 관련 설정
grep -r "redis" src/                                           # Redis 관련 구성
```

### 🎯 Smart Navigation Shortcuts
```bash
# 📂 Domain Navigation  
cd src/main/java/ecommerce/product    # 상품 도메인으로 이동
cd src/main/java/ecommerce/order      # 주문 도메인으로 이동
cd src/main/java/ecommerce/point      # 포인트 도메인으로 이동
cd src/main/java/ecommerce/coupon     # 쿠폰 도메인으로 이동

# 🧪 Test Navigation
cd src/test/java/ecommerce/integration # 통합 테스트로 이동
cd src/test/java/ecommerce/point       # 포인트 테스트로 이동

# 📖 Documentation Navigation  
cd docs/requirements                    # 요구사항 문서
cd docs/architecture                    # 아키텍처 문서
cd docs/references                      # 참조 문서
```

---

## 🔗 External Resources & Tools

### 🌐 Development Environment  
| Resource | URL | Purpose | Status |
|----------|-----|---------|--------|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | API 문서 및 테스트 | ✅ Available |
| **Actuator** | `http://localhost:8080/actuator` | 애플리케이션 헬스체크 | ✅ Available |  
| **H2 Console** | `http://localhost:8080/h2-console` | 테스트 DB 콘솔 | 🧪 Test Only |

### 🗃️ Database Access
| Service | Connection | Credentials | Purpose |
|---------|------------|-------------|---------|
| **MySQL** | `localhost:3306` | application/application | 메인 데이터베이스 |
| **Redis** | `localhost:6379` | No password | 캐시 및 분산 락 |

### 🔧 Development Tools
| Tool | Purpose | Configuration |
|------|---------|---------------|  
| **Docker Compose** | 로컬 인프라 관리 | [docker-compose.yml](../docker-compose.yml) |
| **Gradle** | 빌드 및 의존성 관리 | [build.gradle.kts](../build.gradle.kts) |
| **TestContainers** | 통합 테스트 환경 | [Test Configuration](../src/test/resources/) |

---

## 🎯 Workflow-Based Quick Access

### 🏃‍♂️ Common Development Workflows

#### 🆕 **새로운 기능 추가 워크플로우**
```
1️⃣ 요구사항 분석
   📋 [Functional Requirements](./requirements/functional-requirements.md)
   
2️⃣ 도메인 모델링  
   🧩 [Domain Entities](../src/main/java/ecommerce/*/domain/)
   
3️⃣ 유즈케이스 정의
   🎯 [Use Case Interfaces](../src/main/java/ecommerce/*/application/port/in/)
   
4️⃣ 서비스 구현
   🔧 [Application Services](../src/main/java/ecommerce/*/application/service/)
   
5️⃣ 어댑터 구현  
   🌐 [Web Controllers](../src/main/java/ecommerce/*/adapter/in/web/)
   💾 [Persistence Adapters](../src/main/java/ecommerce/*/adapter/out/persistence/)
   
6️⃣ 테스트 작성
   🧪 [Testing Strategy](./PROJECT_INDEX.md#-test-categories)
```

#### 🐛 **버그 수정 워크플로우**
```  
1️⃣ 문제 분석
   🔍 [Logging Configuration](../src/main/resources/application.yml)
   
2️⃣ 테스트 케이스 작성
   🧪 [Test Cases](../src/test/java/ecommerce/)
   
3️⃣ 수정 구현
   🔧 [Domain Logic](../src/main/java/ecommerce/*/domain/)
   
4️⃣ 통합 테스트  
   🔗 [Integration Tests](../src/test/java/ecommerce/integration/)
   
5️⃣ 코드 리뷰
   📋 [PR Template](../pull_request_template.md)
```

#### ⚡ **성능 최적화 워크플로우**
```
1️⃣ 성능 측정
   📊 [Actuator Metrics](http://localhost:8080/actuator)
   
2️⃣ 분산 락 최적화
   🔐 [DistributedLockAop](../src/main/java/ecommerce/config/DistributedLockAop.java)
   
3️⃣ 캐시 전략
   💾 [Cache Configuration](../src/main/java/ecommerce/config/CacheConfiguration.java)
   
4️⃣ 데이터베이스 튜닝
   🗃️ [Database Guide](./references/chap2-4_server-construction_advance/)
   
5️⃣ 성능 테스트
   🧪 [Performance Testing](./PROJECT_INDEX.md#current-focus-areas)
```

---

## 🚨 Emergency Navigation

### 🔥 Critical Issue Response
```
🚨 Critical Issues Quick Access
├─ 🔴 Application Won't Start
│  ├─ ⚙️ [Application Config](../src/main/resources/application.yml) - 설정 확인
│  ├─ 🐳 [Docker Services](../docker-compose.yml) - 인프라 상태 확인  
│  └─ 🔍 Application Logs - 에러 메시지 분석
│
├─ 🔴 Database Connection Issues  
│  ├─ 🐳 `docker-compose ps` - MySQL 컨테이너 상태
│  ├─ 🔍 `docker-compose logs mysql` - MySQL 로그  
│  └─ 🗃️ [Database Configuration](../src/main/resources/application.yml) - 연결 설정
│
├─ 🔴 Redis Connection Issues
│  ├─ 🐳 `docker-compose ps` - Redis 컨테이너 상태  
│  ├─ 🔍 `docker-compose logs redis` - Redis 로그
│  └─ 🔴 [Redis Configuration](../src/main/java/ecommerce/config/RedissonConfiguration.java) - Redis 설정
│
├─ 🔴 Test Failures
│  ├─ 🧪 [Test Status](./PROJECT_INDEX.md#-completion-status) - 현재 테스트 상태
│  ├─ 🔧 `./gradlew test --info` - 상세 테스트 로그
│  └─ 🐳 TestContainers 환경 확인
│
└─ 🔴 Performance Issues
   ├─ 🔐 [Distributed Lock Issues](../src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java) - 락 관련 문제
   ├─ 💾 Cache 상태 확인 - Redis 캐시 상태  
   └─ 📊 [Actuator Health](http://localhost:8080/actuator/health) - 시스템 상태
```

---

**🎯 Navigation Tips:**
- 🔍 **Ctrl+F**로 이 문서 내에서 빠른 검색 활용
- 🔗 **링크 클릭**으로 관련 파일이나 문서로 즉시 이동  
- 📂 **경로 복사**로 IDE에서 바로 파일 탐색
- 🧭 **북마크 활용**으로 자주 사용하는 섹션 저장

---

*📅 Last Updated: 2025-09-08*  
*🔄 Auto-generated Navigation Guide - [PROJECT_INDEX.md](./PROJECT_INDEX.md)와 함께 활용하세요*