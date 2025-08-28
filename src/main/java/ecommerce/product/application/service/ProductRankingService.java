package ecommerce.product.application.service;

import ecommerce.product.application.port.in.ProductRankingUseCase;
import ecommerce.product.application.port.out.SaveProductRankingPort;
import ecommerce.product.application.port.out.LoadProductRankingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 상품 랭킹 서비스 - Clean Architecture 구조
 * - UseCase 인터페이스를 구현합니다
 * - Port를 통해 외부(Redis)와 통신합니다
 */
@Service
public class ProductRankingService implements ProductRankingUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProductRankingService.class);

    private final SaveProductRankingPort saveProductRankingPort;
    private final LoadProductRankingPort loadProductRankingPort;

    /**
     * Clean Architecture 구조: 생성자 주입으로 Port 의존성 설정
     */
    public ProductRankingService(SaveProductRankingPort saveProductRankingPort, 
                                LoadProductRankingPort loadProductRankingPort) {
        this.saveProductRankingPort = saveProductRankingPort;
        this.loadProductRankingPort = loadProductRankingPort;
        log.info("ProductRankingService 생성자 실행 - Port 주입 완료");
    }

    /**
     * 1단계: 상품 랭킹 점수 증가 - UseCase 구현
     * 주문 완료 시 호출되어 해당 상품의 랭킹 점수를 1 증가시킵니다
     */
    @Override
    public void increaseRanking(Long productId) {
        log.info("상품 랭킹 증가 요청 - productId: {}", productId);
        
        // Port를 통해 외부(Redis)에 저장 - Clean Architecture 패턴
        Double newScore = saveProductRankingPort.increaseRankingScore(productId);
        
        log.info("상품 {} 랭킹 증가 완료. 현재 점수: {}", productId, newScore);
    }

    /**
     * 2단계: 오늘의 Top 10 상품 랭킹 조회 - UseCase 구현
     */
    @Override
    public List<ProductRankingInfo> getTop10Products() {
        log.info("Top 10 상품 랭킹 조회 요청");
        
        // Port를 통해 외부(Redis)에서 조회 - Clean Architecture 패턴
        List<ProductRankingInfo> rankings = loadProductRankingPort.loadTopProducts(10);
        
        log.info("Top 10 상품 랭킹 조회 완료. 총 {}개 상품", rankings.size());
        return rankings;
    }

    /**
     * 3단계: 특정 상품의 현재 순위 조회 - UseCase 구현
     */
    @Override
    public ProductRankingInfo getProductRanking(Long productId) {
        log.info("상품 {} 랭킹 조회 요청", productId);
        
        // Port를 통해 외부(Redis)에서 조회 - Clean Architecture 패턴
        ProductRankingInfo ranking = loadProductRankingPort.loadProductRanking(productId);
        
        if (ranking != null) {
            log.info("상품 {} 랭킹 조회 완료: {}위, 주문 수 {}", productId, ranking.rank(), ranking.orderCount());
        } else {
            log.info("상품 {}는 오늘 주문이 없어서 랭킹에 없습니다", productId);
        }
        
        return ranking;
    }
}
