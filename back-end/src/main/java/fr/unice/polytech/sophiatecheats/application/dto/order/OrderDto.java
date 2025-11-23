package fr.unice.polytech.sophiatecheats.application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        String orderId,
        String userId,
        String restaurantId,
        List<OrderItemDto> items,
        BigDecimal totalAmount,
        String status,
        LocalDateTime orderDateTime,
        LocalDateTime deliveryTime,
        String paymentMethod,
        String deliverySlotId
) {

    public static OrderDto fromEntity(fr.unice.polytech.sophiatecheats.domain.entities.order.Order order) {
        return new OrderDto(
                order.getOrderId(),
                order.getUser().getId().toString(),
                order.getRestaurant().getId().toString(),
                order.getOrderItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .toList(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getOrderDateTime(),
                order.getDeliveryTime(),
                order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null,
                order.getDeliverySlotId() != null ? order.getDeliverySlotId().toString() : null
        );
    }
}
