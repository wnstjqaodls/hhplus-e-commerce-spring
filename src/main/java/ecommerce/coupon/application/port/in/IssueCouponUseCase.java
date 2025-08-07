package ecommerce.coupon.application.port.in;

public interface IssueCouponUseCase {
    Long issueCoupon(Long userId, Long couponId);
}
