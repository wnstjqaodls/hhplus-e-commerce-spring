package ecommerce.coupon.application.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CouponIssueEvent(Long userId, Long couponId, String requestId) {

    @JsonCreator
    public CouponIssueEvent (
        @JsonProperty("userId") Long userId,
        @JsonProperty("couponId") Long couponId,
        @JsonProperty("requestId") String requestId) {
        this.userId = userId;
        this.couponId = couponId;
        this.requestId = requestId;
    }

    @Override
    public String toString () {
        return "CouponIssueEvent{" +
            "userId=" + userId +
            ", couponId=" + couponId +
            ", requestId='" + requestId + '\'' +
            '}';
    }
}
