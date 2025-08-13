package ecommerce.order.domain.payment;

public class Payment {

    private static final long MIN_PAYMENT_AMOUNT = 1L;
    private static final long MAX_PAYMENT_AMOUNT = 100_000_000L;

    private final Long id;
    private final Long orderId;
    private final Long pgId; // 결제 서비스 제공자 ID (예: PG사 ID)
    private final Long amount;
    private final String paymentMethod; // 결제 수단 (예: 카드, 계좌이체 등)
    private final PaymentStatus status;

    // 기본 생성자 (Default Constructor)
    public Payment(Long id, Long orderId, Long pgId, Long amount, String paymentMethod, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.pgId = pgId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Long getId () {
        return id;
    }

    public Long getOrderId () {
        return orderId;
    }

    public Long getPgId () {
        return pgId;
    }

    public Long getAmount () {
        return amount;
    }

    public String getPaymentMethod () {
        return paymentMethod;
    }

    public PaymentStatus getStatus () {
        return status;
    }

    // 정적 팩토리 메서드 - 공용으로 사용할 수 있는 Payment 생성 방법
    public static Payment createPayment(Long orderId, Long amount) {

        // 파라미터 입력값 검증
        if (amount < 0 || amount == null) {
            throw new IllegalArgumentException("Amount must be greater than or equal to 0");
        }

        if( orderId == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }

        // 비즈니스 검증
        validatePayment(orderId,amount);

        return new Payment(
            null, // ID 는 자동채번되어야하기에\
            orderId,
            null, // PG ID는 외부 PG사와 연동 시 설정
            amount,
            "Point", // 결제 수단은 포인트하나밖에 없다고 가정함.
            PaymentStatus.REQUESTED // 결제 상태는 요청 상태로 초기화
        );

    }

    // 결제 유효성 검사 (Private 메서드)
    private static void validatePayment(Long orderId, Long amount) {
        if (amount < MIN_PAYMENT_AMOUNT) {
            throw new IllegalArgumentException("Amount must be greater than " + MIN_PAYMENT_AMOUNT);
        }

        if (amount > MAX_PAYMENT_AMOUNT) {
            throw new IllegalArgumentException("Amount must be less than " + MAX_PAYMENT_AMOUNT);
        }
    }

}
