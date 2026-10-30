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
        add("Margherita Pizza", "Tomato, mozzarella, fresh basil", "11.50");
        add("Chicken Wrap", "Grilled chicken, veggies, garlic sauce", "7.25");
        add("Caesar Salad", "Romaine, parmesan, croutons", "6.75");
        add("French Fries", "Crispy and salted", "3.50");
        add("Lemonade", "Fresh squeezed", "2.50");
        add("Chocolate Cake", "Rich and moist", "4.75");
    }

    private void add(String name, String desc, String price) {
        Product p = new Product();
        p.setName(name); p.setDescription(desc); p.setPrice(new BigDecimal(price));
        products.save(p);
    }
}
