# CLAUDE.md

This file contains essential information about the Spring Boot e-commerce application to help future Claude instances be immediately productive.

## Project Overview

**HangHae Plus E-commerce Spring Application**
- A Spring Boot 3.4.1 e-commerce application implementing Clean Architecture
- Core domains: Product, Order, Point, Coupon
- Features: Product management, order processing, point charging/usage, coupon issuance
- Advanced features: Distributed locking, Redis caching, concurrent transaction handling

## Architecture

**Clean Architecture (Hexagonal Architecture)**
- Based on Robert C. Martin's Clean Architecture principles
- Package structure organized by domain (product, order, point, coupon)
- Each domain follows: `domain` → `application` → `adapter` layers
- Ports and Adapters pattern for external integrations

### Domain Structure
```
src/main/java/ecommerce/
├── product/
│   ├── domain/Product.java              # Core business entity
│   ├── application/
│   │   ├── port/in/                     # Use case interfaces
│   │   ├── port/out/                    # Repository interfaces  
│   │   └── service/                     # Business logic implementation
│   └── adapter/
│       ├── in/web/                      # REST controllers
│       └── out/persistence/             # JPA entities & repositories
├── order/                               # Order domain (similar structure)
├── point/                               # Point domain (similar structure)
├── coupon/                              # Coupon domain (similar structure)
└── config/                              # Cross-cutting concerns
```

## Technology Stack

**Core Framework:**
- Spring Boot 3.4.1
- Java 17
- Gradle (Kotlin DSL)

**Database & Caching:**
- MySQL 8.0 (primary database)
- Redis 7.4.4 (caching and distributed locks)
- Spring Data JPA with Hibernate

**Testing:**
- JUnit 5
- TestContainers (MySQL, Redis)
- Spring Boot Test
- Mockito for unit tests

**Infrastructure:**
- Docker Compose for local development
- Redisson for distributed locking
- SpringDoc OpenAPI for API documentation
- Logback with structured logging

## Quick Start

### Prerequisites
- Java 17
- Docker & Docker Compose

### Running the Application

1. **Start Infrastructure:**
```bash
docker-compose up -d
```

2. **Run Application:**
```bash
./gradlew bootRun
```

3. **Run Tests:**
```bash
# All tests
./gradlew test

# Specific test category
./gradlew test --tests "*Test"
./gradlew test --tests "*IntegrationTest"
```

4. **Build Application:**
```bash
./gradlew build
```

### Database Setup
- MySQL runs on `localhost:3306`
- Database: `hhplus`
- Username: `application` / Password: `application`
- JPA DDL auto-update enabled in local/test profiles

### Redis Setup
- Redis runs on `localhost:6379`
- Used for distributed locking and caching
- Redisson client configuration in `RedissonConfiguration.java`

## Configuration

### Profiles
- **local/test**: Development settings (DDL auto-update, verbose logging)
- **default**: Production-ready settings

### Key Configuration Files
- `application.yml` - Main configuration with profile-specific settings
- `application-test.yml` - Test-specific configuration
- `docker-compose.yml` - Infrastructure setup

### Environment Variables
- `REDIS_HOST` (default: localhost)
- `REDIS_PORT` (default: 6379)

## Key Features & Implementation

### 1. Distributed Locking
- **Annotation**: `@DistributedLock(key = "user:#{userId}")`
- **Implementation**: Redisson-based AOP
- **Use Cases**: Point charging, stock reduction, payment processing
- **Configuration**: `DistributedLockAop.java`, `RedissonConfiguration.java`

### 2. Domain-Driven Design
- **Product Domain**: Stock management, validation
- **Order Domain**: Order creation, lifecycle management
- **Point Domain**: Charging, usage with balance validation
- **Coupon Domain**: First-come-first-served issuance

### 3. Concurrency Control
- Distributed locks for critical operations
- Transaction isolation for data consistency
- Optimistic locking for stock updates

### 4. Testing Strategy
- **Unit Tests**: Domain logic with mocks
- **Integration Tests**: Full stack with TestContainers
- **Concurrency Tests**: Multi-threaded scenarios

## Common Development Tasks

### Adding New Features
1. Start with domain entity in `{domain}/domain/`
2. Define use case interface in `application/port/in/`
3. Implement service in `application/service/`
4. Create web adapter in `adapter/in/web/`
5. Implement persistence adapter in `adapter/out/persistence/`

### Running Specific Tests
```bash
# Domain unit tests
./gradlew test --tests "ecommerce.product.domain.*"

# Service tests  
./gradlew test --tests "ecommerce.*.application.service.*"

# Controller tests
./gradlew test --tests "ecommerce.*.adapter.in.web.*"

# Integration tests
./gradlew test --tests "ecommerce.integration.*"
```

### Database Operations
```bash
# Reset database (careful in production!)
docker-compose down
docker-compose up -d mysql

# View logs
docker-compose logs mysql
docker-compose logs redis
```

### Performance Testing
```bash
# Concurrency tests with distributed locks
./gradlew test --tests "*DistributedLockIntegrationTest"
```

## Project Structure Notes

### Clean Architecture Benefits
- **Maintainability**: Clear separation of concerns
- **Testability**: Easy to mock dependencies
- **Flexibility**: Can swap infrastructure without affecting business logic
- **Modularity**: Domains can be extracted as microservices

### Trade-offs
- **Complexity**: More files and interfaces than layered architecture
- **Learning Curve**: Requires understanding of ports/adapters pattern
- **Overhead**: May be overkill for simple CRUD operations

## Important Files to Know

### Configuration
- `/src/main/java/ecommerce/JpaConfig.java` - JPA configuration
- `/src/main/java/ecommerce/config/DistributedLock.java` - Lock annotation
- `/src/main/java/ecommerce/config/RedissonConfiguration.java` - Redis setup

### Domain Models
- `/src/main/java/ecommerce/product/domain/Product.java`
- `/src/main/java/ecommerce/order/domain/Order.java`  
- `/src/main/java/ecommerce/point/domain/Point.java`

### Key Tests
- `/src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java`
- Tests demonstrate concurrent scenarios and distributed locking

### Documentation
- `/docs/requirements/functional-requirements.md` - Business requirements
- `/docs/task-goals-evaluation-criteria.md` - Project objectives
- `/pull_request_template.md` - PR checklist

## API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (when running)
- **OpenAPI Spec**: Generated automatically via SpringDoc

## Logging

- **Format**: Structured JSON logging via Logback
- **Levels**: DEBUG for ecommerce package, configurable per environment
- **Distributed Lock Logging**: Detailed lock acquisition/release logs

## Common Troubleshooting

### Database Connection Issues
- Ensure MySQL container is running: `docker-compose ps`
- Check connection settings in `application.yml`

### Redis Connection Issues  
- Verify Redis container: `docker-compose logs redis`
- Check Redis configuration in `RedissonConfiguration.java`

### Test Failures
- Ensure TestContainers can start containers
- Check Docker daemon is running
- Verify port availability (3306, 6379)

### Distributed Lock Issues
- Check Redis connectivity
- Verify lock key generation in logs
- Review timeout configurations in `@DistributedLock`

## Development Workflow

1. **Feature Development**: Start with domain tests, implement business logic
2. **Integration**: Add web and persistence adapters
3. **Testing**: Unit tests → Integration tests → Manual testing
4. **PR Process**: Follow checklist in `pull_request_template.md`

This codebase demonstrates advanced Spring Boot patterns with clean architecture, distributed systems concepts, and comprehensive testing strategies.