package ecommerce.coupon.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.common.dto.MessageDto;
import ecommerce.config.KafkaConfig;
import ecommerce.coupon.application.event.CouponIssueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponIssueProducer {
    
    private static final Logger log = LoggerFactory.getLogger(CouponIssueProducer.class);
    
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public CouponIssueProducer(KafkaTemplate<String, MessageDto> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String publishCouponIssueRequest(Long userId, Long couponId) {
        String requestId = UUID.randomUUID().toString();
        CouponIssueEvent event = new CouponIssueEvent(userId, couponId, requestId);
        
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = String.valueOf(couponId); // 쿠폰 ID를 키로 사용하여 동일 쿠폰의 순차 처리 보장

            MessageDto messageDto = new MessageDto();
            messageDto.setId(requestId);
            messageDto.setContent(eventJson);
            
            log.info("쿠폰 발급 요청 발행 시작. couponId: {}, userId: {}, requestId: {}", 
                    couponId, userId, requestId);
            
            kafkaTemplate.send(KafkaConfig.COUPON_ISSUE_REQUEST_TOPIC, key, messageDto)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("쿠폰 발급 요청 발행 성공. requestId: {}, offset: {}", 
                                    requestId, result.getRecordMetadata().offset());
                        } else {
                            log.error("쿠폰 발급 요청 발행 실패. requestId: {}, error: {}", 
                                    requestId, exception.getMessage(), exception);
                        }
                    });
            
            return requestId;
            
        } catch (JsonProcessingException e) {
            log.error("쿠폰 발급 이벤트 JSON 변환 실패. userId: {}, couponId: {}", 
                    userId, couponId, e);
            throw new RuntimeException("쿠폰 발급 요청 처리 중 오류가 발생했습니다.", e);
        }
    }
}
