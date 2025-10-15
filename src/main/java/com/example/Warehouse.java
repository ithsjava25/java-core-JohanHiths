package com.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Warehouse {

    // --- Singleton setup ---
    private static Warehouse instance;

    private final String name;
    private final Map<UUID, Product> products = new ConcurrentHashMap<>();

    private Warehouse(String name) {
        this.name = name;
    }


    public static synchronized Warehouse getInstance(String name) {
        if (instance == null) {
            instance = new Warehouse(name);
        }
        return instance;
    }


    public void clearProducts() {
        products.clear();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }


    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        products.put(product.uuid(), product);
    }


    public void remove(UUID productId) {
        products.remove(productId);
    }


    public Optional<Product> getProductById(UUID productId) {
        return Optional.ofNullable(products.get(productId));
    }


    public List<Product> getProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    public String getName() {
        return name;
    }


    public static void resetInstance() {
        instance = null;
    }
}