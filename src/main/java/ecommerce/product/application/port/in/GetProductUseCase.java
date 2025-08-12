package ecommerce.product.application.port.in;

import ecommerce.product.domain.Product;

public interface GetProductUseCase {
    Product getProduct(Long productId);
}


