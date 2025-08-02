package ecommerce.point.domain;

public class Point {
    private static final long CHARGE_LIMIT = 1_000_000L;

    public long getAmount () {
        return amount;
    }

    public void setAmount (long amount) {
        this.amount = amount;
    }

    public Point () {
        this.id = null; // 초기화 시 ID는 null로 설정
        this.amount = 0; // 초기 잔액은 0으로 설정
    }

    private final Long id;

    private long amount;

    public Point(Long id) {
        this.id = id;
    }
    public Point(Long id, long amount) {
        this.id = id;
        this.amount = amount;
    }

    public void charge(long amount) {
        validateChargeAmount(amount);
        this.amount += amount;

    }

    public void use(long amount) {
        long currentBalance = calculateBalance();
        validateUseAmount(currentBalance, amount);
    }

    public long calculateBalance () {
        return getAmount();
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

}
