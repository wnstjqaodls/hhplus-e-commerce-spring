package ecommerce.product.application.port.in;

import ecommerce.product.domain.Product;
import java.util.List;

public interface GetProductListUseCase {
    List<Product> getProductList();
}
