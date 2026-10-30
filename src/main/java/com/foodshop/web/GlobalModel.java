package com.foodshop.web;

import com.foodshop.model.Cart;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Gives every page the cart item count for the navbar. */
@ControllerAdvice
public class GlobalModel {
    private final Cart cart;

    public GlobalModel(Cart cart) { this.cart = cart; }

    @ModelAttribute("cartCount")
    public int cartCount() { return cart.getCount(); }
}
