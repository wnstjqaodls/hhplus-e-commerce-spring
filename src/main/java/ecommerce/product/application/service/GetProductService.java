package ecommerce.product.application.service;

import ecommerce.config.CacheNames;
import ecommerce.product.application.port.in.GetProductUseCase;
import ecommerce.product.application.port.out.LoadProductPort;
import ecommerce.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetProductService implements GetProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);

    private final LoadProductPort loadProductPort;

    public GetProductService(LoadProductPort loadProductPort) {
        this.loadProductPort = loadProductPort;
        log.info("GetProductService 생성자 실행 - LoadProductPort 주입 완료");
    }

    /**
     * 상품 단건 조회 - Redis 캐시 적용
     * 캐시 키: "product::productId"
     * TTL: 10분 (CacheConfiguration에서 설정)
     */
    @Override
    @Cacheable(value = CacheNames.PRODUCT, key = "#productId")
    public Product getProduct(Long productId) {
        log.info("상품 단건 조회 시작 (DB 조회) - productId: {}", productId);
        Product product = loadProductPort.loadProduct(productId);
        log.info("상품 단건 조회 성공 (DB에서 로드됨) - productId: {}, 상품명: {}",
                productId, product.getProductName());
        return product;
    }
}


