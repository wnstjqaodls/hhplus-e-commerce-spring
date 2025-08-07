package ecommerce.product.adapter.out.persistence;

import ecommerce.product.application.port.out.LoadProductListPort;
import ecommerce.product.domain.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceAdapter implements LoadProductListPort {

    private final ProductRepository productRepository;

    public ProductPersistenceAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> loadProductList() {
        List<ProductJpaEntity> productEntities = productRepository.findAll();

        return productEntities.stream()
            .map(entity -> new Product(
                entity.getId(),
                entity.getProductName(),
                entity.getAmount(),
                entity.getQuantity()
            ))
            .collect(Collectors.toList());
    }
}
