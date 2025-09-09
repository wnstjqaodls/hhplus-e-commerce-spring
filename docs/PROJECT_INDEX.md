# 📋 HH+ E-commerce Spring - Project Documentation Index

## 🎯 Project Overview

**HangHae Plus E-commerce Spring Application**  
- Spring Boot 3.4.1 기반 클린 아키텍처 전자상거래 시스템
- 도메인: Product, Order, Point, Coupon
- 핵심 기술: Distributed Locking, Redis Caching, Kafka Messaging

---

## 🗂️ Documentation Structure

### 📖 Core Documentation
| Document | Purpose | Status | Last Updated |
|----------|---------|--------|--------------|
| [README.md](../README.md) | 프로젝트 개요 및 설정 | ✅ Complete | Current |
| [CLAUDE.md](../CLAUDE.md) | Claude AI 컨텍스트 정보 | ✅ Complete | Current |
| [GEMINI.md](../GEMINI.md) | Gemini AI 컨텍스트 정보 | ✅ Complete | Current |

### 📋 Requirements Documentation
| Document | Purpose | Status | Coverage |
|----------|---------|--------|----------|
| [Functional Requirements](./requirements/functional-requirements.md) | 기능 요구사항 정의서 | ✅ Complete | 13개 요구사항 |
| [Non-Functional Requirements](./requirements/non-functional-requirements.md) | 비기능 요구사항 | ✅ Complete | 성능, 보안, 확장성 |
| [Policy Definition](./requirements/policy-definition.md) | 정책 및 비즈니스 규칙 | ✅ Complete | 도메인별 정책 |

### 🏗️ Architecture Documentation
| Document | Purpose | Status | Key Content |
|----------|---------|--------|-------------|
| [Sequence Diagrams](./architecture/sequenceDiagram.md) | 시스템 플로우 다이어그램 | 🔄 In Progress | 주요 유즈케이스 |
| [Flow Chart](./architecture/flowChart.md) | 비즈니스 플로우 | ✅ Complete | 프로세스 흐름도 |
| [ERD](./architecture/erd.md) | 데이터베이스 설계 | ✅ Complete | 엔티티 관계도 |

### 📡 API Documentation
| Document | Purpose | Status | Endpoints |
|----------|---------|--------|-----------|
| [API Specification](./api/api-specification.md) | REST API 명세서 | ✅ Complete | 전체 엔드포인트 |
| Swagger UI | 인터랙티브 API 문서 | ✅ Live | `http://localhost:8080/swagger-ui.html` |

### 🔧 Infrastructure & Configuration
| Document | Purpose | Status | Components |
|----------|---------|--------|------------|
| [docker-compose.yml](../docker-compose.yml) | 로컬 개발 환경 | ✅ Complete | MySQL, Redis |
| [application.yml](../src/main/resources/application.yml) | 스프링 설정 | ✅ Complete | 전체 환경 설정 |

### 📚 Reference Documentation
| Document | Purpose | Status | Focus Area |
|----------|---------|--------|------------|
| [TDD](./references/chap1-1_tdd/Chapter%201-1%20TDD.md) | TDD 방법론 | ✅ Complete | 테스트 전략 |
| [Design Patterns](./references/chap2-1_design/Chapter%202-1%20서버구축%20-%20설계.md) | 설계 패턴 | ✅ Complete | 아키텍처 가이드 |
| [Database Fundamentals](./references/chap2-3_server-construction_database/Chapter%202-3%20서버구축-데이터베이스%20기본.md) | 데이터베이스 기초 | ✅ Complete | DB 설계 |
| [Advanced Database](./references/chap2-4_server-construction_advance/Chapter%202-4%20서버구축-데이터베이스%20심화.md) | 데이터베이스 심화 | ✅ Complete | 성능 최적화 |
| [Traffic Handling](./references/chap3-1_trafic/Chapter%203-1%20대용량%20트래픽&데이터%20처리.md) | 대용량 트래픽 처리 | ✅ Complete | 확장성 전략 |

### 🚀 Implementation Guides
| Document | Purpose | Status | Domain |
|----------|---------|--------|--------|
| [Kafka Integration Guide](./쿠폰_시스템개선_카프카도입.md) | Kafka 도입 계획 | 🔄 In Progress | Coupon System |
| [Kafka Technical Guide](./카프카에%20대해서.md) | Kafka 기술 가이드 | ✅ Complete | Messaging |

---

## 🏛️ Project Architecture Overview

### 📦 Package Structure
```
src/main/java/ecommerce/
├── 🚀 ServerApplication.java           # 메인 애플리케이션
├── ⚙️ config/                          # 설정 파일들
│   ├── DistributedLock.java           # 분산 락 어노테이션
│   ├── DistributedLockAop.java        # 분산 락 AOP
│   ├── RedissonConfiguration.java     # Redis 설정
│   ├── KafkaConfig.java               # Kafka 설정
│   └── CacheConfiguration.java        # 캐시 설정
├── 🛒 product/                         # 상품 도메인
│   ├── domain/Product.java            # 도메인 엔티티
│   ├── application/                   # 비즈니스 로직
│   │   ├── port/in/                   # 유즈케이스 인터페이스
│   │   ├── port/out/                  # 저장소 인터페이스
│   │   └── service/                   # 서비스 구현
│   └── adapter/                       # 어댑터 계층
│       ├── in/web/                    # REST 컨트롤러
│       └── out/persistence/           # JPA 엔티티 & 저장소
├── 📋 order/                           # 주문 도메인
├── 💰 point/                           # 포인트 도메인
└── 🎫 coupon/                          # 쿠폰 도메인
```

### 🧩 Clean Architecture Layers

#### 🟦 Domain Layer (도메인 계층)
- **Purpose**: 핵심 비즈니스 로직과 규칙
- **Components**: Entity, Value Object, Domain Service
- **Files**: `domain/*.java`

#### 🟩 Application Layer (애플리케이션 계층)  
- **Purpose**: 유즈케이스 조합 및 트랜잭션 관리
- **Components**: Use Case, Port Interface, Application Service
- **Files**: `application/port/**/*.java`, `application/service/*.java`

#### 🟨 Adapter Layer (어댑터 계층)
- **Purpose**: 외부 시스템과의 인터페이스
- **Components**: Web Controller, Persistence Adapter, Message Handler
- **Files**: `adapter/in/**/*.java`, `adapter/out/**/*.java`

---

## 🔧 Technology Stack Reference

### 🌐 Core Framework
| Component | Version | Purpose | Configuration |
|-----------|---------|---------|---------------|
| **Spring Boot** | 3.4.1 | 메인 프레임워크 | [application.yml](../src/main/resources/application.yml) |
| **Java** | 17 | 프로그래밍 언어 | [build.gradle.kts](../build.gradle.kts) |
| **Gradle** | Kotlin DSL | 빌드 도구 | [build.gradle.kts](../build.gradle.kts) |

### 💾 Database & Caching
| Component | Version | Purpose | Configuration |
|-----------|---------|---------|---------------|
| **MySQL** | 8.0 | 주 데이터베이스 | [docker-compose.yml](../docker-compose.yml) |
| **Redis** | 7.4.4 | 캐싱 및 분산 락 | [RedissonConfiguration.java](../src/main/java/ecommerce/config/RedissonConfiguration.java) |
| **Redisson** | 3.24.3 | 분산 락 구현 | [DistributedLockAop.java](../src/main/java/ecommerce/config/DistributedLockAop.java) |

### 🔄 Messaging & Events
| Component | Version | Purpose | Configuration |
|-----------|---------|---------|---------------|
| **Apache Kafka** | Spring Boot Starter | 메시지 큐 | [KafkaConfig.java](../src/main/java/ecommerce/config/KafkaConfig.java) |

### 🧪 Testing
| Component | Version | Purpose | Location |
|-----------|---------|---------|----------|
| **JUnit 5** | 5.11.4 | 단위 테스트 | [src/test/java/](../src/test/java/) |
| **TestContainers** | Latest | 통합 테스트 | [DistributedLockIntegrationTest.java](../src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java) |
| **Spring Boot Test** | 3.4.1 | 통합 테스트 | 전체 테스트 |

---

## 🎯 Domain-Specific Navigation

### 🛒 Product Domain
**Core Files:**
- [Domain Entity](../src/main/java/ecommerce/product/domain/Product.java)
- [Get Product List Use Case](../src/main/java/ecommerce/product/application/port/in/GetProductListUseCase.java)
- [Product Ranking Service](../src/main/java/ecommerce/product/application/service/ProductRankingService.java)
- [Product List Controller](../src/main/java/ecommerce/product/adapter/in/web/GetProductListController.java)

**Key Features:**
- ✅ 상품 목록 조회
- ✅ 상품 상세 조회  
- ✅ 상위 판매 상품 랭킹 (최근 3일)
- ✅ 재고 관리 및 감소
- ✅ 상품 생성

### 📋 Order Domain  
**Core Files:**
- [Domain Entity](../src/main/java/ecommerce/order/domain/Order.java)
- [Place Order Use Case](../src/main/java/ecommerce/order/application/port/in/PlaceOrderUseCase.java)
- [Order & Pay Controller](../src/main/java/ecommerce/order/adapter/in/web/OrderAndPayController.java)
- [Payment Domain](../src/main/java/ecommerce/order/domain/payment/Payment.java)

**Key Features:**
- ✅ 주문 생성
- ✅ 주문 및 결제 통합 처리
- ✅ 주문 내역 조회
- ✅ 결제 상태 관리

### 💰 Point Domain
**Core Files:**
- [Domain Entity](../src/main/java/ecommerce/point/domain/Point.java)
- [Charge Point Use Case](../src/main/java/ecommerce/point/application/port/in/ChargePointUseCase.java)
- [Charge Point Service](../src/main/java/ecommerce/point/application/service/ChargePointService.java)
- [Point Controller](../src/main/java/ecommerce/point/adapter/in/web/ChargePointController.java)

**Key Features:**
- ✅ 포인트 충전
- ✅ 포인트 잔액 조회
- ✅ 분산 락을 통한 동시성 제어

### 🎫 Coupon Domain
**Core Files:**
- [Domain Entity](../src/main/java/ecommerce/coupon/domain/Coupon.java)
- [Issue Coupon Use Case](../src/main/java/ecommerce/coupon/application/port/in/IssueCouponUseCase.java)
- [Issue Coupon Controller](../src/main/java/ecommerce/coupon/adapter/in/web/IssueCouponController.java)
- [Coupon Issue Event](../src/main/java/ecommerce/coupon/application/event/CouponIssueEvent.java)

**Key Features:**
- ✅ 선착순 쿠폰 발급
- ✅ 쿠폰 조회
- ✅ 쿠폰 사용
- 🔄 Kafka 기반 비동기 처리 (진행 중)

---

## 🔬 Testing Strategy Navigation

### 🧪 Test Categories
| Test Type | Location | Purpose | Examples |
|-----------|----------|---------|----------|
| **Unit Tests** | `src/test/java/ecommerce/**/domain/` | 도메인 로직 검증 | [PointTest.java](../src/test/java/ecommerce/point/domain/PointTest.java) |
| **Service Tests** | `src/test/java/ecommerce/**/service/` | 비즈니스 로직 검증 | [ChargePointServiceTest.java](../src/test/java/ecommerce/point/application/service/ChargePointServiceTest.java) |
| **Controller Tests** | `src/test/java/ecommerce/**/web/` | API 계층 검증 | [ChargePointControllerTest.java](../src/test/java/ecommerce/point/adapter/in/web/ChargePointControllerTest.java) |
| **Integration Tests** | `src/test/java/ecommerce/integration/` | 전체 시스템 검증 | [DistributedLockIntegrationTest.java](../src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java) |

### 🏃 Test Execution Commands
```bash
# 전체 테스트 실행
./gradlew test

# 도메인별 테스트
./gradlew test --tests "ecommerce.product.*"
./gradlew test --tests "ecommerce.order.*"  
./gradlew test --tests "ecommerce.point.*"
./gradlew test --tests "ecommerce.coupon.*"

# 통합 테스트
./gradlew test --tests "ecommerce.integration.*"

# 특정 테스트 클래스
./gradlew test --tests "DistributedLockIntegrationTest"
```

---

## ⚙️ Configuration Reference

### 🔧 Key Configuration Files
| File | Purpose | Key Settings |
|------|---------|--------------|
| [application.yml](../src/main/resources/application.yml) | 메인 설정 | DB, Redis, Kafka, 프로파일 |
| [docker-compose.yml](../docker-compose.yml) | 로컬 인프라 | MySQL, Redis 컨테이너 |
| [build.gradle.kts](../build.gradle.kts) | 의존성 관리 | Spring Boot, TestContainers |

### 🌍 Environment Profiles
| Profile | Purpose | Active When | Key Features |
|---------|---------|-------------|--------------|
| **local** | 로컬 개발 | 개발 환경 | DDL auto-update, 상세 로깅 |
| **test** | 테스트 | 테스트 실행 | TestContainers, 격리된 DB |
| **default** | 프로덕션 | 운영 환경 | 보안 강화, 성능 최적화 |

---

## 🚀 Quick Start Navigation

### 🏃‍♂️ Development Workflow
1. **Setup**: [Local Development Setup](../README.md#quick-start)
2. **Architecture**: [Clean Architecture Guide](./references/chap2-1_design/)
3. **Database**: [Database Design](./architecture/erd.md)
4. **Testing**: [TDD Guide](./references/chap1-1_tdd/)
5. **Deployment**: [Infrastructure Setup](../docker-compose.yml)

### 🔗 External Resources
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Spring Boot Actuator**: `http://localhost:8080/actuator`
- **MySQL Admin**: `localhost:3306` (application/application)
- **Redis**: `localhost:6379`

---

## 📊 Project Status & Metrics

### ✅ Completion Status
| Domain | Implementation | Testing | Documentation |
|--------|---------------|---------|---------------|
| **Product** | ✅ 100% | ✅ 95% | ✅ Complete |
| **Order** | ✅ 100% | ✅ 90% | ✅ Complete |
| **Point** | ✅ 100% | ✅ 100% | ✅ Complete |
| **Coupon** | 🔄 80% | ✅ 85% | 🔄 In Progress |

### 🎯 Current Focus Areas
1. **Kafka Integration** - 쿠폰 시스템 비동기 처리 완성
2. **Performance Testing** - 대용량 트래픽 처리 검증
3. **Monitoring** - 시스템 메트릭 및 알림 구축
4. **Documentation** - API 문서 및 운영 가이드 보강

### 📈 Technical Debt
- [ ] Kafka Consumer 에러 처리 강화
- [ ] 데이터베이스 인덱스 최적화
- [ ] 통합 테스트 시나리오 확장
- [ ] 로깅 및 모니터링 시스템 구축

---

## 🤝 Contributing Guide

### 📝 Pull Request Process
1. **Template**: [PR Template](../pull_request_template.md)
2. **Code Review**: 코드 품질 및 테스트 커버리지 확인
3. **Testing**: 모든 테스트 통과 필수
4. **Documentation**: 관련 문서 업데이트

### 📖 Documentation Updates
- 새로운 기능 추가 시 해당 도메인 문서 업데이트
- API 변경 시 API 스펙 문서 갱신
- 아키텍처 변경 시 시퀀스 다이어그램 업데이트

---

*📅 Last Updated: 2025-09-08*  
*🔄 Auto-generated by Claude Code - 수정 시 [PROJECT_INDEX.md] 파일을 직접 편집하세요*