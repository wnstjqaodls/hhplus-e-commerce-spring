package ecommerce.order.application.service;

import ecommerce.order.application.port.in.GetOrderHistoryUseCase;
import ecommerce.order.application.port.out.LoadOrderPort;
import ecommerce.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetOrderHistoryService implements GetOrderHistoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetOrderHistoryService.class);
    
    private final LoadOrderPort loadOrderPort;

    public GetOrderHistoryService(LoadOrderPort loadOrderPort) {
        this.loadOrderPort = loadOrderPort;
        log.info("GetOrderHistoryService 생성자 실행 - LoadOrderPort 주입 완료");
    }

    @Override
    public Order getOrder(Long orderId) {
        log.info("주문 조회 시작 - orderId: {}", orderId);
        
        if (orderId == null) {
            log.warn("주문 조회 실패 - orderId가 null입니다");
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }
        
        try {
            Order order = loadOrderPort.loadOrder(orderId);
            log.info("주문 조회 성공 - orderId: {}, customerName: {}, quantity: {}", 
                    orderId, order.getCustomerName(), order.getQuantity());
            return order;
        } catch (Exception e) {
            log.error("주문 조회 중 오류 발생 - orderId: {}, error: {}", orderId, e.getMessage());
            throw e;
        }
    }
}
