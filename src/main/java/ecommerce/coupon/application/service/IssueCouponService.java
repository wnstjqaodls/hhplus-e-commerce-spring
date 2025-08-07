package ecommerce.coupon.application.service;

import ecommerce.coupon.application.port.in.IssueCouponUseCase;
import ecommerce.coupon.application.port.out.LoadCouponPort;
import ecommerce.coupon.application.port.out.SaveUserCouponPort;
import ecommerce.coupon.domain.Coupon;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IssueCouponService implements IssueCouponUseCase {

    private static final Logger log = LoggerFactory.getLogger(IssueCouponService.class);

    private final LoadCouponPort loadCouponPort;
    private final SaveUserCouponPort saveUserCouponPort;

    public IssueCouponService(LoadCouponPort loadCouponPort, SaveUserCouponPort saveUserCouponPort) {
        this.loadCouponPort = loadCouponPort;
        this.saveUserCouponPort = saveUserCouponPort;
    }

    @Transactional
    @Override
    public Long issueCoupon(Long userId, Long couponId) {
        log.info("IssueCouponService.issueCoupon() 호출됨. userId: {}, couponId: {}", userId, couponId);

        // 1. 쿠폰 조회
        log.info("쿠폰 조회 시작. couponId: {}", couponId);
        Coupon coupon = loadCouponPort.loadCoupon(couponId);
        log.info("쿠폰 조회 완료. 남은 수량: {}", coupon.getRemainingQuantity());

        // 2. 발급 가능 여부 확인 및 발급
        log.info("쿠폰 발급 가능 여부 확인");
        coupon.issue(); // 도메인 로직에서 검증 및 수량 차감
        log.info("쿠폰 발급 완료. 남은 수량: {}", coupon.getRemainingQuantity());

        // 3. 사용자 쿠폰 저장
        log.info("사용자 쿠폰 저장 시작");
        Long userCouponId = saveUserCouponPort.saveUserCoupon(userId, couponId);
        log.info("사용자 쿠폰 저장 완료. userCouponId: {}", userCouponId);

        return userCouponId;
    }
}
