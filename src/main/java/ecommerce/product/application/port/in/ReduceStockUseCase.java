package ecommerce.product.application.port.in;

public interface ReduceStockUseCase {
    void reduceStock(Long productId, int quantity);
}


