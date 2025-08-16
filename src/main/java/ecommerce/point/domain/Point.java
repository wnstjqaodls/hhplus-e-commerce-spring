package ecommerce.point.domain;

public class Point {
    private static final long CHARGE_LIMIT = 1_000_000L;

    private final Long id;

    private long amount;

    public Point () {
        this.id = null; // 초기화 시 ID는 null로 설정
        this.amount = 0; // 초기 잔액은 0으로 설정
    }
    public Point (Long id) {
        this.id = id;
    }

    public Point (Long id, long amount) {
        this.id = id;
        this.amount = amount;
    }

    public long getAmount () {
        return amount;
    }


    public Long getId () {
        return id;
    }

    public void charge (long amount) {
        validateChargeAmount(amount);
        this.amount += amount;

    }

    public void use(long useAmount) {
        // 파라미터 검증
        if (useAmount <= 0) {
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다.");
        }
        
        // 현재객체에서 잔액 가져오기.
        long currentBalance = calculateBalance();

        // 잔액 충분성 검증
        validateUseAmount(currentBalance, useAmount);

        // 실제 포인트 차감 (이 부분이 중요!)
        this.amount -= useAmount;
    }

    public long calculateBalance () {
        return getAmount();
    }

    private void validateChargeAmount (long amount) {
        if (amount > CHARGE_LIMIT) {
            throw new IllegalArgumentException("충전 한도를 초과했습니다.");
        }
    }

    private void validateUseAmount (long currentBalance, long amount) {
        if (currentBalance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }


}
