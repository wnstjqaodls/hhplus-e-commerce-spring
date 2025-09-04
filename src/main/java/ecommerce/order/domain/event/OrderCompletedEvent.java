package ecommerce.order.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderCompletedEvent(Long orderId, Long userId, Long productId, int quantity, Long amount,
                                  Long timestamp) {

    @JsonCreator
    public OrderCompletedEvent (
        @JsonProperty("orderId") Long orderId,
        @JsonProperty("userId") Long userId,
        @JsonProperty("productId") Long productId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("amount") Long amount,
        @JsonProperty("timestamp") Long timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public static OrderCompletedEvent create (Long orderId, Long userId, Long productId, int quantity, Long amount) {
        return new OrderCompletedEvent(orderId, userId, productId, quantity, amount, System.currentTimeMillis());
    }

    @Override
    public String toString () {
        return "OrderCompletedEvent{" +
            "orderId=" + orderId +
            ", userId=" + userId +
            ", productId=" + productId +
            ", quantity=" + quantity +
            ", amount=" + amount +
            ", timestamp=" + timestamp +
            '}';
    }
}
