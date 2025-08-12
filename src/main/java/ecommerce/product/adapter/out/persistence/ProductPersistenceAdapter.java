package ecommerce.product.adapter.out.persistence;

import ecommerce.product.application.port.out.LoadProductListPort;
import ecommerce.product.application.port.out.LoadProductPort;
import ecommerce.product.application.port.out.SaveProductPort;
import ecommerce.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceAdapter implements LoadProductListPort, LoadProductPort, SaveProductPort {

    private static final Logger log = LoggerFactory.getLogger(ProductPersistenceAdapter.class);

    private final ProductRepository productRepository;

    public ProductPersistenceAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> loadProductList() {
        log.debug("[Adapter] loadProductList 시작");
        List<ProductJpaEntity> productEntities = productRepository.findAll();
        List<Product> result = productEntities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
        log.debug("[Adapter] loadProductList 완료 - count: {}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Product loadProduct(Long productId) {
        log.debug("[Adapter] loadProduct 시작 - id: {}", productId);
        ProductJpaEntity entity = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + productId));
        Product product = toDomain(entity);
        log.debug("[Adapter] loadProduct 성공 - id: {}", productId);
        return product;
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        log.debug("[Adapter] saveProduct 시작 - id: {}", product.getId());
        ProductJpaEntity entity = toEntity(product);
        ProductJpaEntity saved = productRepository.save(entity);
        Product savedDomain = toDomain(saved);
        log.debug("[Adapter] saveProduct 완료 - id: {}", savedDomain.getId());
        return savedDomain;
    }

    private Product toDomain(ProductJpaEntity entity) {
        return new Product(
            entity.getId(),
            entity.getProductName(),
            entity.getAmount(),
            entity.getQuantity()
        );
    }

    private ProductJpaEntity toEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setProductName(product.getProductName());
        entity.setAmount(product.getAmount());
        entity.setQuantity(product.getQuantity());
        return entity;
    }
}
