package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.application.dto.GetAvailableDeliverySlotsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.delivery.DeliverySlotDTO;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Use case : récupérer les créneaux de livraison disponibles pour une date donnée.
 *
 * IMPORTANT: Récupère les créneaux directement depuis TimeSlotRepository pour garantir
 * la cohérence avec SelectDeliverySlotUseCase qui utilise également ce repository.
 */
public class GetAvailableDeliverySlotsUseCase implements UseCase<GetAvailableDeliverySlotsRequest, List<DeliverySlotDTO>> {

    private final TimeSlotRepository timeSlotRepository;

    public GetAvailableDeliverySlotsUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * Récupère les créneaux disponibles pour une date donnée depuis le TimeSlotRepository.
     * Utilise une requête optimisée qui filtre directement par restaurantId et date.
     */
    @Override
    public List<DeliverySlotDTO> execute(GetAvailableDeliverySlotsRequest request) {
        UUID restaurantId = request.restaurantId();
        LocalDate date = request.date();

        System.out.println("🔍 [GetAvailableDeliverySlotsUseCase] Fetching slots for restaurant: " + restaurantId + ", date: " + date);

        // Récupérer les créneaux disponibles directement depuis le TimeSlotRepository
        List<TimeSlot> slots = timeSlotRepository.findAvailableSlotsByRestaurantAndDate(restaurantId, date);

        System.out.println("✅ [GetAvailableDeliverySlotsUseCase] Found " + slots.size() + " available slots");

        return slots.stream()
                .map(this::toDto)
                .toList();
    }

    private DeliverySlotDTO toDto(TimeSlot s) {
        boolean available = s.isAvailable();
        return new DeliverySlotDTO(s.getId(), s.getRestaurantId(), s.getStartTime(), s.getEndTime(), s.getMaxCapacity(), s.getReservedCount(), available);
    }
}
