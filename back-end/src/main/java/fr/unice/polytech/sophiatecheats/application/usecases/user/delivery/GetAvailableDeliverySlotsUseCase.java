package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.application.dto.GetAvailableDeliverySlotsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.delivery.DeliverySlotDTO;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Use case : récupérer les créneaux de livraison disponibles pour une date donnée.
 */
public class GetAvailableDeliverySlotsUseCase implements UseCase<GetAvailableDeliverySlotsRequest, List<DeliverySlotDTO>> {

    private final RestaurantService restaurantService;

    public GetAvailableDeliverySlotsUseCase(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Récupère les créneaux pour une date donnée.
     */
    @Override
    public List<DeliverySlotDTO> execute(GetAvailableDeliverySlotsRequest request) {
        UUID restaurantId = request.restaurantId();
        LocalDate date = request.date();
        return restaurantService.getDeliverySlots(restaurantId, date).stream()
                .map(this::toDto)
                .toList();
    }

    private DeliverySlotDTO toDto(TimeSlot s) {
        boolean available = s.isAvailable();
        return new DeliverySlotDTO(s.getId(), s.getRestaurantId(), s.getStartTime(), s.getEndTime(), s.getMaxCapacity(), s.getReservedCount(), available);
    }
}
