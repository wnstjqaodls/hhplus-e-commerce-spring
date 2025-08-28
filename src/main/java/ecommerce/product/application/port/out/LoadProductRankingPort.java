package ecommerce.product.application.port.out;

import ecommerce.product.application.port.in.ProductRankingUseCase.ProductRankingInfo;
import java.util.List;

/**
 * 상품 랭킹 조회 Port
 * - Redis에서 상품 랭킹 데이터를 조회하기 위한 인터페이스
 */
public interface LoadProductRankingPort {
    
    /**
     * 오늘의 상위 N개 상품 랭킹을 조회합니다
     * 
     * @param limit 조회할 상품 수 (예: 10개)
     * @return 상위 N개 상품 랭킹 리스트
     */
    List<ProductRankingInfo> loadTopProducts(int limit);
    
    /**
     * 특정 상품의 현재 순위와 주문 수를 조회합니다
     * 
     * @param productId 조회할 상품 ID
     * @return 상품 랭킹 정보 (순위가 없으면 null)
     */
    ProductRankingInfo loadProductRanking(Long productId);
}