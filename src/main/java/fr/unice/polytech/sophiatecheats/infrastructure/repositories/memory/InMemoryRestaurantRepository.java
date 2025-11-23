package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

/**
 * In-memory implementation of RestaurantRepository for MVP.
 * Contains sample data for testing and development.
 */
public class InMemoryRestaurantRepository extends InMemoryRepository<Restaurant, UUID> implements RestaurantRepository {

    public InMemoryRestaurantRepository() {
        this(true);
    }

    public InMemoryRestaurantRepository(boolean withSampleData) {
        if (withSampleData) {
            initializeWithSampleData();
        }
    }

    @Override
    protected UUID extractId(Restaurant entity) {
        return entity.getId();
    }

    @Override
    public List<Restaurant> findByAvailability(boolean isOpen) {
        return findAll().stream()
                .filter(restaurant -> restaurant.isOpen() == isOpen)
                .toList();
    }

    @Override
    public List<Restaurant> findByDishCategory(DishCategory category) {
        return findAll().stream()
                .filter(restaurant -> hasMenuCategory(restaurant, category))
                .toList();
    }

    @Override
    public List<Restaurant> findOpenByDishCategory(DishCategory category) {
        return findAll().stream()
                .filter(Restaurant::isOpen)
                .filter(restaurant -> hasMenuCategory(restaurant, category))
                .toList();
    }

    private boolean hasMenuCategory(Restaurant restaurant, DishCategory category) {
        return restaurant.getMenu().stream()
                .anyMatch(dish -> dish.getCategory() == category);
    }

    private void initializeWithSampleData() {
        // IDs fixes pour les restaurants de test - ne changent jamais !
        UUID CAFETERIA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID FOOD_TRUCK_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID PIZZERIA_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

        Restaurant cafeteria = Restaurant.builder()
                .id(CAFETERIA_ID)
                .name("La Cafétéria")
                .address("Campus Sophia Antipolis - Bâtiment A")
                .build();
        cafeteria.setSchedule(LocalTime.of(8, 0), LocalTime.of(18, 0));

        cafeteria.addDish(Dish.builder()
                .name("Salade César")
                .description("Salade fraîche avec croûtons et parmesan")
                .price(new BigDecimal("8.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Sandwich Jambon")
                .description("Pain de mie, jambon, beurre, salade")
                .price(new BigDecimal("4.20"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Café")
                .description("Café expresso")
                .price(new BigDecimal("1.50"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Tarte aux pommes")
                .description("Tarte maison aux pommes")
                .price(new BigDecimal("3.80"))
                .category(DishCategory.DESSERT)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        Restaurant foodTruck = Restaurant.builder()
                .id(FOOD_TRUCK_ID)
                .name("Food Truck Bio")
                .address("Parking Sud Campus")
                .build();
        foodTruck.setSchedule(LocalTime.of(11, 30), LocalTime.of(14, 30));

        foodTruck.addDish(Dish.builder()
                .name("Bowl Végétarien")
                .description("Quinoa, légumes grillés, avocat")
                .addDietType(DietType.VEGETARIAN)
                .price(new BigDecimal("9.90"))
                .category(DishCategory.VEGETARIAN)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Burger Bio")
                .description("Steak de bœuf bio, fromage, légumes")
                .price(new BigDecimal("12.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Jus de fruits frais")
                .description("Orange, pomme ou carotte")
                .price(new BigDecimal("3.00"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Brownie Vegan")
                .description("Brownie sans produits animaux")
                .addDietType(DietType.VEGAN)
                .price(new BigDecimal("4.50"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());

        Restaurant pizzeria = Restaurant.builder()
                .id(PIZZERIA_ID)
                .name("Pizzeria du Campus")
                .address("Bâtiment C - Rez-de-chaussée")
                .build();
        pizzeria.setSchedule(LocalTime.of(12, 0), LocalTime.of(22, 0));
        pizzeria.close();

        pizzeria.addDish(Dish.builder()
                .name("Pizza Margherita")
                .description("Tomate, mozzarella, basilic")
                .price(new BigDecimal("11.00"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());
        pizzeria.addDish(Dish.builder()
                .name("Tiramisu")
                .description("Dessert italien traditionnel")
                .price(new BigDecimal("5.50"))
                .category(DishCategory.DESSERT)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());
        pizzeria.addDish(Dish.builder()
                .name("Coca-Cola")
                .description("Boisson gazeuse")
                .price(new BigDecimal("2.50"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        save(cafeteria);
        save(foodTruck);
        save(pizzeria);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        isDuplicate(restaurant, storage);
        storage.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    public static void isDuplicate(Restaurant restaurant, Map<UUID, Restaurant> restaurants) {
        boolean duplicate = restaurants.values().stream().anyMatch(r ->
                !r.getId().equals(restaurant.getId()) && r.getName().equalsIgnoreCase(restaurant.getName())
                        && r.getAddress().equalsIgnoreCase(restaurant.getAddress())
        );

        if (duplicate) {
            throw new DuplicateRestaurantException(restaurant.getName(), restaurant.getAddress());
        }
    }

    @Override
    public Optional<Restaurant> findById(UUID uuid) {
        return Optional.ofNullable(storage.get(uuid));
    }

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return storage.remove(uuid) != null;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return storage.containsKey(uuid);
    }

    public void delete(Restaurant restaurant) {
        storage.remove(restaurant.getId());
    }

    public Restaurant findByName(String name) {
        return storage.values().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

