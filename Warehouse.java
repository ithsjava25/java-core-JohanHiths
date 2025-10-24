package com.example;

//
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    private static Warehouse instance;

    private static final Map<UUID, Product> products = new HashMap<>();

    public Warehouse(String name) {
    }
//
 //


    public static Warehouse getInstance(String name) {
        if (instance == null) {
            instance = new Warehouse(name);
        }
        return instance;
    }
   protected static Warehouse getInstance() {
        if (instance == null) {
            instance = new Warehouse("DefaultWarehouse");
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
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null.");
        products.put(product.uuid(), product);
    }

    public void remove(UUID id) {
        products.remove(id);
    }

    public Optional<Product> getProductById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    public static List<Product> getProducts() {

        return List.copyOf(products.values());
    }

    public Map<Category, List<Product>> getProductsGroupedByCategories() {
        if (products.isEmpty()) return Collections.emptyMap();

        return products.values().stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
    }

    public List<Perishable> expiredProducts() {
        LocalDate now = LocalDate.now();
        return products.values().stream()
                .filter(p -> p instanceof Perishable per
                        && per.expirationDate() != null
                        && per.expirationDate().isBefore(now))
                .map(p -> (Perishable) p)
                .toList();
    }

    public static List<Shippable> shippableProducts() {
        return products.values().stream()
                .filter(p -> p instanceof Shippable)
                .map(p -> (Shippable) p)
                .toList();
    }

    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        Product product = products.get(id);
        if (product == null)
            throw new NoSuchElementException("Product not found with id: " + id);
        product.setPrice(newPrice);
    }
}
