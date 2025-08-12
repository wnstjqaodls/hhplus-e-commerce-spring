package ecommerce.order.application.port.in;

import ecommerce.order.domain.Order;

public interface GetOrderHistoryUseCase {
    Order getOrder(Long orderId);
}
