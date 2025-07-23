package ecommerce.point.domain;

import java.time.LocalDateTime;

// Activity 클래스는 포인트 충전 및 사용 내역을 나타냅니다.
public class Activity {

    private final Long pointId;
    private final long activityId;
    private final long amount;
    private final LocalDateTime timestamp;

    public Activity(Long pointId, long amount) {
        this.pointId = pointId;
        this.activityId = 0; // 기본값, 실제 ID는 DB에서 관리
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
    public Activity(long pointId, long activityId, long amount, LocalDateTime timestamp) {
        this.pointId = pointId;
        this.activityId = activityId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Activity (long l, long l1, long l2, LocalDateTime now, Long pointId, long activityId, long amount, LocalDateTime timestamp) {
        this.pointId = pointId;
        this.activityId = activityId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getAmount() {
        return amount;
    }
}
