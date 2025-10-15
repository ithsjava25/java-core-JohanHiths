package com.example;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all products.
 */
public abstract class Product {


    public abstract Product withUpdatedPrice(BigDecimal newPrice);
    private final UUID uuid;
    private final String name;
    private final Category category;
    private final BigDecimal price;

    protected Product(UUID uuid, String name, Category category, BigDecimal price) {
        if (uuid == null) throw new IllegalArgumentException("UUID cannot be null.");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Product name cannot be blank.");
        if (category == null) throw new IllegalArgumentException("Category cannot be null.");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be negative or null.");

        this.uuid = uuid;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public UUID uuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return uuid.equals(product.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "%s{name='%s', category=%s, price=%s}".formatted(
                getClass().getSimpleName(), name, category.getName(), price);
    }
}