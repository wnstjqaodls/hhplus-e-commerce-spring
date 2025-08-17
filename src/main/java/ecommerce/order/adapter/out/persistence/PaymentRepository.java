package ecommerce.order.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {
    
    /**
     * 주문 ID로 결제 정보를 조회합니다.
     * @param orderId 주문 ID
     * @return 결제 정보 (Optional)
     */
    Optional<PaymentJpaEntity> findByOrderId(Long orderId);
    
    /**
     * 특정 상태의 결제 목록을 조회합니다.
     * @param paymentStatus 결제 상태
     * @return 결제 정보 목록
     */
    java.util.List<PaymentJpaEntity> findByPaymentStatus(String paymentStatus);
    
    /**
     * 주문 ID와 결제 상태로 결제 정보를 조회합니다.
     * @param orderId 주문 ID
     * @param paymentStatus 결제 상태
     * @return 결제 정보 (Optional)
     */
    Optional<PaymentJpaEntity> findByOrderIdAndPaymentStatus(Long orderId, String paymentStatus);
}
