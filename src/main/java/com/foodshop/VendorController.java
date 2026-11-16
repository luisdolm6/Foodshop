package com.foodshop;

import com.foodshop.model.CustomerOrder;
import com.foodshop.model.Product;
import com.foodshop.model.User;
import com.foodshop.repo.OrderRepository;
import com.foodshop.repo.ProductRepository;
import com.foodshop.repo.UserRepository;
import com.foodshop.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/vendor")
public class VendorController {

    private static final String ADMIN_EMAIL = "adminfoodshop@gmail.com";

    private final ProductRepository products;
    private final OrderRepository orders;
    private final UserRepository users;
    private final NotificationService notificationService;

    public VendorController(ProductRepository products,
                            OrderRepository orders,
                            UserRepository users,
                            NotificationService notificationService) {
        this.products = products;
        this.orders = orders;
        this.users = users;
        this.notificationService = notificationService;
    }

    private boolean isAdmin(HttpSession session) {
        for (String key : new String[]{"user", "currentUser", "loggedInUser"}) {
            Object attr = session.getAttribute(key);
            if (attr instanceof User u) {
                return ADMIN_EMAIL.equalsIgnoreCase(u.getEmail());
            }
        }
        for (String key : new String[]{"userId", "currentUserId", "id"}) {
            Object attr = session.getAttribute(key);
            if (attr instanceof Long id) {
                return users.findById(id)
                        .map(u -> ADMIN_EMAIL.equalsIgnoreCase(u.getEmail()))
                        .orElse(false);
            }
        }
        return false;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Long edit,
                            HttpSession session,
                            Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("items", products.findAll());
        model.addAttribute("orders", orders.findAll(Sort.by(Sort.Direction.DESC, "id")));
        Product form = edit != null
                ? products.findById(edit).orElse(new Product())
                : new Product();
        model.addAttribute("form", form);
        return "vendor";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam String description,
                       @RequestParam BigDecimal price,
                       HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        if (name.isBlank() || price.signum() < 0) return "redirect:/vendor";
        Product p = (id != null) ? products.findById(id).orElse(new Product()) : new Product();
        p.setName(name.trim());
        p.setDescription(description.trim());
        p.setPrice(price);
        products.save(p);
        return "redirect:/vendor";
    }

    @PostMapping("/orders/{id}/ready")
    public String markOrderReady(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        CustomerOrder order = orders.findById(id).orElse(null);
        if (order != null) {
            order.setStatus("READY");
            orders.save(order);

            String recipientEmail = (order.getUser() != null) ? order.getUser().getEmail() : null;
            boolean emailSent = notificationService.sendOrderEmail(
                    recipientEmail,
                    order,
                    "Your Foodshop Order #" + order.getId() + " is Ready!",
                    "Good news! Your food is done and ready for pickup/delivery."
            );

            redirectAttributes.addFlashAttribute("notificationBanner",
                    emailSent
                            ? "Order #" + order.getId() + " marked READY — email sent to " + recipientEmail + "!"
                            : "Order #" + order.getId() + " marked READY!"
            );
        }
        return "redirect:/vendor";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        products.deleteById(id);
        return "redirect:/vendor";
    }
}