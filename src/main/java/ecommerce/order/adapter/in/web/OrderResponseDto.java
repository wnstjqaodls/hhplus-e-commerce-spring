package ecommerce.order.adapter.in.web;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String customerName;
    private Long product;
    private int quantity;
    private Long price;
    private LocalDateTime orderTime;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long orderId, Long userId, String customerName, Long product, int quantity, Long price, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
}
