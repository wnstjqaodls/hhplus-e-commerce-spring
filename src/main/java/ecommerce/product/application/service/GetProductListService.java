package ecommerce.product.application.service;

import ecommerce.config.CacheNames;
import ecommerce.product.application.port.in.GetProductListUseCase;
import ecommerce.product.application.port.out.LoadProductListPort;
import ecommerce.product.domain.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class GetProductListService implements GetProductListUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductListService.class);

    private final LoadProductListPort loadProductListPort;

    public GetProductListService(LoadProductListPort loadProductListPort) {
        this.loadProductListPort = loadProductListPort;
    }

    /**
     * 상품 목록 조회 - Redis 캐시 적용
     * 캐시 키: "productList::SimpleKey []" (파라미터가 없으므로)
     * TTL: 10분
     * 
     * 상품 목록은 자주 조회되지만 변경이 적어 캐시 효과가 높음
     */
    @Override
    @Cacheable(value = CacheNames.PRODUCT_LIST)
    public List<Product> getProductList() {
        log.info("상품 목록 조회 시작 (DB 조회)");

        List<Product> products = loadProductListPort.loadProductList();
        
        log.info("상품 목록 조회 완료 (DB에서 로드됨). 상품 수: {}", products.size());

        return products;
    }
}
