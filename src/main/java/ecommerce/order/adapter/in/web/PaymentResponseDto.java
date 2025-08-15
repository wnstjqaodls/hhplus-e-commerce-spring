package ecommerce.order.adapter.in.web;

public class PaymentResponseDto {
    
    private Long paymentId;
    private Long orderId;
    private Long userId;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long paymentId, Long orderId, Long userId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
    }

    public static PaymentResponseDtoBuilder builder() {
        return new PaymentResponseDtoBuilder();
    }

    public static class PaymentResponseDtoBuilder {
        private Long paymentId;
        private Long orderId;
        private Long userId;

        PaymentResponseDtoBuilder() {
        }

        public PaymentResponseDtoBuilder paymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentResponseDtoBuilder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentResponseDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public PaymentResponseDto build() {
            PaymentResponseDto dto = new PaymentResponseDto();
            dto.paymentId = this.paymentId;
            dto.orderId = this.orderId;
            dto.userId = this.userId;
            return dto;
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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
}
