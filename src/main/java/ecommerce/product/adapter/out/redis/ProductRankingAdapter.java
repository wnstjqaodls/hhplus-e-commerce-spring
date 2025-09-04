package ecommerce.product.adapter.out.redis;

import ecommerce.product.application.port.in.ProductRankingUseCase.ProductRankingInfo;
import ecommerce.product.application.port.out.LoadProductRankingPort;
import ecommerce.product.application.port.out.SaveProductRankingPort;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 상품 랭킹 Redis 어댑터 - Clean Architecture 구조
 * - SaveProductRankingPort와 LoadProductRankingPort를 모두 구현
 * - Redis Sorted Set을 활용한 상품 랭킹 시스템
 */
@Component
public class ProductRankingAdapter implements SaveProductRankingPort, LoadProductRankingPort {

    private static final Logger log = LoggerFactory.getLogger(ProductRankingAdapter.class);
    
    private static final String RANKING_KEY_PREFIX = "product:ranking:daily:";
    
    private final RedissonClient redissonClient;
    
    public ProductRankingAdapter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        log.info("ProductRankingAdapter 생성 완료 - Redis 연결 설정됨");
    }
    
    /**
     * SaveProductRankingPort 구현 - 상품 랭킹 점수 증가
     */
    @Override
    public Double increaseRankingScore(Long productId) {
        String key = generateDailyKey();
        RScoredSortedSet<String> rankingSet = redissonClient.getScoredSortedSet(key);
        
        String productKey = String.valueOf(productId);
        
        // ZINCRBY 연산: 해당 상품의 점수를 1 증가
        double newScore = rankingSet.addScore(productKey, 1.0);
        
        log.debug("상품 {} 랭킹 점수 증가: {} -> {}", productId, newScore - 1, newScore);
        
        return newScore;
    }
    
    /**
     * LoadProductRankingPort 구현 - 상위 N개 상품 조회
     */
    @Override
    public List<ProductRankingInfo> loadTopProducts(int limit) {
        String key = generateDailyKey();
        RScoredSortedSet<String> rankingSet = redissonClient.getScoredSortedSet(key);
        
        // ZREVRANGE: 점수 높은 순으로 상위 N개 조회 (역순 정렬)
        Collection<String> topProducts = rankingSet.valueRangeReversed(0, limit - 1);
        
        if (topProducts.isEmpty()) {
            log.debug("오늘 주문된 상품이 없어서 빈 랭킹을 반환합니다");
            return List.of();
        }
        
        // 순위와 점수 정보를 포함한 ProductRankingInfo로 변환
        List<ProductRankingInfo> rankings = IntStream.range(0, topProducts.size())
                .mapToObj(index -> {
                    String productIdStr = topProducts.toArray(String[]::new)[index];
                    Long productId = Long.valueOf(productIdStr);
                    Double score = rankingSet.getScore(productIdStr);
                    int rank = index + 1;
                    
                    return new ProductRankingInfo(
                        rank,
                        productId, 
                        score != null ? score.intValue() : 0
                    );
                })
                .toList();
        
        log.debug("오늘의 상위 {}개 상품 랭킹 조회 완료", rankings.size());
        
        return rankings;
    }
    
    /**
     * LoadProductRankingPort 구현 - 특정 상품 랭킹 조회
     */
    @Override
    public ProductRankingInfo loadProductRanking(Long productId) {
        String key = generateDailyKey();
        RScoredSortedSet<String> rankingSet = redissonClient.getScoredSortedSet(key);
        
        String productKey = String.valueOf(productId);
        
        // ZSCORE: 해당 상품의 점수 조회
        Double score = rankingSet.getScore(productKey);
        
        if (score == null || score == 0) {
            log.debug("상품 {}는 오늘 주문이 없어서 랭킹에 없습니다", productId);
            return null;
        }
        
        // ZREVRANK: 해당 상품의 순위 조회 (역순 정렬에서의 순위)
        Integer rank = rankingSet.revRank(productKey);
        
        if (rank == null) {
            log.warn("상품 {}의 점수는 있지만 순위를 찾을 수 없습니다. score: {}", productId, score);
            return null;
        }
        
        // Redis rank는 0부터 시작하므로 +1
        int actualRank = rank + 1;
        
        log.debug("상품 {} 랭킹 조회: {}위, 주문 수 {}", productId, actualRank, score.longValue());
        
        return new ProductRankingInfo(actualRank, productId, score.intValue());
    }
    
    /**
     * 일별 랭킹 키 생성
     * 형식: product:ranking:daily:2024-01-15
     */
    private String generateDailyKey() {
        LocalDate today = LocalDate.now();
        return RANKING_KEY_PREFIX + today;
    }
}