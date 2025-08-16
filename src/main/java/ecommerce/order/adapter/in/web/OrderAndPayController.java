package ecommerce.order.adapter.in.web;

import ecommerce.order.application.port.in.OrderAndPayUseCase;
import ecommerce.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderAndPayController {

    private final OrderAndPayUseCase orderAndPayUseCase;

    public OrderAndPayController(OrderAndPayUseCase orderAndPayUseCase) {
        this.orderAndPayUseCase = orderAndPayUseCase;
    }

    @PostMapping("/order-and-pay")
    public ResponseEntity<ApiResponse<OrderResponseDto>> orderAndPay(@RequestBody OrderRequestDto requestDto) {
        try {
            Long paymentId = orderAndPayUseCase.orderAndPay(
                requestDto.getUserId(), 
                requestDto.getProductId(), 
                requestDto.getQuantity(), 
                requestDto.getAmount()
            );

            OrderResponseDto responseDto = OrderResponseDto.builder()
                .orderId(null) // 주문 ID는 내부적으로 생성되므로 null
                .userId(requestDto.getUserId())
                .product(requestDto.getProductId())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getAmount())
                .build();

            return ResponseEntity.ok(ApiResponse.success(responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}
