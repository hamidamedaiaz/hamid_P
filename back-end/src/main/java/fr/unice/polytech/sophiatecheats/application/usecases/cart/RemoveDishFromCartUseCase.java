package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.RemoveFromCartRequest;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

/**
 * Use case pour supprimer un CartItem (article) du panier.
 *
 * <p>Ce use case permet de retirer un article complet du panier.
 * Un CartItem contient un Dish avec sa quantité et son prix unitaire.
 * Si l'article n'existe pas dans le panier, une exception est levée.</p>
 *
 * <h3>Flux nominal:</h3>
 * <ol>
 *   <li>Récupère le panier actif de l'utilisateur</li>
 *   <li>Vérifie que le CartItem (identifié par dishId) existe</li>
 *   <li>Supprime le CartItem du panier via cart.removeDish(dishId)</li>
 *   <li>Sauvegarde le panier mis à jour</li>
 * </ol>
 *
 * @author SophiaTech Eats Team
 */
public class RemoveDishFromCartUseCase implements UseCase<RemoveFromCartRequest, Void> {

    private final CartRepository cartRepository;

    public RemoveDishFromCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Void execute(RemoveFromCartRequest request) {
        if (request == null || request.userId() == null || request.dishId() == null) {
            throw new IllegalArgumentException("Invalid request: userId and dishId cannot be null");
        }

        // Récupérer le panier actif
        Cart cart = cartRepository.findActiveCartByUserId(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun panier actif trouvé pour l'utilisateur: " + request.userId()));

        // Vérifier que le CartItem existe dans le panier (via dishId)
        boolean itemExists = cart.getItems().stream()
                .anyMatch(item -> item.getDishId().equals(request.dishId()));

        if (!itemExists) {
            throw new ValidationException(
                    "L'article avec le plat " + request.dishId() + " n'existe pas dans le panier");
        }

        // Supprimer le CartItem (la méthode cart.removeDish supprime l'item par dishId)
        cart.removeDish(request.dishId());

        // Sauvegarder le panier mis à jour
        cartRepository.save(cart);

        return null;
    }
}
