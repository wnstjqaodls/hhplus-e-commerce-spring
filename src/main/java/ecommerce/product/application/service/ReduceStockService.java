package ecommerce.product.application.service;

import ecommerce.config.DistributedLock;
import ecommerce.product.application.port.in.ReduceStockUseCase;
import ecommerce.product.application.port.out.LoadProductPort;
import ecommerce.product.application.port.out.SaveProductPort;
import ecommerce.product.domain.Product;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReduceStockService implements ReduceStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReduceStockService.class);

    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;

    public ReduceStockService(LoadProductPort loadProductPort, SaveProductPort saveProductPort) {
        this.loadProductPort = loadProductPort;
        this.saveProductPort = saveProductPort;
        log.info("ReduceStockService 생성자 실행 - Ports 주입 완료");
    }

    @Override
    @Transactional
    @DistributedLock(key = "'product:stock:' + #productId", waitTime = 5000L, leaseTime = 3000L)
    public void reduceStock(Long productId, int quantity) {
        log.info("ReduceStockService.reduceStock() 호출됨. productId: {}, quantity: {}", productId, quantity);
        log.info("재고 차감 시작 - productId: {}, quantity: {}", productId, quantity);
        Product product = loadProductPort.loadProduct(productId);
        product.reduceStock(quantity);
        saveProductPort.saveProduct(product);
        log.info("재고 차감 완료 - productId: {}, 남은 재고: {}", productId, product.getQuantity());
    }
}


