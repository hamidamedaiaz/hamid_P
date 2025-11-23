package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.UpdateCartItemRequest;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

/**
 * Use case pour mettre à jour la quantité d'un CartItem dans le panier.
 *
 * <p>Ce use case permet de modifier la quantité d'un article existant (CartItem) dans le panier.
 * Un CartItem contient un Dish avec sa quantité et son prix unitaire.
 * Si la nouvelle quantité est 0 ou négative, le CartItem est supprimé du panier.</p>
 *
 * <h3>Flux nominal:</h3>
 * <ol>
 *   <li>Récupère le panier actif de l'utilisateur</li>
 *   <li>Vérifie que le CartItem existe (identifié par dishId)</li>
 *   <li>Met à jour la quantité du CartItem via cart.updateQuantity(dishId, newQuantity)</li>
 *   <li>Si quantité ≤ 0 → le CartItem est supprimé automatiquement</li>
 *   <li>Sauvegarde le panier mis à jour</li>
 * </ol>
 *
 * <h3>Règles métier:</h3>
 * <ul>
 *   <li>Quantité maximale par CartItem: 10 (validé dans Cart.java)</li>
 *   <li>Quantité ≤ 0 → suppression automatique du CartItem</li>
 * </ul>
 *
 * @author SophiaTech Eats Team
 */
public class UpdateCartItemUseCase implements UseCase<UpdateCartItemRequest, Void> {

    private static final int MAX_QUANTITY = 10;

    private final CartRepository cartRepository;

    public UpdateCartItemUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Void execute(UpdateCartItemRequest request) {
        if (request == null || request.userId() == null || request.dishId() == null) {
            throw new IllegalArgumentException("Invalid request: userId and dishId cannot be null");
        }

        // Validation de la quantité maximale (seulement si > 0)
        if (request.newQuantity() > MAX_QUANTITY) {
            throw new ValidationException(
                    "Quantité maximale dépassée. Maximum autorisé: " + MAX_QUANTITY);
        }

        // Récupérer le panier actif
        Cart cart = cartRepository.findActiveCartByUserId(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun panier actif trouvé pour l'utilisateur: " + request.userId()));

        // Vérifier que le CartItem existe dans le panier (identifié par dishId)
        boolean itemExists = cart.getItems().stream()
                .anyMatch(item -> item.getDishId().equals(request.dishId()));

        if (!itemExists) {
            throw new ValidationException(
                    "L'article avec le plat " + request.dishId() + " n'existe pas dans le panier");
        }

        // Mettre à jour la quantité du CartItem
        // Si newQuantity ≤ 0, cart.updateQuantity() supprimera automatiquement le CartItem
        cart.updateQuantity(request.dishId(), request.newQuantity());

        // Sauvegarder le panier mis à jour
        cartRepository.save(cart);

        return null;
    }
}
