# HHPlus E-Commerce — 학습 레퍼런스

> 항해플러스 백엔드 9기 복습 자료 모음  
> 수집일: 2026-04-05

---

## 폴더 구조

```
reference/
├── README.md                          ← 이 파일 (목차)
│
├── 01-course-notes/                   ← 강의 원문 & 챕터 노트
│   ├── notion-raw-content.md          ← Notion 발제자료 원문 전체 (2,760줄)
│   └── chapter-notes.md              ← 챕터별 정리 노트 + 내 코드 연결
│
├── 02-code-analysis/                  ← 내 코드 분석
│   └── project-analysis.md           ← 아키텍처 분석 + 코치 3명 피드백 전체
│
├── 03-study-plan/                     ← 학습 계획
│   └── final-study-plan.md           ← 4주 커리큘럼 (챕터 원문 목표 기반)
│
└── 04-infra/                          ← 인프라 & 운영
    ├── vm-deployment-plan.md          ← LXC 112 배포 계획
    └── verification-report.md        ← API 전체 검증 결과
```

---

## 빠른 참조

### 지금 바로 볼 파일
| 목적 | 파일 |
|------|------|
| 오늘 뭐 공부할지 모르겠다 | `03-study-plan/final-study-plan.md` |
| 코치가 뭐라고 했는지 보고 싶다 | `02-code-analysis/project-analysis.md` → "코치 피드백 종합" 섹션 |
| 챕터 내용이 기억 안 난다 | `01-course-notes/chapter-notes.md` |
| Notion 원문 그대로 보고 싶다 | `01-course-notes/notion-raw-content.md` |

### VM 학습 환경
| 서비스 | 주소 |
|--------|------|
| API 테스트 UI | http://192.168.20.112 |
| E-Commerce API | http://192.168.20.112:8080 |
| Swagger UI | http://192.168.20.112:8080/swagger-ui/index.html |
| TDD API | http://192.168.20.112:8081 |
| Grafana | http://192.168.20.112:3000 |

### 코드 수정 → VM 반영 사이클
```bash
# 1. 로컬에서 코드 수정
# 2. 빌드
./gradlew build -x test --no-daemon

# 3. JAR 전송
pscp -pw "dnjsvudwnd@PR" build/libs/hhplus-e-commerce-spring-*.jar root@192.168.20.2:/tmp/new.jar
plink -ssh root@192.168.20.2 -pw "dnjsvudwnd@PR" -batch "pct push 112 /tmp/new.jar /opt/hhplus/hhplus-ecommerce.jar"

# 4. VM에서 재시작
plink -ssh root@192.168.20.2 -pw "dnjsvudwnd@PR" -batch "pct exec 112 -- bash /tmp/restart-app.sh"

# 5. 확인
curl http://192.168.20.112:8080/actuator/health
```

---

## 챕터별 핵심 요약

| 챕터 | 핵심 한 줄 | 내 코드 상태 |
|------|-----------|-------------|
| **1-1 TDD** | 실패 테스트 먼저, @WebMvcTest + MockMvc로 API 테스트 | ❌ @Mock 사용, MockMvc 미동작 |
| **2-1 설계** | "설계가 명확하지 않으면 코드를 치는 행위는 불필요한 노동" | ⚠️ ERD에 관계 미설정 |
| **2-2 소프트웨어 설계** | 객체에게 적당한 역할과 책임 할당 (Rich Domain Model) | ❌ Anemic Domain Model 잔재 |
| **2-3 DB 기본** | 인덱스는 조회 쿼리 기준으로, 데이터 타입은 변경 가능성 고려 | ❌ 인덱스 미설정 |
| **2-4 DB 심화** | 낙관적 락(충돌 적음) vs 비관적 락(충돌 많음) 선택 기준 이해 | ⚠️ 분산 락만 적용, DB 락 이해 부족 |
| **3-1 분산 락** | @DistributedLock AOP로 락과 트랜잭션 분리 | ✅ 구현됨 |
| **3-2 Redis** | 캐시 레이어로 DB I/O 감소, Redis ZSET으로 랭킹 | ✅ 랭킹 구현, 캐시 미흡 |
| **3-3 Kafka** | @TransactionalEventListener(AFTER_COMMIT) + DLQ/Retry | ❌ 실패 시 log.error만 |
| **3-4 대용량** | 파티션 수 = 동시 처리량, Retention 정책 설계 | ⚠️ 기본 설정만 |
| **4 장애대응** | 부하 테스트로 병목 찾고, Grafana로 실시간 모니터링 | ⚠️ K6 스크립트 있음, Grafana 미설정 |

---

## 코치 피드백 핵심 3줄

> **로이**: "잔액 증가의 역할과 책임은 누구에게?" → Service가 아닌 Domain이 담당해야 한다.  
> **석범**: "FK 없어도 ERD에서 관계는 설정해야 한다."  
> **제이**: "warn 로그만으로는 부족하다. retry와 DLQ가 필요하다."
