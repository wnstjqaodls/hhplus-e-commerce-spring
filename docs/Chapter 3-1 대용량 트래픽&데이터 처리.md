# Chapter. 3-1 대용량 트래픽&데이터 처리

챕터: Chapter 3
과제: 과제 : 이번 챕터 과제 (https://www.notion.so/2432dc3ef51480afa58eed08c1342de2?pvs=21)

<aside>
🔄 Summary : 지난 동시성 문제 챕터 돌아보기

</aside>

- Summary 지난 챕터 돌아보기
    
    ### 실제 현업에서 동시성 문제를 처리하는 방법 ?
    
     여러분들이 항상 바라고 바라는 `대용량 트래픽` 을 마주하는 데에 있어 가장 중요한 것은 동시에 수많은 요청이 들어오더라도 정확하게 데이터를 관리하고, 처리하는 것입니다. 만약 쿠폰 발급과 같이 수많은 사용자들에게 한정적인 수량의 상품을 제공해야한다 거나 콘서트 시나리오와 같이 동일한 좌석에 대한 중복 예약을 막는다던가 하는 부분은 **서비스의 성패** 로 이어지기 때문입니다. 만약 여러분들이 구현한 비즈니스 로직에서 데이터에 대한 동시성 문제가 발생해, 회사가 막대한 금전적 손해를 입게 된다면 ? 
    
    ![Untitled](Untitled.png)
    
    > MBC 예능 프로그램 ‘나 혼자 산다’ 방송화면 캡처
    > 
    
    까진 아니더라도 꽤나 심각한 손해로 이어질 수 있겠죠 😭 그만큼 우리는 데이터의 정합성을 잘 지켜주어야 하며 동시에 성능 문제 또한 고려해야 합니다. 그래서 짤막하게 이를 마주할 때 어떤 방식으로 접근하는 지를 가져와 봤어요.
    
    <aside>
    💡 어떤 Lock 을 적용해야 할지 고려하는 기준과 순서
    
    </aside>
    
    1. 낙관적 Lock 으로 해소 가능한지?
        
        ```
        낙관적 Lock 으로 해소 가능한지는 아래와 같은 판단 기준을 둡니다.
        [BEST] 수정에 실패했을 때, 해당 비즈니스 로직의 실패로 이어져도 되는 경우
        [2] 데이터의 수정이 동시에 많은 충돌을 야기하지 않는 경우 ( Retry 로 해소가능 한 요청량인가? )
        ```
        
    2. 비관적 Lock 으로 해소 가능한지?
        
        ```
        비관적 Lock 은 낙관적 Lock 에 비해 잠금을 위한 범위가 커질 수 있음.
        수정과 조회가 빈번하게 이루어지는 쿼리에서는 Lock 경합이 빈번하게 발생할 수 있다.
        또한 Lock 이 잠그는 범위가 큰 경우, 의도하지 않은 다른 테이블에 대한 조회에서도
        비관적 Lock 에 의한 대기 문제가 발생할 수 있다.
        
        하지만 비관적 Lock 은 `빠르게 처리가능하며` (작은 트랜잭션 범위) `반드시 순서대로
        성공해야 하는` 작업에 대해서 매우 효과적인 해결책이 될 수 있다.
        ```
        

<aside>
⛵ **이번 챕터 목표**

</aside>

- DB 트랜잭션 이상의 범위, 분산 환경에서 Lock 을 적용할 수 있는 방법에 대해 고민해 봅니다.
- 다량의 트래픽을 처리하기 위해 적은 DB 부하로 올바르게 기능을 제공할 방법을 고민해 봅니다.
- 캐시 레이어의 적용을 통해 DB I/O 를 줄일 방법을 고민해 봅니다.

<aside>
🚩 **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**

</aside>

<aside>
💡

점점 늘어나는 고객과 많은 트래픽은 점점 시스템의 높은 Throughput 을 요구하게 됩니다.

이에 RDBMS 만으로는 다양한 비즈니스 가치를 달성하기 어렵습니다.

우리는 다양한 문제를 해결하기 위해 **REDIS** 라는 추가 선택지를 찾게 됩니다.

</aside>

### 1. Distributed Lock 기반의 동시성 제어

### **Distributed Lock ( 분산락 )**

- **분산 시스템에서 서로 다른 서버 인스턴스에 대한 일관된 락을 제공하기 위한 장치 feat. Redis**
- **분산락의 핵심은 `분산된 서버/클러스터` 간에도 Lock 을 보장하는 것**
- **key-value 기반의 원자성을 이용한 Redis 를 통해 DB 부하를 최소화하는 Lock 을 설계**

![Untitled](Untitled%201.png)

                                     <유저의 잔액 충전과 사용의 동시 요청을 레디스를 활용하여 제어>

- **레디스를 활용한 분산락의 대표적인 세가지 방식**
    - `Simple Lock` - key 선점에 의한 lock 획득 실패 시, 비즈니스 로직을 수행하지 않음
        
        ```kotlin
        락 획득 여부 = redis 에서 key 로 확인
        if (락 획득) {
        	try {
        	  로직 실행
        	} finally {
        	  lock 제거
        	}
        } else {
          throw Lock 획득 실패 예외
        }
        ```
        
        - Lock 획득 실패 시 요청에 대한 비즈니스 로직을 수행하지 않음
        - 실패 시 재시도 로직에 대해 고려해야하며 요청의 case 에 따라 실패 빈도가 높음
    - `Spin Lock` - lock 획득 실패 시, 일정 시간/횟수 동안 Lock 획득을 재시도
        
        ```kotlin
        재시도 횟수 = 0
        while (true) {
          락 획득 여부 = redis 에서 key 로 확인 // SETNX key "1"
          if (락 획득) {
        		try {
        		  로직 실행
        		} finally {
        		  lock 제거
        		}
        		break;
          } else {
        	  재시도 횟수 ++
        	  if (재시도 횟수 == 최대 횟수) throw Lock 획득 실패 예외
        	  시간 지연 ( 대기 )
          }
        }
        ```
        
        - Lock 획득 실패 시, 지속적인 재시도로 인한 네트워크 비용 발생
        - 재시도에 지속적으로 실패할 시, 스레드 점유 등 문제 발생
    - `Pub/Sub` - redis pub/sub 구독 기능을 이용해 lock 을 제어
        
        ```kotlin
        락 획득 여부 = redis 에서 lock 데이터 에 대한 subscribe 요청 및 획득 시 값 반환
        // 특정 시간 동안 Lock 에 대해 구독
        if (락 획득) {
        	로직 실행
        } else {
        	// 정해진 시간 내에 Lock 을 획득하지 못한 경우
        	throw Lock 획득 실패 예외
        }
        ```
        
        - 레디스 Pub/Sub 기능을 활용해 락 획득을 실패 했을 시에, “구독” 하고 차례가 될 때까지 이벤트를 기다리는 방식을 이용해 효율적인 Lock 관리가 가능
        - “구독” 한 subscriber 들 중 먼저 선점한 작업만 Lock 해제가 가능하므로 안정적으로 원자적 처리가 가능
        - 직접 구현, 혹은 라이브러리를 이용할 때 해당 방식의 구현이 달라질 수 있으므로 주의해서 사용해야 함

**“레디스”를 활용한 락에서 락 획득과 트랜잭션의 순서의 중요성**

락과 트랜잭션은 데이터의 무결성을 보장하기 위해 아래 순서에 맞게 수행됨을 보장해야 합니다.

![Untitled](Untitled%202.png)

왜 그래야할까요?

**정상적인 케이스**

![Untitled](Untitled%203.png)

동시에 두 요청이 인입되더라도, 락의 범위 내에서 트랜잭션이 일어나면, 상품의 재고차감에 대해 동시성이슈 발생하지 않습니다.

**트랜잭션이 먼저 시작된 뒤 락을 획득할 때 발생할 수 있는 문제**

![Untitled](Untitled%204.png)

트랜잭션이 시작되어 데이터를 조회한 이후에 락을 획득하면, 동시에 처리되고 있는 앞단 트랜잭션의 커밋결과를 확인하지 못한 채 재고를 조회해와 차감하는 로직을 수행하게 되므로, 정상적으로 재고를 차감할 수 없습니다. 

또, 트랜잭션이 시작된 이후에 락 획득에 실패하면 의미없는 트랜잭션을 발생시킵니다. 또 락 획득을 위한 대기시간 동안 데이터베이스의 커넥션을 유지하여야 합니다. 즉, 데이터베이스에 부하를 유발할 수 있습니다.

→ DBCP 로 커넥션이 유지된 자원은 서버단에서 가지고 있으나, 원활한 자원 반납이 늦어짐에 따라 처리 성능감소

**락이 먼저 해제된 뒤 트랜잭션이 커밋될 때 발생할 수 있는 문제**

![Untitled](Untitled%205.png)

락이 해제된 이후에 트랜잭션이 커밋된다면, 대기하던 요청이 락 해제 이후 트랜잭션의 커밋 반영 이전의 재고를 조회해와 차감하는 로직을 수행하게 되므로, 마찬가지로 정상적으로 재고를 차감할 수 없습니다.

**Kafka Messaging** 

- 메세지 큐와 같이 순서 보장이 가능한 장치를 이용해 동시성 이슈를 해결
- Queue 의 성질을 이용, 처리 순서를 보장해 특정 데이터에 대한 동시 접근 문제를 해결
- `Kafka` 의 발행 메세지는 기본적으로 각 파티션에 분산되지만, “동일한 key 로 메세지를 발행 시” 항상 동일한 파티션에 메세지가 발행되는 걸 보장해 컨슈머가 순서대로 처리하도록 할 수 있음
    
    ![Untitled](Untitled%206.png)
    
    - 트랜잭션의 범위를 좁히고, 순차 처리를 보장할 수 있으므로 성능적 우위 가능
    - 비동기 처리를 위한 비즈니스 로직의 분리 및 구조 설계가 중요
    - 카프카 HA (고가용성) , 컨슈머 Scale-out 등 구조를 고려할 수 있어야 함
    - 비동기 처리가 되므로 처리 결과를 바로 확인할 수 없음
    

분산 Lock 으로는 동시성 문제가 모두 해소 가능한지?

```
Redis 등을 활용한 외부 Resource 를 통해 불필요한 DB Connection 까지 차단 가능하다.
하지만 관리주체가 DB + Redis 와 같이 늘어남에 따라 다양한 문제 파생으로 이어진다.
또한 Lock 의 관리주체가 다운되면 서비스 전체의 Down 으로 이어질 수 있는 문제가 있다.

하지만 Redis 의 높은 원자성을 활용해 프로세스 처리단위에 대한 동일한 Lock 을
여러 인스턴스에 대해 적용할 수 있으므로 매우 효과적일 수 있고, DB 의 Conection 이나
오래 걸리는 I/O 에 대한 접근 자체를 차단할 수 있으므로 DB 에 가해지는 직접적인 부하를
원천 차단할 수 있으므로 효과적이다.
```

- DB Transaction 과 Lock 의 범위에 따른 처리 고려
    
    ![Untitled](Untitled%202.png)
    

**[!] 여기서 잠깐! 분산락을 구현할때 아래와 같이 하면..?**

```java
@Transactional
public void charge(Long userId, BigDecimal point) {
	RLock lock = redissonClient.getLock("userChargeLock")

	try {
		if(lock.tryLock() == true) {
			User user = userRepository.findById(userId)
			user.charge(point)
		} else {
			throw new LockAccruedFailedException();
		}
	} finally {
		lock.unlock();
	}
}

@Transactional
public void pay(Long userId, BigDecimal point) {
	RLock lock = redissonClient.getLock("userPayLock")

	try {
		if(lock.tryLock() == true) {
			User user = userRepository.findById(userId)
			user.pay(point)
		} else {
			throw new LockAccruedFailedException();
		}
	} finally {
		lock.unlock();
	}
}
```

- 발생 가능 현상 ( 우리의 의도대로 구현이 되었는가? )
    1. 트랜잭션과 락의 순차보장이 실패해요.
    2. 충전과 결제가 동시에 수행 가능해요.
    3. 충전과 결제 기능 자체에 걸리는 락.
    

  4. Test Code 의 병렬 부하 만으로 부하발생 환경에서 동시성 테스트가 가능한가?

      : jmeter, nGrinder를 사용하여 별도의 부하테스트 필요

https://notspoon.tistory.com/48

### 2. Caching

**Cache**

- 데이터를 임시로 복사해두는 Storage 계층
- 적은 부하로 API 응답을 빠르게 처리하기 위해 캐싱을 사용

<aside>
💡 우리 주변에서 볼 수 있는 **Cache 사례**
* DNS : 웹사이트 IP 를 기록해두어 웹사이트 접근을 위한 DNS 조회 수를 줄인다.
* CPU : 자주 접근하는 데이터의 경우, 메모리에 저장해두고 빠르게 접근할 수 있도록 한다.
* CDN : 이미지나 영상과 같은 컨텐츠를 CDN 서버에 저장해두고 애플리케이션에 접근하지 않고 컨텐츠를 가져오도록 해 부하를 줄인다.

</aside>

**Server Caching 전략**

- `application level` **메모리 캐시**
    - 애플리케이션의 메모리에 데이터를 저장해두고 같은 요청에 대해 데이터를 빠르게 접근해 반환함으로서 API 성능 향상 달성
        
        e.g. **Spring** ( ehcache, caffeine, .. ) , **nest-js** ( @nestjs/cache-manager, .. )
        
        ### Spring Cacheable Example
        
        ```java
        @Cacheable("POPULAR_ITEM")
        @Transactional(readOnly = true)
        public List<PopularItem> getPopularItems() {
        	return statisticsService.findPopularItems();
        }
        
        @Scheduled(cron = "0 0 0 * * *")
        @CacheEvict("POPULAR_ITEM")
        public void evictPopularItemsCache() { }
        ```
        
        ### Nest.js CacheInterceptor Example
        
        ```tsx
        // NestJS Caching
        @Injectable()
        export class HttpCacheInterceptor extends CacheInterceptor {
          trackBy(context: ExecutionContext): string | undefined {
            const cacheKey = this.reflector.get(
              CACHE_KEY_METADATA,
              context.getHandler(),
            );
         
            if (cacheKey) {
              const request = context.switchToHttp().getRequest();
              return `${cacheKey}-${request._parsedUrl.query}`;
            }
         
            return super.trackBy(context);
          }
        }
        ```
        
    - 메모리 캐시 원리
        
        <요청 1>
        
        1. 유저의 API 요청이 들어왔을 때 `Memory` 에 Cache Hit 확인
        2. Cache Miss 시에 비즈니스 로직 수행 ( DB, 외부API 등 통신 ) 후 `Memory` 에 데이터 저장
        3. 이후 응답
        
        ---
        
        <요청 2>
        
        1. 유저의 API 요청이 들어왔을 때 `Memory` 상에 상응하는 응답이 있는지 확인
        2. 있으면 해당 값을 이용해 그대로 응답
        
        ![Untitled](Untitled%207.png)
        
    - 메모리 캐시 특징
        - `신속성` - 인스턴스의 메모리에 캐시 데이터를 저장하므로 속도가 가장 빠름
        - `저비용` - 인스턴스의 메모리에 캐시 데이터를 저장하므로 별도의 네트워크 비용이 발생하지 않음
        - `휘발성` - 애플리케이션이 종료될 때, 캐시 데이터는 삭제됨
        - `메모리 부족` - 활성화된 애플리케이션 인스턴스에 데이터를 올려 캐싱하는 방법이므로 메모리 부족으로 인해 비정상 종료로 이어질 수 있음
        - `분산 환경 문제` - 분산 환경에서 서로 다른 서버 인스턴스 간에 데이터 불일치 문제가 발생할 수 있음
        
        <aside>
        💡 분산 서비스 환경에서의 애플리케이션 캐시
        
        각 서버의 수용 가능 상태에 따라 서로 다른 인스턴스에 요청이 분배될 수 있으며, 아래와 같은 문제가 발생할 수 있습니다.
        
        **유저1 이 동일한 조회 API 를 3번 요청**
        
        - Server2 에 도달한 경우, 이전의 캐시가 존재해 `이석범, 하헌우` 라는 응답을 받음
        - 그 후 `한상진, 허재` 라는 데이터가 추가됨
        - 다음 요청은 캐시가 만료된 Server1 에 도달해 최신의 정보를 모두 가져옴
        - 다음 요청은 캐시가 존재하는 Server2 에 도달해 이전의 정보인 `이석범, 하헌우` 만을 가져옴
        
        ![Untitled](Untitled%208.png)
        
        위와 같이 애플리케이션의 메모리에 의존적인 캐시가 제공될 경우, 유저는 요청마다 어떤 인스턴스에 도달하느냐에 따라 같은 시간대에 다른 응답을 받게 된다.
        
        이런 상황이 유저의 구매나 사용성에 영향을 미치는 기능에서 발생할 경우, 사용자 경험에 지대한 악영향을 끼칠 수 있습니다.
        
        **어떻게 해결할 수 있을까?**
        
        - 캐시 데이터가 저장 / 삭제 될 때마다 다른 인스턴스에도 통지하여 동기화 시키는 방법 ( 추가 네트워크 비용 발생 )
        - 별도의 캐시 스토리지를 두고 모든 인스턴스가 하나의 관리주체를 통해 제공받을 수 있도록 구성 ( 하기 설명 )
        </aside>
        
- `external Level` **별도의 캐시 서비스**
    - 별도의 캐시 Storage 혹은 이를 담당하는 API 서버를 통해 캐싱 환경 제공
        
        e.g. **Redis**, Nginx 캐시, CDN, ..
        
    - 캐시 서비스 원리
        
        < 요청 1 >
        
        1. 유저의 API 요청이 들어왔을 때 `Cache Service` 에 Cache Hit 확인
        2. Cache Miss 일 경우, 비즈니스 로직을 수행 후 결과를 `Cache Service` 에 저장
        3. 이후 응답 반환
        
        ---
        
        < 요청 2 >
        
        1. 유저의 API 요청이 들어왔을 때 `Cache Service` 에 Cache Hit 확인
        2. Cache Hit 일 경우, 해당 데이터를 그대로 활용해 응답 반환
        
        ![Untitled](Untitled%209.png)
        
    - 캐시 서비스 특징
        - `일관성` - 별도의 담당 서비스를 둠으로서 분산 환경 ( Multi - Instance ) 에서도 동일한 캐시 기능을 제공할 수 있음
        - `안정성` - 외부 캐시 서비스의 Disk 에 스냅샷을 저장하여 장애 발생 시 복구가 용이함
        - `고가용성` - 각 인스턴스에 의존하지 않으므로 분산 환경을 위한 HA 구성이 용이함
        - `고비용` - 네트워크 통신을 통해 외부의 캐시 서비스와 소통해야 하므로 네트워크 비용 또한 고려해야 함

### 3. Caching Strategy

<aside>
ℹ️ DB 의 Connection 과 I/O 는 매우 높은 비용을 요구하며, 이는 트래픽이 많아질 수록 기하급수적으로 그 부하가 증가하는 특성을 가지고 있다. 데이터 정합성을 유지하기 위해 각 트랜잭션은 원자적으로 수행되어야 하며 이는 요청이 증가할 수록 더 많은 딜레이가 생긴다는 것을 의미하기 때문이다.

</aside>

### Cache 의 Termination Type

- **Expiration**
    - 캐시 데이터의 유통기한을 두는 방법
    - Lifetime 이 지난 캐시 데이터의 경우, 삭제시키고 새로운 데이터를 사용가능하게 함
- **Eviction**
    - 캐시 메모리 확보를 위해 캐시 데이터를 삭제
    - 명시적으로 캐시를 삭제시키는 기능
    - 특정 데이터가 Stale 해진 경우 ( 상한 경우 ) 기존 캐시를 삭제할 때도 사용

**[ 조회 ]**

- `Cache` 에서 데이터를 먼저 확인하고, 없다면 DB 를 확인
    
    ![Untitled](Untitled%2010.png)
    
- 일반적으로 잦은 조회가 일어나거나 DB I/O 가 많은 조회에 대해 부하를 줄이기 위한 전략
    
    **방식**
    
    1. Cache 에 해당하는 데이터가 있는지 확인 ( Cache Hit )
    2. Cache 에 없을 경우 ( Cache Miss ) DB 에서 데이터 조회
    3. DB 에서 조회한 데이터를 Cache 에 저장
    
    **특징**
    
    - 조회 부하를 낮추는 데에 적합한 방식
    - 빈번하게 변경이 일어나는 데이터에 대해서 정합성 보장을 위해 Eviction 전략을 잘 세워야 함
        - 인스타그램 피드의 좋아요
            - 
        - 이커머스의 인기상품 조회 (실시간(5분 / 10분), 일간, 주간?)
            - 

<aside>
🔔

**원활한 프로젝트 진행을 위한 학습 로드맵**

</aside>

### 10시간 학습 로드맵

## 대용량 트래픽 & 분산 환경 4일 로드맵 (+ 언어별 세팅 가이드)

### 전체 주제

**“대용량 트래픽 환경에서 분산락과 캐시를 이용해 동시성과 성능 문제를 해결하기”**

---

### Day 1: 동시성 이슈와 Lock 전략 다시 이해하기

**목표**

동시성 문제를 실제 서비스 장애 사례와 함께 이해하고, 낙관적/비관적 Lock 선택 기준을 세운다.

**학습 키워드**

- Race Condition
- 낙관적 Lock vs 비관적 Lock
- Lock 경합과 성능 고려

**To-Do**

- 낙관적/비관적 Lock 적용 판단 기준 정리
- 수정 실패 허용 가능 여부 판단 기준 세우기
- Lock 충돌/경합 발생 시나리오 상상해보기

**언어별 세팅**

| 언어 | Lock 구현 방식 |
| --- | --- |
| Java | `@Version`, `@Lock` 사용 |
| Kotlin | `@Version`, `@Lock`, Coroutine 고려 |
| TypeScript | Version 수동 관리, `SELECT ... FOR UPDATE` 수동 작성 |

**자가진단표**

- 낙관적/비관적 Lock의 사용 시점을 구분할 수 있다.
- Lock 충돌 발생 상황을 코드로 설명할 수 있다.
- Lock 선택이 성능에 미치는 영향을 고려할 수 있다.

---

### Day 2: Redis를 이용한 분산락 이해 및 적용하기

**목표**

분산 환경에서 동시성을 제어하기 위해 Redis 기반 분산락을 이해하고 직접 구현해본다.

**학습 키워드**

- Distributed Lock (Redis)
- Simple Lock, Spin Lock, Pub/Sub Lock
- 락과 트랜잭션 순서 보장

**To-Do**

- Redis 기반 Simple Lock 직접 구현
- 락-트랜잭션 순서 문제 사례 정리
- Pub/Sub 기반 락 구조 이해

**언어별 세팅**

| 언어 | 분산락 구현 방식 |
| --- | --- |
| Java | Redisson Client 사용, Lua 스크립트 작성 가능 |
| Kotlin | Coroutine 환경 고려 + Redisson 사용 |
| TypeScript | ioredis 라이브러리로 분산락 구현 |

**자가진단표**

- Redis를 이용한 Lock 획득/해제 플로우를 설명할 수 있다 (1~5점)
- 락 획득 실패/재시도/구독 처리 방식을 구분할 수 있다 (1~5점)
- 트랜잭션과 락 순서 보장의 필요성을 명확히 이해했다 (1~5점)

---

### Day 3: 캐시 전략과 성능 최적화 이해하기

**목표**

캐시의 원리를 이해하고, 트래픽 부하를 줄이기 위한 서버/외부 캐시 전략을 설계할 수 있다.

**학습 키워드**

- Memory Cache vs External Cache
- Expiration / Eviction
- Cache 일관성 문제

**To-Do**

- 메모리 캐시와 Redis 캐시 차이 정리
- Cache Miss 대응 전략 고민
- 분산 환경 캐시 일관성 이슈 정리

**언어별 세팅**

| 언어 | 캐시 설정 방식 |
| --- | --- |
| Java | Spring `@Cacheable`, RedisTemplate 활용 |
| Kotlin | Spring + Redis 연동, Caffeine 적용 가능 |
| TypeScript | @nestjs/cache-manager 사용, Redis 연동 |

**자가진단표**

- 메모리 캐시와 분산 캐시의 차이를 설명할 수 있다 (1~5점)
- Expiration과 Eviction 전략의 차이를 이해했다 (1~5점)
- 분산 환경에서 캐시 일관성 문제가 발생하는 원인을 설명할 수 있다 (1~5점)

---

### Day 4: 분산락 & 캐시 최적 적용 실습하기

**목표**

Redis 기반 분산락과 캐시를 실제 시나리오에 적용하고, 성능 개선 효과를 문서화할 수 있다.

**학습 키워드**

- Redis Lock 최적 적용
- 조회 캐시 전략 적용
- 성능 개선 분석

**To-Do**

- 주문/예약/결제 기능에 Redis 분산락 적용 실습
- 조회 API에 캐시 적용 실습
- Lock + Cache 적용 전후 성능 비교 보고서 작성

**언어별 세팅**

| 언어 | 적용 실습 방식 |
| --- | --- |
| Java | Redisson 분산락 + Spring Cache 적용 |
| Kotlin | Coroutine 락 제어 + Spring Cache 적용 |
| TypeScript | ioredis 락 제어 + NestJS CacheInterceptor 적용 |

**자가진단표**

- Redis 분산락과 캐시를 모두 적용한 실습을 완료했다 (1~5점)
- 적용 결과 성능 차이를 수치로 표현할 수 있다 (1~5점)
- 적절한 Lock 범위와 Cache 범위를 선정할 수 있다 (1~5점)

### 3시간 학습 로드맵

## 3시간 압축 학습 가이드: 분산 환경 동시성 제어 & 캐시 전략 핵심만 딱!

### 시간 분배

| 세션 | 주제 | 소요 시간 |
| --- | --- | --- |
| 1 | 낙관적/비관적 락 전략 | 40분 |
| 2 | Redis 분산락 적용 | 40분 |
| 3 | 캐시 전략과 성능 개선 | 40분 |
| 4 | Lock & Cache 종합 적용 실습 플랜 | 20분 |

---

### 세션 1: 낙관적/비관적 락 전략 (40분)

**핵심 메시지**

- 동시성 문제는 시스템 장애를 초래할 수 있다
- Lock 선택 기준을 명확히 이해하고 적용해야 한다

**퀴즈**

- 낙관적 Lock이 적합한 상황은? (객관식)
- 비관적 Lock을 적용할 때 주의해야 할 점은?

---

### 세션 2: Redis 분산락 적용 (40분)

**핵심 메시지**

- 단일 인스턴스를 넘어 서버 간 동시성 제어를 Redis로 해결할 수 있다
- 락-트랜잭션 순서가 중요하다

**퀴즈**

- Redis 분산락 Simple/Spin/Subscribe 방식 차이는?
- 락을 먼저 잡지 않고 트랜잭션을 시작하면 생기는 문제는?

---

### 세션 3: 캐시 전략과 성능 개선 (40분)

**핵심 메시지**

- 조회 부하를 줄이려면 캐시를 적극 활용해야 한다
- 메모리 캐시 vs 외부 캐시 차이를 이해하고 적절히 선택한다

**퀴즈**

- 메모리 캐시와 외부 캐시의 주요 차이점은?
- Expiration과 Eviction의 차이점은?

---

### 세션 4: Lock & Cache 종합 적용 실습 플랜 (20분)

**핵심 메시지**

- Lock과 Cache는 함께 적용하면 강력하다
- 분산 환경에서 일관성과 성능을 동시에 잡는 구조를 설계한다

**퀴즈**

- Lock과 Cache를 함께 사용할 때 가장 주의할 점은?
- 캐시 일관성 문제를 해결하는 대표적인 방법은?

---

<aside>
🗓️ **Weekly Schedule Summary: 이번 주차 과제 요구 사항**

</aside>

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
    - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!
- PR 템플릿
    
    ```markdown
    ## :pushpin: PR 제목 규칙
    [STEP0X] 이름 - 선택 시나리오 (e-commerce/concert)
    
    ---
    ### **핵심 체크리스트** :white_check_mark:
    
    #### :one: 분산락 적용 (3개)
    - [ ] 적절한 곳에 분산락이 사용되었는가? 
    - [ ] 트랜젝션 순서와 락순서가 보장되었는가?
    
    #### :two: 통합 테스트 (2개)
    - [ ] infrastructure 레이어를 포함하는 통합 테스트가 작성되었는가?
    - [ ] 핵심 기능에 대한 흐름이 테스트에서 검증되었는가?
    - [ ] 동시성을 검증할 수 있는 테스트코드로 작성 되었는가?
    - [ ] Test Container 가 적용 되었는가?
    
    #### :three: Cache 적용 (3개)
    - [ ] 적절하게 Key 적용이 되었는가?
    
    ---
    #### STEP11
    - [ ] Redis 분산락 적용
    - [ ] Test Container 구성
    - [ ] 기능별 통합 테스트
    
    #### STEP12
    - [ ] 캐시 필요한 부분 분석
    - [ ] redis 기반의 캐시 적용
    - [ ] 성능 개선 등을 포함한 보고서 제출
    
    ### **간단 회고** (3줄 이내)
    - **잘한 점**: 
    - **어려운 점**: 
    - **다음 시도**:
    ```
    

<aside>
🚩 **과제 : 이번 챕터 과제**

</aside>

### **`STEP11 - Distributed Lock`**

- Redis 기반의 분산락을 직접 구현해보고 동작에 대한 통합테스트 작성
- 주문/예약/결제 기능 등에 **(1)** 적절한 키 **(2)** 적절한 범위를 선정해 분산락을 적용

### **`STEP12 - Cache`**

- 조회가 오래 걸리거나, 자주 변하지 않는 데이터 등 애플리케이션의 요청 처리 성능을 높이기 위해 캐시 전략을 취할 수 있는 구간을 점검하고, 적절한 캐시 전략을 선정
- 위 구간에 대해 Redis 기반의 캐싱 전략을 시나리오에 적용하고 성능 개선 등을 포함한 보고서 작성 및 제출

<aside>
🚥 **과제 평가 기준과 핵심 역량 Summary**

</aside>

### P/F 기준

<aside>
🚩 **과제 : 이번 챕터 과제 평가 기준에 따라 step의 pass/fail을 정합니다.**

</aside>

### 과제 평가 기준

- 분산 환경에서의 동시성 제어에 대한 이해
- 캐시와 DB 조회 부하에 대한 이해

**[ Step 11 ]**

**< Transaction 의 범위와 Redis 기반의 분산락의 활용에 대한 이해 >**

- 분산락에 대한 이해와 DB Tx 과 혼용할 때 주의할 점을 이해하였는지
- 적절하게 분산락이 적용되는 범위에 대해 구현을 진행하였는지

**[ Step 12 ]**

**< 선택한 시나리오에서의 캐싱 전략 설계 - 리드미 권장 >**

- 각 시나리오에서 발생하는 Query 에 대한 충분한 이해가 있는지
- 각 시나리오에서 캐시 가능한 구간을 분석하였는지
- 대량의 트래픽 발생시 지연이 발생할 수 있는 조회쿼리에 대해 분석하고, 이에 대한 결과를 작성하였는지

### BP 기준

## Distributed Lock & Cache 심화과제 평가 항목

- 트랜잭션의 범위와 분산 환경에서의 Lock 에 대한 이해
- 동시성 이슈 발생 원인 이해와 적절한 Redis 기반의 분산락 적용 전략
- RedisTemplate을 활용한 의존성 최소화 및 Repository 구현의 최적화
- Redis 캐싱 전략(LookAside, WriteThrough 등)의 명확한 이해와 적절한 적용
- 캐시 데이터 직렬화/역직렬화 오류 처리 전략(JSON 직렬화 등)의 적절한 활용
- 캐싱 로직 적용 계층(도메인 vs 레포지토리)의 명확한 기준 및 일관성 유지
- 보고서의 문제 배경 및 흐름(배경, 문제해결, 테스트, 한계점, 결론 등)의 명확한 구성

### 핵심 키워드 및 역량

## 🔒 Distributed Lock (STEP 11)

DB 레벨의 락에서 벗어나 분산 환경의 작업 단위 분산락에 대한 이해

### 🎯 과제 목표

- DB Lock 의 한계점을 파악하고 분산환경에서의 동시성 제어 수단에 대해 고민합니다.
- 분산락의 동작방식을 이해하고 구현해보고 적용사례에 대해 고민합니다.
- Redis 의 Atomicity 기반의 분산락을 구현하고 실제 시나리오에 적용해봅니다.

### 🛠️ 핵심 기술 키워드

- Transaction 의 범위와 DB Lock 의 한계
- 분산환경에서의 Lock
- Redis 의 Atomicity 기반 연산

### 🧠 핵심 역량

| 역량 | 설명 |
| --- | --- |
| Transaction 에 대한 이해 | DB Transaction 이 아닌 “작업단위” 로서의 Transaction 에 대한 올바른 이해 |
| 분산환경에서의 잠금 제어 | 분산환경에서 높은 throughput 을 가진 잠금 구현 |
| Redis 의 Atomicity | 레디스의 원자적 특성을 이해하고, 이를 기반으로 한 분산락을 구현 |

---

## 💽 Cache (STEP 12)

**조회 성능 개선을 위한 캐싱 전략 설계 및 적용**

### 🎯 과제 목표

- 트래픽이 많은 API나 DB 부하가 높은 조회 로직에 대해 캐싱을 통해 성능을 개선합니다.
- 캐시 적용 방식(예: Read-Through, Cache Aside 등)과 적절한 Expiration / Eviction 전략을 설계하고 문서화합니다.
- **Cache Stampede(동시 캐시 부재)** 이슈에 대해 학습하고, 대응 전략을 설계합니다.

### 🛠️ 핵심 기술 키워드

- 캐시 전략 (Memory Cache, Redis Cache)
- Expiration / Eviction
- Cache-Aside / Read-Through 패턴
- 캐시 스탬피드(Cache Stampede)

### 🧠 핵심 역량

| 역량 | 설명 |
| --- | --- |
| **조회 성능 분석력** | 트래픽 병목 지점을 분석하고 캐싱 포인트를 판단하는 능력 |
| **캐시 전략 설계력** | TTL 설정, 캐시 무효화 시점, Expiration/Eviction 방식을 설계 |
| **다양한 캐시 적용 경험** | Application 메모리 캐시 / Redis 캐시 적용 실습 |
| **안정적 서비스 설계** | Cache Stampede 및 Race Condition 방어를 위한 실무적인 고민 |

---

## 🧭 요약 정리

| 구분 | STEP 11 | STEP 12 |
| --- | --- | --- |
| 핵심 주제 | 분산환경에서의 락 | 조회 성능 개선용 캐싱 |
| 과제 예시 | Redis 기반의 분산락을 통한 주문, 결제 제어 | 인기 상품 조회 TTL 캐싱, 피드 좋아요 캐싱 |
| 핵심 자료구조 | Redis (String, TTL) | Memory / Redis (String 등) |
| 제출 형태 | 코드 작성 및 PR 제출 | README 문서화 or 코드 주석 |