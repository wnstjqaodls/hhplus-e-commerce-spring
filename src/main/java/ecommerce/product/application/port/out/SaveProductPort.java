package ecommerce.product.application.port.out;

import ecommerce.product.domain.Product;

public interface SaveProductPort {
    Product saveProduct(Product product);
}


