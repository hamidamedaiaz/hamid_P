package fr.unice.polytech.sophiatecheats.infrastructure.repositories.shared;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository partagé entre les services Restaurant et Order&Payment
 * Utilise un Singleton pour partager les données en mémoire
 */
public class SharedRestaurantRepository implements RestaurantRepository {

    private static final InMemoryRestaurantRepository SHARED_INSTANCE = new InMemoryRestaurantRepository();

    public SharedRestaurantRepository() {
        // Utilise toujours la même instance partagée
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return SHARED_INSTANCE.findById(id);
    }

    @Override
    public List<Restaurant> findAll() {
        return SHARED_INSTANCE.findAll();
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        return SHARED_INSTANCE.save(restaurant);
    }

    @Override
    public boolean deleteById(UUID id) {
        return SHARED_INSTANCE.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return SHARED_INSTANCE.existsById(id);
    }

    @Override
    public List<Restaurant> findByAvailability(boolean isOpen) {
        return List.of();
    }

    @Override
    public List<Restaurant> findByDishCategory(DishCategory category) {
        return List.of();
    }

    @Override
    public List<Restaurant> findOpenByDishCategory(DishCategory category) {
        return List.of();
    }
}
