package ecommerce.order.application.service;

import ecommerce.order.application.port.in.OrderAndPayUseCase;
import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.order.application.port.in.PayOrderUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderAndPayService implements OrderAndPayUseCase {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;

    public OrderAndPayService(PlaceOrderUseCase placeOrderUseCase, PayOrderUseCase payOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
    }

    @Override
    @Transactional
    public Long orderAndPay(Long userId, Long productId, int quantity, Long amount) {
        // 1. 주문 생성 (PlaceOrderService 호출)
        Long orderId = placeOrderUseCase.placeOrder(userId, productId, quantity, amount);
        
        // 2. 결제 처리 (PayOrderService 호출)
        Long paymentId = payOrderUseCase.payOrder(userId, orderId);
        
        return paymentId;
    }
}
