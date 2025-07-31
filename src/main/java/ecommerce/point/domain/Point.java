package ecommerce.point.domain;

import java.util.ArrayList;

public class Point {
    private static final long CHARGE_LIMIT = 1_000_000L;
    
    private final Long id;

    private ActivityWindow activityWindow;

    public Point(Long id) {
        this.id = id;
        this.activityWindow = new ActivityWindow(new ArrayList<>());
    }

    public Point(Long id, ActivityWindow activityWindow) {
        this.id = id;
        this.activityWindow = activityWindow;
    }

    public void charge(long amount) {
        validateChargeAmount(amount);
        Activity newActivity = new Activity(this.id, amount);
        this.activityWindow.addActivity(newActivity);
    }

    public void use(long amount) {
        long currentBalance = calculateBalance();
        validateUseAmount(currentBalance, amount);
        Activity newActivity = new Activity(this.id, -amount);
        this.activityWindow.addActivity(newActivity);
    }

    private void validateChargeAmount(long amount) {
        if (amount > CHARGE_LIMIT) {
            throw new IllegalArgumentException("충전 한도를 초과했습니다.");
        }
    }

    private void validateUseAmount(long currentBalance, long amount) {
        if (currentBalance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public long calculateBalance() {
        return activityWindow.calculateBalance();
    }

    public ActivityWindow getActivityWindow() {
        return activityWindow;
    }
}
