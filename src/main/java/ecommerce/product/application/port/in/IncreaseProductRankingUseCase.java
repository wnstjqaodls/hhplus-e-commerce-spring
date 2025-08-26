package ecommerce.product.application.port.in;

/**
 * 상품 랭킹 점수 증가 UseCase
 * - 주문 완료 시 해당 상품의 랭킹 점수를 증가시킵니다
 */
public interface IncreaseProductRankingUseCase {
    
    /**
     * 상품의 랭킹 점수를 증가시킵니다
     * 
     * @param productId 점수를 증가시킬 상품 ID
     */
    void increaseRanking(Long productId);
}