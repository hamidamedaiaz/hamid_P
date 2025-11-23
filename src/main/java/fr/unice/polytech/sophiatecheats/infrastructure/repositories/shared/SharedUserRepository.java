package fr.unice.polytech.sophiatecheats.infrastructure.repositories.shared;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository partagé pour les utilisateurs
 */
public class SharedUserRepository implements UserRepository {

    private static final InMemoryUserRepository SHARED_INSTANCE = new InMemoryUserRepository();

    @Override
    public Optional<User> findById(UUID id) {
        return SHARED_INSTANCE.findById(id);
    }

    @Override
    public List<User> findAll() {
        return SHARED_INSTANCE.findAll();
    }

    @Override
    public User save(User user) {
        return SHARED_INSTANCE.save(user);
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
    public Optional<User> findByEmail(String email) {
        return SHARED_INSTANCE.findByEmail(email);
    }
}
