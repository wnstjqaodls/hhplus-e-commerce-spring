package ecommerce.order.domain;

public class Order {

    private Long id;
    private String customerName;
    private String product;
    private int quantity;
    private Long price;

    public Order (Long id, String customerName, String product, int quantity, Long price) {
        this.id = id;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId () {
        return id;
    }

    public String getCustomerName () {
        return customerName;
    }

    public String getProduct () {
        return product;
    }

    public int getQuantity () {
        return quantity;
    }

    public double getPrice () {
        return price;
    }

    public static Order createOrder (Long userId, Long productId, int quantity, Long amount) {

        validateOrder(userId, productId, quantity, amount);

        if (amount < 0) {
            throw new IllegalArgumentException("포인트가 부족하여 주문할 수 없습니다.");
        }

        return new Order(
            null, // ID는 자동 생성
            "Customer Name", // 고객 이름은 실제로는 DB에서 조회해야 함
            "Product Name", // 상품 이름은 실제로는 DB에서 조회해야 함
            quantity,
            amount
        );
    }

    public static Order cancelOrder (Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID가 유효하지 않습니다.");
        }
        // 주문 취소 로직 구현
        return new Order(orderId, "Customer Name", "Product Name", 0, 0L);
    }

    public static Order updateOrder (Long orderId, Long productId, int quantity, Long amount) {
        if (orderId == null || productId == null || quantity <= 0 || amount <= 0) {
            throw new IllegalArgumentException("유효하지 않은 주문 정보입니다.");
        }
        // 주문 업데이트 로직 구현
        return new Order(orderId, "Customer Name", "Product Name", quantity, amount);
    }

    private static void validateOrder (Long userId, Long productId, int quantity, Long amount) {
        if (userId == null || productId == null || quantity <= 0 || amount <= 0) {
            throw new IllegalArgumentException("유효하지 않은 주문 정보입니다.");
        }
    }



}
