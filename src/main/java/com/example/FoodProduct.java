package com.example;

import jdk.jfr.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a food item that can perish and be shipped.
 */
public class FoodProduct extends Product implements Perishable, Shippable {

    private final LocalDate expirationDate;
    private final BigDecimal weight;

    public FoodProduct(UUID uuid, String name, Category category, BigDecimal price, LocalDate expirationDate, BigDecimal weight) {
        super(uuid, name, category, price);

        if (expirationDate == null) throw new IllegalArgumentException("Expiration date cannot be null.");
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Weight must be positive.");

        this.expirationDate = expirationDate;
        this.weight = weight;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}