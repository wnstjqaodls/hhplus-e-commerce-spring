package ecommerce.order.domain.event;

/**
 * 주문 완료 이벤트
 * - 주문이 성공적으로 완료되었을 때 발행되는 이벤트
 * - 외부 데이터 플랫폼으로 주문 정보 전송 등의 부가 로직을 트리거함
 */
public record OrderCompletedEvent(
    Long orderId,
    Long userId,
    Long productId,
    int quantity,
    Long amount
) {
    public static OrderCompletedEvent of(Long orderId, Long userId, Long productId, int quantity, Long amount) {
        return new OrderCompletedEvent(orderId, userId, productId, quantity, amount);
    }
}