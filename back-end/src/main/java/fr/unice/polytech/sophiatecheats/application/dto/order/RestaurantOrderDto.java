package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les commandes vues par le restaurant.
 * Exclut les informations sensibles de paiement de l'étudiant.
 */
public record RestaurantOrderDto(
        String orderId,
        String customerName,
        List<OrderItemDto> items,
        BigDecimal totalAmount,
        String status,
        LocalDateTime orderDateTime,
        LocalDateTime deliveryTime,
        boolean isPaid
) {

    public static RestaurantOrderDto fromEntity(Order order) {
        return new RestaurantOrderDto(
                order.getOrderId(),
                order.getUser().getName(),
                order.getOrderItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .toList(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getOrderDateTime(),
                order.getDeliveryTime(),
                order.getStatus().name().equals("PAID") || order.getStatus().name().equals("CONFIRMED")
        );
    }
}

