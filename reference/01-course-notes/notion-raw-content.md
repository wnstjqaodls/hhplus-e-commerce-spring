# 항해플러스 백엔드 코스 9기 — 발제 자료 & 과제

> 수집 날짜: 2026-04-05
> 출처: Notion 공개 페이지 (비공식 API)

---

## 개요

항해플러스 백엔드 코스 9기의 발제 자료, 과제, 학습 자료를 수집한 문서입니다.

### 문서 구분
- 발제 자료: 각 챕터별 강의/발제 내용
- 과제: 코드 구현 과제
- 학습자료: 참고 링크 및 추가 자료

---

## 목차

| 챕터 | 주제 |
|------|------|
| Chapter 1-1 | TDD (Test-Driven Development) |
| Chapter 2-1 | 서버구축 - 설계 |
| Chapter 2-2 | 서버구축 - 소프트웨어 설계 |
| Chapter 2-3 | 서버구축 - 데이터베이스 기본 |
| Chapter 2-4 | 서버구축 - 데이터베이스 심화 |
| Chapter 3-1 | 대용량 트래픽&데이터 처리 |
| Chapter 3-2 | 대용량 트래픽&데이터 처리 |
| Chapter 3-3 | 대용량 트래픽&데이터 처리 |
| Chapter 3-4 | 대용량 트래픽&데이터 처리 |
| Chapter 4 | 장애대응 |

---


## Chapter 1-1 TDD

> **[!]** **들어가면서, Why TDD?**

****TDD, 왜 그렇게 많이 언급 되는건지? 왜 중요할까?****

****현업에서의 TDD란?****

****여기서 우리가 TDD에 대해 알게되는 것들****

> **[!]** **이번 챕터 목표 **

- **테스트 가능한 코드와 테스트 코드 작성에 집중하며, TDD 기반 요구사항 기능 개발**

- TDD,  Testable Code, Test Code 에 대한 학습을 진행합니다.

- 기초 학습자료로 부족한 부분을 학습합니다.

- 주어진 과제를 분석하고 TDD 기반으로 개발을 진행합니다.

> **[!]** **기초 학습 자료 **

### TDD 레슨 (1)

###### TDD는 소프트웨어 설계 방법론이다.
---
  TDD는 소프트웨어 설계 **방법론**이기 때문에 너무 엄격하고 딱딱하게 접근을 하게 되면 TDD가 주는 교훈을 잘못 이해하고, 배우나마나 한 것이 되는 것 같습니다. 
  누군가는 TDD로 개발하는 것이 좋다고 생각할 수도, 누군가는 싫다고 할 수도 있습니다. 다만, 온보딩 과정에서는 목표로 해야하는 것은 TDD가 맞다,안맞다 좋다 안좋다를 생각하는 것이 아닌, **TDD를 이해해보고 경험해보는 것을 목표로 해야합니다. **

###### 테스트 시나리오 또한 언제든 바뀔 수 있다.
---
  처음에 정한 테스트 시나리오(유저 시나리오)를 바꾸지 않는 것이 Best Case 겠지만, 처음에 만든 시나리오에 종속되어 좋지 않은 구조로 소프트웨어를 개발하게 되는 것은 지양해야합니다.
  아주 완벽한 시나리오를 짜는 것은 힘들다고 생각합니다. 더구나 처음 시나리오를 짜본다면 더욱 힘든 것 같습니다. 책에서도 계속해서 테스트 해야 할 경우나 시나리오를 수정해가는 모습을 볼 수 있습니다. **우리의 목표는 좋은 소프트웨어를 설계하는 것이지, 시나리오 대로 Test Code를 만드는 일이 아닙니다.** TDD는 짧은 주기로 iteration을 도는 애자일과 가깝습니다.

###### 테스트 코드에서는 “실패” 케이스가 우선되어야 합니다.
---
  완벽하게 맞는 말은 아닐 수 있겠지만, 테스트 코드는 실패 케이스를 우선으로 한다고 생각합니다. 예를 들어, 살펴보겠습니다.
  저는 결제를 위해 사용자, 상품, 주문서, 결제서가 필요하다고 생각했고 객체를 만들고 테스트 코드를 만들었다고 해보겠습니다.
```
// 객체 예시
export default class Order{
    /*
      멤버 변수
    * 주문번호(unique), 유저아이디
    * 상품번호, 상품명, 상품가격,
    * 주문시간, 주문수량, 주문 가격
    * 배송지,
    * 상태(결제 요청, 결제 승인, 결제 취소, 환불)
    * */

    constructor(orderNo, userID, productNo, productName, productPrice, orderTime, orderCount, addr, state) {
        this._orderNo = orderNo;
        this._userID = userID;
        this._productNo = productNo;
        this._productName = productName;
        this._productPrice = productPrice;
        this._orderTime = orderTime;
        this._orderCount = orderCount;
        this._orderPrice = orderCount * productPrice;
        this._addr = addr;
        this._state = state;
    }
```
```
describe("정보를 생성할 수 있다.", ()=>{
    const user = createUser("ID", "PW", "name", "addr");
    const product = createProduct("productNo", "productName", 1000, 1, true, true);
    const order = createOrder("orderNo", user.userID, product.productNo, product.productName, product.productPrice, 1, user.userAddr, OrderState.PayRequest);
    const pay = createPay("payNo", PayMethod.Card, order.orderPrice);
    const refund = createRefund("refundNo", pay.payPrice, user.userID, pay.payMethod);

    test("사용자 정보를 생성할 수 있다", ()=>{
        expect(user).toEqual( new User("ID", "PW", "name", "addr"));
    })
    test("상품 정보를 생성할 수 있다", ()=>{
        expect(product).toEqual(new Product("productNo", "productName", 1000, 1, true, true));
    })
    test("주문 정보를 생성할 수 있다.", ()=>{
        expect(order).toEqual(new Order("orderNo", user.userID, product.productNo, product.productName, product.productPrice, order.orderTime  , order.orderCount, user.userAddr, OrderState.PayRequest));
    })
    test("결제 정보를 생성할 수 있다.", ()=>{
        expect(pay).toEqual(new Pay("payNo", PayMethod.Card, pay.payPrice, pay.payTime));
    })
    test("환불 정보를 생성할 수 있다.", ()=>{
        expect(refund).toEqual(new Refund("refundNo", pay.payPrice, user.userID, pay.payMethod, refund.refundTime))
    })
})
```
  사실 위와 같이 테스트는 크게 의미가 없다고 생각합니다. createOrder이 동작하는 걸 그대로 가져다가 Test했기 때문입니다. 위와 같은 코드를 작성할 때 테스트 그 자체에 의미를 부여하고 목적을 두면 무언가 이상하다는 느낌을 받습니다. 
  위와 같이 객체가 만들어지는 테스트 코드에서 가질 수 있는 의미는 2개 정도가 있는 것 같습니다.
  1. 기술서
    내가 남의 코드를 볼 때, 모든 코드를 보면서 이해하는 것이 아닌, 시나리오를 이해할 수 있는 테스트 코드를 통해 객체가 어떤 꼴로 생성되는지 이해해볼 수 있습니다.
  1. TDD 시작하는 코드
    TDD로 개발을 하다 보면 조금은 자연스럽게 독립적인 행위를 기반으로 설계를 하게 되는 것 같습니다. 그런 부분에서 “~~한 정보들이 필요하겠지?” 하면서 시작하는 코드 정도
  위와 같은 테스트를 하는 행위에 매몰되다 보면 왜 이런 테스트 케이스까지 해야하는지, 또 수정해야 할 코드들이 많아질 때(객체 생성 파라미터를 바꾼다거나) 일을 늘리기만 하는 느낌을 받을 수 있습니다.
```
// 성공 케이스 보다는 실패 케이스에 초점
describe("정보의 생성과 실패", ()=>{
    describe("각 모델의 정보를 생성하는 **예시**들은 다음과 같다.", ()=>{
        const user = createUser("ID", "PW", "name", "addr");
        const product = createProduct("productNo", "productName", 1000, 1, true, true);

        test("user 정보 생성 예시", ()=>{
            expect(user).toEqual(new User(user.userID, user.userPW, user.userName, user.userAddr));
        })

        test("product 정보 생성 예시", ()=>{
            expect(product).toEqual(new Product(product.productNo, product.productName, product.productPrice, product.productCount, product.isValid, product.canRefund));
        })

        test("order 정보 생성 예시", ()=>{
            const order = createOrder(user, product, 1);
            expect(order).toEqual(new Order(order.orderNo,order.userID, order.productNo, order.productName, order.productPrice, order.orderTime, order.orderCount, order.addr, order.state ))
        })

        test("pay 정보 생성 예시", ()=>{
            const order = createOrder(user, product, 1);
            const pay = createPay(user, order, PayMethod.Card);
            expect(pay).toEqual(new Pay(pay.payNo, pay.payMethod, pay.payPrice, pay.payTime));
        })

        test("refund 정보 생성 예시", ()=>{
            const order = createOrder(user, product, 1);
            const pay = createPay(user, order, PayMethod.Card);
            const refund = createRefund(user, order, pay);
            expect(refund).toEqual(new Refund(refund.refundNo, refund.refundPrice, refund.userID, refund.payMethod, refund.refundTime))
        })
    });
		
**    // 생성할 수 없는 경우에 집중!
**    describe('아래와 같은 경우 각 정보를 생성할 수 없다', ()=>{
      // 이 안에서 여러 정보를 생성하는 경우를 생각해볼 수 있습니다.
			// 정보를 생성할 수 없는 에러 케이스들은 어떤 것이 있을까 생각하면, 데이터베이스에서 Schema를 강제 한다거나
		  // 타입, 꼴을 강제한다 거나 하는 것들로 이해해볼 수 있습니다.
    });
})
```
  정보를 생성할 수 없는 에러 케이스들은 어떤 것이 있을까 생각하면, 데이터베이스에서 Schema를 강제 한다거나타입, 꼴을 강제한다 거나 하는 것들로 이해해 볼 수 있습니다.
  [또 다른 예시]
```
describe("각 status는 아래와 같은 경우 다음 status로 넘어가지 못한다.", ()=>{
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

    describe("다음과 같은 경우 '결제 승인'이 불가능하다.", ()=>{
        test("인가 실패", ()=>{
            const user = createUser("heonil1", "PW", "name", "addr");
            const product = createProduct("productNo", "productName", 1000, 100, true, true);
            const order = requestPay(user, product, 1);

            const card = createCard("cardCompany", "cardNUm", true);
            user._userID = "heonil10"; //
            expect(()=>approvePay(user, order, card, Date.now())).toThrow(AuthError);
        });
        test("카드사 점검 시간", ()=>{
            const user = createUser("heonil1", "PW", "name", "addr");
            const product = createProduct("productNo", "productName", 1000, 100, true, true);
            const order = requestPay(user, product, 1);
            const card = createCard("cardCompany", "cardNUm", true);

            expect(()=>approvePay(user, order, card, 5)).toThrow(CardError)
        });

        describe("카드사 처리 실패", ()=>{
            test("카드가 유효하지 않은 경우 결제 승인 실패", ()=>{
                const user = createUser("heonil1", "PW", "name", "addr");
                const product = createProduct("productNo", "productName", 1000, 100, true, true);
                const order = requestPay(user, product, 1);
                const card = createCard("cardCompany", "cardNUm", false);

                expect(()=>approvePay(user, order, card, Date.now())).toThrow(CardError)
            })

            test("카드의 금액이 부족한 경우 결제 승인 실패", ()=>{
                const user = createUser("heonil1", "PW", "name", "addr");
                const product = createProduct("productNo", "productName", 1000, 100, true, true);
                const order = requestPay(user, product, 1);
                const card = createCard("cardCompany", "cardNUm", true, 1);

                expect(()=>approvePay(user, order, card, Date.now())).toThrow(CardError);
            })
    });

	...
	...
	...
	...
});

```
  기본적인 로직을 완성해두고, 에러가 날 수 있는 가능한 모든 경우들을 생각해 test code를 작성하고 동작하는 코드에서 예외처리를 하는 방식으로 코드를 작성해 나갈 수 있습니다.

###### 확장 가능성 있는 코드
---
  위에서도 말했지만, 일을 2번 하는 듯한 느낌의 생성 그 자체 코드에 대해 매몰되다 보면 수정해야 할 코드들이 늘면서, 또는 테스트 코드 그 자체를 완성하기 위해 확장성 있는 코드를 놓치게 되기도 했던 것 같습니다. 
  user, product, order, pay, refund 객체들이 있는데 객체 생성에 대해서 아래와 같이 객체 생성 코드를 만들었을 때, 각 파라미터가 고정되는데 이런 코드를 테스트를 올바르게 통과시키게 하기 위해 의도적으로 이런 꼴로 만들어지게 되는 경우가 있습니다.
```
const user = createUser("ID", "PW", "name", "addr");
const product = createProduct("productNo", "productName", 1000, 1, true, true);
const order = createOrder("orderNo", user.userID, product.productNo, product.productName, product.productPrice, 1, user.userAddr, OrderState.PayRequest);
const pay = createPay("payNo", PayMethod.Card, order.orderPrice);
const refund = createRefund("refundNo", pay.payPrice, user.userID, pay.payMethod);
```
  User와 Product를 통해 order를 만드는 꼴인데 product를 생성하는 꼴이 바뀌거나 user 객체의 생성 파라미터가 바뀌거나 하면 order를 생성하는 것에 대한 모든 파라미터를 바꿔야 할 수도 있습니다. 
  그래서 확장성 있는 코드를 위해 객체 그 자체로 매개변수를 넘기고 생성 그 자체에 대한 테스트 코드에 대해서는 힘을 빼는게 좋을 것 입니다. 나중에 최종적으로 생성 그 자체에 대한 테스트 코드를 완성시켜도 될 것입니다.(어떤 꼴로 객체가 만들어지는지 기술하는 목적이 더 크다고 보기 때문)
```
createOrder(user, product, ..)
createPay(user, order, ..)
createRefund(user, order, pay, ...)
```

###### 테스트 커버리지 ⇒ 모든 경우를 테스트 하는 것이 아니다
---
  모든 함수들에 대해서 테스트 코드가 필요한 것은 아니다. 위에서도 이야기 했지만, 단순 생성의 경우는 테스트를 할 필요가 없을 수도 있다. Test Coverage를 높일 수 있으면 좋다. 다만, 100%의 Test Coverage를 가지는 것이 좋은 설계가 되는 것인지는 의문이 든다.
  - 처음에 TDD를 잘못이해하고, 모든 코드에 대해 테스트 코드를 만들다 보면 당연하게도 100%의 커버리지를 가지게 된다. 좋은건지는 .. 의문!!

###### 좋은 글들?
  - [“](https://techblog.woowahan.com/2613/)**[테스트 코드 없이 레거시 코드를 다 감수하시겠습니까?](https://techblog.woowahan.com/2613/)**
  - [http://cloudrain21.com/test-driven-development](http://cloudrain21.com/test-driven-development)

### Jest 기초 (1)

##### 기본 패턴
```
describe("그룹명", ()=>{ //describe 생략 가능
	test("테스트 설명", ()=>{
	    expect("검증 대상").toXxx("기대 결과");
	});
})
```

##### 기본 개념

###### 키워드
  - `describe()`: Jest에서 제공하는 테스트 스위트 함수, 테스트를 그룹화할 때 사용한다.
  - `test()` 또는 `it()`: 테스트를 작성할 때 사용한다. `it()` 는 `test()` 의 alias이다.
  - `expect()`: 실제 테스트를 수행할 때 예상되는 값을 검사한다. `expect()` 와 함께 다양한 Matcher함수를 사용하여 테스트를 작성할 수 있다.
  - `beforeEach()`: 각 테스트가 실행되기 전에 반복 실행될 코드를 작성한다.
  - `afterEach()`: 각 테스트가 실행된 후에 반복 실행될 코드를 작성한다.
  - `beforeAll()`: 모든 테스트가 실행되기 전에 한 번 실행될 코드를 작성한다.
  - `afterAll()`: 모든 테스트가 실행된 후에 한 번 실행될 코드를 작성한다.

###### Matcher
  - `toBe()` : 값이 정확하게 같은지 검사한다. 값의 타입까지 정확하게 검사해야하는 경우 사용한다,. 
  - `toEqual()` : 값이 동등한지 검사한다. 객체 또는 배열과 같은 데이터 구조의 값들을 비교할 때 사용한다.
    > **[!]** `toBe()`와 `toEqual()`의 차이
원시적인 타입(number, boolean, string, null)을 사용하면 큰 차이가 없다. 하지만 객체 또는 배열의 경우 차이가 있다.
`toBe()`는 비교하는 두 자료의 참조를 비교하고 `toEqual()`은 비교하는 두 자료의 값들을 비교한다.
      객체의 경우,
      `toBe()`는 두 개의 변수나 객체가 동일한 객체인지를 확인하기 위해 사용된다. 즉, 메모리 상에서 동일한 위치에 저장된 값을 가리키는 경우에만 참으로 판별된다. 자바스크립트에서 `===` 연산자를 사용하여 비교하는 것과 유사하다.
      반면에, `toEqual` 함수는 두 개의 변수나 객체가 동일한 값을 가지는지를 확인하기 위해 사용된다. 이 함수는 객체 내부의 모든 속성 및 값이 일치하는 경우에 참으로 판별된다. 자바스크립트에서 `==` 연산자를 사용하여 비교하는 것과 유사하다.
      예를 들어, 다음과 같다.
```
const obj1 = { a: 1, b: 2 };
const obj2 = { a: 1, b: 2 };
expect(obj1).toBe(obj2); // fail
expect(obj1).toEqual(obj2); // pass
```
      `toBe()`로 비교했을 때 false인 이유는 두 객체가 메모리 상에서 서로 다른 위치에 저장되기 때문이다. 반면에 `toEqual()`로 비교했을 때 두 객체의 속성 값이 모두 같으므로 true를 반환한다.
예를 들어, `expect({a: 1, b: 2}).toEqual({b: 2, a: 1})`과 같이 객체의 프로퍼티 순서가 다르더라도 값이 일치하면 테스트가 통과된다.
      배열의 경우에도 마찬가지이다.
```
test('toBe test', () => {
  const a = [1, 2, 3];
  const b = [1, 2, 3];
  expect(a).toBe(b); // fail
});

test('toEqual test', () => {
  const a = [1, 2, 3];
  const b = [1, 2, 3];
  expect(a).toEqual(b); // pass
});
```
      `toBe()`는 a와 b의 참조가 다르기 때문에 false를 반환하고 `toEqaul()`은 값을 비교하여 두 배열의 값이 같으므로 true를 반환한다.
  - `toStrictEqaul()` : `toEqual()`과 같지만 특정 요소에 undefined가 포함되는 것을 허용하지 않는다.
  - `not.toBe()` : 값이 다른지 검사한다.
  - `not.toEqual()` : 값이 동등하지 않은지 검사한다.
  - `toBeTruthy()` : 값이 truthy한 값인지 검사한다.
  - `toBeFalsy()` : 값이 falsy한 값인지 검사한다.
    > **[!]** 자바스크립트 규칙에 의해 다음과 같은 값들은 falsy한 값으로 간주된다.
`false`, `0`, `''(빈 문자열)`, `undefined`, `NaN`
이 외 나머지 값들은 모두 truthy한 값으로 간주된다.
  - `toThrow()` : 예외를 던지는 함수를 검사한다.
  - `toContain()` : 값이 배열이나 문자열 안에 포함되어 있는지 검사한다.
  - `not.toContain()` : 값이 배열이나 문자열 안에 포함되어 있는지 검사한다.
  - `toBeDefined()` : 값이 정의되어 있는지 검사한다.
  - `toBeUndefined()` : 값이 정의되어 있지 않은지 검사한다.
  - `toBeNull()` : 값이 null인지 검사한다.
  - `toBeGreaterThan()` : 값이 주어진 값보다 큰지 검사한다.
  - `toBeGreaterThanOrEqual()` : 값이 주어진 값보다 크거나 같은지 검사한다.
  - `toBeLessThan()` : 값이 주어진 값보다 작은지 검사한다.
  - `toBeLessThanOrEqual()` : 값이 주어진 값보다 작거나 같은지 검사한다.
  - `toHaveBeenCalledTimes()` : 함수가 호출된 횟수를 검사한다.
  - `toHaveBeenCalledWith()` : 함수가 특정 인수와 함께 호출되었는지 검사한다.

###### 실습
  천천히 따라치면서 익숙해져보아요
  1. initialize
```
npm init -y
```
  1. Jest 라이브러리 설치
```
npm i -D jest
```
  1. package.js의 test 스크립트 수정
```
"script": {
   "test": "jest"
},
```
  1. ES6문법으로 Jest 사용하기
    babel 을 사용해서 ES6 문법을 이전 문법으로 바꿔주는 preset을 사용
```
npm install @babel/preset-env
//(혹은)
yarn add @babel/preset-env
```
    이후 현재 TDD를 진행하고 있는 디렉토리에서 ‘.babelrc’ 파일을 만들어준다. 
    _[이미지]_
    해당 파일에 아래 코드를 추가
```
{
  "presets": ["@babel/preset-env"]
}
```
    import 및 export 구문을 Jest 에서도 사용 가능
```
import User, {get_user_name, get_user_selection, is_basket_empty, is_product_in_basket} from '../logics/func'
```
  1. 실습코드
    1) Matcher 기초 실습 코드
```
test('Matchers test', () => {
  // toBe
  expect(1 + 2).toBe(3);
  expect('Hello' + ' ' + 'World').toBe('Hello World');
  expect([1, 2, 3]).not.toBe([1, 2, 3]);
})

  // toEqual
	test('object assignment', () => {
	  const data = {one: 1};
	  data['two'] = 2;
	  expect(data).toEqual({one: 1, two: 2});
		expect({ name: 'John', age: 30 }).toEqual({ age: 30, name: 'John' });
	  expect([1, 2, 3]).toEqual([1, 2, 3]);
	  expect([1, 2, 3]).not.toEqual([1, 3, 2]);
	});
  
  // toBeNull
	test('null', () => {
	  const n = null;
	  expect(n).toBeNull();
	  expect(n).toBeDefined();
	  expect(n).not.toBeUndefined();
	  expect(n).not.toBeTruthy();
	  expect(n).toBeFalsy();
	  expect(undefined).not.toBeNull();
	});

	test('zero', () => {
	  const z = 0;
	  expect(z).not.toBeNull();
	  expect(z).toBeDefined();
	  expect(z).not.toBeUndefined();
	  expect(z).not.toBeTruthy();
	  expect(z).toBeFalsy();
	});
  
  // toBeUndefined
	test('undefined', ()=>{
	  let b;
	  expect(b).toBeUndefined();
	  expect(null).not.toBeUndefined();
	})

  // toBeTruthy / toBeFalsy
	test('truthy falsy', ()=>{
	  expect('hello').toBeTruthy();
	  expect('').toBeFalsy();
	  expect(0).toBeFalsy();
		expect("0").toBeTruthy();
	  expect(1).toBeTruthy();
	})

  // toContain
	test('contain',()=>{
	  expect('Hello World').toContain('World');
	  expect([1, 2, 3]).toContain(2);
	  expect([1, 2, 3]).not.toContain(4); 
	})

  // toMatch
	test('match', ()=>{
	  expect('hello@test.com').toMatch(/\w+@\w+\.\w+/);
	  expect('123-456-7890').toMatch(/\d{3}-\d{3}-\d{4}/);
	  expect('Hello World').not.toMatch(/\d+/);
	})
});
```
    2) toBe() 기본 실습 코드
```
//stringFunctions.js 파일 생성

function reverseString(str) {
  return str.split("").reverse().join("");
}

function capitalizeString(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export { reverseString, capitalizeString };
```
```
//stringFunction.test.js 파일 생성

import { reverseString, capitalizeString } from "./stringFunctions";

//describe로 전체 그룹화
describe("stringFunctions", () => {
	//describe로 소그룹 구분
  describe("reverseString", () => {
    test("reverse a string", () => {
      expect(reverseString("hello")).toBe("olleh"); //pass
    });

    test("reverse an empty string", () => {
      expect(reverseString("")).toBe(""); //pass
    });
  });

	//describe로 소그룹 구분
  describe("capitalize String", () => {
    test("capitalizes the first letter of a string", () => {
      expect(capitalizeString("hello")).toBe("Hello"); //pass
    });

    test("does not modify an already capitalized string", () => {
      expect(capitalizeString("Hello")).toBe("Hello"); //pass
    });

    test("capitalizes the first letter of a one-letter string", () => {
      expect(capitalizeString("h")).toBe("H"); //pass
    });

    test("does not modify an empty string", () => {
      expect(capitalizeString("")).toBe("");//pass
    });
  });
});
```
    3) beforeEach, afterEach, beforeAll, afterAll, toThrow 예제 코드
```
let counter = 0;

beforeAll(() => {
  console.log('beforeAll');
});

beforeEach(() => {
  counter++;
  console.log(`beforeEach ${counter}`);
});

afterEach(() => {
  console.log(`afterEach ${counter}`);
});

afterAll(() => {
  console.log('afterAll');
});

test('Counter should be incremented', () => {
  expect(counter).toBe(1);
});

test('Counter should be incremented again', () => {
  expect(counter).toBe(2);
});

test('toThrow test', () => {
  function throwError() {
    throw new Error('Error');
  }

  expect(throwError).toThrow();
});
```
```
//결과
beforeAll
beforeEach 1
  ✓ Counter should be incremented (1 ms)
afterEach 1
beforeEach 2
  ✓ Counter should be incremented again (1 ms)
afterEach 2
  ✓ toThrow test (1 ms)
afterAll
```

##### Mocking
    **Mocking이란?**
    Jest에서 가장 많이 사용되는 기능 중 하나로 테스트할 때 실제 객체 대신에 테스트용으로만든 가짜 객체를 사용하는 것을 의미한다. Moking을 사용하면 테스트에서 의존성을 제거하고 코드를 격리시켜서 테스트를 더 쉽고 빠르게 수행할 수 있다. 따라서 Mocking은 테스트의 효율성과 신뢰성을 높이는 데 유용한 기술이다.
    **Mocking의 목적**
    1. **의존성이 있는 코드의 동작을 검증하고자 할 때 사용한다.**
      해당 코드가 의존하는 부분을 직접 생성하기가 어려운 경우 의존성을 대체하는 가짜 객체를 사용하여 테스트에 필요한 동작을 수행한다.
      예시)
      > **[!]** **데이터베이스에서 데이터를 삭제하는 코드에 대한 단위 테스트를 작성할 때**, 실제 데이터베이스를 사용한다면 여러 가지 문제점이 발생할 수 있습니다.
        - 데이테베이스 접속과 같이 Network이나 I/O 작업이 포함된 테스트는 실행 속도가 현저히 떨어질 수 밖에 없습니다.
        - 프로젝트의 규모가 켜져서 한 번에 실행해야 할 테스트 케이스가 많이지면 이러한 작은 속도 저하들이 모여 큰 이슈가 될 수 있으며, CI/CD 파이프라인의 일부로 테스트가 자동화되어 자주 실행되야 한다면 더 큰 문제가 될 수 있습니다.
        - 테스트 자체를 위한 코드보다 데이터베이스와 연결을 맺고 트랜잭션을 생성하고 쿼리를 전송하는 코드가 더 길어질 수 있습니다. 즉, 배보다 배꼽이 더 커질 수 있습니다.
        - 만약 테스트 실행 순간 일시적으로 데이터베이스가 오프라인 작업 중이었다면 해당 테스트는 실패하게 됩니다. 따라서 테스트가 인프라 환경에 영향을 받게됩니다. (non-deterministic)
        - 테스트가 종료 직 후, 데이터베이스에서 변경 데이터를 직접 원복하거나 트렌잭션을 rollback 해줘야 하는데 상당히 번거로운 작업이 될 수 있습니다.
        무엇보다 이런 방식으로 테스트를 작성하게 되면 특정 기능만 분리해서 테스트하겠다는 단위 테스트(Unit Test)의 근본적인 사상에 부합하지 않게 됩니다.
      mocking은 이러한 상황에서 실제 객체인 척하는 가짜 객체를 생성하는 매커니즘을 제공합니다. 또한 테스트가 실행되는 동안 가짜 객체에 어떤 일들이 발생했는지를 기억하기 때문에 가짜 객체가 내부적으로 어떻게 사용되는지 검증할 수 있습니다. 결론적으로, mocking을 이용하면 실제 객체를 사용하는 것보다 훨씬 가볍고 빠르게 실행되면서도, 항상 동일한 결과를 내는 테스트를 작성할 수 있습니다.
      > **[!]** **의존성 있는 코드란?
**특정 코드 또는 모듈이 다른 코드 또는 모듈에 의존하여 동작하는 경우를 말한다.
ex) 데이터베이스에 접근하는 코드, 외부 API를 호출하는 코드, 파일을 읽고 쓰는 코드 등은 모두 외부에 의존성이 있는 코드이다. 이러한 코드를 테스트할 때, 실제 데이터베이스나 외부 API를 사용하여 테스트를 수행하게 되면 테스트가 실패하거나, 실행시간이 느려지거나, 테스트 데이터를 준비하기 어려운 등의 여러가지 문제가 발생할 수 있다.
따라서 mocking을 통해 가짜 객체를 만들어 사용하면 테스트를 보다 쉽게 수행할 수 있다. mock 객체는 실제 의존성이 있는 코드와 비슷한 동작을 수행하면서도 외부 의존성을 대체하여 테스트를 수행하는 것이다.
    1. **의존성을 가진 코드와 독립적으로 테스트를 수행할 수 있다.**
    1. **다양한 시나리오를 쉽게 테스트할 수 있다.**
    **Mock 객체 생성 방법**
    1. `jest.fn()`함수 사용
    1. `jest.mock()` 함수 사용
    1. `jest.spyOn()` 
    **MockFn의 메서드**

### JUnit With Kotlin 

###### JUnit5 를 이용한 테스트 작성을 위해 참고할 만한 자료들을 모아 놓았습니다 🙂

##### Kotlin docs for JUnit
   [https://kotlinlang.org/docs/jvm-test-using-junit.html](https://kotlinlang.org/docs/jvm-test-using-junit.html)

##### Beginners guide for Kotlin Unit Testing
  [https://www.lambdatest.com/learning-hub/kotlin-unit-testing](https://www.lambdatest.com/learning-hub/kotlin-unit-testing)

##### Use JUnit5 to Test Kotlin
  [https://www.waldo.com/blog/junit5-to-test-kotlin](https://www.waldo.com/blog/junit5-to-test-kotlin)

##### JUnit5 with Kotlin for Java developers
  [https://www.arhohuttunen.com/junit-5-kotlin/](https://www.arhohuttunen.com/junit-5-kotlin/)

### **NestJS 단위 테스트: 예제가 포함된 방법 가이드** (1)
  _[이미지]_
  _[이미지]_
  이 튜토리얼은 NestJS의 단위 테스트(테스트 더블을 사용한 모의 포함)에 대해 자세히 설명합니다.
  이 튜토리얼을 최대한 활용하려면 코딩과 함께 `**npm run test:watch**`로컬로 실행하여 우리가 작성한 테스트가 실제로 실행되는 모습을 확인하는 것이 좋습니다!
  이 튜토리얼의 코드를 확인하려면 **[Github repo를](https://github.com/tomwray13/nestjs-unit-testing)** 참조하세요 .
  준비가 됐나요? 갑시다!
  _[이미지]_

##### **단위 테스트란 무엇입니까?**
  단위 테스트는 작은 동작을 확인하는 코드의 자동화입니다.
  올바르게 구현되면 단위 테스트는 탁월한 투자 수익을 얻을 수 있습니다.
  단위 테스트를 추가하면 새로운 기능을 추가하거나 기존 코드를 리팩터링할 때 자신(또는 프로젝트에 참여하는 다른 동료)의 미래 버전이 빠르고 자신 있게 작업할 수 있도록 프로젝트에 투자하게 됩니다.
  단위 테스트도 격리되어야 합니다. 즉, 테스트가 작동하기 위해 다른 종속성에 의존하지 않는다는 의미입니다.
  제 생각에는 이러한 격리 문제를 처리하는 것이 NestJS에서 단위 테스트를 작성하는 데 가장 어려운 부분이므로 이 튜토리얼에서는 많은 예제를 다룰 것입니다.

##### **NestJS에서 좋은 단위 테스트를 만드는 것은 무엇입니까?**
  단위 테스트가 무엇인지 이해하고 나면 대다수의 개발자가 단위 테스트가 훌륭한 아이디어라고 생각한다고 말하는 것이 타당하다고 생각합니다.
  그러나 제대로 구현되지 않은 단위 테스트는 자산보다는 책임이 더 커질 수 있습니다.
  다음은 이 튜토리얼 전체의 예제에서 구현될 몇 가지 규칙입니다.
  - Alignment-Act-Assert 패러다임에 따라 각 테스트를 작게 유지하십시오.
  - 최종 결과/행동에 집중
  - 구현 세부 사항을 테스트하지 않음으로써 취약성 방지
  이 주제에 대한 책 전체가 작성되었으므로(나는 단위 테스트에 관한 **[Vladimir의 책을](https://enterprisecraftsmanship.com/book/)** 추천합니다 ), 좋은 단위 테스트를 만드는 방법에 대한 자신의 의견을 형성하려면 더 많은 책을 읽어야 합니다.

##### **간단한 CRUD 예(모의 없음)**
  NestJS의 단위 테스트뿐만 아니라 단위 테스트를 실습으로 사용하는 완전 초보자라면 이 섹션이 적합합니다! 테스트 더블과 모의에 대해 배우고 싶다면 다음 섹션으로 이동하세요.
  우리는 트윗을 처리하기 위한 몇 가지 기본 CRUD 기능을 갖춘 서비스를 구축한 다음 이에 대한 몇 가지 단위 테스트를 작성할 것입니다.
  다음과 같은 모듈을 추가하는 것부터 시작해 보겠습니다 `**tweets**`.
  `nest g module tweets`
  그런 다음 트윗 서비스를 추가합니다.
  `nest g service tweets`
  이 2개의 명령을 실행하면 내부에 3개의 파일이 포함된 디렉터리가 생성됩니다 `**tweets**`.
  `src / tweets / tweets.module.ts
tweets.service.spec.ts
tweets.service.ts`
  NestJS가 우리를 위해 생성한 테스트 파일을 엽니다. 파일 `**tweets.service.spec.ts**`은 다음과 같습니다.
  **트윗.service.spec.ts**
  `import { Test, TestingModule } from '@nestjs/testing';
import { TweetsService } from './tweets.service';

describe('TweetsService', () => {
  let service: TweetsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TweetsService],
    }).compile();

    service = module.get<TweetsService>(TweetsService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});`
  아직 수행하지 않았다면 다음 명령을 사용하여 로컬에서 테스트를 실행하고 있는지 확인하세요.
  `npm run test:watch`
  터미널은 다음을 출력해야 합니다.
  _[이미지]_
  _[이미지]_
  잘 하셨어요! 이제 단위 테스트가 핫 리로딩으로 실행됩니다. 코드를 변경하면 테스트가 다시 실행됩니다.
  더 많은 테스트를 작성하기 전에 자동 생성된 NestJS 테스트 파일을 살펴보고 무슨 일이 일어나고 있는지 살펴보겠습니다.
  우선 파일명에 가 포함되어 있으므로 `**spec.ts**`Jest **[(](https://jestjs.io/)** NestJS에서 사용하는 테스트 프레임워크)가 자동으로 테스트를 선택합니다. 프로젝트의 다른 파일은 `**spec.ts**`Jest에 의해 선택됩니다.
  파일 자체 내에서는 블록으로 시작됩니다 `**describe**`.
  **트윗.service.spec.ts**
  `describe('TweetsService', () => {
  `_`// ...`_`});`
  블록 의 목적은 `**describe**`관련 테스트를 그룹화하는 것이므로 여기서는 `**TweetsService**`.
  다음으로 후크가 있습니다 `**beforeEach**`.
  **트윗.service.spec.ts**
  `import { Test, TestingModule } from '@nestjs/testing';
import { TweetsService } from './tweets.service';

describe('TweetsService', () => {
  let service: TweetsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TweetsService],
    }).compile();

    service = module.get<TweetsService>(TweetsService);
  });

  `_`// ...`_`});`
  후크 `**beforeEach**`는 각 테스트를 실행하기 전에 발생해야 하는 모든 설정 작업을 처리합니다.
  따라서 `**beforeEach**`여기서 후크가 수행하는 작업은 NestJS 내장 `**Test**`클래스를 사용하여 격리된 NestJS 런타임을 생성하는 것입니다(따라서 종속성 주입과 같은 모든 NestJS 동작을 얻을 수 있습니다).
  이 런타임은 클래스를 사용할 때 정의한 내용으로 제한됩니다 `**Test**`. 위의 예에서는 `**TweetsService**`.
  따라서 이 설정을 통해 `**TweetsService**`.
  자동 생성된 NestJS 테스트 파일에서 검토할 마지막 부분은 테스트입니다!
  다음과 같은 테스트가 표시됩니다 `**it should be defined**`.
  **트윗.service.spec.ts**
  `import { Test, TestingModule } from '@nestjs/testing';
import { TweetsService } from './tweets.service';

describe('TweetsService', () => {
  `_`// ...`_`it('should be defined', () => {
    expect(service).toBeDefined();
  });
});`
  `**expect**`Jest의 함수를 사용하는 것은 **[어설션](https://en.wikipedia.org/wiki/Test_assertion)** 입니다 .
  모든 `**expect**`기능에는 특정 조건이 충족되는지 확인하는 또 다른 방법이 연결되어 있습니다.
  이 예에서는 입니다 `**toBeDefined()**`. 따라서 이 테스트에서는 가 정의되었는지 확인합니다 `**TweetsService**`.
  단위 테스트에 대한 좀 더 구체적인 예를 살펴보겠습니다.
  파일 내부에 `**tweets.service.ts**`몇 가지 CRUD 메서드를 추가합니다.
  **트윗.service.ts**
  `import { Injectable } from '@nestjs/common';

@Injectable()
export class TweetsService {
  tweets: string[] = [];

  createTweet(tweet: string) {
    if (tweet.length > 100) {
      throw new Error(`Tweet too long`);
    }
    this.tweets.push(tweet);
    return tweet;
  }

  updateTweet(tweet: string, id: number) {
    const tweetToUpdate = this.tweets[id];
    if (!tweetToUpdate) {
      throw new Error(`This Tweet does not exist`);
    }
    if (tweet.length > 100) {
      throw new Error(`Tweet too long`);
    }
    this.tweets[id] = tweet;
    return tweet;
  }

  getTweets() {
    return this.tweets;
  }

  deleteTweet(id: number) {
    const tweetToDelete = this.tweets[id];
    if (!tweetToDelete) {
      throw new Error(`This Tweet does not exist`);
    }
    const deletedTweet = this.tweets.splice(id, 1);
    return deletedTweet;
  }
}`
  이 예제를 단순하게 유지하기 위해 이라는 클래스에 공개 필드를 생성하여 상태가 메모리에서 처리되는 것을 확인할 수 있습니다 `**tweets**`.
  트윗 서비스의 첫 번째 메소드에 대해 테스트해야 할 사항을 고려하는 것부터 시작해 보겠습니다 `**createTweet**`.
  **트윗.service.ts**
  `import { Injectable } from '@nestjs/common';

@Injectable()
export class TweetsService {
  tweets: string[] = [];

  createTweet(tweet: string) {
    if (tweet.length > 100) {
      throw new Error(`Tweet too long`);
    }
    this.tweets.push(tweet);
    return tweet;
  }
}`
  어떤 테스트를 작성해야 할지 파악하는 데 도움이 되도록 다음 질문을 스스로에게 물어볼 수 있습니다.
  "여기서 의도된 동작은 무엇입니까? 코드가 내려갈 수 있는 경로가 여러 개 있습니까?"
  방법 에 대한 다음 질문에 답해 보겠습니다 `**createTweets**`.
  1. 유효한 트윗이 생성되면 해당 트윗이 상태에 추가됩니다.
  1. 유효한 트윗이 생성되면 메서드는 해당 트윗을 반환합니다.
  1. 길이가 100자를 초과하는 트윗은 허용되지 않습니다.
  좋아요, 이제 이 3가지 시나리오를 다루기 위한 몇 가지 테스트 작성을 시작하겠습니다!
  파일 에 `**tweets.service.spec.ts**`첫 번째 테스트를 추가합니다.
  **트윗.service.spec.ts**
  `import { Test, TestingModule } from '@nestjs/testing';
import { TweetsService } from './tweets.service';

describe('TweetsService', () => {
  let service: TweetsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TweetsService],
    }).compile();

    service = module.get<TweetsService>(TweetsService);
  });

  describe('createTweet', () => {
    it('should create tweet', () => {
      `_`// Arrange`_`
      service.tweets = [];
      const payload = 'This is my tweet';

      `_`// Act`_`const tweet = service.createTweet(payload);

      `_`// Assert`_`expect(tweet).toBe(payload);
      expect(service.tweets).toHaveLength(1);
    });
  });
});`
  이 테스트에서 무슨 일이 일어나고 있는지 분석해 보겠습니다.
  1. **배열**
    : 테스트 전에 페이로드를 변수에 넣어 약간의 설정을 완료했습니다.
  1. **Act**: Call the `**createTweet**` method, the bit of behavior we are testing
  1. **Assert**: Declare the intended outcome. Here we've checked that the `**createTweet**` method returns the tweet that was passed into the payload. We've also tested that the in-memory state has been updated with the new tweet.
  So we've now covered 2 of the 3 scenarios:
  1. When a valid tweet is created, it adds the tweet to state
  1. When a valid tweet is created, the method returns the respective tweet
  1. A tweet greater than 100 characters in length should not be allowed
  Let's add another test to handle the 3rd scenario!
  **tweets.service.spec.ts**
  `import { Test, TestingModule } from '@nestjs/testing';
import { TweetsService } from './tweets.service';

describe('TweetsService', () => {
  let service: TweetsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TweetsService],
    }).compile();

    service = module.get<TweetsService>(TweetsService);
  });

  describe('createTweet', () => {
    it('should create tweet', () => {
      `_`// ...`_`});

    it('should prevent tweets created which are over 100 characters', () => {
      `_`// Arrange`_`const payload =
        'This is a long tweet over 100 characters This is a long tweet over 100 characters This is a long t...';

      `_`// Act`_`const tweet = () => {
        return service.createTweet(payload);
      };

      `_`// Assert`_`expect(tweet).toThrowError();
    });
  });
});`
  Let's break down this test:
  1. **Arrange**: We've done a bit of setup before the test by putting the payload in a variable
  1. **Act**: Call the `**createTweet**` method inside a `**tweet()**` function.
  1. **Assert**: Declare the intended outcome. Here we've checked that the `**createTweet**` method throws an error with the payload passed in
  The **[Arrange-Act-Assert](https://automationpanda.com/2020/07/07/arrange-act-assert-a-pattern-for-writing-good-tests/)** is a good pattern to follow when writing tests. It helps keep the tests structured.
  I won't include the Arrange-Act-Assert comments in any of the following examples, but I will follow this pattern so try to keep an eye out for it.
  Time to practice your unit testing skills! Go ahead and write unit tests for the other methods in the `**TweetsService**`. You can compare your work with the tests I've added in the **[Github repo](https://github.com/tomwray13/nestjs-unit-testing)**.
  Once you've done that, let's move on to the next part of this tutorial.
  _[이미지]_
  **Free NestJS Course**Want to use NestJS to it's full potential and understand how it really works? Check out my free course which covers concepts like Dependency Injection, IoC Containers and more:**Your email addressGet free course**

##### **Mocking with test doubles in NestJS**

_(참고자료) 학습 관련 링크_

- [JPA 에 대한 이해 ( 영상 자료 )](https://www.youtube.com/watch?v=WnYPdkNSLy8&themeRefresh=1)
- [JUnit 3일 안에 배우기](https://www.guru99.com/ko/junit-tutorial.html)
- [nestjs + jest 로 unit test 따라하기](https://www.tomray.dev/nestjs-unit-testing)

> **[!]** **학습자료 : 이번 챕터에 해야 할 것. 이것만 집중하세요!**


ℹ️ **Test Pyramid**
  _[이미지]_
  `**Unit Testing**`
  - 대상 : 단일 기능 혹은 작은 단위의 함수/객체 등
  - 가벼운 비용으로 새로운 기능 혹은 개선이 기존의 rule 을 위배하지 않는지 점검
  `**Integration Testing**`
  - 대상 : 서로 다른 module / system 의 상호작용
  - 맞물려 돌아가는 기능이 모여 정상적으로 원하는 기능을 제공하는지 점검
  `**End-to-End Testing**`
  - 대상 : 전체 애플리케이션의 흐름
  - 애플리케이션이 제공하는 기능을 사용자 시나리오 기반으로 문제 없는지 점검

ℹ️ **Test Double**
  **테스트 더블**은 실제 컴포넌트를 대체할 수 있도록 하는 대역이다.
  실제 컴포넌트에 대해 행동을 모방하고, 이를 통해 기존의 강한 결합도를 낮추고 테스트 중 제어 가능하도록 한다.
  `Mock`
  - 테스트를 위해 특정 기능에 대해 정해진 응답을 제공하는 객체
  - 입력과 상관없이 **어떤 행동 **을 할 지에 초점을 맞춘 객체
  - **Mock Library** 를 통해 특정 행동에 대한 출력을 정의
  (아래는 예시를 돕기 위한 pseudo-code 입니다.)
```
public class PaymentService {

    private final PayClient payClient;
    
    public boolean pay(final Long amount) {
        if(amount >= 10000) {
            payClient.pay(amount);
            return true;
        }
        
        return false;
    }
}

class PaymentServiceTest {

    @Inject
    private PaymentService paymentService ;
    
    @Mock
    private PayClient payClient;
    
    @Test
    void mock() {
        paymentService.pay(1000);
        
        verify(payClient, times(0)).pay(1000);
    }
}
```
  `**Stub**`
  - 테스트에 필요한 호출에 대해 미리 준비된 응답을 제공하는 객체
  - 입력에 대해 **어떤 상태** 를 반영하는 지에 초점을 맞춘 객체
  - **Interface** 기반으로 테스트에서 보고자하는 (혹은 필요로 하는) 구현에 집중한 구현체를 정의
  (아래는 예시를 돕기 위한 pseudo-code 입니다.)
```
public class MemberService {

    private final MemberRepository memberRepository;
    
    public boolean isRegistered(final String email) {
        return memberRepository.existsByEmail(email);
    }
}

class MemberServiceTest {

    @Inject
    private MemberService memberService;

    @Stub
    private MemberRepository memberRepository;

    @Test
    void stub() {
        String email = "hanghae@gmail.com;
        
        when(memberRepository.existsByEmail(email))
            .thenReturn(true);
        
        boolean result = memberService.isRegistered(email);

        // then
        ç(result).isTrue();
    }
}
```

ℹ️ 고전파(Classicist) vs 런던파(London School 또는 Mockist)

- `**고전파(Classicist)**`** : **협력자로서 실제 객체를 사용하고자 하는 집단

- `**런던파(London School 또는 Mockist)**`** : **테스트 대역을 사용하고자 하는 집단

```
class Point {
 
    private Member member;  
    private int amount;
   
    public Point(Member member) {
        this.member = member;
        this.amoutn = 0;
    }

    public boolean charge(int amount) {
        if (member.isChild()) {
            return false;
        }
        
        this.amount += amount;
        return true;
    }
}

class PointTest {

    @Test
    void Adult일경우_충전성공() {
        Member member = new Member(age = 29);
        
        Point point = new Point(member);

        point.charge(member) shouldBe true;
    }

    @Test
    void Child일경우_충전실패() {
        // 대상이 child가 아닐 경우
        Member member = mock(Member.class);
        when(member.isChild()).thenReturn(true);

        Point point = new Point(member);

        point.charge(member) shouldBe false;
    }
}
```

ℹ️ FIRST principle

- Fast: 테스트는 빠르게 동작하여 자주 돌릴 수 있어야 한다.

- Independent: 각각의 테스트는 독립적이며 서로 의존해서는 안된다.

- Repeatable: 어느 환경에서도 반복 가능해야 한다.

- Self-Validating: 테스트는 성공 또는 실패로 bool 값으로 결과를 내어 자체적으로 검증되어야 한다.

- Timely: 테스트는 적시에 즉, 테스트하려는 실제 코드를 구현하기 직전에 구현해야 한다.

ℹ️ What is TDD?

- TDD
  - 테스트 코드를 먼저 작성하는 개발 방법론

- TDD의 장점
  - 깔끔한 코드를 작성할 수 있다.
  - 장기적으로 개발 비용을 절감할 수 있다.
  - 개발이 끝나면 테스트 코드를 작성하는 것은 매우 귀찮다. 실패 케이스면 더욱 그렇다.

- TDD 개발 방법 및 순서
  1. 빨강: 실패하는 작은 테스트를 작성한다. 처음에는 컴파일조차 되지 않을 수 있다.
  1. 초록: 빨리 테스트가 통과하게끔 만든다. 이를 위해 어떠한 죄악(함수가 무조건 특정 상수만을 반환하는 등)을 저질러도 좋다.
  1. 리팩토링: 일단 테스트를 통과하게만 하는 와중에 생겨난 모든 중복을 제거한다.

_[이미지]_

ℹ️ How to TDD By Example

### How to TDD By Example (1)
  - 요구사항
    - 포인트 충전 기능을 구현하라
    - 포인트는 0원에서 시작해서, 입력으로 받은 amount만큼 증가시킨다

###### Step 1: Point 클래스가 없어서 실패하는 테스트 작성하기
  - 포인트를 충전하는 테스트 작성하기
  - Point 클래스가 없어서 테스트는 실패함
  - Point 클래스의 메서드가 없어서 테스트는 실패함
```
class PointTest {

    @Test
    void 1000원을충전하면_금액이_1000원증가한다() {
        Point point = new Point(); // 아직 클래스가 없음

        point.charge(1000);

        assertThat(point.getAmount()).isEqualTo(1000);
    }
}
```

###### Step 2: Point 클래스 만들기, 아직 구현은 없음
  - Point 클래스와 메서드 생성하기
  - 메서드 내부 구현은 없음
  - 테스트를 실행하고, 그 결과는 실패
```
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

###### Step 3: 충전 기능 구현 But 가짜 구현
  - 메서드 내부를 구현하는데, 가장 빠른 방법인 1000을 항상 더해줌(가짜 구현)
  - 테스트를 실행하고, 그 결과는 성공
```
public class Point {

    private int amount = 0;

    public void charge(int amountToAdd) {
        amount += 1000;
    }

    public int getAmount() {
        return amount;
    }
}
```

###### Step 4: 충전 기능 테스트 추가
  - 기존 1000원 충전 테스트에 더해, 2000원을 충전하는 테스트를 추가함
  - 테스트를 실행하고, 그 결과는 실패
```
class PointTest {

    @Test
    void 1000원을충전하면_금액이_1000원증가한다() {
        Point point = new Point();

        point.charge(1000);

        assertThat(point.getAmount()).isEqualTo(1000);
    }
    
    
    @Test
    void 2000원을충전하면_금액이_2000원증가한다() {
        Point point = new Point();

        point.charge(2000);

        assertThat(point.getAmount()).isEqualTo(2000);
    }
}
```

###### Step 5: 충전 기능 구현 But 진짜 구현
  - 메서드 내부를 구현하는데, 가장 빠른 방법인 파라미터의 amountToAdd을 더해줌(진짜 구현)
  - 테스트를 실행하고, 그 결과는 성공
```
public class Point {

    private int amount = 0;

    public void charge(int amountToAdd) {
        amount += amountToAdd;
    }

    public int getAmount() {
        return amount;
    }
}
```

###### Step 6: 충전 테스트 중복 제거
  - 기존 충전 테스트에 중복이 있음
  - 중복을 제거하고 하나의 테스트만 남김
```
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

_[file]_

ℹ️ **Testable Code**

- 모든 코드를 테스트 가능하게 구현하는 것을 목표로 진행합니다.

- 모든 테스트 케이스가 성공했다는 것은 목표한 기능이 완성되었다는 것을 의미합니다.

- 테스트 커버리지 100% 가 아니라, 정확히 **기능의 동작을 확인하는 테스트를 작성**해 주세요.

- 주요 기능에서 `private` 접근자, 객체간의 강결합 같이 테스트 불가능한 코드는 가능한 한 지양하는 것이 좋습니다.

```
#1) 기능 구현을 원하는 요구사항을 검증하는 테스트 추가
#2) 테스트를 만족하도록 기능 구현
>> 이 때, 테스트는 한번에 많은 Scope 를 잡지 않는다. 이는 불필요한 개발 생산성 저하로 이어진다.
>> 이 시점에서 우리가 몇 배의 시간을 더 투자한다고 해서 테스트 커버리지 100% 를 달성할 수는 없다.
#3) 구현된 기능에 대한 리팩토링 ( 인터페이스 구성, 코드클리닝 등 코드 베이스 정리 )
```

- **참고** : [TestPyramid](https://martinfowler.com/bliki/TestPyramid.html)

> **[!]** **주의 !
단순히 테스트 커버리지 100% 를 목표하기보다 기능에 대해 유의미한 테스트케이스를 고민하고 작성해보세요.**

> **[!]** **과제 : 이번 챕터 과제**

### [ 1주차 과제 ] TDD 로 개발하기 

###### 💻 과제 첨부파일 다운로드
  > 프로젝트 과제
  **Java / Kotlin Spring 프로젝트**
  _[file]_
  _[file]_
  **Typescript Nest.js 프로젝트**
  _[file]_

###### ℹ️ 과제 필수 사항
  - Nest.js 의 경우 Typescript , Spring 의 경우 Kotlin / Java 중 하나로 작성합니다.
    - 프로젝트에 첨부된 설정 파일은 수정하지 않도록 합니다.
  - 테스트 케이스의 작성 및 작성 이유를 주석으로 작성하도록 합니다.
  - 프로젝트 내의 주석을 참고하여 필요한 기능을 작성해주세요.
  - 분산 환경은 고려하지 않습니다.
  > **[!]** 
    **주의사항**
    - 실제 DB 등을 활용하는 것이 아닌 주어진 코드 내의 객체를 활용하여 작성합니다.
---

###### ❓ [과제] `point` 패키지의 TODO 와 테스트코드를 작성해주세요.

###### **요구 사항**
  **API 요구사항**
  - PATCH  `/point/{id}/charge` : 포인트를 충전한다.
  - PATCH `/point/{id}/use` : 포인트를 사용한다.
  - GET `/point/{id}` : 포인트를 조회한다.
  **기능 요구사항**
  - GET `/point/{id}/histories` : 포인트 내역을 조회한다.
  - 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.

##### 🚀 Level-UP
  - 같은 사용자가 동시에 충전할 경우, 해당 요청 모두 정상적으로 반영되어야 합니다.

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정 
(각 요일 자정까지 제출)**


###### `STEP00 - 이것부터 시작해보세요!` 

- PR 템플릿 세팅하기!


---

## Chapter 2-1 서버구축 - 설계

> **[!]** **들어가기 이전에…. **

- 80점이 아닌 100점이 되기를 원하는 코치들

- 어떻게 하면 이직의 확률을 높일 수 있을까? feat. 함께 일하고 싶은 사람

> **[!]** 
  **Hall Of Fame**

- 안은솔 by 로이 코치님
  - 올바른 인터페이스의 활용 및 객체에게 적당한 역할과 책임 할당
  - 적당한 수준의 통합 테스트와 단위 테스트의 구성 + 가독성을 위한 노력들
  - 경계값 위주의 테스트 범위 설정 및 ParameterizedTest와 Steps를 통한 중복 제거

‣

- 박서희 by 이석범 코치님
  - 객체지향적인 코드 및 클래스의 책임 검증 탁월
  - 작업단위의 명확한 커밋 분리로 작업 내용을 파악하기 쉬움

‣

- 이세호 by 한상진 코치님
  - 동시성 제어 기법에 대한 분석
  - 깔끔한 요구사항 구현 및 테스트 작성

‣

**TDD Chapter Summary**

> **[!]** **들어가면서, TDD 적용하기**

> **[!]** **`이번 챕터 목표 **

- 시나리오가 요구하는 요구사항을 명확히 분석하기

- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기

- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기

- 견고하지만 유연한 서버 애플리케이션을 구축하기

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**

```
설계가 명확하면, "코드를 치는 행위" 는 목표를 달성하는 "수단" 이 된다.
설계가 명확하지 않으면, "코드를 치는 행위" 는 불필요한 "노동" 이 된다.
```


###### 1. Service Scenario 선택
  - `**베이직**`** : **각 시나리오의 기본 요구사항**
**`**챌린지**`** **(선택사항) :  각 시나리오의 심화 요구사항
    > **[!]** 아키텍처와 테스트 코드 작성에 집중하며, 견고하고 유연한 서버 개발이 목표인 사람 (챌린지 과제가 포함되어 있습니다)
### e-커머스 서비스 
        > **[!]** 아래 명세를 잘 읽어보고, 서버를 구현합니다.

##### Description
        - `e-커머스 상품 주문 서비스`를 구현해 봅니다.
        - 상품 주문에 필요한 메뉴 정보들을 구성하고 조회가 가능해야 합니다.
        - 사용자는 상품을 여러개 선택해 주문할 수 있고, 미리 충전한 잔액을 이용합니다.
        - 상품 주문 내역을 통해 판매량이 가장 높은 상품을 추천합니다.

##### Requirements
        - 아래 5가지 API 를 구현합니다.
          - 잔액 충전 / 조회 API
          - 상품 조회 API
          - 주문 / 결제 API
          - 선착순 쿠폰 API
          - 인기 판매 상품 조회 API
        - 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
        - **(심화) **재고 관리에 문제 없도록 구현합니다.
        - **(심화) **동시성 이슈를 고려하여 구현합니다.
        - **(심화) **다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.

##### API Specs

###### 기본과제
        1️⃣ `**주요**` **잔액 충전 / 조회 API**
        - 결제에 사용될 금액을 충전하는 API 를 작성합니다.
        - 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
        - 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.
        2️⃣ `**기본**`** 상품 조회 API**
        - 상품 정보 ( ID, 이름, 가격, 잔여수량 ) 을 조회하는 API 를 작성합니다.
        - 조회시점의 상품별 잔여수량이 정확하면 좋습니다.
        3️⃣  `**주요**`** 선착순 쿠폰 기능**
        - 선착순 쿠폰 발급 API 및 보유 쿠폰 목록 조회 API 를 작성합니다.
        - 사용자는 선착순으로 할인 쿠폰을 발급받을 수 있습니다.
        - 주문 시에 유효한 할인 쿠폰을 함께 제출하면, 전체 주문금액에 대해 할인 혜택을 부여받을 수 있습니다.
        4️⃣ `**주요**` **주문 / 결제 API**
        - 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API 를 작성합니다.
        - 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 합니다.
        - 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 합니다. ( 데이터 플랫폼이 어플리케이션 `외부` 라는 가정만 지켜 작업해 주시면 됩니다 )
        > 데이터 플랫폼으로의 전송 기능은 Mock API, Fake Module 등 다양한 방법으로 접근해 봅니다.
        5️⃣  `**기본**`** 상위 상품 조회 API**
        - 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API 를 작성합니다.
        - 통계 정보를 다루기 위한 기술적 고민을 충분히 해보도록 합니다.
---
        > **[!]** **KEY POINT**
        - 동시에 여러 주문이 들어올 경우, 유저의 보유 잔고에 대한 처리가 정확해야 합니다.
        - 각 상품의 재고 관리가 정상적으로 이루어져 잘못된 주문이 발생하지 않도록 해야 합니다.


###### **2. 개발 환경 준비**


###### **3. 시나리오 분석 및 작업 계획**


###### 4. API Spec Documentation

```
1. 요구사항 분석 및 API Spec 정의 ( 문서, 나열, 정리 )
2. Mock API 개발
3. Swagger-UI 작성
4. 본격적으로 "구체적인" 설계를 들어간다. ( 개발을 위한 )
```

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 2주차 학습 로드맵 (개인의 상황에 맞게 참고하여 활용해주세요!)

> **[!]** **과제 : 이번 챕터 과제**


###### 시나리오를 선택해 서버 애플리케이션 구축

`**기본과제**`** : **각 시나리오의 기본 요구사항**
**`**심화과제**` : 각 시나리오의 심화 요구사항

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정 (각 요일 자정까지 제출)**

`**REPO를 새로 생성하셨다면 이것부터 세팅해주세요!**`** **

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!
  **PR 템플릿**


###### `**STEP03 - 분석**`** **

- 시나리오 요구사항 분석 및 문서 작성 ( e.g. 시퀀스 다이어그램, ERD 등 )


###### `**STEP04 - 실행**`

- Mock API 및 Swagger-API 코드 작성

- **(NiceToHave)** API E2E 테스트 작성해보기

> **[!]** **과제 평가 기준과 핵심 역량 Summary**

**과제 평가 기준**

**BestPractice 평가 기준**

**핵심 키워드 및 역량**

---


###### FAQ

**TDD 가 아닌 DDD 로 개발하는 것은 안될까요 ?**

**위 서비스 3개를 선정한 기준이 어떻게 되나요?**


---

## Chapter 2-2 서버구축 - 소프트웨어 설계

> **[!]** Summary : TDD, 설계 돌아보기

**Summary 이전 챕터 돌아보기**

> **[!]** **들어가면서, Why 클린아키텍처?**

****클린 아키텍처, 헥사고날 아키텍처 등 다양한 아키텍처 패턴들을 다 알아야 하나요 ?****

****클린 코드의 중요성****

****프로젝트가 커질수록 왜 코드 수정이 무서워질까요?****

> **[!]** **이번 챕터 목표**

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

> **[!]** **🎯 과제와 연계된 목표**

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

> **[!]** **학습자료 : 이번 챕터에서 할 일**


###### **⚠️ 중요: 그대로 따라하지 마세요!**

여러분의 프로젝트 상황에 맞는 아키텍처를 선택하고, README에 그 이유를 명확히 설명해주세요.


###### ℹ️ **Software Architecture Pattern**

> **[!]** 좋은 아키텍처 패턴이란? 
- 지속적으로 성장 가능한 안정적인 소프트웨어를 잡기 위한 최고의 가이드라인
- 코드를 어디에 넣을지 명확한 기준을 제공하는 것 
- 변경과 확장에 유연한 구조를 만드는 것 
- 테스트하기 쉬운 코드를 작성할 수 있게 하는 것 
- 지켜야 할 기본적인 개발 가이드라인을 잡아주는 틀


##### **🤔 왜 아키텍처가 필요한가?**


###### **실제로 겪게 되는 문제들**

**상황 1: "이 코드 어디에 넣어야 하지?"**

- 상품 조회시 조회수 증가 로직은 Service에? Repository에?

- 포인트 사용 이력 저장은 어느 계층의 책임일까?

**상황 2: "같은 로직을 여러 곳에서 써야 하는데..."**

- 포인트 차감 로직이 주문, 쿠폰 구매, 이벤트 응모에서 모두 필요

- 복사해서 붙여넣기? 공통 클래스? 어떻게 해결할까?

**상황 3: "테스트는 어떻게 하지?"**

- 외부 결제 API를 호출하는 코드를 테스트하려면?

- DB 없이 비즈니스 로직만 테스트할 수 있을까?

이런 문제들을 해결하기 위해 아키텍처 패턴이 필요합니다.

---


###### 레이어드 아키텍처


###### ⭐ **레이어드 + 인터페이스 아키텍처 **


###### ⭐ 헥사고날 아키텍처


###### ⭐ 클린 아키텍처


###### 도메인?


###### **Clean Code**에서 추구하는 것

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**

**Java 기반 로드맵**

**Kotlin 기반 로드맵**

**TypeScript 기반 로드맵**

> **[!]** **과제 : 이번 챕터 과제**

> **[!]** 아키텍처와 테스트 코드 작성에 집중하며, 견고하고 유연한 서버 개발이 목표인 사람 (챌린지 과제가 포함되어 있습니다)
### e-커머스 서비스
    > **[!]** 아래 명세를 잘 읽어보고, 서버를 구현합니다.

##### Description
    - `e-커머스 상품 주문 서비스`를 구현해 봅니다.
    - 상품 주문에 필요한 메뉴 정보들을 구성하고 조회가 가능해야 합니다.
    - 사용자는 상품을 여러개 선택해 주문할 수 있고, 미리 충전한 잔액을 이용합니다.
    - 상품 주문 내역을 통해 판매량이 가장 높은 상품을 추천합니다.

##### Requirements
    - 아래 5가지 API 를 구현합니다.
      - 잔액 충전 / 조회 API
      - 상품 조회 API
      - 주문 / 결제 API
      - 선착순 쿠폰 API
      - 인기 판매 상품 조회 API
    - 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
    - **(심화) **재고 관리에 문제 없도록 구현합니다.
    - **(심화) **동시성 이슈를 고려하여 구현합니다.
    - **(심화) **다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.

##### API Specs

###### 기본과제
    1️⃣ `**주요**` **잔액 충전 / 조회 API**
    - 결제에 사용될 금액을 충전하는 API 를 작성합니다.
    - 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
    - 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.
    2️⃣ `**기본**`** 상품 조회 API**
    - 상품 정보 ( ID, 이름, 가격, 잔여수량 ) 을 조회하는 API 를 작성합니다.
    - 조회시점의 상품별 잔여수량이 정확하면 좋습니다.
    3️⃣  `**주요**`** 선착순 쿠폰 기능**
    - 선착순 쿠폰 발급 API 및 보유 쿠폰 목록 조회 API 를 작성합니다.
    - 사용자는 선착순으로 할인 쿠폰을 발급받을 수 있습니다.
    - 주문 시에 유효한 할인 쿠폰을 함께 제출하면, 전체 주문금액에 대해 할인 혜택을 부여받을 수 있습니다.
    4️⃣ `**주요**` **주문 / 결제 API**
    - 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API 를 작성합니다.
    - 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 합니다.
    - 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 합니다. ( 데이터 플랫폼이 어플리케이션 `외부` 라는 가정만 지켜 작업해 주시면 됩니다 )
    > 데이터 플랫폼으로의 전송 기능은 Mock API, Fake Module 등 다양한 방법으로 접근해 봅니다.
    5️⃣  `**기본**`** 상위 상품 조회 API**
    - 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API 를 작성합니다.
    - 통계 정보를 다루기 위한 기술적 고민을 충분히 해보도록 합니다.
---
    > **[!]** **KEY POINT**
    - 동시에 여러 주문이 들어올 경우, 유저의 보유 잔고에 대한 처리가 정확해야 합니다.
    - 각 상품의 재고 관리가 정상적으로 이루어져 잘못된 주문이 발생하지 않도록 해야 합니다.
### 콘서트 예약 서비스
    > **[!]** 아래 명세를 잘 읽어보고, 서버를 구현합니다.

##### Description
    - `**콘서트 예약 서비스**`를 구현해 봅니다.
    - 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
    - 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
    - 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

##### Requirements
    - 아래 5가지 API 를 구현합니다.
      - 유저 토큰 발급 API
      - 예약 가능 날짜 / 좌석 API
      - 좌석 예약 요청 API
      - 잔액 충전 / 조회 API
      - 결제 API
    - 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
    - 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
    - 동시성 이슈를 고려하여 구현합니다.
    - 대기열 개념을 고려해 구현합니다.

##### API Specs
    1️⃣ `**주요**`** 유저 대기열 토큰 기능**
    - 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
    - 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
    - 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.
    > 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.
*** 대기열 토큰 발급 API
* 대기번호 조회 API**
    **2️⃣ **`**기본**`** 예약 가능 날짜 / 좌석 API**
    - 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
    - 예약 가능한 날짜 목록을 조회할 수 있습니다.
    - 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.
    > 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.
    3️⃣ `**주요**`** 좌석 예약 요청 API**
    - 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
    - 좌석 예약과 동시에 해당 좌석은 그 유저에게 약** 5분**간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
    - 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 한다.
    - 누군가에게 점유된 동안에는 해당 좌석은 다른 사용자가 예약할 수 없어야 한다.
    4️⃣ `**기본**`** ** **잔액 충전 / 조회 API**
    - 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
    - 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
    - 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.
    5️⃣ `**주요**`** 결제 API**
    - 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
    - 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.
    > **[!]** **KEY POINT**
    - 유저간 대기열을 요청 순서대로 정확하게 제공할 방법을 고민해 봅니다.
    - 동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 합니다.
### 맛집 검색 서비스 (번외)
    > **[!]** 아래 명세를 잘 읽어보고, 서버를 구현합니다.

##### Description
    - 오픈 API 를 이용한 `맛집 검색 서비스` 를 구현해 봅니다.
    - 키워드를 이용해 맛집을 검색할 수 있습니다.
    - 인기 키워드 제공을 통해 사람들이 관심 있는 맛집이 무엇인지 알 수 있도록 합니다.

##### Requirements
    - 아래 2가지 API 를 구현합니다.
      - 맛집 검색 API
      - 인기 키워드 API
    - 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
    - 외부 API 에서 장애가 발생하더라도 문제 없이 기능이 동작할 수 있도록 작성합니다.
    - 대량의 검색 기록 데이터를 저장하는 것을 염두해 구현합니다.
    - 정확도 높은 인기 키워드를 제공할 수 있도록 작성합니다.

##### API Specs
    1️⃣ `**주요**`** ****맛집 검색 API**
    - 키워드와 지역정보 등을 이용해 맛집 정보를 검색하는 API 를 작성합니다.
    - 검색 조건으로 Sorting ( 정확도 순, 리뷰 개수 순 ) 을 지원해야 합니다.
    - 검색 결과는 Pagination 을 통해 제공합니다.
    - 검색 소스는 기본적으로 네이버 지역검색 API ( [https://developers.naver.com/docs/serviceapi/search/local/local.md](https://developers.naver.com/docs/serviceapi/search/local/local.md) )  를 활용합니다.
    - 검색 소스로 카카오 로컬 API ( [https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword](https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword) ) 가 추가될 수 있음을 고려해 작성해야 합니다.
    2️⃣ `**주요**`** ****인기 키워드 API**
    - 사용자들이 많이 검색한 키워드를 최대 10개까지 조회하는 API 를 작성합니다.
    - 각 키워드 별 검색 횟수 또한 함께 응답으로 내려주어야 합니다.
    > 인기 검색 키워드를 효율적으로 다루기 위한 방안을 고민해 봅니다.
---

###### 심화 과제
    3️⃣ `**심화**`** 카테고리 기능**
    - 인기 키워드 API를 지역 기반 카테고리와 함께 제공하는 기능을 제공합니다.
    - H2 코드 기반으로 검색 결과를 색인하기 위한 방법에 대해 고민해봅니다.
    - e.g. `강남` , `홍대` , `정자` , `성수` 등
    > **[!]** **KEY POINT**
    - 외부 API 에 장애가 발생했을 때, 다른 API 로 전환해 정상적으로 서비스를 유지할 수 있도록 구현합니다.
    - 인기 키워드를 저장, 조회하는 로직이 검색 성능에 영향을 최대한 끼치지 않아야 합니다.

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정 (각 요일 자정까지 제출)**

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!

**PR 템플릿**

`STEP05`

- 선택한 아키텍처 패턴을 적용하여 **패키지 구조 설계** 및 **핵심 비즈니스 로직** 개발

- 각 시나리오별 필수 기능 구현 및 **단위 테스트** 작성
  - `e-commerce`
    상품 조회 (상품 목록, 상세 정보),주문/결제 (재고 확인, 재고 차감, 결제 처리),포인트 충전 및 사용
  - `concert`
    콘서트 조회 (콘서트 목록, 좌석 정보),예약/결제 (좌석 선택, 예약 생성, 결제 처리),포인트 충전 및 사용

- **단위 테스트**는 Mock/Stub을 활용하여 대상 객체/기능에 대한 의존성만 존재해야 함

`STEP06`

- 각 시나리오별 추가 기능 구현 및 단위 테스트 작성
  - `e-commerce` 선착순 쿠폰 기능, 결제 실패 시 재고 복구 처리,
  - `concert` 대기열 기능 , 좌석 임시 배정 (5분간 좌석 홀드), 예약 만료 자동 취소 처리

- 선택한 아키텍처 패턴의 각 레이어별 책임에 대해 README에 정의하고, 책임에 위배되지 않도록 구현

> **[!]** **과제 평가 기준과 핵심 역량 Summary**

**과제 평가 기준**

**BestPractice 평가 기준**

**핵심 키워드 및 역량**


###### FAQ

**저는 다른 아키텍처들도 시도해보고 싶은데요 !**

****언제 아키텍처를 고민해야 할까요?****

****🤷 레이어드 아키텍처로도 충분하지 않나요?****


###### 팀별토론


---

## Chapter 2-3 서버구축 - 데이터베이스 기본

> **[!]** **들어가면서, TDD와 클린아키텍처 적용하기**

**TDD Chapter Summary**

**클린아키텍처 잘 적용 했는지 아는 방법**

> **[!]** **이번 챕터 목표 **

- 시나리오가 요구하는 요구사항을 명확히 분석하기

- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기

- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기

- 견고하지만 유연한 서버 애플리케이션을 구축하기

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**


#### DB Table 설계


#### DB Index, Query Plan, Query Optimization

---

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 10시간 학습 가이드

**Java 기반 학습 로드맵**

**Kotlin 기반 학습 로드맵**

**TypeScript 기반 학습 로드맵**


###### 3시간 학습 가이드

**DB 설계 & 조회 성능 최적화 핵심 로드맵**

> **[!]** **과제 : 이번 챕터 과제**

> **[!]** **Weekly Schedule Summary: 이번 주차 과제 요구 사항**

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!

**PR 템플릿**


###### `**STEP07 - Integration**`** **

- (선택) 기존 설계된 테이블 구조의 개선이 필요한 점을 식별하고 ERD에 반영

- Infrastructure Layer 작성

- 기능별 통합 테스트 작성

> `Infrastructure` 는 RDBMS ( MySQL ) 기반으로 작성합니다.


###### `**STEP08 - DB**`** **

- 조회 성능 저하가 발생할 수 있는 기능을 식별하고, 해당 원인을 분석하여 쿼리 재설계 / 인덱스 설계 등 최적화 방안을 제안하는 보고서 작성

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### PASS/FAIL 기준


###### BP 기준


###### 핵심 키워드 및 역량


###### FAQ

**트랜잭션을 적용할 필요는 없을까요?**

**저는 다른 아키텍처들도 시도해보고 싶은데요 !**

**TDD 가 아닌 DDD 로 개발하는 것은 안될까요 ?**

**위 서비스 3개를 선정한 기준이 어떻게 되나요?**


###### 팀별토론


---

## Chapter 2-4 서버구축 - 데이터베이스 심화

> **[!]** **들어가면서, TDD와 클린아키텍처 적용하기**

**TDD Chapter Summary**

**클린아키텍처 잘 적용 했는지 아는 방법**

> **[!]** **이번 챕터 목표 **

- 시나리오가 요구하는 요구사항을 명확히 분석하기

- 유지보수, 확장 가능한 코드에 대해 끊임없이 고민하기

- 테스트 가능한 구조 및 테스트 코드 작성에 집중하기

- 견고하지만 유연한 서버 애플리케이션을 구축하기

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**


###### **서버 개발 Summary**


###### Transaction


###### DB Transaction


###### 동시성 문제


###### DB 동시성 문제


###### Database Lock


###### 우리의 시나리오에서 동시성 이슈가 발생할 수 있는 비즈니스 로직은?


###### 콘서트 예약 서비스


###### e-커머스 서비스

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 10시간 학습 가이드


###### 3시간 학습 가이드

> **[!]** **Weekly Schedule Summary: 이번 주차 과제 요구 사항**

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!

**PR 템플릿**


###### `**STEP09 - Concurrency**`** **

- 동시성 문제에 대한 개념, 트랜잭션과 격리수준, DB Lock에 대한 학습을 진행

- 나의 서비스에서 발생하는 동시성 문제의 DB를 활용한 적절한 해결 방법을 선정하고 관련된 내용을 문서로 작성하여 제출

> 보고서는 문제 식별 - 분석 - 해결 등의 항목들을 기재해 주시기 바랍니다.


###### ** **`**STEP10 - Finalize**`** **

- STEP09에서 정리한 동시성 문제 해결 방안을 구현하고 통합테스트로 검증

**참고**

-  다음주부터 Chapter 3(대용량 트래픽&데이터 처리)가 진행됩니다.

-  이번주 과제와 무관하게, 구현이 미비한 기능이나 부족한 테스트가 있다면 보완해주시기 바랍니다.

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### 도전 항목


###### 핵심 키워드 및 역량


###### 팀별토론


###### **토론 주제 1) 트랜잭션 크기는 얼마나 작게 가져가야 할까?**

트랜잭션은 원자성을 보장하기 위해 최소한의 작업 단위로 묶어야 하지만, 종종 너무 큰 트랜잭션으로 인해 DB 성능 저하 및 Lock 경합이 발생하기도 합니다. 반면, 지나치게 잘게 나누면 데이터 정합성 관리가 어려워질 수도 있습니다.


###### **토론 주제 2) Deadlock(교착 상태) 방지 방안**

서비스 운영 중 여러 트랜잭션이 동시에 여러 리소스(테이블, 행 등)를 점유하면서 교착 상태가 발생하는 경우가 있습니다. 특히 동시성이 높은 예약 기능이나  빈번하게 발생할 수 있습니다. 


###### **토론 주제 3) DB 커넥션 풀**(Connection Pool)** 고갈**

운영 중인 서비스에 트래픽이 몰리면 커넥션 풀이 고갈되는 상황이 자주 발생합니다.

단순하게 커넥션 풀을 늘리기 전에 왜 커넥션이 부족해졌는지 파악할 필요가 있습니다. 아무런 작업이 되어있지 않다면 트래픽이 몰릴 때 애플리케이션 서버의 자원은 놀고 있고, DB만 바쁠 가능성이 있습니다.
어떤 사항들이 DB 커넥션을 비효율적으로 사용하게 만들까요? 커넥션 풀이 모자라다면 어떤 점들을 확인해야 할까요?


---

## Chapter 3-1 대용량 트래픽&데이터 처리

> **[!]** Summary : 지난 동시성 문제 챕터 돌아보기

**Summary 지난 챕터 돌아보기**

> **[!]** **이번 챕터 목표 **

- DB 트랜잭션 이상의 범위, 분산 환경에서 Lock 을 적용할 수 있는 방법에 대해 고민해 봅니다.

- 다량의 트래픽을 처리하기 위해 적은 DB 부하로 올바르게 기능을 제공할 방법을 고민해 봅니다.

- 캐시 레이어의 적용을 통해 DB I/O 를 줄일 방법을 고민해 봅니다.

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**

> **[!]** 
  점점 늘어나는 고객과 많은 트래픽은 점점 시스템의 높은 Throughput 을 요구하게 됩니다.
  이에 RDBMS 만으로는 다양한 비즈니스 가치를 달성하기 어렵습니다.
  우리는 다양한 문제를 해결하기 위해** ****REDIS **라는 추가 선택지를 찾게 됩니다.


###### 1. Distributed Lock 기반의 동시성 제어


###### 2. Caching


###### 3. Caching Strategy

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 10시간 학습 로드맵


###### 3시간 학습 로드맵

> **[!]** **Weekly Schedule Summary: 이번 주차 과제 요구 사항**

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!

**PR 템플릿**

> **[!]** **과제 : 이번 챕터 과제**


###### `**STEP11 - Distributed Lock**`

- Redis 기반의 분산락을 직접 구현해보고 동작에 대한 통합테스트 작성

- 주문/예약/결제 기능 등에 **(1) **적절한 키 **(2) **적절한 범위를 선정해 분산락을 적용


###### `**STEP12 - Cache**`** **

- 조회가 오래 걸리거나, 자주 변하지 않는 데이터 등 애플리케이션의 요청 처리 성능을 높이기 위해 캐시 전략을 취할 수 있는 구간을 점검하고, 적절한 캐시 전략을 선정

- 위 구간에 대해 Redis 기반의 캐싱 전략을 시나리오에 적용하고 성능 개선 등을 포함한 보고서 작성 및 제출

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### BP 기준


###### 핵심 키워드 및 역량


---

## Chapter 3-2 대용량 트래픽&데이터 처리

> **[!]** Summary : : 지난 동시성 문제 챕터 돌아보기

**Summary 지난 챕터 돌아보기**

> **[!]** **이번 챕터 목표 **

- Redis 의 특성에 따른 활용 방식을 고민해보고 올바른 설계로 풀어낼 방법을 고민해봅니다.

- 다량의 트래픽을 처리하기 위해 적은 DB 부하로 올바르게 기능을 제공할 방법을 고민해 봅니다.

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**


###### 1. Redis 자료구조


###### 2. Redis 기반의 랭킹 시스템


###### 3. Redis 기반의 구조 개선

`REPO를 새로 생성하셨다면 이것부터 세팅해주세요!`

- PR 템플릿 세팅하기!
  - Repo를 생성하고 `.github` 폴더를 생성 후 `pull_request_template.md` 파일을 만들어서 아래 템플릿을 복사/붙여넣기해주세요!

**PR 템플릿**

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 10시간 학습 로드맵


###### 3시간 학습 로드맵

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정**


###### `**STEP 13 Ranking Design**`**  **

- **이커머스 시나리오**
  가장 많이 주문한 상품 랭킹을 Redis 기반으로 개발하고 설계 및 구현

- **콘서트 예약 시나리오**
  (인기도) 빠른 매진 랭킹을 Redis 기반으로 개발하고 설계 및 구현


###### `**STEP 14 Asynchronous Design**`

- **이커머스 시나리오**
  선착순 쿠폰발급 기능에 대해 Redis 기반의 설계를 진행하고, 적절하게 동작할 수 있도록 쿠폰 발급 로직을 개선해 제출

- **콘서트 시나리오**
  대기열 기능에 대해 Redis 기반의 설계를 진행하고, 적절하게 동작할 수 있도록 토큰 발급 로직을 개선해 제출

> _각 시스템 ( 랭킹, 비동기 ) 디자인 설계 및 개발 후 회고 내용을 담은 보고서 제출_

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### 도전 항목


###### 핵심 키워드 및 역량


###### Todo Discussion


---

## Chapter 3-3 대용량 트래픽&데이터 처리

> **[!]** Summary : 지난챕터 돌아보기

**Summary 지난 챕터 돌아보기**

> **[!]** **이번 챕터 목표 **


##### 이벤트를 활용한 관심사 및 트랜잭션 분리

- 현재 여러분들이 구현한 비즈니스 로직 별 트랜잭션의 범위를 파악하고 사이드 이펙트에 대해 고려해 봅니다.

- 비즈니스를 적절하게 핸들링할 수 있도록 선후관계를 파악하고, 애플리케이션 이벤트를 활용해 관심사를 분리하도록 개선해 봅니다.

- 도메인간 트랜잭션이 분리된다면 발생할 수 있는 문제와 해결하는 방법을 학습해봅시다.

> **[!]** **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**


###### 1. 비즈니스 로직과 트랜잭션의 범위


###### 2. 애플리케이션 이벤트를 통한 관심사 분리

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### 10시간 학습 로드맵


###### 3시간 학습 로드맵

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정**


###### `**STEP 15 **``Application Event` 

- 실시간 주문정보(이커머스) & 예약정보(콘서트)를 데이터 플랫폼에 전송(mock API 호출)하는 요구사항을 이벤트를 활용하여 트랜잭션과 관심사를 분리하여 서비스를 개선합니다.


###### `**STEP 16 Transaction Diagnosis**`

- 서비스의 확장에 따라 어플리케이션 서버와 DB를 도메인별로 분리했을때, 트랜잭션 처리의 한계와 대응 방안에 대한 설계 문서 제출 

( Try if you want ) 

보상트랜잭션, Saga 패턴 등 활용하여 우리의 프로젝트를 고도화 해봅시다.

- Facade 활용한다면 트랜잭션을 도메인 단위로 분리하고 발생하는 분산 트랜잭션을 올바르게 구현하기

- Facade 없이 서비스간 의존하는 구조라면 어플리케이션 이벤트를 활용하여 각 서비스 의존을 없애기

**PR 템플릿**

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### 도전 항목


###### 핵심 키워드 및 역량

—

0823 QnA

```
이벤트 구조의 단점 중 하나는 코드의 트래킹이 어려울 수 있다는 점이라고 느낍니다.그렇다면 순환참조가 발생할 가능성은 보통 어떻게 안전하게 방지하는지 궁금합니다. 문서화 말고 구조적(코드 구조 or 린팅 or 컨벤션 등)으로 흔히 강제하는 패턴이 있는지 궁금합니다.

제가 생각해본 해결책은 다음과 같습니다:
  
코드 베이스가 분리된 MSA 구조에서의 해결책 => 이벤트 스키마 관련 코드 있는 쪽에 이벤트 토폴로지 (호출구조) 강제 린팅

하나의 코드 베이스 내에서의 event 이용에서의 해결책 => 이벤트가 어떤 도메인의 최상단 함수에서 발행되고 소비되는지 기록해두어서 순환참조 방지하는 파일 구현


--
단일 소스코드에서 여러 도메인을 관리할때 순환참조가 발생하면, application 실행에 실패하죠. 순환참조에 의해서..

1. MSA 내에서 서비스간 순환 참조는 발생하지 않는 것을 원칙으로 하지만, 필요에 의해서 반복적인 순환 참조가 아니라면 가능할 수도 있다.

**2. 어떤 비즈니스를 신규로 개발할때 관련된 도메인의 팀에서 전체적인 아키텍처를 함께 설계하는 방식


첫 구매 쿠폰?
이 유저에게 첫 구매 쿠폰을 발급해줘! -> 쿠폰의 도메인.
첫구매임을 확인하기 위해서는 이 유저의 주문 히스토리가 필요해요.

쿠폰 -> 주문

주문에서 쿠폰을 사용해줘! (주문 -> 쿠폰)
이때 이 쿠폰이 첫구매쿠폰이라면?
이 첫구매 쿠폰이 사용 가능한지 확인하려면, 주문의 히스토리가 필요해요.
(주문 -> 쿠폰 -> 주문)
쿠폰 사용 가능 여부 판단 API에 주문 히스토리를 함께 전달해줘!

쿠폰팀, 주문팀 모여서 순환참조 발생하지 않고, 가장 심플하고 유지보수 쉬운 설계를 같이 진행해요.


--
도메인에 DB를 각 도메인에 맞게 두 개씩 둔다는 사실을 본 것이 있는데
A 도메인 서비스 -> A DB + B` DB
B 도메인 서비스 -> B DB + A` DB
-> A의 구조 변경이 제약이 많이 생길 수 있어요.
-> 어느 정도 B 도메인에서 A 도메인의 특정 데이터를 비정규화해서 B 도메인의 메인 비즈니스로 관리하는 것은 일반적인 것 같아요. A` 로 두는게 아니라 B 안에 자연스럽게 녹여서 저장하는 구조가 더 일반적입니다.

**
```

```
<주문 정보 외부 플랫폼 전송> 이벤트가 실패했을 시에 보상이 필요할까요?

주문 및 결제 트랜잭션 커밋 완료 ➡️ 주문 정보 외부 플랫폼 전송(이벤트)

이벤트를 활용해 핵심 로직과 부가 로직에 대해 관심사 분리의 필요성을 이해했습니다. 그런데, 핵심 로직은 성공했는데, 부가 로직이 실패한 경우에도 원복을 위한 보상 작업이 필요할까요?

외부 플랫폼에서 사용하는 주문 정보가 꼭 정합성이 맞아야 하지 않는다면 필요없을 것 같긴한데, 요구사항에 따라 다를 것 같긴합니다. 부가 로직에 재시도를 구현해 최대한 정합성을 맞춰보려는 구현 정도면 괜찮지 않을까 합니다.

--
당연히 재시도가 필요할 것입니다. 다만, 이번 과제에서는 포함하지 않아도 괜찮습니다.
```

```
이벤트 기반 서비스에서, 실패 또한 이벤트를 발행해 보상로직을 동작시키는 구조가 될 것 같은데, 

그 과정에서 이벤트가 유실된다면?? 실패인지 성공인지 다음 또는 이전 서비스는 알 방법이 없는데 이런 경우에는 어떤 해결방식이 필요한가요?

--
비동기 아키텍처에서 문제가 발생하는 꽤나 빈도가 높은 문제에요.
Transactional Outbox Pattern , Inbox Pattern 등을 활용해요.

```

```
DB를 도메인별로 물리적으로 분리한 상황에서 SAGA 패턴을 적용한다고 가정했을 때, 보상 트랜잭션의 순서 보장 문제에 대해 질문 드립니다.

예를 들어, 주문 → 결제 → 배송 순서로 트랜잭션이 흘러가다가 배송 단계에서 실패한다면, 배송 취소 → 결제 취소 → 주문 취소처럼 원래 실행 순서의 역순으로 보상 트랜잭션이 실행되어야 데이터 정합성이 맞다고 이해했는데요,

제가 고민되는 부분은...

DB가 분리되어 있어 분산 트랜잭션을 쓸 수 없는 상황에서, 보상 트랜잭션을 어떻게 하면 올바른 순서로 실행 보장할 수 있을까요?
이 순서 보장은 이벤트 발행/소비 레벨에서 해결 가능한지, 아니면 중앙에서 관리할.. 어떤 관리자(?)가 있어야만 가능한지 궁금합니다.

**실제 서비스에서는 보상 트랜잭션의 순서를 100% 보장하기보다는, 비즈니스적으로 허용 가능한 불일치를 정의하고 관리하는 방식도 쓰는지 궁금합니다..!**

--
롤백 API를 만들어서 호출하기 보다는, 배송 실패에 대한 이벤트를 정의하고. 상위 도메인에서 이 실패 이벤트를 수집해서 각각의 트랜잭션을 원복하는 프로세스를 개발하는 것이 제게는 조금더 일반적인 설계였다..
```

```
"- **핵심 로직에 비동기 적용 사례가 있으셨는지 궁금합니다.**
    
    위 사례를 통해, 대부분의 핵심 로직은 사용자가 요청과 함께 결과를 즉시 확인해야 하기 때문에 동기적으로 실행되는 것이 적합하다고 생각이 되었습니다. 따라서 대부분의 비동기 처리는 사용자와 직접 관계가 없는 내부 로직이나 통계 데이터 수집과 같은 용도로 활용되는 경우가 많을 것 같습니다. 
    
    하지만, 대규모 트래픽 환경에서 빠른 처리를 위해 핵심 로직임에도 불구하고 비동기 방식으로 구현한 사례가 있는지 궁금합니다."
"처음에는 모든 프로세스를 단순히 이벤트 드리븐으로 처리해야겠다고 생각했지만, **Message Queue를 통한 이벤트 처리 방식은 기본적으로 비동기** 처리라는 점을 고려하면서 생각이 바뀌었습니다.

-> 합리적인 사고가 맞습니다.
다만, 동기방식의 한계가 있어요. API 라고 하면, 응답에 직접적인 영향을 받죠.
API의 응답이 늦거나, 다른 시스템의 일시적인 장애가 발생했을때 고객관점에서는 어짜피 주문 못하는 상황이 됩니다.
하지만, 시스템 관점에서는 조금 다를 수 있어요.

고객입장 -> 주문요청. 서비스에서 주문을 생성하고 주문 생성 중이라는 상태와 함께 응답을 반환한 뒤, 주문완료 이벤트를 발행해서 재고를 차감하고, 포인트를 차감하고, 각각의 프로세스가 완료가 되었을때 주문 상태를 변경해요.

FE관점에서 고객에게 전달하는 내용은
1. 주문 생성 중입니다. 이따가 확인하세요~~
2. 폴링을 통해서 주문 생성이 완료될 때까지 500ms 마다 서버에 주문 상태를 확인하는 요청을 통해 프로세스가 완전히 종료된 이후에 고객에게 결과를 노출한다.

결제 플랫폼은 결국 카드사 / 은행 서비스의 온전한 트랜잭션을 처리한 결과를 반영해야하기 때문에.. 느릴 수 밖에 없어요.
주문서버와 결제서버가 할 일은 크게 없는데 이 결제서버가 PG사를 통해 실제 결제를 진행하는 이 시점에 지연이 매우 빈번하게 발생해요.
이 결제가 지연되는 상황에서 모든 서비스가 줄줄이 소세지로 스레드를 붙잡고 있으면.. 또다른 장애로 이어질 위험이 발생해요.

주문서비스에서 결제에 대한 타임아웃을 5초로 설정했어요.
그런데, 이 과정에서 타임아웃이 발생하면.. 단순히 주문을 실패처리하지 않고, 실 결제가 완료(실패든 성공이든)될 때까지 잠시 보류상태로 대기하도록 구현했어요.

FE에서 이 상태 코드를 받으면, 폴링하도록 구현해뒀어요.

잠시만 기다려주세요... ... => 결제 진행이 최대 2분까지 소요될 수 있습니다.
주문에서는 최대 2분까지 기다렸다가, 2분이 지났는데도 응답이 안오면 주문을 실패처리 해요.




---
**유저는 주문을 하면 즉시 주문 성공 여부를 확인**할 수 있어야 하기 때문에, **주문 등록 → 상품 수량 체크 → 주문 상세 등록 → 결제 등록까지는 동기**적으로 처리하는 것이 적절하지만. 반면, 외부 데이터 플랫폼 전송과 같이 **유저가 성공 여부를 바로 확인할 필요가 없는 작업은 Message Queue를 활용한 비동기 이벤트 처리**로 진행하는 것이 적합하다고 생각합

즉, **1번~4번은 동기** 처리, **5번은 비동기 처리라는 방식으로 Message Queue**를 활용하는 식으로 접근을 하였는데 제가 Message Queue를 활용한 비동기 처리 방식에 대해서 정확하게 이해를 하고 있는게 맞는지 궁금합니다."


---
"예를 들어 외부 API에 요청을 보내는 로직이 있다고 가정할 때, 보상 트랜잭션이 구현되어 있지 않고 API 요청이 한 건이라면 메서드 내에서 가장 먼저 실행하도록 구성하여 해당 로직이 실패하면 메서드 전체가 실패하도록 제어할 수 있어 보상 트랜잭션이 발생하지 않도록 할 수 있을 것 같습니다

하지만, 외부 API 요청이 두 건 이상일 경우에는 이러한 방식으로 실패를 관리하기 어렵기 때문에, 이러한 상황에서 저희가 취할 수 있는 대처 방법이 있는지 궁금합니다."
```

```
"차주까지 해서 OrderFacade를 이벤트 흐름으로 트랜젝션을 분리하여 구현에 도전해보려고 합니다.
트랜젝션이나 복원의 흐름이 기존 로직에서 크게 달라지는 부분들이 있어 Facade에 대한 테스트 코드가 통과하도록 유지하면서 구현을 할 수 있을까 걱정이되었습니다.

테스트 코드를 작성하는 이유는 주요 로직이 지속적으로 성공하는 것을 보장하면서 진행한다고 생각이 되는데, 이런 경우에 테스트 코드를 더 수정을 하면서 개발을 하는 지 아니면 새로운 설계에 맞는 테스트 코드를 새로 작성하고 기존 테스트 코드는 폐기 후에 새로 개발을 해야할 지 궁금합니다."

--
TDD에서의 테스트케이스는 **지금 설계한 각각의 클래스의 책임을 검증하는 목적
그런데, OrderFacade를 더이상 활용하지 않고 다른 클래스들을 활용하도록 아키텍처를 변경한다면,
OrderFacadeTest는 더이상 무의미해지는거죠.**
```

```
"스프링으로 이벤트 기반 처리 시 트랜잭션 증가에 따른 DB 부하는 어떻게 관리해야 하나요?

- @TransactionalEventListener(AFTER_COMMIT)를 활용하면 이벤트마다 새로운 트랜잭션이 열려 DB Connection Pool 점유가 늘어날 수 있다고 생각합니다.
- 실무에서는 이러한 이벤트 기반 트랜잭션 증가로 인한 성능 문제나 커넥션 풀 관리 문제를 어떻게 해결하고 계신지 궁금합니다."

--
우리는 하나의 프로젝트에서 하나의 DB로 운영하고 있기 때문에 하나의 커넥션으로 수행할 수 있지만 분리하는 것처럼 느껴질 수 있어요.
기본적으로, 도메인 단위의 응집도를 높이는 구조를 채택하기 때문에 도메인별 데이터베이스를 별도로 가져가는 케이스가 많았던 것 같아요.


```

```
"ApplicationEvent를 활용한 관심사 분리 방식이 어느 정도까지 확장 가능할까요?
서비스 규모가 커지면 Kafka 같은 이벤트 플랫폼을 쓰게 될 텐데, 그 전환 기준은 보통 어디에서 결정되는지 궁금합니다."

--
설계자가 누구냐에 따라 달라지기도 하고, 같은 상황인데도 누구는 굳이 나누지말자, 누구는 꼭 나눠야한다.
https://product.kyobobook.co.kr/detail/S000061897997
소프트웨어 아키텍처 - The hard parts
```

```
비동기 로직이 너무 오래걸리고, 비동기 로직을 갖고있는 api가 많이 호출된다면 자원 점유가 문제가 될것 같은데, 이미 해당 api들은 응답이 되었을테니 병목 지점을 찾기 힘들것 같다는 생각이 드는데 어떤 방식으로 해결해야하나요?

--
이 api 요청에 대한 응답시간으로는 병목을 확인할 수가 없다. 근데 자원 점유에 대한 문제는 발생할 수 있죠.

주문이 완료된 이후에 slack 메시지를 보내는 요구사항.
slack 메시지를 전송하는 로직을 after_commit 시점으로만 설정했었어요.
datadog 같은 모니터링툴을 확인하면, 각 서비스의 흐름이 가시화되어 어떤 프로세스가 자원을 오래 점유하는지, 얼마나 오래 걸리는지 등을 파악할 수 있어요.

```

```
"통합테스트에서는 전체 흐름을 확인해보는 것으로 이해하고있는데
이벤트 발행/구독에 대한 테스트 코드 작성은 어떻게 해야하는 지 궁금합니다.
실제 이벤트가 발행되고 구독한 이벤트가 정상적으로 실행했는를 한번에 테스트하는 지, 
이벤트 발행과 보상트랜잭션을 따로 테스트하는 지 궁금합니다.
"

--
다른 개발자분들과, 우리 다른 코치님들과 다른 의견일 수 있을 것 같아요.
제 주관인 견해를 말씀드려보자면,

테스트코드를 해당 클래스의 책임을 검증하는 관점에서 작성하는 것을 매우 좋아해요.
전체적인 flow를 단위테스트로 모두 검증할 수는 없어요.
- 서비스에서는 이벤트 발행을 하는 applicationEventPublisher.publish() 가 1번 잘 호출되었는지..
- 이벤트 리스너에서는 이런 이벤트가 인입되었을때 이 클래스가 해야할 일들을 수행을 하는지...

@Transactional 어노테이션이 걸린 메서드가 트랜잭션이 잘 걸리는지 통합테스트로 검증 하나요? -> 아니요.

이벤트 퍼블리셔와 리스너가 정상동작하는지는 굳이 테스트코드를 통해서 검증하지는 않는 편이에요.

```

오케스트레이터 vs 코레오그래피 via 챗지피티

주문을 생성하고, 재고를 차감하고, 포인트를 차감하는 분산트랜잭션을 설계하고 있는데, 오케스트레이터와 코레오그래피 패턴으로 구현하는 샘플을 좀 제시해줘~~

_[이미지]_

_[이미지]_


---

## Chapter 3-4 대용량 트래픽&데이터 처리

> **[!]** Summary : 지난 챕터 돌아보기

**Summary 지난 챕터 돌아보기**

> **[!]** **이번 챕터 목표 **

- 카프카란 무엇인지, 왜 대량의 트래픽을 처리하는 서비스에서 사용하고 있는지 알아봅니다.

- 카프카를 활용해서 이벤트를 서비스 단위로 확장하고, 안정적인 이벤트 처리를 위한 방법을 알아봅니다.

> **[!]** **What to do: 이번 주에 해야 할 것. 이것만 집중하세요!**

> **[!]** 요즘 왜 다들 **카프카, 카프카**** **하는 거지?
- 대규모 실시간 데이터 스트리밍을 위한 **분산 메세징 시스템
**- 높은 처리량 및 개발 효율을 위한 분산 시스템에서 **고가용성과 유연함**을 갖춘 연계시스템이 필요


###### 카프카 Overview


###### 카프카 Overview


###### 비동기 메세지 통신을 통한 책임 분리


###### 대용량 트래픽 프로세스 개선

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### ✅ **10시간 학습 로드맵** (프로젝트 적용 및 성능 설계까지)


###### ✅ **3시간 학습 로드맵** (카프카 기초 학습 중심)

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정**


###### `**STEP 17 카프카 기초 학습 및 활용**`

- 실시간 주문정보(이커머스) & 예약정보(콘서트)를 카프카 메시지로 발행하도록 변경합니다.
  - 카프카에 대한 기초 개념을 학습하고 문서로 작성합니다.
    - 로컬에서 카프카를 설치하고 기본적인 기능을 수행해봅니다.
    - 어플리케이션에서 카프카를 연결하여 Producer & Consumer를 동작시켜봅니다.


###### `**STEP 18 카프카를 활용하여 비즈니스 프로세스 개선**`** **

- 각 프로젝트의 대용량 트래픽 프로세스를 카프카를 활용하도록 변경해봅니다.

- 개선한 내용에 대한 설계 문서(비즈니스 시퀀스 다이어그램, 카프카 구성 등)를 작성합니다. 

( Try if you want )

어플리케이션 이벤트 기반으로 확장된 우리 서비스를 배포 모듈을 완전히 분리한다고 가정하고 카프카를 활용하도록 전환해봅시다.

**PR 템플릿**

> **[!]** 
  **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### 도전 항목


###### 핵심 키워드 및 역량

열정이 식지 않는 너를 위한 선물…☆


###### Transactional Outbox Pattern


###### QnA(2025-08-30)

```
선착순 쿠폰에서 쿠폰 --> 파티션 <-- 컨슈머가 1:1:1로 매핑되는 것 이상으로 처리량은 늘릴 수 있는 방법이 있을까요?

생각해본 시나리오는 선착순 쿠폰에서 
3개의 쿠폰 --> 파티션_1 <--컨슈머_1 로 처리하다가 

아래와 같이 분리하면 병렬로 동작해서 처리량이 늘어날 거 같은데 이거 이상으로 늘릴 수 있는 방법이 있을까요?
쿠폰_1 --> 파티션_1 <-- 컨슈머_1
쿠폰_2--> 파티션_2 <-- 컨슈머_2
쿠폰_3 --> 파티션_3 <-- 컨슈머_3 

--
그렇다면, 파티션을 N개로 늘리면 N개의 병렬처리가 가능하기 때문에 처리량 확장이 가능합니다. 단, 컨슈머도 동일한 수로 맞춰줄 필요는 있겠죠.

```

```
정리해주신 대로 리밸런싱 중에는 잠시 메시지 처리가 중단되는데, 만약 저희 서비스에 컨슈머가 자주 추가되거나 장애가 발생한다면 리밸런싱이 너무 자주 일어나 서비스 전체에 영향을 줄 것 같습니다. 
이 'Stop-the-world' 리밸런싱의 영향을 최소화할 수 있는 최신 카프카의 기능이나 컨슈머 설정 전략이 있을까요?"

--
제가 알기로는 아직 없는걸로 알고 있어요.
리밸런싱을 최소화하기 위해서 컨슈머의 수와 파티션의 수를 고정적으로 설정하는 편이에요.

컨슈머의 오토스케일링이나, 점진적 배포 과정에서 컨슈머의 수가 변경될 수 있어요.
컨슈머 수가 변경되더라도 기존에 매핑되어있던 연결을 멈추지 않고 변경이 필요한 파티션과 컨슈머만 stop the world 를 하도록 설정할 수 있는걸로 기억해요.

파티션이 3개가 있고
1 - c1
2 - c2
**3 - c1 - 변경이 필요한 파티션과 컨슈머만 리밸런싱을 진행하는 옵션이 있을거에요.**
```

```
쿠폰 경합 케이스에서 대부분의 경우 그 선착순 쿠폰이 **동일 시간대에 하나의 쿠폰에 대해 경합이 몰릴 것 같습니다.**

이렇게 가정하고 볼 경우 백엔드 네트워크에 도달하는 순서대로 쿠폰 선착순이 보장되는 것이 이상적이라고 생각합니다. (프론트엔드, 디바이스, 라우터 이슈를 제외하고 백엔드에 처음 도달한 순간을 선착순이라고 가정)

그렇다면, coupon-issue 토픽에 대해 파티션이 여러개 존재할 경우 발제에서 말씀주신대로 순서보장이 사라질 것입니다. 그리고 단일 파티션으로 구현해도 컨슈머는 1개를 초과할 수 없으니 처리량이 적다고 생각합니다.

**=> 파티션이 여러개 있더라도 하나의 쿠폰에 대한 발급 요청만 인입된다면 하나의 파티션만 활용하게 됩니다. 단 순서 보장은 가능하죠. 처리량은 떨어지더라도**

그랬을때 제가 생각한 조금 더 나은 최적화는 다음과 같습니다:

<1> 선착순 번호를 붙여서 메세지 발행
 
API 서버에서 요청을 받자마자 **원자 증가로 전역 번호** 발급 (예: seq = INCR requests:coupon123 (Redis/DB 시퀀스))
이 seq를 메시지에 포함해 coupon-requests(N개 파티션)로 발행.
소비자는 어느 파티션에서 읽든 seq ≤ 100만 성공 처리, 그 외 실패.
=> 100개 중에 실패가 있다면 또 곤란해지는 문제도 발생할 것 같았습니다. (100번 이상 번호 중에 재처리를 또 해주어야하니 구조가 복잡해짐)

<2> redis 카운터 + 쿠폰 예약 로직
예약은 앞쪽 서비스에서 진행 => kafka => 컨슈머에서 예약 확정

<3> kafka 먼저 쓰고 동시성 포기 => 이후 redis로 처리
카프카가 필요한 시나리오들을 커버할 수 있을 것 같습니다 (쿠폰별 파티션 해시 나누기 등)

<4> 3번으로 할거면 굳이 kafka 안쓰고 redis만 쓰는 방식

이 방식들의 판단의 방향성이 올바른 것일까요?

--
**쿠폰 1개의 발급에 대해서는 항상 1개의 발급 프로세스만 동작해야하죠.
**

```

```
순서보장 관련해서 궁금한 점이 있습니다.
카프카는 항상 **1번 메시지가 처리된 후에 2번 메세지를 가져오는게 실행**되는건가요?
1번 메세지를 가져가서 실행하는 동안 2번 메세지를 가져가서 실행하면 메세지는 순서대로 가져가지만 처리속도에 따라 2번이 먼저 처리될 수도 있지 않나 해서 질문드립니다.

--
컨슈머 1의 프로세스
offset 101번 읽었어! 다음 메세지 줘!
102번 오프셋의 메시지를 받아와서 처리를 하겠죠. 이 프로세스를 수행한 이후에 오프셋을 커밋해요.
다음 메시지 줘!
103번 오프셋의 메시지를 받아올 수 있게 됩니다.



```

카프카 클러스터링

_[이미지]_


###### QnA(2025-09-01)

```
카프카는 데이터를 메모리에 영구적으로 저장하기 때문에, 이벤트 메시지를 카프카에서 조회하더라도 데이터가 계속 남아 있는 것으로 알고 있습니다. 이를 관리하기 위해 Retention 정책이나 Log Compaction을 활용할 수 있는 것으로 알고 있는데, 코치님은 이러한 기능을 어떻게 활용하고 계신지 궁금합니다.

--
카프카 메시지를 하드디스크에 저장하기 때문에 영구적이다. 라고 볼 수도 있겠는데요.
카프카의 특성상 발행된 메시지를 모든 컨슈머가 읽었다면 그 이전 메시지는 활용도가 없다고 보는 것이 일반적인 카프카의 활용입니다.

따라서, 운영 관리 비용을 줄이기 위해서 카프카의 리텐션 정책을 반드시 활용하는 편입니다.
메시지가 정말 많이 발행되는 이벤트의 경우에는 리텐션 기간을 1일정도 혹은 더 짧게 설정하기도 하고요.
일반적인 케이스에는 최대 7일에서 14일까지 보관하는 편입니다.
```

```
"제가 생각하기에 카프카를 도입한 가장 큰 이유는 Redis의 부담을 줄이기 위함이라고 이해했습니다.
Redis도 일정 수준의 Queue 기능을 제공하지만, 본래 Redis는 캐싱에 최적화된 시스템이라고 생각됩니다.
따라서 선착순 쿠폰처럼 대규모 트래픽이 몰리는 상황에서 Queue 용도로 Redis를 함께 사용하면 부담이 커질 수 있습니다.
이런 문제를 해결하기 위해 카프카를 도입한 것으로 이해했는데, 제가 올바르게 이해한 것인지 궁금합니다."

--

사실 레디스든 카프카든 우리의 설계에서 적절하게 활용할 수 있다면 채택해서 사용하면 됩니다.
꼭, 레디스 단독으로 썼을때 부하를 더 줄이기 위해서 카프카를 도입한다 => (X)
레디스 없이 카프카만을 활용하기도 해요.
학습하는 쿼리큘럼 과정에서 레디스를 먼저 다뤘고, 선착순 쿠폰, 대기열 들이 레디스로 혹은 카프카로 설계했을때 더 적절한 상황이 있기 때문에 나중에 학습한게 더 나은 방식은 아니라고 이해하시면 좋겠습니다.

**선착순 **쿠폰은 반드시 Queue 가 필요할 것 같고, 이 큐를 메모리를 활용하는 레디스에 트래픽을 쏟아붇기보다는 카프카로 큐를 적재하는 것이 좀 더 안정성이 확보될 수는 있겠다.
```

```
기존 DB와 Redis에서와 마찬가지로, 카프카 또한 Producer와 Consumer를 추상화하여 구현함으로써 유연성을 확보하는 방식이 바람직한 접근인지, 그리고 현업에서는 실제로 어떻게 활용되고 있는지 궁금합니다.
--
카프카를 항상 사용할 것이냐..?
- 다른 메시지큐인 SQS, RabbitMQ, ... 다른 메시지 큐로 전환한다면 추상화하여 구현하는 것이 유연성이 더 높아진다. (O)
- 저는 MySQL -> PostgreSQL, Oracle.. 등등으로 변경한 적이 없고, 또 Kafka 를 다른 메시지큐로 변환한 적이 없어요,
예전에 래빗엠큐를 쓰다가 Kafka로 다들 전환하세요~~ 해서 전환해본 적은 있지만 추상화가 되었었다면 더 편했을까?는 잘 모르겠어요.

1. 각각의 메시지큐 서비스들은 각각의 서비스만의 특성들이 존재해요. 그래서 우리가 실제로 서비스를 활용할때에도 이 특성을 함께 활용하기 때문에, 추상화된 공통의 메시지큐 기능만을 사용하지 않는 경우가 많아요.
카프카의 파티셔너라던지,, 래빗엠큐는 컨슈머가 메시지를 폴링하지 않고 컨슈머에게 메시지를 찔러주는 형태이기도 하고.. 이런 서비스만의 다른 특성들이 추상화를 어렵게 하기도 하고 하더라도 현재 서비스의 특징과 장점을 살리지 못하는 설계가 되는 경우가 많아서 추상화를 잘 하지는 않아요.

2. 요즘 컨슈머나 프로듀서들이 라이브러리들을 활용했을때 구현체가 매우 복잡하지가 않아요. 그래서 저수준 모듈을 구현하기가 어렵지 않아서 굳이 추상화가 필요 없다는 생각도 들어요.
```

```
"Transactional Outbox 패턴은 결국 DB → Kafka 이중 기록을 기반으로 한다고 이해했습니다.
실제로 Kafka로 메시지를 전송하는 방법은 크게 2가지가 있는걸로 알고 있는데요,

- DB에 Outbox 저장 + 배치 발행 (Polling Publisher)
	- 이벤트 발행에 지연이 발생할 것 같고
- 또는 Kafka로 직접 발행하되 이벤트 실패 시 SQS/DLQ 같은 보조 채널로 처리(CDC)
  - 실시간으로 이벤트를 발행하지만, 실패에 대한 보상동작 구현이 필요하죠.
이런 방식을 비교했을 때, 어떤 기준으로 Outbox 패턴을 선택하는 게 맞는지 궁금합니다."

일반적인 서비스에서는 실시간으로 이벤트를 발행해야 하는 것이 더 일반적인 방법이기에 후자를 더 많이 사용하는 것 같아요. 다만, 데이터수집 플랫폼처럼 꼭 실시간 데이터를 반드시 보장해야하는 요구사항이 아니라면 메인 서비스의 성능을 아끼고 배치 프로세스에서 부가 로직을 수행하는 방법도 괜찮은 접근이 될 수 있습니다.
```


---

## Chapter 4 장애대응

> **[!]** Summary : 지난 모니터링 챕터 돌아보기

**Summary 지난 챕터 돌아보기**

> **[!]** **들어가면서, Why 장애대응?**

**장애대응 환경 구축하기**

**장애대응에서 가장 중요한 포인트는!?**

**장애대응 프로세스**

> **[!]** **이번 챕터 목표**

- **애플리케이션에서 발생하는 장애를 대응하고 개선합니다.**

- 앞서 고민한 로깅과 Alert 파이프라인을 통해 실제 장애가 발생하기 전에 미리 탐지하고 이를 개선할 수 있도록 고민해봅니다. Error Tolerant 한 애플리케이션 개발 관점을 생각해봅니다.
  - `Testable Code` - 변경에 유연하며 관리포인트의 집중을 통해 테스트 커버리지를 높일 수 있습니다.
  - `Code/Peer Review` - 우리는 모두 사람이기에, 항상 내 코드가 완벽하지 않을 수 있습니다. 동료가 작성한 코드를 함께 고민하고, 더 나은 애플리케이션 개발을 위해 팀 전체가 고민하며 더 좋은 소프트웨어 개발을 향해 나아갑니다.

- 장애가 발생할 수 있는 포인트 들에 대해 고민하고 논의해 봅니다.

- 애플리케이션의 장애 유형과 대응방법을 고민해보고 앞서 개발한 서버를 개선할 방법을 모색해봅니다.

> **[!]** **What to do: 이번 주에 해야 할 것. 이것만 집중하세요!**


###### 서론 : 현업에서 장애를 감지하기 위해 하는 노력들( 모니터링, 로깅 )


###### STEP 01 부하 테스트


###### STEP 02 장애 발생과 대응 시나리오


###### 자연재해는 누가 막을 수 있을까?

> **[!]** 
  **원활한 프로젝트 진행을 위한 학습 로드맵**


###### ✅ **10시간 학습 로드맵**


###### ✅ **3시간 학습 로드맵**

> **[!]** **Weekly Schedule Summary: 이번 챕터의 주간 일정**


###### `**STEP 19**`

- 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성

- 적합한 테스트 스크립트를 작성하고 수행

> `NiceToHave` Docker 의 실행 옵션 (cpu, memory) 등을 조정하면서 애플리케이션을 실행하여 성능 테스트를 진행해보면서 적절한 배포 스펙 고려도 한번 진행해보세요! 


###### `**STEP 20**`

- 위 테스트를 진행하며 획득한 다양한 성능 지표를 분석 및 시스템 내의 병목을 탐색 및 개선해보고 **(가상) **장애 대응 문서를 작성하고 제출

- 최종 발표 자료 작성 및 제출

**PR 템플릿**

> **[!]** **과제 평가 기준과 핵심 역량 Summary**


###### P/F 기준


###### 도전항목


###### 핵심 키워드 및 역량


---

## 멘토링 노트

> **[!]** 

###### 멘토링 예약 유의사항
  1. 멘토링 예약 시간
    1. ~ 토 17:59 | 예약 페이지 활성화 & 예약 불가
    1. `토 18:00 ~` | 예약 시작
  1. 아래 2가지 경우가 확인되면 해당 팀의 예약은 일괄 취소되고 가장 마지막에 예약해야 합니다.
    1. 토 18:00 이전에 예약한 경우
    1. 팀에서 2명 이상이 예약한 경우


###### 사전 노트 작성 유의사항

> **[!]** 
  1. 팀원들은 함께 논의하며 `멘토링 전날 저녁 10시`까지 각 주차 멘토링 사전 노트를 작성해 주세요.
  1. 코치님들은 사전 노트를 보시고 여러분의 한 주 학습 현황을 파악합니다. 사전 노트를 최대한 꼼꼼히 작성해야 많이 배워가실 수 있습니다!
  1. 멘토링은 크게 **(1) 기술적 조언 / (2) 한 주 단위 목표 설정 및 회고**로 이뤄집니다.
    1.  가장 중요한 것은** 이번 주에 내가 한 일을 정리**하는 것
    1. 그 과정에서 어려움이 있었던 부분에 대해 구체적으로 **무엇이, 왜 어려웠는지 회고하는 것**
    1. 다음에는 어떻게 하면 좋을지 배운 것을 작성!


---

## 랜덤 과제 리뷰

> **[!]** 
  **팀 편성 확인하기**
  *팀 편성에 본인 이름이 없으시면 김혜진 매니저에게 문의해주시기 바랍니다!
  ‣

> **[!]** 

###### 진행 가이드
  `지난주 과제 리뷰`는 코드 중심 리뷰, 경험 중심 리뷰 중 팀에서 선택해 진행합니다.
  1시간 동안 알찬 기술 소통을 할 수 있는 방식으로 자유롭게 선택해 주세요.
  (시간이 남으면 두 개 다 하셔ㅈ도 좋습니다!)
  1. 코드 중심 리뷰 (PR 중심) – 권장!
    - 과제를 하며 고민한 코드 블럭을 함께 보고, 더 나은 구조나 접근을 토론합니다.
    - 여러 팀원의 다양한 코드 스타일과 해결법을 직접 비교하며 배울 수 있어요.
    - 특히 과제를 완성했거나, 특정 코드에 대한 피드백을 받고 싶은 분들에게 추천합니다.
  1. 경험 중심 리뷰 (발표 중심)
    - 이번 과제에서 겪은 어려움, 배운 점, 고민했던 순간들을 중심으로 발표합니다.ㅁ
    - 과제를 끝내지 못했더라도, 도전했던 시도나 막힘 포인트 하나만 있으면 충분합니다.
    - 조금 더 가볍고 자유로운 분위기에서 서로의 이야기를 나누고 싶을 때 추천합니다.
      - “커스텀 훅을 만들려고 했는데 구조가 꼬여서 포기했어요”
      - “styled-components에서 조건 분기를 깔끔하게 처리하려다 헤맸어요”
      - “이번 주는 과제를 하다 말았지만, 여전히 궁금한 건 이거예요”


---

## WIL (Weekly I Learned)

    > **[!]** **진행 방식**
      1. “이번주 발제부터 과제 제출까지” 한 주를 돌아보며 WIL을 작성합니다.
      1. WIL 링크를 이 페이지에 공유합니다.
      1. 다른 동료들의 WIL을 참고하여 서로 응원의 댓글을 남기면 더욱 좋습니다.
      **SEO 최적화 가이드**
    > **[!]** 
      **KPT 작성법**
      - Keep : 현재 만족하고 계속 유지할 부분
      - Problem : 개선이 필요하다고 생각하는 문제점
      - Try : 문제점을 해결하기 위해 시도해야 할 것

WIL 제출 링크

WIL 응답 링크


---

## 팀별 토론



---
