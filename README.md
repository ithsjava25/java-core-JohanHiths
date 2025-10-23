🕵️‍♂️ The Case of the Vanishing Code

Once upon a time in the bustling digital halls of the ITHS Java Lab, a brilliant team of developers built a sleek,
elegant application. It was fast. It was flawless. It was... undocumented.

But then—disaster struck. During a routine migration to a new version control system, someone (we won’t name names, but
let’s just say their coffee-to-commit ratio was dangerously high ☕💻) accidentally triggered the mythical git nuke
--force command. The source code vanished into the void.

Gone. Poof. Like it never existed.

All that remained were the sacred test files—meticulously crafted, oddly untouched by the chaos. These tests, like
ancient scrolls, held the secrets of what the application was supposed to do. They whispered of methods, behaviors, and
edge cases. They were our only clue.

Now, the mission falls to you: the brave code archaeologists of Java25. Your task is to reconstruct the lost
application, piece by piece, using only the tests as your guide. Think of it as reverse-engineering a fossilized
dinosaur from its footprints... except the dinosaur is a Java program, and the footprints are JUnit assertions.

Luckily, a few fragments of implementation survived—like half-buried ruins in the sand. Use them wisely. Interpret the
tests. Rebuild the logic. And maybe, just maybe, restore the glory of the lost codebase.

# Warehouse Kata – Instructions

Welcome! In this exercise you will implement a small domain around a Warehouse with products. Your goal is to:

- Create and complete several classes/interfaces under src/main/java that are currently missing or partially
  implemented.
- Pick correct data types for parameters and return values so the project compiles and tests can run.
- Make all tests in BasicTest green first.
- Commit after major steps. When all BasicTest tests are green, push your commits.
- Extra credit: implement the advanced features so that EdgeCaseTest is also green.

## 1) Getting started

1. Open this project in your IDE (IntelliJ IDEA recommended).
2. Ensure you have JDK 25.
3. Build once to see current state:
    - ./mvnw compile

## 2) What you will need to create/finish

You will work primarily in src/main/java/com/example. Some files already exist but contain TODOs or empty methods.
Implement the following:

- Category (value object)
    - Use a private constructor and a public static factory Category.of(String name).
    - Validate input: null => "Category name can't be null"; empty/blank => "Category name can't be blank".
    - Normalize name with initial capital letter (e.g., "fruit" -> "Fruit").
    - Cache/flyweight: return the same instance for the same normalized name.

- Product (abstract base class)
    - Keep UUID id, String name, Category category, BigDecimal price.
    - Provide getters named uuid(), name(), category(), price() and a setter price(BigDecimal).
    - Provide an abstract String productDetails() for polymorphism.

- FoodProduct (extends Product)
    - Implements Perishable and Shippable.
    - Fields: LocalDate expirationDate, BigDecimal weight (kg).
    - Validations: negative price -> IllegalArgumentException("Price cannot be negative."); negative weight ->
      IllegalArgumentException("Weight cannot be negative.").
    - productDetails() should look like: "Food: Milk, Expires: 2025-12-24".
    - Shipping rule: cost = weight * 50.

- ElectronicsProduct (extends Product)
    - Implements Shippable.
    - Fields: int warrantyMonths, BigDecimal weight (kg).
    - Validation: negative warranty -> IllegalArgumentException("Warranty months cannot be negative.").
    - productDetails() should look like: "Electronics: Laptop, Warranty: 24 months".
    - Shipping rule: base 79, add 49 if weight > 5.0 kg.

- Interfaces
    - Perishable: expose expirationDate() and a default isExpired() based on LocalDate.now().
    - Shippable: expose calculateShippingCost() and weight() (used by shipping optimizer in extra tests).

- Warehouse (singleton per name)
    - getInstance(String name) returns the same instance per unique name.
    - addProduct(Product): throw IllegalArgumentException("Product cannot be null.") if null.
    - getProducts(): return an unmodifiable copy.
    - getProductById(UUID): return Optional.
    - updateProductPrice(UUID, BigDecimal): when not found, throw NoSuchElementException("Product not found with
      id: <uuid>"). Also track changed products in getChangedProducts().
    - expiredProducts(): return List<Perishable> that are expired.
    - shippableProducts(): return List<Shippable> from stored products.
    - remove(UUID): remove the matching product if present.

- WarehouseAnalyzer (extra credit)
    - Implement the advanced methods used by EdgeCaseTest: price-range search (inclusive), expiring-within-days,
      case-insensitive name search, above-price search, weighted average per category (round to 2 decimals), price
      outliers (population stddev), shipping group optimization (first‑fit decreasing by weight), expiration-based
      discounts, inventory validation summary, and inventory statistics.

## 3) Workflow to follow

1. Implement the missing classes/interfaces and methods so the project compiles.
2. Run tests:
    - Basic first: ./mvnw -Dtest=BasicTest test
    - When green, commit with a clear message.
3. Extra credit: make EdgeCaseTest green:
    - ./mvnw -Dtest=EdgeCaseTest test
4. Commit after each major milestone (e.g., "Implement Product & FoodProduct", "Warehouse behaviors", "Analyzer advanced
   features").
5. Push when BasicTest is fully green (and EdgeCaseTest too if you do the extra credit).

## 4) Tips

- Prefer BigDecimal for prices and weights (exact values in tests). Where an interface requires Double (e.g., weight()),
  convert BigDecimal to double on return.
- Always round monetary results to 2 decimals using HALF_UP when tests assert exact values.
- Keep public APIs exactly as tests expect (method names, exception messages).
- Ensure Warehouse.clearProducts() is called in tests; do not share state between tests.

Good luck and have fun!
