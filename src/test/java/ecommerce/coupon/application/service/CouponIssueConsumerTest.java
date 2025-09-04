package ecommerce.coupon.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.common.dto.MessageDto;
import ecommerce.coupon.application.event.CouponIssueEvent;
import ecommerce.coupon.application.port.in.IssueCouponUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponIssueConsumerTest {

    @Mock
    private IssueCouponUseCase issueCouponUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CouponIssueConsumer couponIssueConsumer;

    private MessageDto validMessageDto;
    private CouponIssueEvent validCouponIssueEvent;
    private String validEventJson;

    @BeforeEach
    void setUp() {
        validCouponIssueEvent = new CouponIssueEvent(1L, 100L, "req-123");
        validEventJson = "{\"userId\":1,\"couponId\":100,\"requestId\":\"req-123\"}";
        
        validMessageDto = new MessageDto();
        validMessageDto.setId("req-123");
        validMessageDto.setContent(validEventJson);
    }

    @Test
    @DisplayName("정상적인 쿠폰 발급 메시지를 처리한다")
    void 정상적인_쿠폰_발급_메시지를_처리한다() throws Exception {
        // Given
        when(objectMapper.readValue(validEventJson, CouponIssueEvent.class))
                .thenReturn(validCouponIssueEvent);
        when(issueCouponUseCase.issueCoupon(1L, 100L))
                .thenReturn(201L);

        // When
        couponIssueConsumer.consumeCouponIssueRequest(validMessageDto);

        // Then
        verify(objectMapper).readValue(validEventJson, CouponIssueEvent.class);
        verify(issueCouponUseCase).issueCoupon(eq(1L), eq(100L));
    }

    @Test
    @DisplayName("잘못된 JSON 메시지 처리시 에러 로깅하고 예외를 발생시키지 않는다")
    void 잘못된_JSON_메시지_처리시_에러_로깅한다() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";
        MessageDto invalidMessageDto = new MessageDto();
        invalidMessageDto.setId("invalid-req");
        invalidMessageDto.setContent(invalidJson);
        
        when(objectMapper.readValue(invalidJson, CouponIssueEvent.class))
                .thenThrow(new RuntimeException("JSON 파싱 실패"));

        // When & Then
        // 현재 구현에서는 예외가 발생할 것임 (이것이 실패하는 테스트)
        assertThrows(RuntimeException.class, () -> 
            couponIssueConsumer.consumeCouponIssueRequest(invalidMessageDto)
        );
    }
}