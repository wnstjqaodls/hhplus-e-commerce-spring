package ecommerce.order.application.port.out;

import ecommerce.order.domain.Order;

public interface LoadOrderPort {
    Order loadOrder(Long orderId);
}
