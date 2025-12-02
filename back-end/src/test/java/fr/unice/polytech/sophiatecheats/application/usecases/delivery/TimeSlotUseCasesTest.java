package fr.unice.polytech.sophiatecheats.application.usecases.delivery;

import fr.unice.polytech.sophiatecheats.application.dto.GetAvailableDeliverySlotsRequest;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.GetAvailableDeliverySlotsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.SelectSlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.ValidateDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour les cas d’utilisation concernant les créneaux de livraison.
 */
class TimeSlotUseCasesTest {

    private DeliveryService deliveryService;
    private RestaurantRepository repository;
    private RestaurantService restaurantService;
    private GetAvailableDeliverySlotsUseCase getAvailableUC;
    private SelectSlotUseCase selectSlotUC;
    private ValidateDeliverySlotUseCase validateSlotUC;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RestaurantRepository.class);
        TimeSlotRepository timeSlotRepository =
                Mockito.mock(fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository.class);

        restaurantService = new RestaurantService(repository, timeSlotRepository);
        deliveryService = Mockito.mock(DeliveryService.class);

        getAvailableUC = new GetAvailableDeliverySlotsUseCase(restaurantService);
        selectSlotUC = new SelectSlotUseCase(deliveryService);
        validateSlotUC = new ValidateDeliverySlotUseCase(deliveryService);
        restaurantId = UUID.randomUUID();
    }

    @Test
    void testGetAvailableDeliverySlots() {
        LocalDate today = LocalDate.now();

        GetAvailableDeliverySlotsRequest request = new GetAvailableDeliverySlotsRequest(
                restaurantId,
                today
        );

        // Mock the restaurant repository to return a restaurant
        fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant mockRestaurant =
                new fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant(
                        restaurantId,
                        "Test Restaurant",
                        "Test Address",
                        fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Schedule.defaultSchedule(),
                        true,
                        java.util.List.of(),
                        new fr.unice.polytech.sophiatecheats.domain.entities.delivery.DeliverySchedule(restaurantId)
                );
        when(repository.findById(restaurantId)).thenReturn(java.util.Optional.of(mockRestaurant));

        var result = getAvailableUC.execute(request);

        assertEquals(0, result.size()); // Le restaurant n'a pas de slots configurés pour cette date
    }

    @Test
    void testSelectDeliverySlotReturnsTrueWhenAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(slotId, restaurantId,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 2);
        SelectSlotUseCase.Input input = new SelectSlotUseCase.Input(slotId, today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of(slot));

        boolean ok = selectSlotUC.execute(input);

        assertTrue(ok, "Le créneau devrait être sélectionnable");
        verify(deliveryService).getAvailableSlots(today);
    }

    @Test
    void testSelectDeliverySlotReturnsFalseWhenNotAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        SelectSlotUseCase.Input input = new SelectSlotUseCase.Input(slotId, today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of());

        boolean ok = selectSlotUC.execute(input);
        assertFalse(ok, "Le créneau devrait être indisponible");
    }

    @Test
    void testValidateDeliverySlotReservesWhenStillAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(slotId, restaurantId,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 2);
        ValidateDeliverySlotUseCase.Input input = new ValidateDeliverySlotUseCase.Input(slot.getId(), today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of(slot));

        validateSlotUC.execute(input);

        verify(deliveryService).getAvailableSlots(today);
        verify(deliveryService).reserveSlot(slotId);
    }

    @Test
    void testValidateDeliverySlotThrowsIfNotAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of());
        ValidateDeliverySlotUseCase.Input input = new ValidateDeliverySlotUseCase.Input(slotId, today);

        assertThrows(SlotNotFoundException.class, () -> validateSlotUC.execute(input));
        verify(deliveryService).getAvailableSlots(today);
        verify(deliveryService, never()).reserveSlot(any());
    }
}