package fr.unice.polytech.sophiatecheats.application.dto;

import java.time.LocalDate;

public record GetAvailableDeliverySlotsRequest(
        java.util.UUID restaurantId,
        LocalDate date
) implements DTO {
    public boolean isValid() {
        return restaurantId!=null && date!=null;
    }

}
