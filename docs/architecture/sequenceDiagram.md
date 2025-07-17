# 이커머스 시퀀스다이어그램

## 목표
1. 주제와 상관없는 플로우는 빼거나 생략하자
2. 보기쉬운 문서를 만들자
3. 보는사람 에 적합한 다이어그램 그리기 현재는 (개발자 , 기획자)
4. 고수준의 각 도메인 모듈 수준에서 그려보려한다.

## 시퀀스 다이어그램

### 사용자 상품 구매 플로우
```mermaid
sequenceDiagram
    autonumber
    actor U as 사용자
    participant W as Web Client
    participant A as API Gateway
    participant O as Order Service
    participant P as Product Service
    participant PA as Payment Service
    participant DB as Database
    
    U->>W: 상품 주문하기 클릭
    W->>A: POST /api/orders 요청
    A->>O: 주문 생성 요청
    
    O->>P: 상품 재고 확인
    P->>DB: 재고 조회
    DB-->>P: 재고 정보 반환
    
    alt 재고 있음
        P-->>O: 재고 확인 완료
        O->>PA: 결제 요청
        PA->>PA: 결제 처리
        
        alt 결제 성공
            PA-->>O: 결제 성공
            O->>P: 재고 차감 요청
            P->>DB: 재고 업데이트
            O->>DB: 주문 정보 저장
            O-->>A: 주문 성공 응답
            A-->>W: 주문 완료
            W-->>U: 주문 완료 페이지 표시
        else 결제 실패
            PA-->>O: 결제 실패
            O-->>A: 주문 실패 응답
            A-->>W: 에러 응답
            W-->>U: 결제 실패 메시지
        end
    else 재고 없음
        P-->>O: 재고 부족
        O-->>A: 재고 부족 응답
        A-->>W: 재고 부족 에러
        W-->>U: 재고 부족 알림
    end
```

### 포인트 사용 플로우
```mermaid
sequenceDiagram
    autonumber
    actor U as 사용자
    participant W as Web Client  
    participant A as API Gateway
    participant PO as Point Service
    participant O as Order Service
    participant DB as Database
    
    U->>W: 포인트 사용해서 주문
    W->>A: 포인트 사용 주문 요청
    A->>PO: 포인트 잔액 확인
    PO->>DB: 사용자 포인트 조회
    
    alt 포인트 충분
        DB-->>PO: 포인트 정보
        PO-->>A: 포인트 사용 가능
        A->>O: 주문 처리 진행
        O->>PO: 포인트 차감 요청
        PO->>DB: 포인트 차감
        DB-->>PO: 차감 완료
        PO-->>O: 포인트 차감 성공
        O-->>A: 주문 성공
        A-->>W: 주문 완료
        W-->>U: 주문 완료
    else 포인트 부족
        DB-->>PO: 포인트 부족
        PO-->>A: 포인트 부족 에러
        A-->>W: 포인트 부족
        W-->>U: 포인트 부족 메시지
    end
```

## Mermaid 시퀀스 주요문법 및 사용예제

### 기본 문법
- `actor A as 이름` : 액터 정의
- `participant B as 이름` : 참여자 정의  
- `A->>B: 메시지` : 실선 화살표
- `A-->>B: 메시지` : 점선 화살표 (응답)
- `A--)B: 메시지` : 점선 화살표

### 조건문
```
alt 조건
    처리내용
else 다른조건  
    다른처리내용
end
```

### 활성화
```
activate 참여자
deactivate 참여자
```

### 기타
- `autonumber` : 자동 번호 매기기
- 주석은 `%% 내용` 으로 작성

## 주의사항
- 너무 복잡하게 그리지 말고 핵심 플로우만 
- 에러 케이스도 중요한건 포함시키기
- 실제 서비스 구조랑 맞춰서 그리기

## 문서정보
작성일 : 2025-01-17 <br>
작성자 : 김준섭(5팀) <br>
문서유형 : 설계서
