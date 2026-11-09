package com.foodshop.web;

import com.foodshop.model.*;
import com.foodshop.repo.OrderRepository;
import com.foodshop.repo.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ShopController {

    private final ProductRepository products;
    private final OrderRepository orders;
    private final Cart cart;

    public ShopController(ProductRepository products, OrderRepository orders, Cart cart) {
        this.products = products;
        this.orders = orders;
        this.cart = cart;
    }

    // ---------- menu ----------
    @GetMapping("/")
    public String menu(Model model) {
        // Loads available items directly from your MySQL 'menu' table
        model.addAttribute("products", products.findByIsAvailableTrue());
        return "index";
    }

    // ---------- cart ----------
    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable Long id) {
        if (products.existsById(id)) {
            cart.add(id);
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<CartLine> lines = lines();
        model.addAttribute("lines", lines);
        model.addAttribute("total", total(lines));
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String update(@PathVariable Long id, @RequestParam int quantity) {
        cart.set(id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id) {
        cart.remove(id);
        return "redirect:/cart";
    }

    // ---------- checkout ----------
    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        List<CartLine> lines = lines();
        if (lines.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("lines", lines);
        model.addAttribute("total", total(lines));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             Model model) {
        List<CartLine> lines = lines();
        if (lines.isEmpty()) {
            return "redirect:/cart";
        }
        if (customerName.isBlank() || phone.isBlank() || address.isBlank()) {
            model.addAttribute("error", "Please fill in your name, phone and address.");
            model.addAttribute("lines", lines);
            model.addAttribute("total", total(lines));
            return "checkout";
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(customerName.trim());
        order.setPhone(phone.trim());
        order.setAddress(address.trim());
        order.setCreatedAt(LocalDateTime.now());
        order.setTotal(total(lines));

        List<OrderItem> items = new ArrayList<>();
        for (CartLine l : lines) {
            OrderItem i = new OrderItem();
            i.setOrder(order);
            i.setProductName(l.getProduct().getName());
            i.setPrice(l.getProduct().getPrice());
            i.setQuantity(l.getQuantity());
            items.add(i);
        }
        order.setItems(items);
        orders.save(order);
        cart.clear();

        return "redirect:/order/" + order.getId();
    }

    @GetMapping("/order/{id}")
    public String confirmation(@PathVariable Long id, Model model) {
        CustomerOrder order = orders.findById(id).orElse(null);
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "confirmation";
    }

    // ---------- helpers ----------
    private List<CartLine> lines() {
        List<CartLine> lines = new ArrayList<>();
        cart.getItems().forEach((id, qty) ->
                products.findById(id).ifPresent(p -> lines.add(new CartLine(p, qty))));
        return lines;
    }

    private BigDecimal total(List<CartLine> lines) {
        return lines.stream()
                .map(CartLine::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}