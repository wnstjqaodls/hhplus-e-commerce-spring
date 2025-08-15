package ecommerce.order.adapter.out.persistence;

import ecommerce.order.application.port.out.SavePayPort;
import ecommerce.order.application.port.out.LoadPayPort;
import ecommerce.order.domain.payment.Payment;
import ecommerce.order.domain.payment.PaymentStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class PaymentPersistenceAdapter implements SavePayPort, LoadPayPort {

    private static final Logger log = LoggerFactory.getLogger(PaymentPersistenceAdapter.class);

    private final PaymentRepository paymentRepository;

    public PaymentPersistenceAdapter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment savePayment(Payment payment, Long userId) {
        log.info("PaymentPersistenceAdapter.savePayment() 호출됨. orderId: {}, amount: {}, status: {}", 
                payment.getOrderId(), payment.getAmount(), payment.getStatus());

        PaymentJpaEntity paymentJpaEntity;

        if (payment.getId() == null) {
            log.info("새로운 결제 정보 생성");
            paymentJpaEntity = new PaymentJpaEntity();
            paymentJpaEntity.setCreatedAt(LocalDateTime.now());
        } else {
            log.info("기존 결제 정보 수정. Payment ID: {}", payment.getId());
            paymentJpaEntity = paymentRepository.findById(payment.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Payment with ID " + payment.getId() + " not found"));
        }

        // Payment 도메인 → JPA Entity 매핑
        paymentJpaEntity.setOrderId(payment.getOrderId());
        paymentJpaEntity.setPgId(payment.getPgId());
        paymentJpaEntity.setAmount(payment.getAmount());
        paymentJpaEntity.setPaymentMethod(payment.getPaymentMethod());
        paymentJpaEntity.setPaymentStatus(payment.getStatus().name()); // Enum → String 변환
        paymentJpaEntity.setUpdatedAt(LocalDateTime.now());

        log.info("PaymentJpaEntity 저장 시작");
        PaymentJpaEntity savedPaymentJpaEntity = paymentRepository.save(paymentJpaEntity);
        log.info("PaymentJpaEntity 저장 완료. 저장된 ID: {}", savedPaymentJpaEntity.getId());

        // JPA Entity → Payment 도메인 매핑
        Payment savedPayment = new Payment(
                savedPaymentJpaEntity.getId(),
                savedPaymentJpaEntity.getOrderId(),
                savedPaymentJpaEntity.getPgId(),
                savedPaymentJpaEntity.getAmount(),
                savedPaymentJpaEntity.getPaymentMethod(),
                PaymentStatus.valueOf(savedPaymentJpaEntity.getPaymentStatus()) // String → Enum 변환
        );

        log.info("Payment 도메인 객체 생성 완료. Payment ID: {}, Order ID: {}, Amount: {}원", 
                savedPayment.getId(), savedPayment.getOrderId(), savedPayment.getAmount());

        return savedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment loadPaymentByOrderId(Long orderId) {
        log.info("PaymentPersistenceAdapter.loadPaymentByOrderId() 호출됨. orderId: {}", orderId);

        PaymentJpaEntity paymentJpaEntity = paymentRepository.findByOrderId(orderId).orElse(null);

        if (paymentJpaEntity == null) {
            log.info("주문 ID {}에 해당하는 결제 정보가 없습니다.", orderId);
            return null;
        }

        // JPA Entity → Payment 도메인 매핑
        Payment payment = new Payment(
                paymentJpaEntity.getId(),
                paymentJpaEntity.getOrderId(),
                paymentJpaEntity.getPgId(),
                paymentJpaEntity.getAmount(),
                paymentJpaEntity.getPaymentMethod(),
                PaymentStatus.valueOf(paymentJpaEntity.getPaymentStatus())
        );

        log.info("결제 정보 조회 완료. Payment ID: {}, Amount: {}원, Status: {}", 
                payment.getId(), payment.getAmount(), payment.getStatus());

        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment loadPayment(Long paymentId) {
        log.info("PaymentPersistenceAdapter.loadPayment() 호출됨. paymentId: {}", paymentId);

        PaymentJpaEntity paymentJpaEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment with ID " + paymentId + " not found"));

        // JPA Entity → Payment 도메인 매핑
        Payment payment = new Payment(
                paymentJpaEntity.getId(),
                paymentJpaEntity.getOrderId(),
                paymentJpaEntity.getPgId(),
                paymentJpaEntity.getAmount(),
                paymentJpaEntity.getPaymentMethod(),
                PaymentStatus.valueOf(paymentJpaEntity.getPaymentStatus())
        );

        log.info("결제 정보 조회 완료. Payment ID: {}, Order ID: {}, Amount: {}원", 
                payment.getId(), payment.getOrderId(), payment.getAmount());

        return payment;
    }
}
