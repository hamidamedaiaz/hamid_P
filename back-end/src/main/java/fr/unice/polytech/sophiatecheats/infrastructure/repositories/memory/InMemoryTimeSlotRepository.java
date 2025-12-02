package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;

import java.time.LocalDate;
import java.util.*;

public class InMemoryTimeSlotRepository implements TimeSlotRepository {

    // Shared static storage across all instances (all microservices)
    private static final Map<UUID, TimeSlot> SHARED_STORAGE = new java.util.concurrent.ConcurrentHashMap<>();

    private final Map<UUID, TimeSlot> storage = SHARED_STORAGE;

    public InMemoryTimeSlotRepository() {
        System.out.println("🕒 [InMemoryTimeSlotRepository] Created with shared storage, current size: " + storage.size());
    }

    @Override
    public List<TimeSlot> findAvailableSlots(LocalDate date) {
        return storage.values().stream()
                .filter(slot -> slot.getStartTime().toLocalDate().equals(date))
                .filter(TimeSlot::isAvailable)
                .toList();
    }

    @Override
    public TimeSlot save(TimeSlot slot) {
        storage.put(slot.getId(), slot);
        System.out.println("💾 [InMemoryTimeSlotRepository] Saved slot: " + slot.getId() + " | Total slots in storage: " + storage.size());
        return slot;
    }

    @Override
    public void update(TimeSlot slot) {
        storage.put(slot.getId(), slot);
    }

    @Override
    public Optional<TimeSlot> findById(UUID id) {
        TimeSlot slot = storage.get(id);
        System.out.println("🔍 [InMemoryTimeSlotRepository] findById(" + id + ") - Found: " + (slot != null) + " | Total slots: " + storage.size());
        if (slot == null && !storage.isEmpty()) {
            System.out.println("📋 [InMemoryTimeSlotRepository] Available slot IDs:");
            storage.keySet().stream().limit(5).forEach(slotId ->
                System.out.println("  - " + slotId)
            );
        }
        return Optional.ofNullable(slot);
    }

    @Override
    public List<TimeSlot> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public Optional<TimeSlot> findByIdAndRestaurantId(UUID slotId, UUID restaurantId) {
        TimeSlot slot = storage.get(slotId);
        System.out.println("🔍 [InMemoryTimeSlotRepository] findByIdAndRestaurantId(slotId=" + slotId + ", restaurantId=" + restaurantId + ")");

        if (slot != null) {
            System.out.println("  ✅ Slot found with restaurantId: " + slot.getRestaurantId());
            // Vérifier que le créneau appartient bien au restaurant
            if (slot.getRestaurantId().equals(restaurantId)) {
                System.out.println("  ✅ Slot belongs to restaurant!");
                return Optional.of(slot);
            } else {
                System.out.println("  ❌ Slot does NOT belong to restaurant (expected: " + restaurantId + ", got: " + slot.getRestaurantId() + ")");
            }
        } else {
            System.out.println("  ❌ Slot not found in storage");
        }

        return Optional.empty();
    }

    @Override
    public List<TimeSlot> findAvailableSlotsByRestaurantAndDate(UUID restaurantId, LocalDate date) {
        System.out.println("🔍 [InMemoryTimeSlotRepository] findAvailableSlotsByRestaurantAndDate(restaurantId=" + restaurantId + ", date=" + date + ")");

        List<TimeSlot> slots = storage.values().stream()
                .filter(slot -> slot.getRestaurantId().equals(restaurantId))
                .filter(slot -> slot.getStartTime().toLocalDate().equals(date))
                .filter(TimeSlot::isAvailable)
                .toList();

        System.out.println("  ✅ Found " + slots.size() + " available slots for restaurant " + restaurantId);
        return slots;
    }
}
