package fr.unice.polytech.sophiatecheats.application.mappers;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.RestaurantDto;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;

import java.util.stream.Collectors;

public class RestaurantMapper {
    public static RestaurantDto toDto(Restaurant restaurant) {
        return new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.isOpen(),
                restaurant.getMenu().stream()
                        .map(DishMapper::toDto)
                        .collect(Collectors.toList())
                , restaurant.getRestaurantType()
        );
    }
}
