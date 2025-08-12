package ecommerce.coupon.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.coupon.application.port.in.IssueCouponUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
public class IssueCouponController {

    private static final Logger log = LoggerFactory.getLogger(IssueCouponController.class);

    private final IssueCouponUseCase issueCouponUseCase;

    public IssueCouponController(IssueCouponUseCase issueCouponUseCase) {
        this.issueCouponUseCase = issueCouponUseCase;
        log.info("IssueCouponController 생성자 실행 - IssueCouponUseCase 주입 완료");
    }

    @PostMapping("/{couponId}/issue/{userId}")
    public ResponseEntity<ApiResponse<IssueCouponResponseDto>> issue(@PathVariable("couponId") Long couponId,
                                                                     @PathVariable("userId") Long userId) {
        log.info("POST /coupons/{}/issue/{} 요청 수신", couponId, userId);
        try {
            Long userCouponId = issueCouponUseCase.issueCoupon(userId, couponId);
            IssueCouponResponseDto response = new IssueCouponResponseDto(userCouponId, userId, couponId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("쿠폰 발급 실패(잘못된 요청) - {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("쿠폰 발급 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}


