package com.foodshop;

import com.foodshop.model.Product;
import com.foodshop.repo.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/vendor")
public class VendorController {
    private final ProductRepository products;

    public VendorController(ProductRepository products) { this.products = products; }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("items", products.findAll());
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
                       @RequestParam BigDecimal price) {
        if (name.isBlank() || price.signum() < 0) return "redirect:/vendor";
        Product p = (id != null) ? products.findById(id).orElse(new Product()) : new Product();
        p.setName(name.trim());
        p.setDescription(description.trim());
        p.setPrice(price);
        products.save(p);
        return "redirect:/vendor";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        products.deleteById(id);
        return "redirect:/vendor";
    }
}