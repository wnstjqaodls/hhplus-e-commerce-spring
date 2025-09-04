package ecommerce.coupon.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.common.dto.MessageDto;
import ecommerce.coupon.application.event.CouponIssueEvent;
import ecommerce.coupon.application.port.in.IssueCouponUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CouponIssueConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(CouponIssueConsumer.class);

    private final IssueCouponUseCase issueCouponUseCase;
    private final ObjectMapper objectMapper;

    public CouponIssueConsumer(IssueCouponUseCase issueCouponUseCase, ObjectMapper objectMapper) {
        this.issueCouponUseCase = issueCouponUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "coupon-issue-request")
    public void consumeCouponIssueRequest(MessageDto messageDto) throws Exception {
        log.info("쿠폰 발급 요청 메시지 수신. requestId: {}", messageDto.getId());
        
        // JSON 문자열을 CouponIssueEvent 객체로 변환
        CouponIssueEvent event = objectMapper.readValue(messageDto.getContent(), CouponIssueEvent.class);
        
        // 쿠폰 발급 처리
        Long userCouponId = issueCouponUseCase.issueCoupon(event.userId(), event.couponId());
        
        log.info("쿠폰 발급 완료. userCouponId: {}, requestId: {}", userCouponId, event.requestId());
    }
}