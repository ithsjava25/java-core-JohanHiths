package com.example;

import com.example.Category;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
//
/**
 * Analyzer class that provides advanced warehouse operations.
 */
@SuppressWarnings("unused")
public class WarehouseAnalyzer {

    private final Warehouse warehouse;

    public WarehouseAnalyzer(Warehouse warehouse) {
        this.warehouse = Objects.requireNonNull(warehouse, "Warehouse cannot be null.");
    }

    // === Search and Filter Methods ===

    /**
     * Finds all products in a specific category.
     */
    public List<Product> findProductsInCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        List<Product> result = Warehouse.getProducts().stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());

        Collections.reverse(result);
        return result;
    }

    /**
     * Finds all products within a price range (inclusive).
     */
    public List<Product> findProductsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> result = new ArrayList<>();
        for (Product p : Warehouse.getProducts()) {
            BigDecimal price = p.price();
            if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Finds all perishables expiring within a number of days (including today).
     */
    public List<Perishable> findProductsExpiringWithinDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);
        List<Perishable> result = new ArrayList<>();

        for (Product p : Warehouse.getProducts()) {
            if (p instanceof Perishable per) {
                LocalDate exp = per.expirationDate();
                if (!exp.isBefore(today) && !exp.isAfter(end)) {
                    result.add(per);
                }
            }
        }
        return result;
    }

    /**
     * Performs a case-insensitive partial name search.
     */
    public List<Product> searchProductsByName(String searchTerm) {
        String term = searchTerm.toLowerCase(Locale.ROOT);
        List<Product> result = new ArrayList<>();

        for (Product p : Warehouse.getProducts()) {
            if (p.name().toLowerCase(Locale.ROOT).contains(term)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Returns all products whose price is strictly greater than the given price.
     */
    public List<Product> findProductsAbovePrice(BigDecimal price) {
        List<Product> result = new ArrayList<>();
        for (Product p : Warehouse.getProducts()) {
            if (p.price().compareTo(price) > 0) {
                result.add(p);
            }
        }
        return result;
    }

    // === Analytics Methods ===

    /**
     * Computes the weighted average price per category.
     */
    public Map<Category, BigDecimal> calculateWeightedAveragePriceByCategory() {
        Map<Category, List<Product>> byCat = Warehouse.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        Map<Category, BigDecimal> result = new HashMap<>();

        for (Map.Entry<Category, List<Product>> e : byCat.entrySet()) {
            Category cat = (Category) e.getKey();
            List<Product> items = e.getValue();

            BigDecimal weightedSum = BigDecimal.ZERO;
            BigDecimal weightSum = BigDecimal.ZERO;

            for (Product p : items) {
                if (p instanceof Shippable s) {
                    BigDecimal w = Optional.ofNullable(s.weight()).orElse(BigDecimal.ZERO);
                    if (w.compareTo(BigDecimal.ZERO) > 0) {
                        weightedSum = weightedSum.add(p.price().multiply(w));
                        weightSum = weightSum.add(w);
                    }
                }
            }

            BigDecimal avg;
            if (weightSum.compareTo(BigDecimal.ZERO) > 0) {
                avg = weightedSum.divide(weightSum, 2, RoundingMode.HALF_UP);
            } else {
                BigDecimal sum = items.stream()
                        .map(Product::price)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                avg = sum.divide(BigDecimal.valueOf(items.size()), 2, RoundingMode.HALF_UP);
            }

            result.put(cat, avg);
        }

        return result;
    }

    /**
     * Calculates discounts for perishable products based on expiration proximity.
     */
    public Map<Product, BigDecimal> calculateExpirationBasedDiscounts() {
        Map<Product, BigDecimal> result = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Product p : Warehouse.getProducts()) {
            BigDecimal discounted = p.price();

            if (p instanceof FoodProduct f) {
                LocalDate exp = LocalDate.from(f.getExpirationDate());
                long daysBetween = ChronoUnit.DAYS.between(today, exp);

                if (daysBetween == 0) {
                    discounted = p.price().multiply(new BigDecimal("0.50"));
                } else if (daysBetween == 1) {
                    discounted = p.price().multiply(new BigDecimal("0.70"));
                } else if (daysBetween > 1 && daysBetween <= 3) {
                    discounted = p.price().multiply(new BigDecimal("0.85"));
                }

                discounted = discounted.setScale(2, RoundingMode.HALF_UP);
            }

            result.put(p, discounted);
        }

        return result;
    }

    /**
     * Validates warehouse inventory constraints.
     */
    public InventoryValidation validateInventoryConstraints() {
        List<Product> items = Warehouse.getProducts();
        if (items.isEmpty()) return new InventoryValidation(0.0, 0);

        BigDecimal highValueThreshold = new BigDecimal("1000");
        long highValueCount = items.stream()
                .filter(p -> p.price().compareTo(highValueThreshold) >= 0)
                .count();
/// /
        double percentage = (highValueCount * 100.0) / items.size();
        int diversity = (int) items.stream()
                .map(Product::getCategory)
                .distinct()
                .count();

        return new InventoryValidation(percentage, diversity);
    }

    /**
     * Aggregates key warehouse statistics.
     */
    public InventoryStatistics getInventoryStatistics() {
        List<Product> items = Warehouse.getProducts();
        int totalProducts = items.size();

        BigDecimal totalValue = items.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averagePrice = totalProducts == 0
                ? BigDecimal.ZERO
                : totalValue.divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP);

        int expiredCount = (int) items.stream()
                .filter(p -> p instanceof FoodProduct f && f.getExpirationDate().isBefore(LocalDate.now()))
                .count();

        int categoryCount = (int) items.stream()
                .map(Product::getCategory)
                .distinct()
                .count();

        Product mostExpensive = items.stream()
                .max(Comparator.comparing(Product::price))
                .orElse(null);

        Product cheapest = items.stream()
                .min(Comparator.comparing(Product::price))
                .orElse(null);

        return new InventoryStatistics(
                totalProducts,
                totalValue,
                averagePrice,
                expiredCount,
                categoryCount,
                mostExpensive,
                cheapest
        );
    }
//
    /**
     * Optimizes grouping of shippable items by max weight.
     */
    public List<ShippingGroup> optimizeShippingGroups(BigDecimal maxWeightPerGroup) {
        if (maxWeightPerGroup == null || maxWeightPerGroup.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Max weight per group must be positive.");
        }

        List<Shippable> items = new ArrayList<>(Warehouse.shippableProducts());
        items.sort((a, b) -> b.weight().compareTo(a.weight()));

        List<List<Shippable>> bins = new ArrayList<>();

        for (Shippable item : items) {
            BigDecimal itemWeight = item.weight() != null ? item.weight() : BigDecimal.ZERO;
            boolean placed = false;

            for (List<Shippable> bin : bins) {
                BigDecimal currentWeight = bin.stream()
                        .map(Shippable::weight)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (currentWeight.add(itemWeight).compareTo(maxWeightPerGroup) <= 0) {
                    bin.add(item);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                bins.add(new ArrayList<>(List.of(item)));
            }
        }

        return bins.stream()
                .map(ShippingGroup::new)
                .toList();
    }

    // === Inner Helper Classes ===

    public static class ShippingGroup {
        private final List<com.example.Shippable> products;
        private final BigDecimal totalWeight;
        private final BigDecimal totalShippingCost;

        public ShippingGroup(List<com.example.Shippable> products) {
            this.products = new ArrayList<com.example.Shippable>(products);
            this.totalWeight = products.stream()
                    .map(Shippable::weight)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalShippingCost = products.stream()
                    .map(Shippable::calculateShippingCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public List<Shippable> getProducts() { return new ArrayList<>(products); }
        public BigDecimal getTotalWeight() { return totalWeight; }
        public BigDecimal getTotalShippingCost() { return totalShippingCost; }
    }
    //
    public static class InventoryValidation {
        private final double highValuePercentage;
        private final int categoryDiversity;
        private final boolean highValueWarning;
        private final boolean minimumDiversity;

        public InventoryValidation(double highValuePercentage, int categoryDiversity) {
            this.highValuePercentage = highValuePercentage;
            this.categoryDiversity = categoryDiversity;
            this.highValueWarning = highValuePercentage > 70.0;
            this.minimumDiversity = categoryDiversity >= 2;
        }

        public double getHighValuePercentage() { return highValuePercentage; }
        public int getCategoryDiversity() { return categoryDiversity; }
        public boolean isHighValueWarning() { return highValueWarning; }
        public boolean hasMinimumDiversity() { return minimumDiversity; }
    }

    public record InventoryStatistics(
            int totalProducts,
            BigDecimal totalValue,
            BigDecimal averagePrice,
            int expiredCount,
            int categoryCount,
            Product mostExpensiveProduct,
            Product cheapestProduct
    ) { }
}