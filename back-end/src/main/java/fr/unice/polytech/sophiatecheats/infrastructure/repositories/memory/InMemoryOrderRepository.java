package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory implementation of OrderRepository for MVP.
 */
public class InMemoryOrderRepository extends InMemoryRepository<Order, String> implements OrderRepository {

    private final fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository restaurantRepository;
    private final fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository userRepository;

    /**
     * Constructeur par défaut pour les tests (sans données mockées).
     */
    public InMemoryOrderRepository() {
        this.restaurantRepository = null;
        this.userRepository = null;
        // Pas d'initialisation de données mockées pour les tests
    }

    /**
     * Constructeur avec injection de dépendances pour créer des données mockées.
     */
    public InMemoryOrderRepository(
            fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository restaurantRepository,
            fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        initializeMockOrders();
    }

    private void initializeMockOrders() {
        if (restaurantRepository == null || userRepository == null) {
            return;
        }

        List<fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant> restaurants = restaurantRepository.findAll();
        List<fr.unice.polytech.sophiatecheats.domain.entities.user.User> users = userRepository.findAll();

        if (restaurants.isEmpty() || users.isEmpty()) {
            return;
        }

        // Récupérer le premier restaurant et les premiers utilisateurs
        fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant restaurant1 = restaurants.get(0);
        fr.unice.polytech.sophiatecheats.domain.entities.user.User user1 = users.get(0);

        // Commande 1: Utilisateur 1 commande au Restaurant 1 (PAID)
        if (!restaurant1.getMenu().isEmpty()) {
            List<fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem> items1 = new java.util.ArrayList<>();
            items1.add(new fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem(
                    restaurant1.getMenu().get(0), 1));
            if (restaurant1.getMenu().size() > 1) {
                items1.add(new fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem(
                        restaurant1.getMenu().get(1), 1));
            }

            Order order1 = new Order(user1, restaurant1, items1,
                    fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod.STUDENT_CREDIT);
            order1.assignDeliverySlot(UUID.randomUUID(), java.time.LocalDateTime.now().plusHours(2));
            order1.markAsPaid();
            save(order1);
        }

        // Commande 2: Si on a un deuxième utilisateur et restaurant (PAID)
        if (users.size() > 1 && restaurants.size() > 1) {
            fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant restaurant2 = restaurants.get(1);
            fr.unice.polytech.sophiatecheats.domain.entities.user.User user2 = users.get(1);

            if (!restaurant2.getMenu().isEmpty()) {
                List<fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem> items2 = new java.util.ArrayList<>();
                items2.add(new fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem(
                        restaurant2.getMenu().get(0), 2));

                Order order2 = new Order(user2, restaurant2, items2,
                        fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod.EXTERNAL_CARD);
                order2.assignDeliverySlot(UUID.randomUUID(), java.time.LocalDateTime.now().plusHours(3));
                order2.markAsPaid();
                save(order2);
            }
        }

        // Commande 3: Utilisateur 1 commande au Restaurant 1 (CONFIRMED)
        if (!restaurant1.getMenu().isEmpty() && restaurant1.getMenu().size() > 2) {
            List<fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem> items3 = new java.util.ArrayList<>();
            items3.add(new fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem(
                    restaurant1.getMenu().get(2), 1));

            Order order3 = new Order(user1, restaurant1, items3,
                    fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod.STUDENT_CREDIT);
            order3.assignDeliverySlot(UUID.randomUUID(), java.time.LocalDateTime.now().plusHours(4));
            order3.markAsPaid();
            order3.confirm();
            save(order3);
        }
    }

    @Override
    protected String extractId(Order order) {
        return order.getOrderId();
    }

    @Override
    public boolean existsActiveOrderByUserId(UUID userId) {
        for (Order order : storage.values()) {
            if (order.getUser().getId().equals(userId) &&
                    (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.PAID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Order> findAllByStatus(OrderStatus status) {
        return storage.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllByRestaurantId(UUID restaurantId) {
        return storage.values().stream()
                .filter(order -> order.getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.toList());
    }
}