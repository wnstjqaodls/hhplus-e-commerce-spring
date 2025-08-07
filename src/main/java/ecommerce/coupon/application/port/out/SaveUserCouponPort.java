package ecommerce.coupon.application.port.out;

public interface SaveUserCouponPort {
    Long saveUserCoupon(Long userId, Long couponId);
}
