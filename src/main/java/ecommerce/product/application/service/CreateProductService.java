package ecommerce.product.application.service;

import ecommerce.product.application.port.in.CreateProductUseCase;
import ecommerce.product.application.port.out.SaveProductPort;
import ecommerce.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateProductService implements CreateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateProductService.class);

    private final SaveProductPort saveProductPort;

    public CreateProductService(SaveProductPort saveProductPort) {
        this.saveProductPort = saveProductPort;
        log.info("CreateProductService 생성자 실행 - SaveProductPort 주입 완료");
    }

    @Override
    public Long createProduct(String productName, Long amount, int quantity) {
        log.info("상품 생성 시작 - name: {}, amount: {}, quantity: {}", productName, amount, quantity);
        Product product = Product.create(productName, amount, quantity);
        Product saved = saveProductPort.saveProduct(product);
        log.info("상품 생성 완료 - id: {}", saved.getId());
        return saved.getId();
    }
}


