package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.FindCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;


public class FindActiveCartUseCase implements UseCase<FindCartRequest, FindCartResponse> {

    private final CartRepository cartRepository;

    public FindActiveCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public FindCartResponse execute(FindCartRequest findCartRequest) {
        if (cartRepository.hasActiveCart(findCartRequest.userId())) {
            return cartRepository.findActiveCartByUserId(findCartRequest.userId()).map(
                    cart -> new FindCartResponse(
                            cart.getId().toString(),
                            cart.getUserId().toString()
                    )
            ).orElseGet(() -> createNotFoundResponse(findCartRequest.userId().toString()));
        }
        return null;
    }

    private FindCartResponse createNotFoundResponse(String string) {
        return new FindCartResponse(
                null,
                string
        );
    }
}
