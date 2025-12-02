package fr.unice.polytech.sophiatecheats.application.dto;

import java.util.UUID;

public record FindCartRequest(
        UUID userId
) implements DTO {

    @Override
    public boolean isValid() {
        return userId != null;
    }
}
