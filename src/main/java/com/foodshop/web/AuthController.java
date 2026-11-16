package com.foodshop.web;

import com.foodshop.model.CustomerOrder;
import com.foodshop.model.User;
import com.foodshop.repo.OrderRepository;
import com.foodshop.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AuthController {

    private final UserRepository users;
    private final OrderRepository orders;

    public AuthController(UserRepository users, OrderRepository orders) {
        this.users = users;
        this.orders = orders;
    }

    @ControllerAdvice
    public static class UserSessionAdvice {
        @ModelAttribute("currentUser")
        public User currentUser(HttpSession session) {
            return (User) session.getAttribute("user");
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
        String cleanEmail = email.trim().toLowerCase();
        if (fullName.isBlank() || cleanEmail.isBlank() || phone.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Please fill in all fields.");
            return "register";
        }
        if (users.existsByEmail(cleanEmail)) {
            model.addAttribute("error", "An account with that email already exists.");
            return "register";
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(cleanEmail);
        user.setPhone(phone.trim());
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        users.save(user);

        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = users.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/orders")
    public String orderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<CustomerOrder> myOrders = orders.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("orders", myOrders);
        return "orders";
    }
}
