package ecommerce.point.domain;

public class Point {
    private static final long CHARGE_LIMIT = 1_000_000L;
    
    private final Long id;

    public Point(Long id) {
        this.id = id;
    }

    /**
     * 포인트 충전 - 새로운 Activity를 생성하여 반환
     * 실제 저장은 애플리케이션 서비스에서 담당
     */
    public Activity charge(ActivityWindow activityWindow, long amount) {
        validateChargeAmount(amount);
        // 새로운 충전 Activity 생성 (아직 저장되지 않은 상태)
        return new Activity(this.id, amount);
    }

    /**
     * 포인트 사용 - 새로운 Activity를 생성하여 반환  
     * 실제 저장은 애플리케이션 서비스에서 담당
     */
    public Activity use(ActivityWindow activityWindow, long amount) {
        long currentBalance = activityWindow.calculateBalance();
        validateUseAmount(currentBalance, amount);
        // 새로운 사용 Activity 생성 (아직 저장되지 않은 상태)
        return new Activity(this.id, -amount);
    }

    private void validateChargeAmount(long amount) {
        if (amount > CHARGE_LIMIT) {
            throw new IllegalArgumentException();
        }
    }

    private void validateUseAmount(long currentBalance, long amount) {
        if (currentBalance < amount) {
            throw new IllegalArgumentException();
        }
    }

    public Long getId() {
        return id;
    }

    // 잔액을 알고싶으면 ActivityWindow를 파라미터로 받아야함
    public long getBalance(ActivityWindow activityWindow) {
        return activityWindow.calculateBalance(); // 항상 계산해서 구함
    }
}
