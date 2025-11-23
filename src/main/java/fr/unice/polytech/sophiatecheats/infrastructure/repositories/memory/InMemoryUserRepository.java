package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation en mémoire du repository utilisateur.
 * Utilisée pour les tests et le développement.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        // Repository vide - les utilisateurs seront créés dynamiquement si nécessaire
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return users.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return users.containsKey(id);
    }

    public long count() {
        return users.size();
    }

    public void clear() {
        users.clear();
    }

    public User createTestUser(String email, String name, BigDecimal credit) {
        User user = new User(email, name);
        if (credit != null && credit.compareTo(BigDecimal.ZERO) > 0) {
            user.addCredit(credit);
        }
        return save(user);
    }
}
