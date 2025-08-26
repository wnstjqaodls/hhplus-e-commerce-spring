package ecommerce.product.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 상품 랭킹 서비스
 * - Redis SortedSet을 사용하여 상품 주문 랭킹을 관리합니다
 * - 가장 많이 주문된 상품 순으로 랭킹을 제공합니다
 */
@Service
public class ProductRankingService {

    private static final Logger log = LoggerFactory.getLogger(ProductRankingService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 1단계: 상품 주문 수를 1 증가시킵니다
     * 
     * @param productId 주문된 상품 ID
     */
    public void increaseOrderCount(Long productId) {
        // ========================================
        // 1. 오늘 날짜로 랭킹 키 생성
        // ========================================
        // 예: "product:ranking:order:daily:20240824"
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String rankingKey = "product:ranking:order:daily:" + dateString;
        
        // ========================================
        // 2. 상품 키 생성
        // ========================================
        // 예: "product:123"
        String productKey = "product:" + productId;
        
        // ========================================
        // 3. Redis SortedSet에 점수 1 증가
        // ========================================
        // incrementScore: 해당 member의 점수를 지정된 값만큼 증가
        // 만약 member가 없으면 새로 생성하고 점수 설정
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Double newScore = zSetOps.incrementScore(rankingKey, productKey, 1.0);
        
        log.info("상품 {} 주문 수 증가 완료. 현재 주문 수: {}", productId, newScore);
        
        // ========================================
        // 4. TTL 설정 (하루 후 자동 삭제)
        // ========================================
        // 일일 랭킹이므로 다음날 자정에 만료되도록 설정
        // 86400초 = 24시간
        redisTemplate.expire(rankingKey, java.time.Duration.ofDays(1));
    }

    /**
     * 2단계: 오늘의 Top 10 상품 랭킹을 조회합니다
     * 
     * @return 상위 10개 상품 랭킹 리스트
     */
    public List<ProductRankingInfo> getTop10Products() {
        // ========================================
        // 1. 오늘 날짜 랭킹 키 생성
        // ========================================
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String rankingKey = "product:ranking:order:daily:" + dateString;
        
        log.info("Top 10 상품 랭킹 조회 시작. 키: {}", rankingKey);
        
        // ========================================
        // 2. Redis에서 상위 10개 데이터 조회
        // ========================================
        // reverseRangeWithScores: 점수 높은 순으로 조회
        // 0~9 = 1위부터 10위까지 (0-based index)
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> top10Results = zSetOps.reverseRangeWithScores(rankingKey, 0, 9);
        
        // ========================================
        // 3. Redis 결과를 우리가 사용할 형태로 변환
        // ========================================
        List<ProductRankingInfo> rankings = new ArrayList<>();
        int rank = 1; // 1위부터 시작
        
        for (ZSetOperations.TypedTuple<Object> result : top10Results) {
            // "product:123" → "123" 추출
            String productKey = result.getValue().toString();
            String productIdString = productKey.replace("product:", "");
            Long productId = Long.parseLong(productIdString);
            
            // 주문 수 (점수)
            int orderCount = result.getScore().intValue();
            
            // 랭킹 정보 객체 생성
            ProductRankingInfo rankingInfo = new ProductRankingInfo(rank, productId, orderCount);
            rankings.add(rankingInfo);
            
            log.info("{}위: 상품 ID {}, 주문 수 {}", rank, productId, orderCount);
            rank++;
        }
        
        log.info("Top 10 상품 랭킹 조회 완료. 총 {}개 상품", rankings.size());
        return rankings;
    }

    /**
     * 3단계: 특정 상품의 현재 순위와 주문 수를 조회합니다
     * 
     * @param productId 조회할 상품 ID
     * @return 상품의 랭킹 정보 (순위가 없으면 null)
     */
    public ProductRankingInfo getProductRanking(Long productId) {
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String rankingKey = "product:ranking:order:daily:" + dateString;
        String productKey = "product:" + productId;
        
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        
        // 상품의 현재 점수(주문 수) 조회
        Double score = zSetOps.score(rankingKey, productKey);
        if (score == null) {
            log.info("상품 {}는 오늘 주문이 없어서 랭킹에 없습니다", productId);
            return null;
        }
        
        // 상품의 현재 순위 조회 (0-based이므로 +1)
        Long rankIndex = zSetOps.reverseRank(rankingKey, productKey);
        int rank = (rankIndex != null) ? rankIndex.intValue() + 1 : -1;
        
        ProductRankingInfo rankingInfo = new ProductRankingInfo(rank, productId, score.intValue());
        log.info("상품 {} 랭킹 정보: {}위, 주문 수 {}", productId, rank, score.intValue());
        
        return rankingInfo;
    }

    /**
     * 랭킹 정보를 담는 간단한 클래스
     * - 나중에 별도 파일로 분리할 수 있습니다
     */
    public static class ProductRankingInfo {
        private int rank;        // 순위 (1위, 2위, ...)
        private Long productId;  // 상품 ID
        private int orderCount;  // 주문 수

        public ProductRankingInfo(int rank, Long productId, int orderCount) {
            this.rank = rank;
            this.productId = productId;
            this.orderCount = orderCount;
        }

        // Getter 메서드들
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