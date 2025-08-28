package ecommerce.product.application.port.out;

/**
 * 상품 랭킹 저장 Port
 * - Redis에 상품 랭킹 데이터를 저장하기 위한 인터페이스
 */
public interface SaveProductRankingPort {
    
    /**
     * 상품의 랭킹 점수를 1 증가시킵니다
     * 
     * @param productId 점수를 증가시킬 상품 ID
     * @return 증가 후 총 점수
     */
    Double increaseRankingScore(Long productId);
}