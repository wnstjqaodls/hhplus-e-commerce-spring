package ecommerce.product.application.port.in;

public interface CreateProductUseCase {
    Long createProduct(String productName, Long amount, int quantity);
}


