package ecommerce.coupon.adapter.in.web;

public class IssueCouponResponseDto {
    private Long userCouponId;
    private Long userId;
    private Long couponId;
    private String requestId;
    private String status;

    public IssueCouponResponseDto() {}

    public IssueCouponResponseDto(Long userCouponId, Long userId, Long couponId) {
        this.userCouponId = userCouponId;
        this.userId = userId;
        this.couponId = couponId;
    }

    public IssueCouponResponseDto(Long userCouponId, Long userId, Long couponId, String requestId, String status) {
        this.userCouponId = userCouponId;
        this.userId = userId;
        this.couponId = couponId;
        this.requestId = requestId;
        this.status = status;
    }

    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


