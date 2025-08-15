package ecommerce.order.application.service;

import ecommerce.order.application.port.in.PayOrderUseCase;
import ecommerce.order.application.port.out.LoadOrderPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.order.application.port.out.LoadPayPort;
import ecommerce.order.application.port.out.SavePayPort;
import ecommerce.order.domain.Order;
import ecommerce.order.domain.payment.Payment;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PayOrderService implements PayOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(PayOrderService.class);

    private final LoadPointPort loadPointPort;
    private final LoadOrderPort loadOrderPort;
    private final SavePointPort savePointPort;
    private final SaveOrderPort saveOrderPort;
    private final LoadPayPort loadPayPort;
    private final SavePayPort savePayPort;

    public PayOrderService(LoadPointPort loadPointPort, LoadOrderPort loadOrderPort,
                           SavePointPort savePointPort, SaveOrderPort saveOrderPort,
                           LoadPayPort loadPayPort, SavePayPort savePayPort) {
        this.loadPointPort = loadPointPort;
        this.loadOrderPort = loadOrderPort;
        this.savePointPort = savePointPort;
        this.saveOrderPort = saveOrderPort;
        this.loadPayPort = loadPayPort;
        this.savePayPort = savePayPort;
    }

    @Override
    @Transactional
    public Long payOrder(Long userId, Long orderId) {
        log.info("PayOrderService.payOrder() 호출됨. userId: {}, orderId: {}", userId, orderId);
        
        // 1. 주문 정보 조회
        log.info("주문 정보 조회 시작. orderId: {}", orderId);
        Order order = loadOrderPort.loadOrder(orderId);
        log.info("주문 정보 조회 완료. 주문 금액: {}원", order.getPrice());
        
        // 2. 현재 포인트 조회
        log.info("사용자 포인트 조회 시작. userId: {}", userId);
        Point userPoint = loadPointPort.loadPoint(userId);
        log.info("사용자 포인트 조회 완료. 현재 포인트: {}원", userPoint.getAmount());
        
        // 3. 포인트 차감 (현재 use 메서드 시그니처에 맞게 호출)
        log.info("포인트 차감 시작. 차감할 금액: {}원", order.getPrice());
        userPoint.use(order.getPrice());
        log.info("포인트 차감 완료. 남은 포인트: {}원", userPoint.getAmount());
        
        // 4. 차감된 포인트 저장
        log.info("차감된 포인트 저장 시작");
        Point savedPoint = savePointPort.savePoint(userPoint, userId);
        log.info("차감된 포인트 저장 완료. 최종 잔액: {}원", savedPoint.getAmount());
        
        // 5. 결제 정보 생성 및 저장
        log.info("결제 정보 생성 시작");
        Payment payment = Payment.createPayment(orderId, order.getPrice());
        Payment savedPayment = savePayPort.savePayment(payment, userId);
        log.info("결제 정보 저장 완료. Payment ID: {}", savedPayment.getId());
        
        return savedPayment.getId();
    }
}
