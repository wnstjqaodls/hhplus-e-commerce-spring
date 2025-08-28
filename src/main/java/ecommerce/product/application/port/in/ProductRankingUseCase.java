package ecommerce.product.application.port.in;

import java.util.List;

/**
 * 상품 랭킹 UseCase - 통합 인터페이스
 * - 상품 랭킹 증가 및 조회 기능을 제공합니다
 */
public interface ProductRankingUseCase {
    
    /**
     * 상품의 랭킹 점수를 1 증가시킵니다
     * 
     * @param productId 점수를 증가시킬 상품 ID
     */
    void increaseRanking(Long productId);

    /**
     * 오늘의 Top 10 상품 랭킹을 조회합니다
     * 
     * @return 상위 10개 상품 랭킹 리스트
     */
    List<ProductRankingInfo> getTop10Products();
    
    /**
     * 특정 상품의 현재 순위를 조회합니다
     * 
     * @param productId 순위를 조회할 상품 ID
     * @return 상품 랭킹 정보 (순위가 없으면 null)
     */
    ProductRankingInfo getProductRanking(Long productId);
    
    /**
     * 상품 랭킹 정보를 담는 DTO
     */
    public record ProductRankingInfo(
        int rank,        // 순위 (1위, 2위, ...)
        Long productId,  // 상품 ID  
        int orderCount   // 주문 수
    ) {}
}
