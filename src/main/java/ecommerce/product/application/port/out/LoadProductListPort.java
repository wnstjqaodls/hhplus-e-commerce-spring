package ecommerce.product.application.port.out;

import ecommerce.product.domain.Product;
import java.util.List;

public interface LoadProductListPort {
    List<Product> loadProductList();
}
