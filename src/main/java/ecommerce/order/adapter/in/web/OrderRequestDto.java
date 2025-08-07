package ecommerce.order.adapter.in.web;

public class OrderRequestDto {
    private Long userId;
    private Long productId;
    private int quantity;
    private Long amount;

    public OrderRequestDto() {
    }

    public OrderRequestDto(Long userId, Long productId, int quantity, Long amount) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
