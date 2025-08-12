package ecommerce.order.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.order.application.port.in.PlaceOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class PlaceOrderController {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderController.class);

    private final PlaceOrderUseCase placeOrderUseCase;

    public PlaceOrderController(PlaceOrderUseCase placeOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
    }

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderResponseDto>> placeOrder(@RequestBody OrderRequestDto requestDto) {
        log.info("POST /orders/place 요청 수신: userId={}, productId={}, quantity={}, amount={}", 
                requestDto.getUserId(), requestDto.getProductId(), requestDto.getQuantity(), requestDto.getAmount());

        try {
            Long orderId = placeOrderUseCase.placeOrder(
                requestDto.getUserId(), 
                requestDto.getProductId(), 
                requestDto.getQuantity(), 
                requestDto.getAmount()
            );

            OrderResponseDto responseDto = new OrderResponseDto().builder()
                .orderId(orderId)
                .userId(requestDto.getUserId())
                .product(requestDto.getProductId())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getAmount())
                .build();

            log.info("주문 생성 성공: userId={}, orderId={}", requestDto.getUserId(), orderId);
            return ResponseEntity.ok(ApiResponse.success(responseDto));

        } catch (IllegalArgumentException e) {
            log.warn("주문 생성 실패 (잘못된 요청): {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("주문 생성 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }

}


