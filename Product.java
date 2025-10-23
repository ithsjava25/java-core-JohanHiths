package com.example;

import java.math.BigDecimal;
import java.util.UUID;
//
public abstract class Product {
    private final UUID uuid;
    private final String name;
    private final Category category;
    private BigDecimal price;

    public Product(UUID uuid, String name, Category category, BigDecimal price) {
        this.uuid = uuid;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal price() {
        return price;
    }

    public void setPrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = newPrice;
    }

    public abstract String productDetails();

    public Object category() {
        return null;
    }

    public UUID id() {
        return null;
    }
}
