package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case pour permettre à un manager de définir ou modifier la capacité max d'un créneau.
 */
public class SetTimeSlotCapacityUseCase implements UseCase<SetTimeSlotCapacityUseCase.Request, Void> {
    private final RestaurantRepository restaurantRepository;

    public SetTimeSlotCapacityUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Void execute(Request request) {
        if (request == null || request.restaurantId == null || request.slotId == null)
            throw new IllegalArgumentException("Paramètres invalides");
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant non trouvé: " + request.restaurantId));
        Optional<TimeSlot> slotOpt = restaurant.getDeliverySchedule().findSlotById(request.slotId);
        TimeSlot slot = slotOpt.orElseThrow(() -> new SlotNotFoundException("Créneau non trouvé: " + request.slotId));
        slot.setMaxCapacity(request.newMaxCapacity); // Validation métier dans TimeSlot
        restaurantRepository.save(restaurant);
        return null;
    }

    public record Request(UUID restaurantId, UUID slotId, int newMaxCapacity) {
    }
}

