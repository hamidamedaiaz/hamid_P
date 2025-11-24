package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.SelectDeliverySlotResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;

import java.util.UUID;

/**
 * Use case pour sélectionner un créneau de livraison pour le PANIER (avant paiement).
 *
 * FLUX : Cart → Slot → Payment → Order
 *
 * Ce use case permet à l'utilisateur de sélectionner un créneau de livraison
 * AVANT de payer. Le créneau est stocké dans le panier et sera utilisé lors
 * de la transformation du panier en commande (PlaceOrderUseCase).
 *
 * Étapes :
 * 1. Vérifie que le panier existe et n'est pas vide
 * 2. Valide que le créneau existe et appartient au restaurant du panier
 * 3. Valide la disponibilité et capacité du créneau
 * 4. Ajoute le créneau au panier (SANS le réserver)
 * 5. Sauvegarde le panier
 *
 * Note : Le créneau sera réservé lors du paiement (PlaceOrderUseCase), pas ici.
 */
public class SelectDeliverySlotUseCase implements UseCase<SelectDeliverySlotRequest, SelectDeliverySlotResponse> {

    private final CartRepository cartRepository;
    private final TimeSlotRepository timeSlotRepository;

    public SelectDeliverySlotUseCase(CartRepository cartRepository,
                                     TimeSlotRepository timeSlotRepository) {
        this.cartRepository = cartRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public SelectDeliverySlotResponse execute(SelectDeliverySlotRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        // 1. Récupérer le panier actif (en utilisant orderId comme userId dans la requête)
        UUID userId = UUID.fromString(request.orderId()); // orderId contient en fait le userId pour le panier
        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Panier non trouvé pour l'utilisateur: " + userId));

        // 2. Vérifier que le panier n'est pas vide
        if (cart.isEmpty()) {
            throw new fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException(
                "Le panier est vide. Veuillez ajouter des plats avant de sélectionner un créneau.");
        }

        // 3. Récupérer et valider le créneau
        TimeSlot slot = timeSlotRepository.findById(request.slotId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Créneau de livraison non trouvé: " + request.slotId()));

        // 4. Vérifier que le créneau appartient au restaurant du panier
        if (!slot.getRestaurantId().equals(cart.getRestaurantId())) {
            throw new fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException(
                "Le créneau de livraison ne correspond pas au restaurant de votre panier.");
        }

        // 5. Vérifier que le créneau est disponible
        if (!slot.isAvailable()) {
            throw new fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException(
                "Le créneau de livraison sélectionné n'est plus disponible.");
        }

        // 6. Vérifier la capacité du créneau
        if (slot.getReservedCount() >= slot.getMaxCapacity()) {
            throw new fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException(
                "Le créneau de livraison est complet. Veuillez en choisir un autre.");
        }

        // 7. Ajouter le créneau au panier (SANS le réserver - il sera réservé lors du paiement)
        cart.setDeliverySlot(request.slotId());

        // 8. Sauvegarder le panier
        cartRepository.save(cart);

        // 9. Retourner la réponse
        return new SelectDeliverySlotResponse(
                null, // Pas d'orderId pour un panier
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                "Slot successfully selected for cart"
        );
    }
}
