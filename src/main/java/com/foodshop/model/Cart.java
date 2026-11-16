package com.foodshop.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.util.LinkedHashMap;
import java.util.Map;

/** The shopping cart: one per browser session. Maps product id to quantity. */
@Component
@SessionScope
public class Cart {
    private final Map<Long, Integer> items = new LinkedHashMap<>();

    public void add(Long id) { items.merge(id, 1, Integer::sum); }

    public void set(Long id, int qty) {
        if (qty <= 0) items.remove(id); else items.put(id, Math.min(qty, 99));
    }

    public void remove(Long id) { items.remove(id); }
    public void clear() { items.clear(); }
    public Map<Long, Integer> getItems() { return items; }
    public int getCount() { return items.values().stream().mapToInt(Integer::intValue).sum(); }
}
