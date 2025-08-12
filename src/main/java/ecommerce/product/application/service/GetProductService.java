package ecommerce.product.application.service;

import ecommerce.product.application.port.in.GetProductUseCase;
import ecommerce.product.application.port.out.LoadProductPort;
import ecommerce.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetProductService implements GetProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);

    private final LoadProductPort loadProductPort;

    public GetProductService(LoadProductPort loadProductPort) {
        this.loadProductPort = loadProductPort;
        log.info("GetProductService 생성자 실행 - LoadProductPort 주입 완료");
    }

    @Override
    public Product getProduct(Long productId) {
        log.info("상품 단건 조회 시작 - productId: {}", productId);
        Product product = loadProductPort.loadProduct(productId);
        log.info("상품 단건 조회 성공 - productId: {}", productId);
        return product;
    }
}


