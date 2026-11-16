package com.foodshop.model;

import java.math.BigDecimal;

// calculate subtotal for each cart line so quantity x price
public class CartLine {
    private final Product product;
    private final int quantity;

    public CartLine(Product product, int quantity) { this.product = product; this.quantity = quantity; }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return product.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}
