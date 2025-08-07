package ecommerce.coupon.domain;

public class Coupon {
    
    private Long id;
    private int totalQuantity;
    private int remainingQuantity;

    public Coupon(Long id, int totalQuantity, int remainingQuantity) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public static Coupon create(int totalQuantity) {
        validateCoupon(totalQuantity);
        
        return new Coupon(
            null, // ID는 자동 생성
            totalQuantity,
            totalQuantity // 초기에는 전체 수량과 동일
        );
    }

    public boolean canIssue() {
        return remainingQuantity > 0;
    }

    public void issue() {
        if (!canIssue()) {
            throw new IllegalArgumentException("발급 가능한 쿠폰이 없습니다");
        }
        this.remainingQuantity--;
    }

    private static void validateCoupon(int totalQuantity) {
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException("쿠폰 수량은 1 이상이어야 합니다");
        }
    }

    // Getters
    public Long getId() { return id; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
}
