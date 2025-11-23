package fr.unice.polytech.sophiatecheats.application.dto;

public record FindCartResponse(
        String cartId,
        String userId

) implements DTO {
    public boolean isValid() {
        return cartId != null && userId != null;
    }
}
