package ecommerce.order.application.service;

import ecommerce.order.application.port.in.OrderAndPayUseCase;
import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.order.application.port.in.PayOrderUseCase;
import ecommerce.order.application.event.OrderCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderAndPayService implements OrderAndPayUseCase {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public OrderAndPayService(PlaceOrderUseCase placeOrderUseCase, PayOrderUseCase payOrderUseCase, 
                             ApplicationEventPublisher eventPublisher) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Long orderAndPay(Long userId, Long productId, int quantity, Long amount) {
        // 1. 주문 생성 (PlaceOrderService 호출)
        Long orderId = placeOrderUseCase.placeOrder(userId, productId, quantity, amount);
        
        // 2. 결제 처리 (PayOrderService 호출)
        Long paymentId = payOrderUseCase.payOrder(userId, orderId);
        
        // 3. 주문 완료 이벤트 발행 (트랜잭션 커밋 후 카프카로 전송됨)
        OrderCompletedEvent event = OrderCompletedEvent.create(orderId, userId, productId, quantity, amount);
        eventPublisher.publishEvent(event);
        
        return paymentId;
    }
}
