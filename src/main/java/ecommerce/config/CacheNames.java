package ecommerce.config;

/**
 * 캐시 이름 상수 관리
 * - 캐시 키 중복 방지
 * - 캐시 정책 통합 관리
 */
public final class CacheNames {
    
    // 상품 관련 캐시
    public static final String PRODUCT = "product";
    public static final String PRODUCT_LIST = "productList";
    
    // 사용자 포인트 관련 캐시 (짧은 TTL)
    public static final String USER_POINT = "userPoint";
    
    // 주문 내역 캐시
    public static final String ORDER_HISTORY = "orderHistory";
    
    private CacheNames() {
        throw new IllegalStateException("Utility class");
    }
}
