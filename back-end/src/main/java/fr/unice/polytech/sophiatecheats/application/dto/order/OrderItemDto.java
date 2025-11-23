package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;

public record OrderItemDto(
        DishDto dish,
        String name,
        int quantity,
        String totalPrice
) {
    public static OrderItemDto fromEntity(fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem orderItem) {
        return new OrderItemDto(
                DishDto.fromEntity(orderItem.getDish()),
                orderItem.getDish().getName(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice().toString()
        );
    }
}
