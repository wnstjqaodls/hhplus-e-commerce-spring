package ecommerce.order.application.port.out;

import ecommerce.order.domain.payment.Payment;

public interface LoadPayPort {
    
    /**
     * 결제 ID로 결제 정보를 조회합니다.
     * @param paymentId 결제 ID
     * @return 결제 정보
     * @throws IllegalArgumentException 결제 정보가 없을 때
     */
    Payment loadPayment(Long paymentId);
    
    /**
     * 주문 ID로 결제 정보를 조회합니다.
     * @param orderId 주문 ID
     * @return 결제 정보 (없으면 null)
     */
    Payment loadPaymentByOrderId(Long orderId);
}
