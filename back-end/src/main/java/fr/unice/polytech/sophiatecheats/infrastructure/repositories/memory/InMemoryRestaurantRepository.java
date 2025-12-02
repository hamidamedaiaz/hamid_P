package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory implementation of RestaurantRepository for MVP.
 * Contains sample data for testing and development.
 *
 * IMPORTANT: Uses SHARED STATIC storage to ensure all microservices
 * (Restaurant Service and Consumer Service) access the same data.
 */
public class InMemoryRestaurantRepository extends InMemoryRepository<Restaurant, UUID> implements RestaurantRepository {

    // Shared static storage across all instances (all microservices)
    private static final java.util.concurrent.ConcurrentHashMap<UUID, Restaurant> SHARED_STORAGE = new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    public InMemoryRestaurantRepository() {
        this(true);
    }

    public InMemoryRestaurantRepository(boolean withSampleData) {
        // Use parent constructor with shared storage
        super(SHARED_STORAGE);

        System.out.println("🏗️ [InMemoryRestaurantRepository] Constructor called - withSampleData=" + withSampleData + ", initialized=" + initialized + ", storage size=" + storage.size());

        // Initialize sample data only once across all instances
        if (withSampleData && !initialized) {
            synchronized (InMemoryRestaurantRepository.class) {
                if (!initialized) {
                    System.out.println("🔄 [InMemoryRestaurantRepository] Initializing SHARED storage with sample data");
                    System.out.println("📊 [InMemoryRestaurantRepository] Storage before init: " + storage.size() + " restaurants");
                    initializeWithSampleData();
                    initialized = true;
                    System.out.println("✅ [InMemoryRestaurantRepository] Shared storage initialized with " + storage.size() + " restaurants");
                }
            }
        } else if (withSampleData) {
            System.out.println("ℹ️ [InMemoryRestaurantRepository] Using existing SHARED storage with " + storage.size() + " restaurants");
        } else {
            System.out.println("⚠️ [InMemoryRestaurantRepository] Created WITHOUT sample data, storage has " + storage.size() + " restaurants");
        }
    }

    private void isDuplicate(Restaurant restaurant) {
        boolean duplicate = storage.values().stream().anyMatch(r ->
                !r.getId().equals(restaurant.getId()) && r.getName().equalsIgnoreCase(restaurant.getName())
                        && r.getAddress().equalsIgnoreCase(restaurant.getAddress())
        );

        if (duplicate) {
            throw new DuplicateRestaurantException(restaurant.getName(), restaurant.getAddress());
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

//    private void initializeWithSampleData() {
//        Restaurant cafeteria = new Restaurant("La Cafétéria", "Campus Sophia Antipolis - Bâtiment A");
//        cafeteria.setSchedule(LocalTime.of(8, 0), LocalTime.of(18, 0));
//
//        cafeteria.addDish(Dish.builder()
//                .name("Salade César")
//                .description("Salade fraîche avec croûtons et parmesan")
//                .price(new BigDecimal("8.50"))
//                .category(DishCategory.MAIN_COURSE)
//                .addDietType(DietType.VEGETARIAN)
//                .available(true)
//                .build());
//        cafeteria.addDish(Dish.builder()
//                .name("Sandwich Jambon")
//                .description("Pain de mie, jambon, beurre, salade")
//                .price(new BigDecimal("4.20"))
//                .category(DishCategory.MAIN_COURSE)
//                .addDietType(DietType.NONE)
//                .available(true)
//                .build());
//        cafeteria.addDish(Dish.builder()
//                .name("Café")
//                .description("Café expresso")
//                .price(new BigDecimal("1.50"))
//                .category(DishCategory.BEVERAGE)
//                .addDietType(DietType.VEGAN)
//                .available(true)
//                .build());
//        cafeteria.addDish(Dish.builder()
//                .name("Tarte aux pommes")
//                .description("Tarte maison aux pommes")
//                .price(new BigDecimal("3.80"))
//                .category(DishCategory.DESSERT)
//                .addDietType(DietType.VEGETARIAN)
//                .available(true)
//                .build());
//
//        Restaurant foodTruck = new Restaurant("Food Truck Bio", "Parking Sud Campus");
//        foodTruck.setSchedule(LocalTime.of(11, 30), LocalTime.of(14, 30));
//
//        foodTruck.addDish(Dish.builder()
//                .name("Bowl Végétarien")
//                .description("Quinoa, légumes grillés, avocat")
//                .addDietType(DietType.VEGETARIAN)
//                .price(new BigDecimal("9.90"))
//                .category(DishCategory.VEGETARIAN)
//                .available(true)
//                .build());
//        foodTruck.addDish(Dish.builder()
//                .name("Burger Bio")
//                .description("Steak de bœuf bio, fromage, légumes")
//                .price(new BigDecimal("12.50"))
//                .category(DishCategory.MAIN_COURSE)
//                .addDietType(DietType.NONE)
//                .available(true)
//                .build());
//        foodTruck.addDish(Dish.builder()
//                .name("Jus de fruits frais")
//                .description("Orange, pomme ou carotte")
//                .price(new BigDecimal("3.00"))
//                .category(DishCategory.BEVERAGE)
//                .addDietType(DietType.VEGAN)
//                .available(true)
//                .build());
//        foodTruck.addDish(Dish.builder()
//                .name("Brownie Vegan")
//                .description("Brownie sans produits animaux")
//                .addDietType(DietType.VEGAN)
//                .price(new BigDecimal("4.50"))
//                .category(DishCategory.DESSERT)
//                .available(true)
//                .build());
//
//        Restaurant pizzeria = new Restaurant("Pizzeria du Campus", "Bâtiment C - Rez-de-chaussée");
//        pizzeria.setSchedule(LocalTime.of(12, 0), LocalTime.of(22, 0));
//        pizzeria.close();
//
//        pizzeria.addDish(Dish.builder()
//                .name("Pizza Margherita")
//                .description("Tomate, mozzarella, basilic")
//                .price(new BigDecimal("11.00"))
//                .category(DishCategory.MAIN_COURSE)
//                .addDietType(DietType.VEGETARIAN)
//                .available(true)
//                .build());
//        pizzeria.addDish(Dish.builder()
//                .name("Tiramisu")
//                .description("Dessert italien traditionnel")
//                .price(new BigDecimal("5.50"))
//                .category(DishCategory.DESSERT)
//                .addDietType(DietType.VEGETARIAN)
//                .available(true)
//                .build());
//        pizzeria.addDish(Dish.builder()
//                .name("Coca-Cola")
//                .description("Boisson gazeuse")
//                .price(new BigDecimal("2.50"))
//                .category(DishCategory.BEVERAGE)
//                .addDietType(DietType.VEGAN)
//                .available(true)
//                .build());
//
//        save(cafeteria);
//        save(foodTruck);
//        save(pizzeria);
//    }

    private void initializeWithSampleData() {
        // ========== LA CAFÉTÉRIA ==========
        Restaurant cafeteria = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440001"))
                .name("La Cafétéria")
                .address("Campus Sophia Antipolis - Bâtiment A")
                .build();

        cafeteria.setSchedule(LocalTime.of(8, 0), LocalTime.of(18, 0));

        cafeteria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440001"))
                .name("Salade César")
                .description("Salade fraîche avec croûtons et parmesan")
                .price(new BigDecimal("8.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        cafeteria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440002"))
                .name("Sandwich Jambon")
                .description("Pain de mie, jambon, beurre, salade")
                .price(new BigDecimal("4.20"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        cafeteria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440003"))
                .name("Café")
                .description("Café expresso")
                .price(new BigDecimal("1.50"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        cafeteria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440004"))
                .name("Tarte aux pommes")
                .description("Tarte maison aux pommes")
                .price(new BigDecimal("3.80"))
                .category(DishCategory.DESSERT)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        // ========== FOOD TRUCK BIO ==========
        Restaurant foodTruck = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440002"))
                .name("Food Truck Bio")
                .address("Parking Sud Campus")
                .build();

        foodTruck.setSchedule(LocalTime.of(11, 30), LocalTime.of(14, 30));

        foodTruck.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440005"))
                .name("Bowl Végétarien")
                .description("Quinoa, légumes grillés, avocat")
                .addDietType(DietType.VEGETARIAN)
                .price(new BigDecimal("9.90"))
                .category(DishCategory.VEGETARIAN)
                .available(true)
                .build());

        foodTruck.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440006"))
                .name("Burger Bio")
                .description("Steak de bœuf bio, fromage, légumes")
                .price(new BigDecimal("12.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        foodTruck.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440007"))
                .name("Jus de fruits frais")
                .description("Orange, pomme ou carotte")
                .price(new BigDecimal("3.00"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        foodTruck.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440008"))
                .name("Brownie Vegan")
                .description("Brownie sans produits animaux")
                .addDietType(DietType.VEGAN)
                .price(new BigDecimal("4.50"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());

        // ========== PIZZERIA DU CAMPUS ==========
        Restaurant pizzeria = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440003"))
                .name("Pizzeria du Campus")
                .address("Bâtiment C - Rez-de-chaussée")
                .build();

        pizzeria.setSchedule(LocalTime.of(12, 0), LocalTime.of(22, 0));
        pizzeria.close(); // Fermée par défaut

        pizzeria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440009"))
                .name("Pizza Margherita")
                .description("Tomate, mozzarella, basilic")
                .price(new BigDecimal("11.00"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        pizzeria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440010"))
                .name("Tiramisu")
                .description("Dessert italien traditionnel")
                .price(new BigDecimal("5.50"))
                .category(DishCategory.DESSERT)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        pizzeria.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440011"))
                .name("Coca-Cola")
                .description("Boisson gazeuse")
                .price(new BigDecimal("2.50"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        // ========== SUSHI BAR CAMPUS ==========
        Restaurant sushiBar = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440004"))
                .name("Sushi Bar Campus")
                .address("Bâtiment B - 1er étage")
                .build();

        sushiBar.setSchedule(LocalTime.of(11, 30), LocalTime.of(20, 0));

        sushiBar.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440012"))
                .name("California Roll")
                .description("Saumon, avocat, concombre, sésame")
                .price(new BigDecimal("9.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        sushiBar.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440013"))
                .name("Veggie Maki")
                .description("Assortiment de makis végétariens")
                .price(new BigDecimal("8.00"))
                .category(DishCategory.VEGETARIAN)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        sushiBar.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440014"))
                .name("Miso Soup")
                .description("Soupe miso traditionnelle")
                .price(new BigDecimal("3.50"))
                .category(DishCategory.STARTER)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        sushiBar.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440015"))
                .name("Thé Vert")
                .description("Thé vert japonais")
                .price(new BigDecimal("2.00"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        // ========== LE COUSCOUS D'OR ==========
        Restaurant couscous = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440005"))
                .name("Le Couscous d'Or")
                .address("Avenue Valrose")
                .build();

        couscous.setSchedule(LocalTime.of(12, 0), LocalTime.of(15, 0));

        couscous.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440016"))
                .name("Couscous Poulet")
                .description("Couscous traditionnel au poulet et légumes")
                .price(new BigDecimal("13.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        couscous.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440017"))
                .name("Tajine Végétarien")
                .description("Tajine aux légumes de saison")
                .price(new BigDecimal("11.00"))
                .category(DishCategory.VEGETARIAN)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        couscous.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440018"))
                .name("Brick à l'œuf")
                .description("Brick croustillante garnie d'un œuf")
                .price(new BigDecimal("4.50"))
                .category(DishCategory.STARTER)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        couscous.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440019"))
                .name("Thé à la Menthe")
                .description("Thé vert à la menthe fraîche")
                .price(new BigDecimal("2.50"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        // ========== TACOS & BURRITOS ==========
        Restaurant tacos = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440006"))
                .name("Tacos & Burritos")
                .address("Place Jean-Paul II")
                .build();

        tacos.setSchedule(LocalTime.of(11, 0), LocalTime.of(23, 0));

        tacos.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440020"))
                .name("Burrito Poulet")
                .description("Burrito au poulet grillé, riz, haricots")
                .price(new BigDecimal("10.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        tacos.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440021"))
                .name("Tacos Végétariens")
                .description("3 tacos aux haricots noirs et fromage")
                .price(new BigDecimal("9.00"))
                .category(DishCategory.VEGETARIAN)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        tacos.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440022"))
                .name("Nachos")
                .description("Nachos avec guacamole et fromage fondu")
                .price(new BigDecimal("6.50"))
                .category(DishCategory.STARTER)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        tacos.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440023"))
                .name("Limonade Maison")
                .description("Limonade fraîche faite maison")
                .price(new BigDecimal("3.00"))
                .category(DishCategory.BEVERAGE)
                .addDietType(DietType.VEGAN)
                .available(true)
                .build());

        // ========== PASTA MILANO ==========
        Restaurant pasta = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440007"))
                .name("Pasta Milano")
                .address("Rue Albert Einstein")
                .build();

        pasta.setSchedule(LocalTime.of(12, 0), LocalTime.of(21, 30));

        pasta.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440024"))
                .name("Carbonara")
                .description("Pâtes à la crème, lardons, parmesan")
                .price(new BigDecimal("12.00"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        pasta.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440025"))
                .name("Penne Arrabbiata")
                .description("Pennes à la sauce tomate épicée")
                .price(new BigDecimal("10.00"))
                .category(DishCategory.VEGETARIAN)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        pasta.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440026"))
                .name("Pizza 4 Fromages")
                .description("Mozzarella, gorgonzola, parmesan, chèvre")
                .price(new BigDecimal("13.00"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        pasta.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440027"))
                .name("Panna Cotta")
                .description("Dessert italien onctueux au coulis de fruits")
                .price(new BigDecimal("5.00"))
                .category(DishCategory.DESSERT)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        // ========== LE WOK EXPRESS ==========
        Restaurant wok = Restaurant.builder()
                .id(UUID.fromString("750e8400-e29b-41d4-a716-446655440008"))
                .name("Le Wok Express")
                .address("Boulevard de la Madeleine")
                .build();

        wok.setSchedule(LocalTime.of(11, 30), LocalTime.of(22, 0));

        wok.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440028"))
                .name("Pad Thai Crevettes")
                .description("Nouilles sautées aux crevettes et légumes")
                .price(new BigDecimal("11.50"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        wok.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440029"))
                .name("Riz Cantonais")
                .description("Riz sauté aux légumes et œuf")
                .price(new BigDecimal("8.50"))
                .category(DishCategory.VEGETARIAN)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        wok.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440030"))
                .name("Nems Végétariens")
                .description("6 nems aux légumes avec sauce")
                .price(new BigDecimal("5.50"))
                .category(DishCategory.STARTER)
                .addDietType(DietType.VEGETARIAN)
                .available(true)
                .build());

        wok.addDish(Dish.builder()
                .id(UUID.fromString("650e8400-e29b-41d4-a716-446655440031"))
                .name("Bobun Bœuf")
                .description("Vermicelles, bœuf grillé, légumes croquants")
                .price(new BigDecimal("12.00"))
                .category(DishCategory.MAIN_COURSE)
                .addDietType(DietType.NONE)
                .available(true)
                .build());

        // Sauvegarder les restaurants
        save(cafeteria);
        save(foodTruck);
        save(pizzeria);
        save(sushiBar);
        save(couscous);
        save(tacos);
        save(pasta);
        save(wok);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        isDuplicate(restaurant);
        storage.put(restaurant.getId(), restaurant);
        return restaurant;
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
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
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