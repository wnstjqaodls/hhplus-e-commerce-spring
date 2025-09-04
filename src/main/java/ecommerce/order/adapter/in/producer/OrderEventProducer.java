package ecommerce.order.adapter.in.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.common.dto.MessageDto;
import ecommerce.config.KafkaConfig;
import ecommerce.order.application.event.OrderCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Service
public class OrderEventProducer {
    
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public OrderEventProducer(KafkaTemplate<String, MessageDto> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = String.valueOf(event.userId()); // 사용자 ID를 키로 사용
            
            // MessageDto로 래핑
            MessageDto messageDto = new MessageDto();
            messageDto.setId(String.valueOf(event.orderId()));
            messageDto.setContent(eventJson);
            
            log.info("주문 완료 이벤트 발행 시작. orderId: {}, userId: {}", 
                    event.orderId(), event.userId());
            
            kafkaTemplate.send(KafkaConfig.ORDER_COMPLETED_TOPIC, key, messageDto)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("주문 완료 이벤트 발행 성공. orderId: {}, offset: {}", 
                                    event.orderId(), result.getRecordMetadata().offset());
                        } else {
                            log.error("주문 완료 이벤트 발행 실패. orderId: {}, error: {}", 
                                    event.orderId(), exception.getMessage(), exception);
                        }
                    });
            
        } catch (JsonProcessingException e) {
            log.error("주문 완료 이벤트 JSON 변환 실패. orderId: {}", event.orderId(), e);
        }
    }

}
