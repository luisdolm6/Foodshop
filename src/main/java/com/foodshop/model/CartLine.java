package com.foodshop.model;

import java.math.BigDecimal;

/** One row in the cart page (not stored in the database). */
public class CartLine {
    private final Product product;
    private final int quantity;

    public CartLine(Product product, int quantity) { this.product = product; this.quantity = quantity; }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return product.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}
