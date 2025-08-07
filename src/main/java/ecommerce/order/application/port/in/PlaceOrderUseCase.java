package ecommerce.order.application.port.in;

public interface PlaceOrderUseCase {
    Long placeOrder(Long userId, Long productId, int quantity, Long amount);
}
