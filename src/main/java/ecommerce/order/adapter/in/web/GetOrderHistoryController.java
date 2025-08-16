package ecommerce.order.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.order.application.port.in.GetOrderHistoryUseCase;
import ecommerce.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class GetOrderHistoryController {

    private static final Logger log = LoggerFactory.getLogger(GetOrderHistoryController.class);

    private final GetOrderHistoryUseCase getOrderHistoryUseCase;

    public GetOrderHistoryController(GetOrderHistoryUseCase getOrderHistoryUseCase) {
        this.getOrderHistoryUseCase = getOrderHistoryUseCase;
        log.info("GetOrderHistoryController 생성자 실행 - GetOrderHistoryUseCase 주입 완료");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable("orderId") Long orderId) {
        log.info("GET /orders/{} 요청 수신", orderId);

        try {
            Order order = getOrderHistoryUseCase.getOrder(orderId);
            log.info("주문 조회 성공 - orderId: {}", orderId);

            OrderResponseDto response = OrderResponseDto.builder()
                .orderId(order.getId())
                .customerName("User" + order.getUserId())
                // productId, userId, orderTime은 현재 도메인에 없으므로 null 유지
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("주문 조회 실패 (잘못된 요청) - orderId: {}, message: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("주문 조회 중 서버 오류 - orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}


