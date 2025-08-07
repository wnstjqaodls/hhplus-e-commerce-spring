package ecommerce.product.application.service;

import ecommerce.product.application.port.in.GetProductListUseCase;
import ecommerce.product.application.port.out.LoadProductListPort;
import ecommerce.product.domain.Product;
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

    @Override
    public List<Product> getProductList() {
        log.info("GetProductListService.getProductList() 호출됨");

        List<Product> products = loadProductListPort.loadProductList();
        
        log.info("상품 목록 조회 완료. 상품 수: {}", products.size());

        return products;
    }
}
