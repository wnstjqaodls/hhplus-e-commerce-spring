package ecommerce.order.adapter.in.web;

import ecommerce.order.application.port.in.PayOrderUseCase;
import ecommerce.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class PayOrderController {

    private final PayOrderUseCase payOrderUseCase;

    public PayOrderController(PayOrderUseCase payOrderUseCase) {
        this.payOrderUseCase = payOrderUseCase;
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> payOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        try {
            Long paymentId = payOrderUseCase.payOrder(userId, orderId);

            PaymentResponseDto responseDto = PaymentResponseDto.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .userId(userId)
                .build();

            return ResponseEntity.ok(ApiResponse.success(responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}
