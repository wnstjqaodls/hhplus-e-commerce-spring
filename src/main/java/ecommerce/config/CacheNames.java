package ecommerce.config;

/**
 * 캐시 이름 상수 관리
 * - 캐시 키 중복 방지
 * - 캐시 정책 통합 관리
 * - 도메인별 캐시 전략 수립
 */
public final class CacheNames {
    
    // 상품 관련 캐시 (긴 TTL - 자주 변경되지 않음)
    public static final String PRODUCT = "product";
    public static final String PRODUCT_LIST = "productList";
    public static final String PRODUCT_CATEGORY = "productCategory";
    
    // 사용자 포인트 관련 캐시 (짧은 TTL - 실시간 변경)
    public static final String USER_POINT = "userPoint";
    public static final String POINT_HISTORY = "pointHistory";
    
    // 주문 내역 캐시 (중간 TTL - 주문 상태 변경)
    public static final String ORDER_HISTORY = "orderHistory";
    public static final String ORDER_STATUS = "orderStatus";
    
    // 쿠폰 관련 캐시
    public static final String COUPON = "coupon";
    public static final String USER_COUPON = "userCoupon";
    
    // 공통 캐시
    public static final String COMMON_CONFIG = "commonConfig";
    
    private CacheNames() {
        throw new IllegalStateException("Utility class");
    }
}
