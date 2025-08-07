package ecommerce.order.application.port.out;

import ecommerce.order.domain.Order;

public interface SaveOrderPort {
    Order saveOrder(Order order, Long userId);
}
