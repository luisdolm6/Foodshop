package com.foodshop;

import com.foodshop.model.Product;
import com.foodshop.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Sample menu so the shop is not empty. Edit or replace with your own items. */
@Component
public class DataLoader implements CommandLineRunner {
    private final ProductRepository products;

    public DataLoader(ProductRepository products) { this.products = products; }

    @Override
    public void run(String... args) {
        add("Classic Burger", "Beef patty, lettuce, tomato, cheese", "8.99");
        }

    private void add(String name, String desc, String price) {
        Product p = new Product();
        p.setName(name); p.setDescription(desc); p.setPrice(new BigDecimal(price));
        products.save(p);
    }
}
