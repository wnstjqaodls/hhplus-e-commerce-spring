package ecommerce.coupon.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.coupon.application.service.CouponIssueProducer;
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

    private final CouponIssueProducer couponIssueProducer;

    public IssueCouponController(CouponIssueProducer couponIssueProducer) {
        this.couponIssueProducer = couponIssueProducer;
        log.info("IssueCouponController 생성자 실행 - CouponIssueProducer 주입 완료");
    }

    @PostMapping("/{couponId}/issue/{userId}")
    public ResponseEntity<ApiResponse<IssueCouponResponseDto>> issue(@PathVariable("couponId") Long couponId,
                                                                     @PathVariable("userId") Long userId) {
        log.info("POST /coupons/{}/issue/{} 요청 수신 (비동기 처리)", couponId, userId);
        try {
            // 카프카로 쿠폰 발급 요청 발행 (비동기)
            String requestId = couponIssueProducer.publishCouponIssueRequest(userId, couponId);
            
            // 즉시 요청 접수 응답 (실제 쿠폰 발급은 비동기로 처리됨)
            IssueCouponResponseDto response = new IssueCouponResponseDto(null, userId, couponId, requestId, "REQUESTED");
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("쿠폰 발급 요청 처리 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("쿠폰 발급 요청 처리 중 오류가 발생했습니다."));
        }
    }
}


