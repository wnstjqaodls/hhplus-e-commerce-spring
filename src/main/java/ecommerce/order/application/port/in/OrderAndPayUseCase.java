package ecommerce.order.application.port.in;

// 파사드가 없기에 해당 유즈케이스에서 여러 유즈케이스를 호출함.
public interface OrderAndPayUseCase {

    Long orderAndPay(Long userId, Long productId, int quantity, Long amount);

}
