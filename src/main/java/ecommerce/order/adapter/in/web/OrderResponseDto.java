package ecommerce.order.adapter.in.web;

public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String orderTime;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long orderId, Long userId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderTime = orderTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
