package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Classe utilitaire pour créer des données de test avec des UUIDs FIXES.
 * Utilisez ces UUIDs dans vos tests Postman/curl.
 */
public class TestDataFactory {

    // ========== UUIDs FIXES POUR LES TESTS ==========

    // RESTAURANTS
    public static final UUID CAFETERIA_ID = UUID.fromString("750e8400-e29b-41d4-a716-446655440001");
    public static final UUID FOOD_TRUCK_ID = UUID.fromString("750e8400-e29b-41d4-a716-446655440002");
    public static final UUID PIZZERIA_ID = UUID.fromString("750e8400-e29b-41d4-a716-446655440003");

    // PLATS - CAFETERIA
    public static final UUID DISH_SALADE_CESAR_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    public static final UUID DISH_SANDWICH_JAMBON_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440002");
    public static final UUID DISH_CAFE_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440003");
    public static final UUID DISH_TARTE_POMMES_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440004");

    // PLATS - FOOD TRUCK
    public static final UUID DISH_BOWL_VEGETARIEN_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440005");
    public static final UUID DISH_BURGER_BIO_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440006");
    public static final UUID DISH_JUS_FRUITS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440007");
    public static final UUID DISH_BROWNIE_VEGAN_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440008");

    // PLATS - PIZZERIA
    public static final UUID DISH_PIZZA_MARGHERITA_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440009");
    public static final UUID DISH_TIRAMISU_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440010");
    public static final UUID DISH_COCA_COLA_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440011");

    // TIME SLOTS
    public static final UUID SLOT_12H00_ID = UUID.fromString("950e8400-e29b-41d4-a716-446655440001");
    public static final UUID SLOT_12H30_ID = UUID.fromString("950e8400-e29b-41d4-a716-446655440002");
    public static final UUID SLOT_13H00_ID = UUID.fromString("950e8400-e29b-41d4-a716-446655440003");

    /**
     * Crée le restaurant Cafétéria avec des UUIDs fixes.
     */
    public static Restaurant createCafeteria() {
        Restaurant cafeteria = Restaurant.builder()
                .id(CAFETERIA_ID)
                .name("La Cafétéria")
                .address("Campus Sophia Antipolis - Bâtiment A")
                .build();

        cafeteria.setSchedule(LocalTime.of(8, 0), LocalTime.of(18, 0));

        cafeteria.addDish(createDish(
                DISH_SALADE_CESAR_ID,
                "Salade César",
                "Salade fraîche avec croûtons et parmesan",
                new BigDecimal("8.50"),
                DishCategory.MAIN_COURSE
        ));

        cafeteria.addDish(createDish(
                DISH_SANDWICH_JAMBON_ID,
                "Sandwich Jambon",
                "Pain de mie, jambon, beurre, salade",
                new BigDecimal("4.20"),
                DishCategory.MAIN_COURSE
        ));

        cafeteria.addDish(createDish(
                DISH_CAFE_ID,
                "Café",
                "Café expresso",
                new BigDecimal("1.50"),
                DishCategory.BEVERAGE
        ));

        cafeteria.addDish(createDish(
                DISH_TARTE_POMMES_ID,
                "Tarte aux pommes",
                "Tarte maison aux pommes",
                new BigDecimal("3.80"),
                DishCategory.DESSERT
        ));

        return cafeteria;
    }

    /**
     * Crée le Food Truck Bio avec des UUIDs fixes.
     */
    public static Restaurant createFoodTruck() {
        Restaurant foodTruck = Restaurant.builder()
                .id(FOOD_TRUCK_ID)
                .name("Food Truck Bio")
                .address("Parking Sud Campus")
                .build();

        foodTruck.setSchedule(LocalTime.of(11, 30), LocalTime.of(14, 30));

        foodTruck.addDish(createDish(
                DISH_BOWL_VEGETARIEN_ID,
                "Bowl Végétarien",
                "Quinoa, légumes grillés, avocat",
                new BigDecimal("9.90"),
                DishCategory.VEGETARIAN
        ));

        foodTruck.addDish(createDish(
                DISH_BURGER_BIO_ID,
                "Burger Bio",
                "Steak de bœuf bio, fromage, légumes",
                new BigDecimal("12.50"),
                DishCategory.MAIN_COURSE
        ));

        foodTruck.addDish(createDish(
                DISH_JUS_FRUITS_ID,
                "Jus de fruits frais",
                "Orange, pomme ou carotte",
                new BigDecimal("3.00"),
                DishCategory.BEVERAGE
        ));

        foodTruck.addDish(createDish(
                DISH_BROWNIE_VEGAN_ID,
                "Brownie Vegan",
                "Brownie sans produits animaux",
                new BigDecimal("4.50"),
                DishCategory.DESSERT
        ));

        return foodTruck;
    }

    /**
     * Crée la Pizzeria avec des UUIDs fixes.
     */
    public static Restaurant createPizzeria() {
        Restaurant pizzeria = Restaurant.builder()
                .id(PIZZERIA_ID)
                .name("Pizzeria du Campus")
                .address("Bâtiment C - Rez-de-chaussée")
                .build();

        pizzeria.setSchedule(LocalTime.of(12, 0), LocalTime.of(22, 0));
        pizzeria.close(); // Fermée par défaut

        pizzeria.addDish(createDish(
                DISH_PIZZA_MARGHERITA_ID,
                "Pizza Margherita",
                "Tomate, mozzarella, basilic",
                new BigDecimal("11.00"),
                DishCategory.MAIN_COURSE
        ));

        pizzeria.addDish(createDish(
                DISH_TIRAMISU_ID,
                "Tiramisu",
                "Dessert italien traditionnel",
                new BigDecimal("5.50"),
                DishCategory.DESSERT
        ));

        pizzeria.addDish(createDish(
                DISH_COCA_COLA_ID,
                "Coca-Cola",
                "Boisson gazeuse",
                new BigDecimal("2.50"),
                DishCategory.BEVERAGE
        ));

        return pizzeria;
    }

    /**
     * Méthode utilitaire pour créer un plat avec un UUID fixe.
     */
    private static Dish createDish(UUID id, String name, String description, BigDecimal price, DishCategory category) {
        return Dish.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(true)
                .build();
    }
}
