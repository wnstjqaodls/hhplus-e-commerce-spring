package ecommerce.product.adapter.in.web;

public class ProductResponseDto {
    private Long id;
    private String productName;
    private Long amount;
    private int quantity;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String productName, Long amount, int quantity) {
        this.id = id;
        this.productName = productName;
        this.amount = amount;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
