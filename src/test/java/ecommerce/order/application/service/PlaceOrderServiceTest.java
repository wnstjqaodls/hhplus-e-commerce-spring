package ecommerce.order.application.service;

import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.order.application.port.out.LoadPointPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.order.domain.Order;
import ecommerce.point.domain.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceOrderServiceTest {

    @Mock
    private LoadPointPort loadPointPort;
    
    @Mock
    private SaveOrderPort saveOrderPort;
    
    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Test
    @DisplayName("충분한_포인트로_주문_성공")
    public void place_order_with_sufficient_points_succeeds() {
        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 30_000L;
        
        Point userPoint = new Point(userId, 50_000L); // 충분한 포인트
        Order expectedOrder = Order.createOrder(userId, productId, quantity, amount);
        
        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        when(saveOrderPort.saveOrder(any(Order.class), eq(userId))).thenReturn(expectedOrder);
        
        System.out.println("=== 🛒 Point 패턴 Order 서비스 테스트 시작 ===");
        System.out.println("사용자 ID: " + userId);
        System.out.println("상품 ID: " + productId);
        System.out.println("주문 수량: " + quantity);
        System.out.println("주문 금액: " + amount + "원");
        System.out.println("사용자 포인트: " + userPoint.getAmount() + "원");

        //when
        Long result = placeOrderService.placeOrder(userId, productId, quantity, amount);

        //then
        assertThat(result).isNotNull();
        
        // Point 패턴과 동일한 verify 검증
        verify(loadPointPort, times(1)).loadPoint(userId);
        verify(saveOrderPort, times(1)).saveOrder(any(Order.class), eq(userId));
        
        System.out.println("✅ 주문 성공! 주문 ID: " + result);
        System.out.println("🔍 호출 검증:");
        System.out.println("  - LoadPointPort.loadPoint(): 1회 호출");
        System.out.println("  - SaveOrderPort.saveOrder(): 1회 호출");
        System.out.println("=== 🛒 Point 패턴 Order 서비스 테스트 완료 ===");
    }

    @Test
    @DisplayName("포인트_부족으로_주문_실패")
    public void place_order_with_insufficient_points_fails() {
        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 50_000L;
        
        Point userPoint = new Point(userId, 30_000L); // 부족한 포인트
        
        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        
        System.out.println("=== ❌ 포인트 부족 실패 케이스 테스트 시작 ===");
        System.out.println("필요 금액: " + amount + "원");
        System.out.println("보유 포인트: " + userPoint.getAmount() + "원");
        System.out.println("부족 금액: " + (amount - userPoint.getAmount()) + "원");

        //when & then
        assertThatThrownBy(() -> {
            placeOrderService.placeOrder(userId, productId, quantity, amount);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("포인트가 부족하여 주문할 수 없습니다");

        // 포인트 조회는 했지만 주문 저장은 하지 않았음을 검증 (Point 패턴과 동일)
        verify(loadPointPort, times(1)).loadPoint(userId);
        verify(saveOrderPort, never()).saveOrder(any(Order.class), any());
        
        System.out.println("✅ 포인트 부족으로 정상 실패!");
        System.out.println("🔍 호출 검증:");
        System.out.println("  - LoadPointPort.loadPoint(): 1회 호출 (포인트 확인)");
        System.out.println("  - SaveOrderPort.saveOrder(): 0회 호출 (실패로 저장 안함)");
        System.out.println("=== ❌ 포인트 부족 실패 케이스 테스트 완료 ===");
    }
}
