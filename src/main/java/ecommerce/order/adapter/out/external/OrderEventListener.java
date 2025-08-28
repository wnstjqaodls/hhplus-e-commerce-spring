package ecommerce.order.adapter.out.external;

import ecommerce.order.domain.event.OrderCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * 주문 이벤트 리스너
 * - 주문 완료 이벤트를 처리하여 부가 로직을 수행
 * - @TransactionalEventListener(AFTER_COMMIT)으로 핵심 트랜잭션 완료 후 실행
 * - @Async로 비동기 처리하여 핵심 로직에 영향 없음
 */
@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    /**
     * 주문 정보를 외부 데이터 플랫폼으로 전송
     * - 트랜잭션 커밋 후 비동기로 실행
     * - 실패해도 핵심 주문 로직에 영향 없음
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleOrderDataTransmission(OrderCompletedEvent event) {
        try {
            log.info("주문 정보 외부 플랫폼 전송 시작 - orderId: {}, userId: {}", 
                    event.orderId(), event.userId());
            
            // Mock API 호출 - 실제로는 외부 데이터 플랫폼 API 호출
            sendOrderToDataPlatform(event);
            
            log.info("주문 정보 외부 플랫폼 전송 완료 - orderId: {}", event.orderId());
            
        } catch (Exception e) {
            // 부가 로직 실패는 핵심 로직에 영향을 주지 않음
            log.warn("주문 정보 외부 플랫폼 전송 실패 - orderId: {}, error: {}", 
                    event.orderId(), e.getMessage());
        }
    }

    /**
     * Mock 외부 데이터 플랫폼 API 호출
     */
    private void sendOrderToDataPlatform(OrderCompletedEvent event) {
        try {
            // 실제로는 HTTP 클라이언트로 외부 API 호출
            // 여기서는 시뮬레이션으로 처리 시간 추가
            Thread.sleep(1000); // 외부 API 호출 시뮬레이션
            
            log.info("외부 데이터 플랫폼으로 주문 데이터 전송: orderId={}, userId={}, productId={}, quantity={}, amount={}", 
                    event.orderId(), event.userId(), event.productId(), event.quantity(), event.amount());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("외부 API 호출 중 인터럽트 발생", e);
        }
    }
}