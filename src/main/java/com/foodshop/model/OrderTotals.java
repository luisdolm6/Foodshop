package com.foodshop.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderTotals {
    public static final BigDecimal TAX_RATE = new BigDecimal("0.07");
    public static final BigDecimal JUST_CUZ_FEE = new BigDecimal("2.50");

    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal fee;
    private final BigDecimal total;

    public OrderTotals(BigDecimal subtotal) {
        this.subtotal = subtotal;
        this.tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        this.fee = JUST_CUZ_FEE;
        this.total = subtotal.add(tax).add(fee);
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getFee() { return fee; }
    public BigDecimal getTotal() { return total; }
}