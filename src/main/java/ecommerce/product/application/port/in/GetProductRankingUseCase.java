package ecommerce.product.application.port.in;

import java.util.List;

/**
 * 상품 랭킹 조회 UseCase  
 * - 상품 주문 기반 랭킹을 조회합니다
 */
public interface GetProductRankingUseCase {
    
    /**
     * 오늘의 상위 10개 상품 랭킹을 조회합니다
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
    class ProductRankingInfo {
        private int rank;        // 순위 (1위, 2위, ...)
        private Long productId;  // 상품 ID  
        private int orderCount;  // 주문 수

        public ProductRankingInfo(int rank, Long productId, int orderCount) {
            this.rank = rank;
            this.productId = productId;
            this.orderCount = orderCount;
        }

        public int getRank() {
            return rank;
        }

        public Long getProductId() {
            return productId;
        }

        public int getOrderCount() {
            return orderCount;
        }

        @Override
        public String toString() {
            return String.format("ProductRankingInfo{rank=%d, productId=%d, orderCount=%d}", 
                               rank, productId, orderCount);
        }
    }
}