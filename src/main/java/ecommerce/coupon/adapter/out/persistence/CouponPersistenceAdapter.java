package ecommerce.coupon.adapter.out.persistence;

import ecommerce.coupon.application.port.out.LoadCouponPort;
import ecommerce.coupon.application.port.out.SaveUserCouponPort;
import ecommerce.coupon.domain.Coupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CouponPersistenceAdapter implements LoadCouponPort, SaveUserCouponPort {

    private static final Logger log = LoggerFactory.getLogger(CouponPersistenceAdapter.class);

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponPersistenceAdapter(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon loadCoupon(Long couponId) {
        log.debug("[Adapter] loadCoupon 시작 - id: {}", couponId);
        CouponJpaEntity entity = couponRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다. id=" + couponId));
        Coupon coupon = toDomain(entity);
        log.debug("[Adapter] loadCoupon 완료 - id: {}, remaining: {}", couponId, coupon.getRemainingQuantity());
        return coupon;
    }

    @Override
    @Transactional
    public Long saveUserCoupon(Long userId, Long couponId) {
        log.debug("[Adapter] saveUserCoupon 시작 - userId: {}, couponId: {}", userId, couponId);
        UserCouponJpaEntity entity = new UserCouponJpaEntity();
        entity.setUserId(userId);
        entity.setCouponId(couponId);
        Long id = userCouponRepository.save(entity).getId();
        log.debug("[Adapter] saveUserCoupon 완료 - userCouponId: {}", id);
        return id;
    }

    private Coupon toDomain(CouponJpaEntity entity) {
        return new Coupon(entity.getId(), entity.getTotalQuantity(), entity.getRemainingQuantity());
    }
}


