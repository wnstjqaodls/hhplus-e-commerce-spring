package ecommerce.order.adapter.in.web;

import ecommerce.order.application.port.in.PlaceOrderUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PlaceOrderController.class)
public class PlaceOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceOrderUseCase placeOrderUseCase;

    @Test
    @DisplayName("주문_생성_API_성공_테스트")
    public void place_order_api_success() throws Exception {
        //given
        Long expectedOrderId = 123L;
        when(placeOrderUseCase.placeOrder(anyLong(), anyLong(), anyInt(), anyLong()))
            .thenReturn(expectedOrderId);

        String requestJson = """
            {
                "userId": 1,
                "productId": 100,
                "quantity": 2,
                "amount": 30000
            }
            """;

        System.out.println("=== 🛒 Point 패턴 Order Controller 테스트 시작 ===");
        System.out.println("요청 JSON: " + requestJson.trim());

        //when & then
        mockMvc.perform(post("/orders/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(expectedOrderId))
                .andExpect(jsonPath("$.data.userId").value(1));

        // Point 패턴과 동일한 verify 검증
        verify(placeOrderUseCase, times(1)).placeOrder(1L, 100L, 2, 30000L);

        System.out.println("✅ API 호출 성공 검증 완료!");
        System.out.println("🔍 UseCase 호출 검증:");
        System.out.println("  - placeOrder(1L, 100L, 2, 30000L): 1회 호출");
        System.out.println("=== 🛒 Point 패턴 Order Controller 테스트 완료 ===");
    }

    @Test
    @DisplayName("주문_생성_API_실패_테스트")
    public void place_order_api_failure() throws Exception {
        //given
        when(placeOrderUseCase.placeOrder(anyLong(), anyLong(), anyInt(), anyLong()))
            .thenThrow(new IllegalArgumentException("포인트가 부족하여 주문할 수 없습니다"));

        String requestJson = """
            {
                "userId": 1,
                "productId": 100,
                "quantity": 2,
                "amount": 100000
            }
            """;

        System.out.println("=== ❌ Order Controller 실패 케이스 테스트 시작 ===");
        System.out.println("요청 JSON: " + requestJson.trim());

        //when & then
        mockMvc.perform(post("/orders/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("포인트가 부족하여 주문할 수 없습니다"));

        // Point 패턴과 동일한 verify 검증
        verify(placeOrderUseCase, times(1)).placeOrder(1L, 100L, 2, 100000L);

        System.out.println("✅ API 실패 응답 검증 완료!");
        System.out.println("🔍 예외 처리 확인:");
        System.out.println("  - HTTP 400 Bad Request 응답");
        System.out.println("  - 에러 메시지 포함");
        System.out.println("=== ❌ Order Controller 실패 케이스 테스트 완료 ===");
    }
}
