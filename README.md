# FoodShop (barebone)

Spring Boot 3 + Thymeleaf + H2 (in-memory). Needs Java 17+ and Maven.

Run: `mvn spring-boot:run` (or run `FoodShopApplication` in your IDE), then open http://localhost:8080

Flow: Menu (/) -> Add to cart -> Cart (/cart: change quantity, remove) -> Checkout (/checkout) -> Confirmation (/order/{id}).
Orders are saved in the database (tables: orders, order_items). Data resets on restart. H2 console: /h2-console (JDBC URL jdbc:h2:mem:foodshop).

Edit the menu items in `DataLoader.java`. No login and no real payment (barebone).
