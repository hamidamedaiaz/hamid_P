package fr.unice.polytech.sophiatecheats.application.mappers;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;

public class DishMapper {
    public static DishDto toDto(Dish dish) {
        return new DishDto(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                dish.isAvailable(),
                dish.getDietTypes()
        );
    }
}
