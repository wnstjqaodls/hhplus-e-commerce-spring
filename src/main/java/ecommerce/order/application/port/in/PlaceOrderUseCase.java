package ecommerce.order.application.port.in;

import ecommerce.order.domain.Order;

public interface PlaceOrderUseCase {
    Order placeOrder(Order order);
    Order cancelOrder(Order order);
    String SendOrderDataUseCase(Order order);

}
