package fr.unice.polytech.sophiatecheats.application.dto.order.response;

import fr.unice.polytech.sophiatecheats.application.dto.order.RestaurantOrderDto;

import java.util.List;

/**
 * Réponse contenant la liste des commandes pour un restaurant.
 */
public record GetRestaurantOrdersResponse(
        List<RestaurantOrderDto> orders
) {
}

