package com.foodshop;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VendorMenuService {
    private final Map<Long, VendorMenuItem> items = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public VendorMenuService() {
        add(new VendorMenuItem(null, "Classic Burger",
            "Beef patty, lettuce, tomato, cheese", new BigDecimal("8.99")));
    }

    public List<VendorMenuItem> findAll() {
        List<VendorMenuItem> list = new ArrayList<>(items.values());
        list.sort(Comparator.comparing(VendorMenuItem::getId));
        return list;
    }

    public Optional<VendorMenuItem> findById(Long id) { return Optional.ofNullable(items.get(id)); }

    public VendorMenuItem add(VendorMenuItem item) {
        item.setId(nextId.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    public void update(Long id, VendorMenuItem updated) {
        findById(id).ifPresent(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
        });
    }

    public void delete(Long id) { items.remove(id); }
}