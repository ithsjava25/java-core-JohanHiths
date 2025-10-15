package com.example;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class Warehouse {

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


    public Map<Category, List<Product>> getProductsGroupedByCategories() {
        if (products.isEmpty()) {
            return Collections.emptyMap();
        }


        return products.values().stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
    }


    public void updateProductPrice(UUID productId, BigDecimal newPrice) {
        if (productId == null) throw new IllegalArgumentException("Product ID cannot be null.");
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("New price must be non-negative.");

        Product product = products.get(productId);
        if (product == null) {
            throw new NoSuchElementException("Product not found with id: " + productId);
        }


        Product updated = product.withUpdatedPrice(newPrice);
        products.put(productId, updated);
    }


    public String getName() {
        return name;
    }


    public static void resetInstance() {
        instance = null;
    }


    }
