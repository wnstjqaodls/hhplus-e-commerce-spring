package ecommerce.order.application.service;

import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.order.application.port.out.LoadPointPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.order.domain.Order;
import ecommerce.order.domain.event.OrderCompletedEvent;
import ecommerce.point.domain.Point;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlaceOrderService implements PlaceOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderService.class);

    private final LoadPointPort loadPointPort;
    private final SaveOrderPort saveOrderPort;
    private final ApplicationEventPublisher eventPublisher;

    public PlaceOrderService(LoadPointPort loadPointPort, SaveOrderPort saveOrderPort, ApplicationEventPublisher eventPublisher) {
        this.loadPointPort = loadPointPort;
        this.saveOrderPort = saveOrderPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public Long placeOrder(Long userId, Long productId, int quantity, Long amount) {
        log.info("PlaceOrderService.placeOrder() 호출됨. userId: {}, productId: {}, quantity: {}, amount: {}", 
                userId, productId, quantity, amount);

        // 1. 사용자 포인트 조회
        log.info("사용자 포인트 조회 시작. userId: {}", userId);
        Point userPoint = loadPointPort.loadPoint(userId);
        log.info("사용자 포인트 조회 완료. 현재 포인트: {}원", userPoint.getAmount());

        // 2. 포인트 충분성 검증 (차감은 PayOrderService에서 처리)
        log.info("포인트 충분성 검증 시작. 필요: {}원, 보유: {}원", amount, userPoint.getAmount());
        if (userPoint.getAmount() < amount) {
            log.error("포인트 부족. 필요: {}원, 보유: {}원", amount, userPoint.getAmount());
            throw new IllegalArgumentException("포인트가 부족하여 주문할 수 없습니다.");
        }
        log.info("포인트 충분성 검증 완료");

        // 3. Order 도메인 객체 생성 (Point.charge()와 동일 패턴)
        log.info("Order 도메인 객체 생성 시작");
        Order order = Order.createOrder(userId, productId, quantity, amount);
        log.info("Order 도메인 객체 생성 완료");

        // 4. 주문 저장 (Point.savePoint()와 동일 패턴)
        log.info("Order 애그리거트 저장 시작");
        Order savedOrder = saveOrderPort.saveOrder(order, userId);
        log.info("Order 애그리거트 저장 완료. 저장된 Order ID: {}, 주문 금액: {}", savedOrder.getId(), savedOrder.getPrice());

        // 5. 주문 완료 이벤트 발행 - 트랜잭션 커밋 후 부가 로직 실행
        log.info("주문 완료 이벤트 발행 시작 - orderId: {}", savedOrder.getId());
        OrderCompletedEvent event = OrderCompletedEvent.of(
            savedOrder.getId(), 
            userId, 
            productId, 
            quantity, 
            amount
        );
        eventPublisher.publishEvent(event);
        log.info("주문 완료 이벤트 발행 완료 - orderId: {}", savedOrder.getId());

        return savedOrder.getId();
    }
}
