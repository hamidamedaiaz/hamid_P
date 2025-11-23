package fr.unice.polytech.sophiatecheats.application.usecases.user.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.OrderDto;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

public class GetOrderUseCase {
    private final OrderRepository orderRepository;

    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDto execute(String orderId) {
        return orderRepository.findById(orderId)
                .map(OrderDto::fromEntity)
                .orElse(null);
    }
}
