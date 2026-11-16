package com.foodshop.web;

import com.foodshop.model.*;
import com.foodshop.repo.OrderRepository;
import com.foodshop.repo.ProductRepository;
import com.foodshop.repo.UserRepository;
import com.foodshop.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Controller for handling shop-related web requests, including menu, cart, and checkout.
@Controller
public class ShopController {

    private final ProductRepository products;
    private final OrderRepository orders;
    private final UserRepository users;
    private final Cart cart;
    private final NotificationService notificationService;

    public ShopController(ProductRepository products,
                          OrderRepository orders,
                          UserRepository users,
                          Cart cart,
                          NotificationService notificationService) {
        this.products = products;
        this.orders = orders;
        this.users = users;
        this.cart = cart;
        this.notificationService = notificationService;
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
        model.addAttribute("totals", totals(lines));
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
        model.addAttribute("totals", totals(lines));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        List<CartLine> lines = lines();
        if (lines.isEmpty()) {
            return "redirect:/cart";
        }
        if (customerName.isBlank() || phone.isBlank() || address.isBlank()) {
            model.addAttribute("error", "Please fill in your name, phone and address.");
            model.addAttribute("lines", lines);
            model.addAttribute("totals", totals(lines));
            return "checkout";
        }

        OrderTotals t = totals(lines);

        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(customerName.trim());
        order.setPhone(phone.trim());
        order.setAddress(address.trim());
        order.setCreatedAt(LocalDateTime.now());
        order.setSubtotal(t.getSubtotal());
        order.setTax(t.getTax());
        order.setFee(t.getFee());
        order.setTotal(t.getTotal());

        User loggedInUser = resolveLoggedInUser(session);
        if (loggedInUser != null) {
            order.setUser(loggedInUser);
        }

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
        CustomerOrder savedOrder = orders.save(order);
        cart.clear();

        // Send email notification if logged-in user has an email
        String recipientEmail = (loggedInUser != null) ? loggedInUser.getEmail() : null;
        boolean emailSent = notificationService.sendOrderEmail(
                recipientEmail,
                savedOrder,
                "Foodshop Order Confirmation #" + savedOrder.getId(),
                "Your order has been received and sent to the kitchen!"
        );

        redirectAttributes.addFlashAttribute("notificationBanner",
                emailSent
                        ? "Order #" + savedOrder.getId() + " placed! Confirmation email sent to " + recipientEmail + "."
                        : "Order #" + savedOrder.getId() + " confirmed!"
        );

        return "redirect:/order/" + savedOrder.getId();
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
    private User resolveLoggedInUser(HttpSession session) {
        for (String key : new String[]{"user", "currentUser", "loggedInUser"}) {
            Object attr = session.getAttribute(key);
            if (attr instanceof User u) {
                return u;
            }
        }
        for (String key : new String[]{"userId", "currentUserId", "id"}) {
            Object attr = session.getAttribute(key);
            if (attr instanceof Long id) {
                return users.findById(id).orElse(null);
            }
        }
        return null;
    }

    private List<CartLine> lines() {
        List<CartLine> lines = new ArrayList<>();
        cart.getItems().forEach((id, qty) ->
                products.findById(id).ifPresent(p -> lines.add(new CartLine(p, qty))));
        return lines;
    }

    // adds up every line's subtotal (price x quantity) = the subtotal before tax and fee
    private BigDecimal total(List<CartLine> lines) {
        return lines.stream()
                .map(CartLine::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // wraps the subtotal in OrderTotals, which adds tax and the fee
    private OrderTotals totals(List<CartLine> lines) {
        return new OrderTotals(total(lines));
    }
}