package ecommerce.product.adapter.in.web;

public class CreateProductRequestDto {
    private String productName;
    private Long amount;
    private int quantity;

    public CreateProductRequestDto() {}

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}


