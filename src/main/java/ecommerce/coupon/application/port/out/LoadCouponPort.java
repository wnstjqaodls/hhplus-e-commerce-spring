package ecommerce.coupon.application.port.out;

import ecommerce.coupon.domain.Coupon;

public interface LoadCouponPort {
    Coupon loadCoupon(Long couponId);
}
