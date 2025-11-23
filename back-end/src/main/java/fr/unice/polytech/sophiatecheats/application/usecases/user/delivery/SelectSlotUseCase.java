package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Sélection simple d'un créneau, pas de réservation définitive.
 */
public class SelectSlotUseCase implements UseCase<SelectSlotUseCase.Input, Boolean> {

    private final DeliveryService deliveryService;

    public SelectSlotUseCase(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * @param input les données d'entrée : identifiant du créneau et date
     * @return true si le créneau est encore disponible
     */
    @Override
    public Boolean execute(Input input) {
        return deliveryService.getAvailableSlots(input.date).stream()
                .anyMatch(s -> s.getId().equals(input.slotId));
    }

    public record Input(UUID slotId, LocalDate date) {
    }
}
