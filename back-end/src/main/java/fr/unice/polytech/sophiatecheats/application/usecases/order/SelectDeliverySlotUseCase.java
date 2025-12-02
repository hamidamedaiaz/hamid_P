package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.SelectDeliverySlotResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.util.UUID;

/**
 * Use case pour sélectionner et réserver un créneau de livraison pour une commande.
 * <p>
 * Ce use case implémente la deuxième étape du flux: Order → Slot → Payment
 * Flux:
 * 1. Vérifie que la commande existe et n'a pas déjà de créneau
 * 2. Trouve le créneau demandé dans le restaurant
 * 3. Réserve le créneau
 * 4. Associe le créneau à la commande
 * 5. Sauvegarde la commande mise à jour
 */
public class SelectDeliverySlotUseCase implements UseCase<SelectDeliverySlotRequest, SelectDeliverySlotResponse> {

    private final CartRepository cartRepository;
    private final RestaurantRepository restaurantRepository;

    public SelectDeliverySlotUseCase(CartRepository cartRepository,
                                     RestaurantRepository restaurantRepository) {
        this.cartRepository = cartRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public SelectDeliverySlotResponse execute(SelectDeliverySlotRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        System.out.println("🔍 [SelectDeliverySlotUseCase] Executing with request: " + request);
        System.out.println("🔍 [SelectDeliverySlotUseCase] Looking for slot ID: " + request.slotId());

        // 1. Récupérer le panier actif (en utilisant orderId comme userId dans la requête)
        UUID userId = UUID.fromString(request.orderId()); // orderId contient en fait le userId pour le panier
        System.out.println("🔍 [SelectDeliverySlotUseCase] Looking for cart for user: " + userId);

        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Panier non trouvé pour l'utilisateur: " + userId));

        System.out.println("✅ [SelectDeliverySlotUseCase] Cart found: " + cart.getId());
        System.out.println("🍽️ [SelectDeliverySlotUseCase] Cart restaurant: " + cart.getRestaurantId());

        // 2. Vérifier que le panier n'est pas vide
        if (cart.isEmpty()) {
            throw new fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException(
                    "Le panier est vide. Veuillez ajouter des plats avant de sélectionner un créneau.");
        }

        // 3. Récupérer le restaurant depuis le repository (utilise SHARED_STORAGE entre les microservices)
        UUID restaurantId = cart.getRestaurantId();
        System.out.println("🔍 [SelectDeliverySlotUseCase] Cart restaurant: " + restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Restaurant non trouvé: " + restaurantId));

        System.out.println("✅ [SelectDeliverySlotUseCase] Restaurant found: " + restaurant.getName());

        // 4. Récupérer le créneau depuis le DeliverySchedule du restaurant
        System.out.println("🔍 [SelectDeliverySlotUseCase] Searching for slot in restaurant's DeliverySchedule...");
        System.out.println("🔍 [SelectDeliverySlotUseCase] SlotId: " + request.slotId());

        TimeSlot slot = restaurant.getDeliverySchedule().findSlotById(request.slotId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Créneau de livraison non trouvé dans le restaurant: " + request.slotId()));

        System.out.println("✅ [SelectDeliverySlotUseCase] Slot found: " + slot.getId() + " (" + slot.getStartTime() + " - " + slot.getEndTime() + ")");

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
