package ecommerce.order.domain;

import ecommerce.point.domain.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    // domain 테스트용 객체는 어떻게하지? 메서드가아니라 도메인 객체인데

    @Test
    @DisplayName("특정사용자가_특정상품을주문_포인트가부족하여_실패한다")
    public void A_certain_user_orders_specific_product_Point_shortage_fails(){
        //given
        Long userId = 1L;
        Long productId = 2L;
        int quantity = 2;
        Long amount = 20_000L; // 상품 금액
        Point point = new Point(userId,15_000L); // 사용자의 포인트 잔액

        //when
        Order.createOrder(
            userId,
            productId,
            quantity,
            amount
        );

        //then
        assertThatThrownBy(()-> {
            Order.createOrder(
                userId,
                productId,
                quantity,
                amount
            );
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("포인트가 부족하여 주문할 수 없습니다.");

    }

    @Test
    @DisplayName("특정사용자가_상품을_주문하는것에_성공한다")
    public void sucessed_order_specific_user(){

        //given
        Long userId = 1L;
        Long productId = 2L;
        int quantity = 2;
        Long amount = 20_000L;

        Point point = new Point(userId,15_000L);

        //when
        Order orderResult = Order.createOrder(userId,productId,quantity,amount);

        //then
        assertThat(orderResult).isNotNull();
        assertThat(orderResult.getId()).isNull(); // ID 는 자동생성이기에 null 이어야함
        assertThat(orderResult.getQuantity()).isEqualTo(quantity);
        assertThat(orderResult.getPrice()).isEqualTo(amount);


    }



}
