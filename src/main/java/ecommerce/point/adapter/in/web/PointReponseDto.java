package ecommerce.point.adapter.in.web;

public class PointReponseDto {
    private long pointId;
    private long amount;
    private String dateTime;
    private String message;

    public PointReponseDto(long pointId, long amount, String dateTime, String message) {
        this.pointId = pointId;
        this.amount = amount;
        this.dateTime = dateTime;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
