package ecommerce.product.application.port.out;

import ecommerce.product.domain.Product;

public interface LoadProductPort {
    Product loadProduct(Long productId);
}


