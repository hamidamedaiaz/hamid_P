package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.order.RestaurantOrderDto;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.GetRestaurantOrdersResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.util.List;
import java.util.UUID;

/**
 * Use case pour récupérer toutes les commandes d'un restaurant.
 * Filtre pour ne montrer que les commandes payées et confirmées.
 */
public class GetRestaurantOrdersUseCase implements UseCase<UUID, GetRestaurantOrdersResponse> {

    private final OrderRepository orderRepository;

    public GetRestaurantOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public GetRestaurantOrdersResponse execute(UUID restaurantId) {
        List<Order> orders = orderRepository.findAllByRestaurantId(restaurantId);

        // Filtrer uniquement les commandes payées ou confirmées
        List<RestaurantOrderDto> restaurantOrders = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID ||
                                order.getStatus() == OrderStatus.CONFIRMED ||
                                order.getStatus() == OrderStatus.PREPARING)
                .map(RestaurantOrderDto::fromEntity)
                .toList();

        return new GetRestaurantOrdersResponse(restaurantOrders);
    }
}

