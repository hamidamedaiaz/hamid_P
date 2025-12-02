package fr.unice.polytech.sophiatecheats.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record FindCartResponse(
        String cartId,
        String userId,
        List<CartItemDto> items,
        BigDecimal totalAmount,
        int totalItems,
        String restaurantId

) implements DTO {
    public boolean isValid() {
        return cartId != null && userId != null;
    }

    public record CartItemDto(
            String dishId,
            String dishName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal
    ) {
    }
}
