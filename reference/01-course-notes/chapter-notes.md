# 항해플러스 백엔드 9기 — 챕터별 학습노트

> 작성 기준: Notion 원문 (`hhplus-notion-content.md`) 전체 내용 기반
> 코드베이스 확인일: 2026-04-05
> 원문에 없는 내용은 이 노트에도 없다.

---

## Chapter 1-1 TDD

### 이 챕터의 목표 (원문)

- 테스트 가능한 코드와 테스트 코드 작성에 집중하며, TDD 기반 요구사항 기능 개발
- TDD, Testable Code, Test Code에 대한 학습을 진행합니다.
- 기초 학습자료로 부족한 부분을 학습합니다.
- 주어진 과제를 분석하고 TDD 기반으로 개발을 진행합니다.

---

### 핵심 개념 (원문 기반)

#### TDD는 소프트웨어 설계 방법론이다

TDD는 소프트웨어 설계 방법론이기 때문에 너무 엄격하고 딱딱하게 접근을 하게 되면 TDD가 주는 교훈을 잘못 이해하고, 배우나마나 한 것이 되는 것 같습니다.
온보딩 과정에서는 목표로 해야 하는 것은 TDD가 맞다, 안맞다, 좋다, 안좋다를 생각하는 것이 아닌, **TDD를 이해해보고 경험해보는 것을 목표로 해야 합니다.**

#### 테스트 시나리오 또한 언제든 바뀔 수 있다

처음에 정한 테스트 시나리오(유저 시나리오)를 바꾸지 않는 것이 Best Case겠지만, 처음에 만든 시나리오에 종속되어 좋지 않은 구조로 소프트웨어를 개발하게 되는 것은 지양해야 합니다.
**우리의 목표는 좋은 소프트웨어를 설계하는 것이지, 시나리오 대로 Test Code를 만드는 일이 아닙니다.** TDD는 짧은 주기로 iteration을 도는 애자일과 가깝습니다.

#### 테스트 코드에서는 "실패" 케이스가 우선되어야 합니다

테스트 코드는 실패 케이스를 우선으로 한다. 기본적인 로직을 완성해두고, 에러가 날 수 있는 가능한 모든 경우들을 생각해 test code를 작성하고 동작하는 코드에서 예외처리를 하는 방식으로 코드를 작성해 나갈 수 있습니다.

단순한 객체 생성 테스트(createOrder가 동작하는 걸 그대로 가져다 Test)는 크게 의미가 없습니다. 위와 같은 테스트가 가질 수 있는 의미는:
1. **기술서** - 테스트 코드를 통해 객체가 어떤 꼴로 생성되는지 이해해볼 수 있습니다.
2. **TDD 시작하는 코드** - 독립적인 행위를 기반으로 설계하게 되는 시작점

#### 확장 가능성 있는 코드

객체 그 자체로 매개변수를 넘기고 생성 그 자체에 대한 테스트 코드에 대해서는 힘을 빼는 게 좋을 것 입니다.

```java
// 나쁜 예: 각 파라미터를 직접 꺼내서 넘김 (파라미터 변경 시 전체 수정 필요)
createOrder("orderNo", user.userID, product.productNo, product.productName, product.productPrice, 1, user.userAddr, OrderState.PayRequest);

// 좋은 예: 객체 자체를 매개변수로 넘김 (확장성)
createOrder(user, product, ..)
createPay(user, order, ..)
createRefund(user, order, pay, ...)
```

#### 테스트 커버리지 ⇒ 모든 경우를 테스트 하는 것이 아니다

모든 함수들에 대해서 테스트 코드가 필요한 것은 아니다. 단순 생성의 경우는 테스트를 할 필요가 없을 수도 있다. 100%의 Test Coverage를 가지는 것이 좋은 설계가 되는 것인지는 의문이 든다.

#### Test Pyramid (원문 학습자료)

- **Unit Testing** - 대상: 단일 기능 혹은 작은 단위의 함수/객체 등. 가벼운 비용으로 새로운 기능 혹은 개선이 기존의 rule을 위배하지 않는지 점검
- **Integration Testing** - 대상: 서로 다른 module/system의 상호작용. 맞물려 돌아가는 기능이 모여 정상적으로 원하는 기능을 제공하는지 점검
- **End-to-End Testing** - 대상: 전체 애플리케이션의 흐름. 애플리케이션이 제공하는 기능을 사용자 시나리오 기반으로 문제 없는지 점검

#### Test Double

테스트 더블은 실제 컴포넌트를 대체할 수 있도록 하는 대역이다. 실제 컴포넌트에 대해 행동을 모방하고, 이를 통해 기존의 강한 결합도를 낮추고 테스트 중 제어 가능하도록 한다.

- **Mock**: 테스트를 위해 특정 기능에 대해 정해진 응답을 제공하는 객체. 입력과 상관없이 **어떤 행동을** 할지에 초점을 맞춘 객체
- **Stub**: 테스트에 필요한 호출에 대해 미리 준비된 응답을 제공하는 객체. 입력에 대해 **어떤 상태**를 반영하는 지에 초점을 맞춘 객체

#### 고전파 vs 런던파

- **고전파(Classicist)**: 협력자로서 실제 객체를 사용하고자 하는 집단
- **런던파(London School 또는 Mockist)**: 테스트 대역을 사용하고자 하는 집단

#### FIRST principle

- **Fast**: 테스트는 빠르게 동작하여 자주 돌릴 수 있어야 한다.
- **Independent**: 각각의 테스트는 독립적이며 서로 의존해서는 안된다.
- **Repeatable**: 어느 환경에서도 반복 가능해야 한다.
- **Self-Validating**: 테스트는 성공 또는 실패로 bool 값으로 결과를 내어 자체적으로 검증되어야 한다.
- **Timely**: 테스트는 적시에 즉, 테스트하려는 실제 코드를 구현하기 직전에 구현해야 한다.

#### TDD 개발 방법 및 순서

1. **빨강**: 실패하는 작은 테스트를 작성한다. 처음에는 컴파일조차 되지 않을 수 있다.
2. **초록**: 빨리 테스트가 통과하게끔 만든다. 이를 위해 어떠한 죄악(함수가 무조건 특정 상수만을 반환하는 등)을 저질러도 좋다.
3. **리팩토링**: 일단 테스트를 통과하게만 하는 와중에 생겨난 모든 중복을 제거한다.

#### Testable Code

- 모든 코드를 테스트 가능하게 구현하는 것을 목표로 진행합니다.
- 모든 테스트 케이스가 성공했다는 것은 목표한 기능이 완성되었다는 것을 의미합니다.
- 테스트 커버리지 100%가 아니라, 정확히 **기능의 동작을 확인하는 테스트를 작성**해 주세요.
- 주요 기능에서 `private` 접근자, 객체간의 강결합 같이 테스트 불가능한 코드는 가능한 한 지양하는 것이 좋습니다.

---

### 원문 코드 예시

#### 실패 케이스 우선 작성 예시

```javascript
// 성공 케이스 보다는 실패 케이스에 집중
describe('아래와 같은 경우 각 정보를 생성할 수 없다', ()=>{
describe("다음가 같은 경우 '결제 요청'이 불가능하다.", ()=>{
    test("인증 실패", ()=>{
        const user = createUser("heonil10", "PW", "name", "addr");
        const product = createProduct("productNo", "productName", 1000, 100, true, true);
        expect(()=>requestPay(user, product, 1)).toThrow(AuthError);
    });

    test("재고 부족", ()=>{
        const user = createUser("heonil1", "PW", "name", "addr");
        const product = createProduct("productNo", "productName", 1000, 1, true, true);
        expect(()=>requestPay(user, product, 10)).toThrow(ProductError);
    })

    test("구매 불가 상품", ()=>{
        const user = createUser("heonil1", "PW", "name", "addr");
        const product = createProduct("productNo", "productName", 1000, 10, false, true);
        expect(()=>requestPay(user, product, 1)).toThrow(ProductError);
    });
});
```

#### How to TDD By Example: Point 클래스 (원문 전체 스텝)

```java
// Step 1: Point 클래스가 없어서 실패하는 테스트 작성하기
class PointTest {
    @Test
    void 1000원을충전하면_금액이_1000원증가한다() {
        Point point = new Point(); // 아직 클래스가 없음
        point.charge(1000);
        assertThat(point.getAmount()).isEqualTo(1000);
    }
}
```

```java
// Step 2: Point 클래스 만들기, 아직 구현은 없음
public class Point {
    private int amount = 0;
    public void charge(int amountToAdd) {
        // 아직 구현 없음
    }
    public int getAmount() {
        return amount;
    }
}
```

```java
// Step 3: 가짜 구현 (1000을 항상 더해줌)
public class Point {
    private int amount = 0;
    public void charge(int amountToAdd) {
        amount += 1000;  // 가짜 구현
    }
    public int getAmount() {
        return amount;
    }
}
```

```java
// Step 5: 진짜 구현
public class Point {
    private int amount = 0;
    public void charge(int amountToAdd) {
        amount += amountToAdd;  // 진짜 구현
    }
    public int getAmount() {
        return amount;
    }
}
```

```java
// Step 6: 중복 제거 (ParameterizedTest)
class PointTest {
    @ParameterizedTest(name = "{0}원을 충전하면 금액이 {0}원 증가한다")
    @ValueSource(ints = {1000, 2000})
    void 포인트를_충전하면_금액이_정상적으로_증가한다(int chargeAmount) {
        Point point = new Point();
        point.charge(chargeAmount);
        assertThat(point.getAmount()).isEqualTo(chargeAmount);
    }
}
```

#### Jest 기초 - Mocking 예시 (원문)

```java
// Mock 예시 (pseudo-code)
class PaymentServiceTest {
    @Inject
    private PaymentService paymentService;

    @Mock
    private PayClient payClient;

    @Test
    void mock() {
        paymentService.pay(1000);
        verify(payClient, times(0)).pay(1000);
    }
}
```

```java
// Stub 예시 (pseudo-code)
class MemberServiceTest {
    @Inject
    private MemberService memberService;

    @Stub
    private MemberRepository memberRepository;

    @Test
    void stub() {
        String email = "hanghae@gmail.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);
        boolean result = memberService.isRegistered(email);
        assertThat(result).isTrue();
    }
}
```

#### Arrange-Act-Assert 패턴 예시 (NestJS, 원문)

```typescript
describe('createTweet', () => {
  it('should create tweet', () => {
    // Arrange
    service.tweets = [];
    const payload = 'This is my tweet';

    // Act
    const tweet = service.createTweet(payload);

    // Assert
    expect(tweet).toBe(payload);
    expect(service.tweets).toHaveLength(1);
  });

  it('should prevent tweets created which are over 100 characters', () => {
    // Arrange
    const payload = 'This is a long tweet over 100 characters...';

    // Act
    const tweet = () => {
      return service.createTweet(payload);
    };

    // Assert
    expect(tweet).toThrowError();
  });
});
```

---

### 과제 명세

#### [1주차 과제] TDD 로 개발하기

**과제 필수 사항**
- Nest.js의 경우 Typescript, Spring의 경우 Kotlin / Java 중 하나로 작성합니다.
- 프로젝트에 첨부된 설정 파일은 수정하지 않도록 합니다.
- 테스트 케이스의 작성 및 작성 이유를 주석으로 작성하도록 합니다.
- 프로젝트 내의 주석을 참고하여 필요한 기능을 작성해주세요.
- 분산 환경은 고려하지 않습니다.
- 실제 DB 등을 활용하는 것이 아닌 주어진 코드 내의 객체를 활용하여 작성합니다.

**`point` 패키지의 TODO와 테스트코드를 작성하세요.**

**API 요구사항**
- PATCH `/point/{id}/charge` : 포인트를 충전한다.
- PATCH `/point/{id}/use` : 포인트를 사용한다.
- GET `/point/{id}` : 포인트를 조회한다.

**기능 요구사항**
- GET `/point/{id}/histories` : 포인트 내역을 조회한다.
- 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.

**Level-UP**
- 같은 사용자가 동시에 충전할 경우, 해당 요청 모두 정상적으로 반영되어야 합니다.

**STEP00 - 이것부터 시작해보세요!**
- PR 템플릿 세팅하기!

---

### 내 코드와 연결

**구현됨:**

- `/d/dev/workspace/hhplus-tdd-jvm-java/hhplus-tdd-java/src/main/java/io/hhplus/tdd/point/service/PointService.java`
  - `chargePoint()`, `usePoint()`, `getPointById()`, `getPointHistoryById()` 구현
  - 생성자 주입 방식 적용 (생성자 주입이 더 좋다고 해서 변경함 - 주석에 이유도 명시)
  - 충전 한도(100만원), 최대 잔고(500만원) 정책 구현

- `/d/dev/workspace/hhplus-tdd-jvm-java/hhplus-tdd-java/src/test/java/io/hhplus/tdd/point/PointControllerTest.java`
  - `@ExtendWith(MockitoExtension.class)` — 단위 테스트 (스프링 컨텍스트 없이)
  - `@Mock PointService`, `@BeforeEach`에서 `new PointController(pointService)` 직접 생성
  - `when().thenReturn()` / `when().thenThrow()` Stub 패턴 적용
  - 실패 케이스 테스트 포함: 충전 한도 초과, 잔액 부족, 음수 금액, 부정 충전 감지
  - `assertThatThrownBy()` — 예외 메시지까지 검증

**미구현/부족:**

- **Arrange-Act-Assert 패턴 미적용** — 원문에서 이 패턴을 권장하지만, 내 테스트 코드 일부는 Act와 Assert가 명확히 분리되지 않음
- **경계값 테스트 미완성** — `chargeAmountLimitBoundary()` 테스트가 주석 처리된 채로 남아 있음 (TODO 상태)
- **동시성 테스트 없음** — 과제 Level-UP 요구사항인 "같은 사용자가 동시에 충전할 경우 모두 정상 반영" 미구현. `synchronized`, DB 락, 분산락 중 어떤 것도 적용되지 않음
- **`@InjectMocks` 대신 생성자 직접 호출** — 이유를 주석으로 명시했으나, 실제로 더 복잡한 의존성이 생기면 이 방식이 불편해질 수 있음
- **TDD 순서(빨강→초록→리팩토링) 준수 여부 확인 불가** — 커밋 히스토리가 없어서 원문의 스텝별 흐름대로 개발했는지 확인 불가

---

## Chapter 2-1 서버구축 - 설계

### Hall of Fame (우수 사례)

**안은솔 by 로이 코치님**
- 올바른 인터페이스의 활용 및 객체에게 적당한 역할과 책임 할당
- 적당한 수준의 통합 테스트와 단위 테스트의 구성 + 가독성을 위한 노력들
- 경계값 위주의 테스트 범위 설정 및 ParameterizedTest와 Steps를 통한 중복 제거

**박서희 by 이석범 코치님**
- 객체지향적인 코드 및 클래스의 책임 검증 탁월
- 작업단위의 명확한 커밋 분리로 작업 내용을 파악하기 쉬움

**이세호 by 한상진 코치님**
- 동시성 제어 기법에 대한 분석
- 깔끔한 요구사항 구현 및 테스트 작성

---

### 이 챕터의 목표 (원문)

- 시나리오가 요구하는 요구사항을 명확히 분석하기
- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기
- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기
- 견고하지만 유연한 서버 애플리케이션을 구축하기

```
설계가 명확하면, "코드를 치는 행위" 는 목표를 달성하는 "수단" 이 된다.
설계가 명확하지 않으면, "코드를 치는 행위" 는 불필요한 "노동" 이 된다.
```

---

### 핵심 개념 (원문 기반)

#### 작업 진행 순서

1. Service Scenario 선택 (베이직 / 챌린지)
2. 개발 환경 준비
3. 시나리오 분석 및 작업 계획
4. API Spec Documentation

```
1. 요구사항 분석 및 API Spec 정의 ( 문서, 나열, 정리 )
2. Mock API 개발
3. Swagger-UI 작성
4. 본격적으로 "구체적인" 설계를 들어간다. ( 개발을 위한 )
```

---

### 과제 명세

#### e-커머스 서비스

**Description**
- `e-커머스 상품 주문 서비스`를 구현해 봅니다.
- 상품 주문에 필요한 메뉴 정보들을 구성하고 조회가 가능해야 합니다.
- 사용자는 상품을 여러개 선택해 주문할 수 있고, 미리 충전한 잔액을 이용합니다.
- 상품 주문 내역을 통해 판매량이 가장 높은 상품을 추천합니다.

**Requirements**
- 아래 5가지 API를 구현합니다.
  - 잔액 충전 / 조회 API
  - 상품 조회 API
  - 주문 / 결제 API
  - 선착순 쿠폰 API
  - 인기 판매 상품 조회 API
- 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
- **(심화)** 재고 관리에 문제 없도록 구현합니다.
- **(심화)** 동시성 이슈를 고려하여 구현합니다.
- **(심화)** 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.

**API Specs - 기본과제**

1. `주요` 잔액 충전 / 조회 API
   - 결제에 사용될 금액을 충전하는 API를 작성합니다.
   - 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
   - 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

2. `기본` 상품 조회 API
   - 상품 정보 (ID, 이름, 가격, 잔여수량)을 조회하는 API를 작성합니다.
   - 조회시점의 상품별 잔여수량이 정확하면 좋습니다.

3. `주요` 선착순 쿠폰 기능
   - 선착순 쿠폰 발급 API 및 보유 쿠폰 목록 조회 API를 작성합니다.
   - 사용자는 선착순으로 할인 쿠폰을 발급받을 수 있습니다.
   - 주문 시에 유효한 할인 쿠폰을 함께 제출하면, 전체 주문금액에 대해 할인 혜택을 부여받을 수 있습니다.

4. `주요` 주문 / 결제 API
   - 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API를 작성합니다.
   - 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 합니다.
   - 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 합니다. (데이터 플랫폼이 어플리케이션 `외부`라는 가정만 지켜 작업해 주시면 됩니다)
   - 데이터 플랫폼으로의 전송 기능은 Mock API, Fake Module 등 다양한 방법으로 접근해 봅니다.

5. `기본` 상위 상품 조회 API
   - 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API를 작성합니다.
   - 통계 정보를 다루기 위한 기술적 고민을 충분히 해보도록 합니다.

**KEY POINT**
- 동시에 여러 주문이 들어올 경우, 유저의 보유 잔고에 대한 처리가 정확해야 합니다.
- 각 상품의 재고 관리가 정상적으로 이루어져 잘못된 주문이 발생하지 않도록 해야 합니다.

**STEP03 - 분석**
- 시나리오 요구사항 분석 및 문서 작성 (e.g. 시퀀스 다이어그램, ERD 등)

**STEP04 - 실행**
- Mock API 및 Swagger-API 코드 작성
- (NiceToHave) API E2E 테스트 작성해보기

---

### 내 코드와 연결

**구현됨:**

- `/d/dev/workspace/hhplus-e-commerce-spring/src/main/java/ecommerce/product/domain/Product.java`
  - `hasStock()`, `reduceStock()` — 재고 확인 및 차감 도메인 로직
  - `create()` 정적 팩토리 메서드로 생성 시 유효성 검증

- `/d/dev/workspace/hhplus-e-commerce-spring/src/main/java/ecommerce/point/domain/Point.java`
  - `charge()`, `use()`, `calculateBalance()` — 포인트 도메인 로직
  - 충전 한도 1,000,000원 상수 관리

- `/d/dev/workspace/hhplus-e-commerce-spring/src/main/java/ecommerce/order/domain/Order.java`
  - `createOrder()`, `cancelOrder()` — 주문 생성/취소 도메인 로직
  - `validateOrder()` private 검증 메서드

**미구현/부족:**
- **시퀀스 다이어그램, ERD 문서** — STEP03에서 요구했으나 확인된 파일 없음
- **Swagger-UI** — Mock API 코드와 함께 요구했으나 확인 불가
- **데이터 플랫폼 전송** — 결제 성공 시 외부 플랫폼 전송 Mock 구현 여부 미확인

---

## Chapter 2-2 서버구축 - 소프트웨어 설계

### 이 챕터의 목표 (원문)

```
1. 계층별로 코드를 나누는 이유를 이해합니다
   - Controller에 모든 로직을 넣으면 생기는 문제 경험
   - 계층을 나누면 테스트와 유지보수가 쉬워짐을 체감
   - "왜 Service가 필요한가?" 스스로 답할 수 있음

2. 의존성 방향을 일관되게 관리할 수 있습니다
   - 상위 계층이 하위 계층을 호출하는 단방향 흐름 구현
   - 순환 참조가 왜 문제인지 이해하고 해결
   - 인터페이스를 활용한 유연한 구조 설계 (선택사항)

3. 테스트 가능한 구조로 코드를 작성합니다
   - Mock을 활용한 단위 테스트 작성
   - 외부 의존성(DB, API)을 격리하여 테스트
   - 각 계층별 책임에 맞는 테스트 전략 수립
```

**과제와 연계된 목표:**

```
📋 STEP05
- 선택한 패턴으로 e-commerce/concert 시나리오 구현
- 최소 3개 이상의 계층으로 책임 분리
- 각 계층별 단위 테스트 작성

📋 STEP06
- 동시성 문제(재고, 좌석)를 고려한 설계
- 트랜잭션 처리 전략 적용
- 기존 코드의 문제점 찾고 개선
```

---

### 핵심 개념 (원문 기반)

#### 좋은 아키텍처 패턴이란?

- 지속적으로 성장 가능한 안정적인 소프트웨어를 잡기 위한 최고의 가이드라인
- 코드를 어디에 넣을지 명확한 기준을 제공하는 것
- 변경과 확장에 유연한 구조를 만드는 것
- 테스트하기 쉬운 코드를 작성할 수 있게 하는 것
- 지켜야 할 기본적인 개발 가이드라인을 잡아주는 틀

#### 왜 아키텍처가 필요한가? — 실제로 겪게 되는 문제들

**상황 1: "이 코드 어디에 넣어야 하지?"**
- 상품 조회시 조회수 증가 로직은 Service에? Repository에?
- 포인트 사용 이력 저장은 어느 계층의 책임일까?

**상황 2: "같은 로직을 여러 곳에서 써야 하는데..."**
- 포인트 차감 로직이 주문, 쿠폰 구매, 이벤트 응모에서 모두 필요
- 복사해서 붙여넣기? 공통 클래스? 어떻게 해결할까?

**상황 3: "테스트는 어떻게 하지?"**
- 외부 결제 API를 호출하는 코드를 테스트하려면?
- DB 없이 비즈니스 로직만 테스트할 수 있을까?

#### 원문에서 언급된 아키텍처 패턴들

- 레이어드 아키텍처
- 레이어드 + 인터페이스 아키텍처
- 헥사고날 아키텍처
- 클린 아키텍처

**중요 원문 경고:**
> 여러분의 프로젝트 상황에 맞는 아키텍처를 선택하고, README에 그 이유를 명확히 설명해주세요.
> 그대로 따라하지 마세요!

---

### 과제 명세

**STEP05**
- 선택한 아키텍처 패턴을 적용하여 패키지 구조 설계 및 핵심 비즈니스 로직 개발
- 각 시나리오별 필수 기능 구현 및 단위 테스트 작성
  - `e-commerce`: 상품 조회(상품 목록, 상세 정보), 주문/결제(재고 확인, 재고 차감, 결제 처리), 포인트 충전 및 사용
- 단위 테스트는 Mock/Stub을 활용하여 대상 객체/기능에 대한 의존성만 존재해야 함

**STEP06**
- 각 시나리오별 추가 기능 구현 및 단위 테스트 작성
  - `e-commerce`: 선착순 쿠폰 기능, 결제 실패 시 재고 복구 처리
- 선택한 아키텍처 패턴의 각 레이어별 책임에 대해 README에 정의하고, 책임에 위배되지 않도록 구현

---

### 내 코드와 연결

**구현됨:**

- 이커머스 프로젝트는 헥사고날 아키텍처(포트/어댑터 패턴) 적용
  - `application/port/in/` — UseCase 인터페이스 (ChargePointUseCase, PayOrderUseCase 등)
  - `application/port/out/` — Repository 포트 인터페이스
  - `adapter/in/web/` — Controller (인바운드 어댑터)
  - `adapter/out/` — JPA Repository 구현체 (아웃바운드 어댑터)
  - 분산락 테스트에서 `ChargePointUseCase`, `PayOrderUseCase`, `PlaceOrderUseCase`, `ReduceStockUseCase` 포트 인터페이스를 DI로 주입받는 구조 확인됨

**미구현/부족:**
- **README에 아키텍처 레이어별 책임 정의** — STEP06 요구사항이지만 확인 불가
- **결제 실패 시 재고 복구 처리** — 도메인 코드(`Order.java`, `Product.java`)에서 보상 로직 확인 안됨

---

## Chapter 2-3 서버구축 - 데이터베이스 기본

### 이 챕터의 목표 (원문)

- 시나리오가 요구하는 요구사항을 명확히 분석하기
- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기
- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기
- 견고하지만 유연한 서버 애플리케이션을 구축하기

**이번 챕터에서 해야 할 것:**
- DB Table 설계
- DB Index, Query Plan, Query Optimization

---

### 과제 명세

**STEP07 - Integration**
- (선택) 기존 설계된 테이블 구조의 개선이 필요한 점을 식별하고 ERD에 반영
- Infrastructure Layer 작성
- 기능별 통합 테스트 작성
- Infrastructure는 RDBMS (MySQL) 기반으로 작성합니다.

**STEP08 - DB**
- 조회 성능 저하가 발생할 수 있는 기능을 식별하고, 해당 원인을 분석하여 쿼리 재설계 / 인덱스 설계 등 최적화 방안을 제안하는 보고서 작성

---

### 내 코드와 연결

**구현됨:**
- 이커머스 프로젝트에서 Infrastructure Layer (JPA 기반 어댑터) 구현됨
- `DistributedLockIntegrationTest.java` — 통합 테스트에서 `@SpringBootTest`, `@Testcontainers` 사용. Testcontainers로 Redis 컨테이너를 띄워 실제 분산락 동작 검증

**미구현/부족:**
- **DB Index 설계 보고서** — STEP08 요구사항인 쿼리 최적화 분석 보고서 확인 안됨
- **MySQL 기반 통합 테스트** — 분산락 통합 테스트는 Redis 대상이고, MySQL(JPA) 통합 테스트 파일 별도 확인 필요

---

## Chapter 2-4 서버구축 - 데이터베이스 심화

### 이 챕터의 목표 (원문)

- 시나리오가 요구하는 요구사항을 명확히 분석하기
- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기
- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기
- 견고하지만 유연한 서버 애플리케이션을 구축하기

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
- 서버 개발 Summary
- Transaction
- DB Transaction
- 동시성 문제
- DB 동시성 문제
- Database Lock
- 우리의 시나리오에서 동시성 이슈가 발생할 수 있는 비즈니스 로직은?
- e-커머스 서비스

---

### 팀별토론 주제 (원문)

**토론 주제 1) 트랜잭션 크기는 얼마나 작게 가져가야 할까?**

트랜잭션은 원자성을 보장하기 위해 최소한의 작업 단위로 묶어야 하지만, 종종 너무 큰 트랜잭션으로 인해 DB 성능 저하 및 Lock 경합이 발생하기도 합니다. 반면, 지나치게 잘게 나누면 데이터 정합성 관리가 어려워질 수도 있습니다.

**토론 주제 2) Deadlock(교착 상태) 방지 방안**

서비스 운영 중 여러 트랜잭션이 동시에 여러 리소스(테이블, 행 등)를 점유하면서 교착 상태가 발생하는 경우가 있습니다. 특히 동시성이 높은 예약 기능이나 빈번하게 발생할 수 있습니다.

**토론 주제 3) DB 커넥션 풀(Connection Pool) 고갈**

운영 중인 서비스에 트래픽이 몰리면 커넥션 풀이 고갈되는 상황이 자주 발생합니다.
단순하게 커넥션 풀을 늘리기 전에 왜 커넥션이 부족해졌는지 파악할 필요가 있습니다. 아무런 작업이 되어있지 않다면 트래픽이 몰릴 때 애플리케이션 서버의 자원은 놀고 있고, DB만 바쁠 가능성이 있습니다.

---

### 과제 명세

**STEP09 - Concurrency**
- 동시성 문제에 대한 개념, 트랜잭션과 격리수준, DB Lock에 대한 학습을 진행
- 나의 서비스에서 발생하는 동시성 문제의 DB를 활용한 적절한 해결 방법을 선정하고 관련된 내용을 문서로 작성하여 제출
- 보고서는 문제 식별 - 분석 - 해결 등의 항목들을 기재해 주시기 바랍니다.

**STEP10 - Finalize**
- STEP09에서 정리한 동시성 문제 해결 방안을 구현하고 통합테스트로 검증
- 다음 주부터 Chapter 3(대용량 트래픽&데이터 처리)가 진행됩니다.
- 이번 주 과제와 무관하게, 구현이 미비한 기능이나 부족한 테스트가 있다면 보완해 주시기 바랍니다.

---

### 내 코드와 연결

**구현됨:**
- `DistributedLockIntegrationTest.java` — DB Lock이 아닌 Redis 분산락으로 동시성 해결. 이 테스트는 Chapter 3-1 과제(STEP11)에 더 가까운 내용임. 엄밀히 말하면 이 챕터에서 요구한 DB Lock 기반 해결 방안과는 다름

**미구현/부족:**
- **DB Lock 기반 동시성 해결 보고서** — STEP09에서 요구한 DB 활용 해결 방안 문서 확인 안됨. `@Lock(LockModeType.PESSIMISTIC_WRITE)` 등 JPA 비관적 락이나 낙관적 락 적용 여부 확인 필요
- **격리수준 분석** — 트랜잭션 격리수준에 대한 분석이 코드 어디에도 반영됐는지 확인 안됨

---

## Chapter 3-1 대용량 트래픽&데이터 처리

### 이 챕터의 목표 (원문)

- DB 트랜잭션 이상의 범위, 분산 환경에서 Lock을 적용할 수 있는 방법에 대해 고민해 봅니다.
- 다량의 트래픽을 처리하기 위해 적은 DB 부하로 올바르게 기능을 제공할 방법을 고민해 봅니다.
- 캐시 레이어의 적용을 통해 DB I/O를 줄일 방법을 고민해 봅니다.

**원문 배경:**
> 점점 늘어나는 고객과 많은 트래픽은 점점 시스템의 높은 Throughput을 요구하게 됩니다.
> 이에 RDBMS만으로는 다양한 비즈니스 가치를 달성하기 어렵습니다.
> 우리는 다양한 문제를 해결하기 위해 **REDIS** 라는 추가 선택지를 찾게 됩니다.

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
1. Distributed Lock 기반의 동시성 제어
2. Caching
3. Caching Strategy

---

### 과제 명세

**STEP11 - Distributed Lock**
- Redis 기반의 분산락을 직접 구현해보고 동작에 대한 통합테스트 작성
- 주문/예약/결제 기능 등에 **(1) 적절한 키 (2) 적절한 범위를 선정**해 분산락을 적용

**STEP12 - Cache**
- 조회가 오래 걸리거나, 자주 변하지 않는 데이터 등 애플리케이션의 요청 처리 성능을 높이기 위해 캐시 전략을 취할 수 있는 구간을 점검하고, 적절한 캐시 전략을 선정
- 위 구간에 대해 Redis 기반의 캐싱 전략을 시나리오에 적용하고 성능 개선 등을 포함한 보고서 작성 및 제출

---

### 내 코드와 연결

**구현됨:**

- `/d/dev/workspace/hhplus-e-commerce-spring/src/main/java/ecommerce/config/DistributedLockAop.java`
  - Redisson 기반 분산락 AOP 구현
  - `@DistributedLock` 어노테이션 선언 시 동작
  - `REDISSON_LOCK_PREFIX = "MY_LOCK:"` 접두사로 락 키 관리
  - `tryLock(waitTime, leaseTime, timeUnit)` — 정의된 waitTime까지 획득 시도
  - 별도 트랜잭션으로 실행: `aopForTransaction.proceed(joinPoint)` — 락 해제 전에 트랜잭션이 커밋됨을 보장
  - `finally` 블록에서 `rLock.unlock()`, `IllegalMonitorStateException` 처리 (이미 해제된 경우 로그만 남김)

- `/d/dev/workspace/hhplus-e-commerce-spring/src/test/java/ecommerce/integration/DistributedLockIntegrationTest.java`
  - `@Testcontainers` + `GenericContainer` — Redis 7 Alpine 컨테이너를 테스트 중에 실제로 띄움
  - `@DynamicPropertySource` — 컨테이너의 동적 포트를 Spring 설정에 주입
  - `CountDownLatch`, `ExecutorService`, `AtomicInteger` — 실제 동시성 시뮬레이션
  - 4가지 시나리오 검증: 포인트 충전 동시성, 재고 차감 동시성, 결제 중복 방지, 사용자별 락 키 격리

**미구현/부족:**
- **캐싱 전략 보고서** — STEP12 요구사항. 어떤 구간에 캐시를 적용했는지 문서 확인 안됨
- **캐시 적용 코드** — `@Cacheable`, `@CacheEvict` 등 Spring Cache 또는 Redisson 캐시 사용 여부 확인 필요

---

## Chapter 3-2 대용량 트래픽&데이터 처리

### 이 챕터의 목표 (원문)

- Redis의 특성에 따른 활용 방식을 고민해보고 올바른 설계로 풀어낼 방법을 고민해봅니다.
- 다량의 트래픽을 처리하기 위해 적은 DB 부하로 올바르게 기능을 제공할 방법을 고민해 봅니다.

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
1. Redis 자료구조
2. Redis 기반의 랭킹 시스템
3. Redis 기반의 구조 개선

---

### 과제 명세

**STEP 13 Ranking Design**
- 이커머스 시나리오: 가장 많이 주문한 상품 랭킹을 Redis 기반으로 개발하고 설계 및 구현

**STEP 14 Asynchronous Design**
- 이커머스 시나리오: 선착순 쿠폰발급 기능에 대해 Redis 기반의 설계를 진행하고, 적절하게 동작할 수 있도록 쿠폰 발급 로직을 개선해 제출

> 각 시스템 (랭킹, 비동기) 디자인 설계 및 개발 후 회고 내용을 담은 보고서 제출

---

### 내 코드와 연결

**구현됨:**
- `CouponIssueConsumer.java` — 쿠폰 발급 로직이 Kafka 기반으로 구현됨. 이는 STEP 14에서 시작된 비동기 쿠폰 발급 설계가 Chapter 3-4(Kafka)까지 이어진 것으로 보임
  - `@KafkaListener(topics = "coupon-issue-request")` — 토픽에서 쿠폰 발급 요청 메시지 수신
  - `IssueCouponUseCase.issueCoupon(userId, couponId)` 호출
  - 실패 시 `log.error()`만 있고 재시도/DLQ 처리 없음

**미구현/부족:**
- **Redis Sorted Set 기반 랭킹** — STEP 13 요구사항인 인기 상품 랭킹 Redis 구현 여부 확인 필요. Kafka Consumer로 구현된 쿠폰 발급은 있으나, 랭킹 로직은 코드에서 직접 확인 안됨
- **Redis 기반 선착순 쿠폰 발급** — STEP 14는 Redis 기반이었으나, 실제 코드는 Kafka로 구현됨. Redis→Kafka 전환 과정이 있었는지 불분명

---

## Chapter 3-3 대용량 트래픽&데이터 처리

### 이 챕터의 목표 (원문)

#### 이벤트를 활용한 관심사 및 트랜잭션 분리

- 현재 여러분들이 구현한 비즈니스 로직 별 트랜잭션의 범위를 파악하고 사이드 이펙트에 대해 고려해 봅니다.
- 비즈니스를 적절하게 핸들링할 수 있도록 선후관계를 파악하고, 애플리케이션 이벤트를 활용해 관심사를 분리하도록 개선해 봅니다.
- 도메인간 트랜잭션이 분리된다면 발생할 수 있는 문제와 해결하는 방법을 학습해봅시다.

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
1. 비즈니스 로직과 트랜잭션의 범위
2. 애플리케이션 이벤트를 통한 관심사 분리

---

### 과제 명세

**STEP 15 Application Event**
- 실시간 주문정보(이커머스)를 데이터 플랫폼에 전송(mock API 호출)하는 요구사항을 이벤트를 활용하여 트랜잭션과 관심사를 분리하여 서비스를 개선합니다.

**STEP 16 Transaction Diagnosis**
- 서비스의 확장에 따라 어플리케이션 서버와 DB를 도메인별로 분리했을 때, 트랜잭션 처리의 한계와 대응 방안에 대한 설계 문서 제출

(Try if you want)
- 보상트랜잭션, Saga 패턴 등 활용하여 우리의 프로젝트를 고도화 해봅시다.
- Facade 활용한다면 트랜잭션을 도메인 단위로 분리하고 발생하는 분산 트랜잭션을 올바르게 구현하기
- Facade 없이 서비스간 의존하는 구조라면 어플리케이션 이벤트를 활용하여 각 서비스 의존을 없애기

---

### 원문 QnA 핵심 내용

#### 순환참조 방지 방안 (원문)

```
단일 소스코드에서 여러 도메인을 관리할 때 순환참조가 발생하면, application 실행에 실패하죠.

1. MSA 내에서 서비스간 순환 참조는 발생하지 않는 것을 원칙으로 하지만,
   필요에 의해서 반복적인 순환 참조가 아니라면 가능할 수도 있다.

2. 어떤 비즈니스를 신규로 개발할 때 관련된 도메인의 팀에서 전체적인 아키텍처를
   함께 설계하는 방식
```

#### 이벤트 실패 시 재시도 (원문)

```
주문 및 결제 트랜잭션 커밋 완료 → 주문 정보 외부 플랫폼 전송(이벤트)

핵심 로직은 성공했는데, 부가 로직이 실패한 경우에도 원복을 위한 보상 작업이 필요할까요?

— 당연히 재시도가 필요할 것입니다.
  다만, 이번 과제에서는 포함하지 않아도 괜찮습니다.
```

#### 이벤트 유실 대응 (원문)

```
이벤트가 유실된다면?? 실패인지 성공인지 다음 또는 이전 서비스는 알 방법이 없는데
이런 경우에는 어떤 해결방식이 필요한가요?

— 비동기 아키텍처에서 꽤나 빈도가 높은 문제에요.
  Transactional Outbox Pattern, Inbox Pattern 등을 활용해요.
```

#### 핵심 비즈니스와 비동기 처리 구분 (원문)

```
유저는 주문을 하면 즉시 주문 성공 여부를 확인할 수 있어야 하기 때문에,
주문 등록 → 상품 수량 체크 → 주문 상세 등록 → 결제 등록까지는 동기적으로 처리하는 것이 적절하다.
반면, 외부 데이터 플랫폼 전송과 같이 유저가 성공 여부를 바로 확인할 필요가 없는 작업은
Message Queue를 활용한 비동기 이벤트 처리로 진행하는 것이 적합하다.
```

#### TDD와 아키텍처 변경 시 테스트코드 처리 (원문)

```
TDD에서의 테스트케이스는 지금 설계한 각각의 클래스의 책임을 검증하는 목적.
그런데, OrderFacade를 더이상 활용하지 않고 다른 클래스들을 활용하도록 아키텍처를 변경한다면,
OrderFacadeTest는 더이상 무의미해지는거죠.
```

#### 오케스트레이터 vs 코레오그래피 (원문)

분산트랜잭션 설계에서 두 가지 패턴:
- 오케스트레이터: 중앙 관리자가 각 서비스를 지휘
- 코레오그래피: 각 서비스가 이벤트를 발행하고 구독하며 자율적으로 동작

---

### 내 코드와 연결

**구현됨:**
- `CouponIssueConsumer.java`의 `@KafkaListener` — ApplicationEvent 이후 Kafka로 이벤트 처리가 이어진 구조. 외부 플랫폼(데이터 플랫폼) 전송을 별도 Consumer로 분리한 패턴과 유사

**미구현/부족:**
- **@TransactionalEventListener 기반 ApplicationEvent** — STEP 15는 Spring ApplicationEvent 사용이 출발점이었으나, 최종 코드가 Kafka Consumer로 바로 간 것인지, 중간에 ApplicationEvent 레이어가 있는지 확인 필요
- **Transactional Outbox Pattern** — 원문 QnA에서 이벤트 유실 대응으로 언급됐으나, `CouponIssueConsumer`에는 실패 시 `log.error()`만 있고 재시도나 Outbox 구현 없음
- **STEP 16 설계 문서** — 분산 트랜잭션 한계와 대응 방안 문서 확인 안됨

---

## Chapter 3-4 대용량 트래픽&데이터 처리

### 이 챕터의 목표 (원문)

- 카프카란 무엇인지, 왜 대량의 트래픽을 처리하는 서비스에서 사용하고 있는지 알아봅니다.
- 카프카를 활용해서 이벤트를 서비스 단위로 확장하고, 안정적인 이벤트 처리를 위한 방법을 알아봅니다.

**원문 배경:**
> 요즘 왜 다들 카프카, 카프카 하는 거지?
> - 대규모 실시간 데이터 스트리밍을 위한 **분산 메세징 시스템**
> - 높은 처리량 및 개발 효율을 위한 분산 시스템에서 **고가용성과 유연함**을 갖춘 연계시스템이 필요

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
- 카프카 Overview
- 비동기 메세지 통신을 통한 책임 분리
- 대용량 트래픽 프로세스 개선
- Transactional Outbox Pattern (선물 내용)

---

### 과제 명세

**STEP 17 카프카 기초 학습 및 활용**
- 실시간 주문정보(이커머스)를 카프카 메시지로 발행하도록 변경합니다.
  - 카프카에 대한 기초 개념을 학습하고 문서로 작성합니다.
  - 로컬에서 카프카를 설치하고 기본적인 기능을 수행해봅니다.
  - 어플리케이션에서 카프카를 연결하여 Producer & Consumer를 동작시켜봅니다.

**STEP 18 카프카를 활용하여 비즈니스 프로세스 개선**
- 각 프로젝트의 대용량 트래픽 프로세스를 카프카를 활용하도록 변경해봅니다.
- 개선한 내용에 대한 설계 문서(비즈니스 시퀀스 다이어그램, 카프카 구성 등)를 작성합니다.

(Try if you want)
- 어플리케이션 이벤트 기반으로 확장된 우리 서비스를 배포 모듈을 완전히 분리한다고 가정하고 카프카를 활용하도록 전환해봅시다.

---

### 원문 QnA 핵심 내용

#### 파티션과 처리량 (원문)

```
파티션을 N개로 늘리면 N개의 병렬처리가 가능하기 때문에 처리량 확장이 가능합니다.
단, 컨슈머도 동일한 수로 맞춰줄 필요는 있겠죠.
```

#### 카프카 Retention 정책 (원문)

```
카프카의 특성상 발행된 메시지를 모든 컨슈머가 읽었다면 그 이전 메시지는
활용도가 없다고 보는 것이 일반적인 카프카의 활용입니다.

따라서, 운영 관리 비용을 줄이기 위해서 카프카의 리텐션 정책을 반드시 활용하는 편입니다.
메시지가 정말 많이 발행되는 이벤트의 경우에는 리텐션 기간을 1일 정도 혹은 더 짧게 설정하기도 하고요.
일반적인 케이스에는 최대 7일에서 14일까지 보관하는 편입니다.
```

#### Redis vs Kafka 선택 기준 (원문)

```
사실 레디스든 카프카든 우리의 설계에서 적절하게 활용할 수 있다면 채택해서 사용하면 됩니다.
꼭, 레디스 단독으로 썼을 때 부하를 더 줄이기 위해서 카프카를 도입한다 => (X)
레디스 없이 카프카만을 활용하기도 해요.

선착순 쿠폰은 반드시 Queue가 필요할 것 같고, 이 큐를 메모리를 활용하는 레디스에 트래픽을 쏟아붓기보다는
카프카로 큐를 적재하는 것이 좀 더 안정성이 확보될 수는 있겠다.
```

#### Transactional Outbox Pattern (원문)

```
일반적인 서비스에서는 실시간으로 이벤트를 발행해야 하는 것이 더 일반적인 방법이기에 후자를 더 많이 사용.
다만, 데이터수집 플랫폼처럼 꼭 실시간 데이터를 반드시 보장해야하는 요구사항이 아니라면
메인 서비스의 성능을 아끼고 배치 프로세스에서 부가 로직을 수행하는 방법도 괜찮은 접근.
```

#### 카프카 Consumer 순서 보장 (원문)

```
컨슈머 1의 프로세스:
offset 101번 읽었어! 다음 메세지 줘!
102번 오프셋의 메시지를 받아와서 처리를 하겠죠. 이 프로세스를 수행한 이후에 오프셋을 커밋해요.
다음 메시지 줘!
103번 오프셋의 메시지를 받아올 수 있게 됩니다.
```

---

### 내 코드와 연결

**구현됨:**

- `/d/dev/workspace/hhplus-e-commerce-spring/src/main/java/ecommerce/coupon/application/service/CouponIssueConsumer.java`
  - `@KafkaListener(topics = "coupon-issue-request")` — coupon-issue-request 토픽 소비
  - `MessageDto`를 `CouponIssueEvent`로 역직렬화 (ObjectMapper)
  - `IssueCouponUseCase.issueCoupon(userId, couponId)` 포트 호출
  - 실패 시 `log.error()` — 재시도 없음, DLQ 없음

**미구현/부족:**
- **Producer 코드** — 쿠폰 발급 요청 메시지를 `coupon-issue-request` 토픽에 발행하는 Producer 코드 확인 필요
- **카프카 기초 학습 문서** — STEP 17 요구사항인 개념 학습 문서 확인 안됨
- **Transactional Outbox Pattern** — Consumer에 실패 시 재처리 로직 없음. 메시지 유실 시 쿠폰이 발급되지 않은 채로 끝남
- **카프카 구성 설계 문서** — STEP 18의 시퀀스 다이어그램, 파티션 수, Consumer Group 구성 등 문서 확인 안됨
- **리밸런싱 대응** — 원문 QnA에서 Stop-the-world 리밸런싱 최소화 방안이 논의됐으나 코드에 적용 여부 확인 안됨

---

## Chapter 4 장애대응

### 이 챕터의 목표 (원문)

- **애플리케이션에서 발생하는 장애를 대응하고 개선합니다.**
- 앞서 고민한 로깅과 Alert 파이프라인을 통해 실제 장애가 발생하기 전에 미리 탐지하고 이를 개선할 수 있도록 고민해봅니다. Error Tolerant한 애플리케이션 개발 관점을 생각해봅니다.
  - `Testable Code` - 변경에 유연하며 관리포인트의 집중을 통해 테스트 커버리지를 높일 수 있습니다.
  - `Code/Peer Review` - 우리는 모두 사람이기에, 항상 내 코드가 완벽하지 않을 수 있습니다. 동료가 작성한 코드를 함께 고민하고, 더 나은 애플리케이션 개발을 위해 팀 전체가 고민하며 더 좋은 소프트웨어 개발을 향해 나아갑니다.
- 장애가 발생할 수 있는 포인트들에 대해 고민하고 논의해 봅니다.
- 애플리케이션의 장애 유형과 대응방법을 고민해보고 앞서 개발한 서버를 개선할 방법을 모색해봅니다.

**이번 챕터에서 해야 할 것 (원문 섹션 제목):**
- 서론: 현업에서 장애를 감지하기 위해 하는 노력들 (모니터링, 로깅)
- STEP 01 부하 테스트
- STEP 02 장애 발생과 대응 시나리오
- 자연재해는 누가 막을 수 있을까?

---

### 과제 명세

**STEP 19**
- 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성
- 적합한 테스트 스크립트를 작성하고 수행
- (NiceToHave) Docker의 실행 옵션 (cpu, memory) 등을 조정하면서 애플리케이션을 실행하여 성능 테스트를 진행해보면서 적절한 배포 스펙 고려도 한번 진행해보세요!

**STEP 20**
- 위 테스트를 진행하며 획득한 다양한 성능 지표를 분석 및 시스템 내의 병목을 탐색 및 개선해보고 **(가상) 장애 대응 문서**를 작성하고 제출
- 최종 발표 자료 작성 및 제출

---

### 내 코드와 연결

**구현됨:**
- `DistributedLockIntegrationTest.java` — 동시성 시뮬레이션으로 부하 측면의 일부 검증. 단, k6, JMeter, Gatling 같은 전용 부하 테스트 도구는 확인 안됨
- `DistributedLockAop.java`의 `log.info()` — 락 해제 실패 시 로그 남김. 최소한의 장애 탐지 로그 존재

**미구현/부족:**
- **부하 테스트 스크립트** — k6, JMeter, Locust 등 외부 부하 테스트 도구 스크립트 확인 안됨
- **장애 대응 문서** — STEP 20 요구사항인 (가상) 장애 대응 문서 확인 안됨
- **모니터링 파이프라인** — Datadog, Prometheus, Grafana 등 모니터링 설정 확인 안됨. 원문에서 Datadog으로 각 서비스 흐름을 가시화한다는 내용이 있었으나 적용 여부 확인 안됨
- **최종 발표 자료** — STEP 20 요구사항이나 확인 안됨

---

## 전체 요약: 내가 잘한 것 vs 부족한 것

### 원문 기준으로 잘 구현된 부분

1. **헥사고날 아키텍처(포트/어댑터)** — Chapter 2-2에서 요구한 계층 분리, UseCase 인터페이스 기반 설계
2. **분산락 AOP** — Chapter 3-1 STEP11의 핵심 요구사항. Redisson 기반, 별도 트랜잭션 분리까지 구현
3. **Testcontainers 통합 테스트** — 실제 Redis 컨테이너를 띄워 동시성 테스트 수행
4. **Kafka Consumer** — Chapter 3-4 STEP17 요구사항. 쿠폰 발급 비동기 처리 구현
5. **도메인 로직의 유효성 검증** — `Point.charge()`, `Product.reduceStock()` 등 도메인 내부에서 검증 수행

### 원문 기준으로 부족한 부분

1. **TDD 순서 준수 여부** — Chapter 1-1의 빨강→초록→리팩토링 사이클을 지켰는지 확인 불가. TDD 프로젝트 코드에 TODO 주석이 많고 경계값 테스트가 미완성 상태로 남아 있음
2. **Hall of Fame 기준 미달** — Chapter 2-1 HoF: "경계값 위주의 테스트 범위 설정 및 ParameterizedTest를 통한 중복 제거". 내 TDD 코드에서 경계값 테스트가 주석 처리된 채로 남아 있고, ParameterizedTest 미사용
3. **보고서류 미작성** — STEP08(DB 인덱스), STEP09(동시성 문제 분석), STEP12(캐시 전략), STEP16(분산 트랜잭션), STEP18(카프카 설계 문서) 등 문서 과제 다수 미확인
4. **Transactional Outbox Pattern 미구현** — 이벤트 유실 대응이 없음. 원문 QnA에서 명확히 언급된 패턴
5. **부하 테스트 미수행** — Chapter 4 STEP19의 핵심 과제
