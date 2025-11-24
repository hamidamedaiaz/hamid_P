package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.FindCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

import java.util.Optional;


public class FindActiveCartUseCase implements UseCase<FindCartRequest, FindCartResponse> {

    private final CartRepository cartRepository;

    public FindActiveCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public FindCartResponse execute(FindCartRequest findCartRequest) {
        // Chercher le panier actif
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUserId(findCartRequest.userId());

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            return new FindCartResponse(
                    cart.getId().toString(),
                    cart.getUserId().toString()
            );
        }

        // Si pas de panier, retourner une réponse "pas de panier"
        return new FindCartResponse(
                null,
                findCartRequest.userId().toString()
        );
    }

    private FindCartResponse createNotFoundResponse(String string) {
        return new FindCartResponse(
                null,
                string
        );
    }
}
