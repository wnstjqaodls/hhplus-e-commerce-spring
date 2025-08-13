package ecommerce.order.application.port.out;

import ecommerce.order.domain.payment.Payment;

public interface SavePayPort {
    Payment savePayment(Payment payment, Long userId);
}
