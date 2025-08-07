package ecommerce.order.application.service;

import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.order.domain.Order;

public class PlaceOrderService implements PlaceOrderUseCase {

    @Override
    public Order placeOrder (Order order) {
        return null;
    }

    @Override
    public Order cancelOrder (Order order) {
        return null;
    }

    @Override
    public String SendOrderDataUseCase (Order order) {
        return "";
    }
}
