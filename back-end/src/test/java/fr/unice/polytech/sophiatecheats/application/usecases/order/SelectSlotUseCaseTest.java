package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SelectDeliverySlotUseCase - Validation panier avant créneau")
class SelectSlotUseCaseTest {
    private CartRepository cartRepository;
    private TimeSlotRepository timeSlotRepository;
    private SelectDeliverySlotUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        timeSlotRepository = mock(TimeSlotRepository.class);
        useCase = new SelectDeliverySlotUseCase(cartRepository, timeSlotRepository);
    }

    // TODO: Mettre à jour ce test car SelectDeliverySlotUseCase gère maintenant les CARTS, pas les ORDERS
    // Dans le nouveau flux : Cart → Slot → Payment → Order
    // Le slot est sélectionné AVANT la création de la commande, donc pas de validation Order ici

    /*
    @Test
    @DisplayName("Impossible de réserver un créneau pour un panier vide")
    void cannotReserveSlotForEmptyCart() {
        // TODO: Implémenter ce test pour valider qu'on ne peut pas sélectionner un slot
        // si le panier est vide
    }
    */
}
