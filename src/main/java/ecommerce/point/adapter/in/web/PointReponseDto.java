package ecommerce.point.adapter.in.web;

public class PointReponseDto {
    private long pointId;
    private long amount;
    private long userId;
    private String dateTime;

    public PointReponseDto(long pointId, long amount, String dateTime, String message) {
        this.pointId = pointId;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public PointReponseDto (long amount, long userId, String dateTime) {
        this.amount = amount;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public PointReponseDto() {
    }

    // 순수한 getter와 setter 메소드
    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getUserId () {
        return userId;
    }

    public void setUserId (long userId) {
        this.userId = userId;
    }
}
