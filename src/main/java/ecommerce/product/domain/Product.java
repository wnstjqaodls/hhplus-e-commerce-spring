package ecommerce.product.domain;

public class Product {
    
    private Long id;
    private String productName;
    private Long amount;
    private int quantity; // 재고 수량

    public Product(Long id, String productName, Long amount, int quantity) {
        this.id = id;
        this.productName = productName;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static Product create(String productName, Long amount, int quantity) {
        validateProduct(productName, amount, quantity);
        
        return new Product(
            null, // ID는 자동 생성
            productName,
            amount,
            quantity
        );
    }

    public boolean hasStock(int requestQuantity) {
        return this.quantity >= requestQuantity;
    }

    public void reduceStock(int requestQuantity) {
        if (!hasStock(requestQuantity)) {
            throw new IllegalArgumentException("재고가 부족합니다. 요청: " + requestQuantity + ", 보유: " + quantity);
        }
        this.quantity -= requestQuantity;
    }

    private static void validateProduct(String productName, Long amount, int quantity) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다");
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public Long getAmount() { return amount; }
    public int getQuantity() { return quantity; }
}
