package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.RestaurantDto;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.api.HttpUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.ResponseSender;
import fr.unice.polytech.sophiatecheats.infrastructure.api.RouteHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Handler pour les opérations sur les plats (Dishes)
 * Permet au service Order & Payment d'interroger le service Restaurant
 */
public class DishApiHandler implements RouteHandler {

    private final RestaurantRepository restaurantRepository;

    public DishApiHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws Exception {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                String dishId = pathParams.get("dishId");
                if (dishId != null) {
                    if (exchange.getRequestURI().getPath().endsWith("/restaurant")) {
                        // GET /api/dishes/{dishId}/restaurant
                        getRestaurantByDishId(UUID.fromString(dishId), sender);
                    } else {
                        // GET /api/dishes/{dishId}
                        getDishById(UUID.fromString(dishId), sender);
                    }
                }
            } else if ("OPTIONS".equals(method)) {
                sender.send(HttpUtils.OK, "", null);
            } else {
                sender.send(HttpUtils.BAD_REQUEST, "Method not supported", null);
            }
        } catch (Exception e) {
            GlobalExceptionHandler.callWithGlobalExceptionHandling(exchange, () -> {
                throw e;
            });
        }
    }

    private void getDishById(UUID dishId, ResponseSender sender) throws IOException {
        Optional<Dish> dishOpt = findDishById(dishId);

        if (dishOpt.isEmpty()) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Dish not found", null);
            return;
        }

        Dish dish = dishOpt.get();
        DishDto dishDto = new DishDto(
            dish.getId(),
            dish.getName(),
            dish.getDescription(),
            dish.getPrice(),
            dish.getCategory(),
            dish.isAvailable(),
            dish.getDietTypes()
        );

        sender.send(
            HttpUtils.OK,
            JaxsonUtils.toJson(dishDto),
            Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
        );
    }

    private void getRestaurantByDishId(UUID dishId, ResponseSender sender) throws IOException {
        Optional<Restaurant> restaurantOpt = findRestaurantByDishId(dishId);

        if (restaurantOpt.isEmpty()) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Restaurant not found for this dish", null);
            return;
        }

        Restaurant r = restaurantOpt.get();
        RestaurantDto restaurantDto = new RestaurantDto(
            r.getId(),
            r.getName(),
            r.getAddress(),
            r.getOpeningTime(),
            r.getClosingTime(),
            r.isOpen(),
            r.getMenu().stream().map(d -> new DishDto(
                d.getId(), d.getName(), d.getDescription(), d.getPrice(),
                d.getCategory(), d.isAvailable(), d.getDietTypes())
            ).toList()
        );

        sender.send(
            HttpUtils.OK,
            JaxsonUtils.toJson(restaurantDto),
            Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
        );
    }

    private Optional<Dish> findDishById(UUID dishId) {
        return restaurantRepository.findAll().stream()
            .flatMap(restaurant -> restaurant.getMenu().stream())
            .filter(dish -> dish.getId().equals(dishId))
            .findFirst();
    }

    private Optional<Restaurant> findRestaurantByDishId(UUID dishId) {
        return restaurantRepository.findAll().stream()
            .filter(restaurant -> restaurant.getMenu().stream()
                .anyMatch(dish -> dish.getId().equals(dishId)))
            .findFirst();
    }
}

