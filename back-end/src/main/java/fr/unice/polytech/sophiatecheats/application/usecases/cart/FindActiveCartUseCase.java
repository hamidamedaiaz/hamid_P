package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.FindCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

            // Convertir les items du panier en DTOs
            List<FindCartResponse.CartItemDto> itemDtos = cart.getItems().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new FindCartResponse(
                    cart.getId().toString(),
                    cart.getUserId().toString(),
                    itemDtos,
                    cart.calculateTotal(),
                    cart.getTotalItems(),
                    cart.getRestaurantId() != null ? cart.getRestaurantId().toString() : null
            );
        }


        // Si pas de panier, retourner une réponse "pas de panier"
        return new FindCartResponse(
                null,
                findCartRequest.userId().toString(),
                Collections.emptyList(),
                BigDecimal.ZERO,
                0,
                null
        );
    }

    private FindCartResponse.CartItemDto convertToDto(CartItem item) {
        return new FindCartResponse.CartItemDto(
                item.getDish().getId().toString(),
                item.getDish().getName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
