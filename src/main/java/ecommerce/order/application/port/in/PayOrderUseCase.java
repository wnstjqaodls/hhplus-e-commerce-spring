package ecommerce.order.application.port.in;

public interface PayOrderUseCase {
    Long payOrder (Long userId, Long orderId);
}
