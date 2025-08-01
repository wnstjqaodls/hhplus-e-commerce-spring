package ecommerce.point.adapter.in.web;

public class PointRequestDto {
    private Long userId;
    private long amount;

    public PointRequestDto() {
    }

    public PointRequestDto(Long userId, long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
