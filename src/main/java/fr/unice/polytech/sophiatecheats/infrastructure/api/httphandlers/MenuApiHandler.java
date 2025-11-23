package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.infrastructure.api.HttpUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.ResponseSender;
import fr.unice.polytech.sophiatecheats.infrastructure.api.RouteHandler;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class MenuApiHandler implements RouteHandler {

    private final RestaurantService restaurantService;

    public MenuApiHandler(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        String method = exchange.getRequestMethod();
        UUID restaurantId = UUID.fromString(pathParams.get("id"));
        UUID dishId = pathParams.containsKey("dishId") ? UUID.fromString(pathParams.get("dishId")) : null;

        switch (method) {
            case "GET":
                getMenu(exchange, restaurantId, sender);
                break;
            case "POST":
                addDish(exchange, restaurantId, sender);
                break;
            case "PUT":
                if (dishId != null) updateDish(exchange, restaurantId, dishId, sender);
                break;
            case "DELETE":
                if (dishId != null) removeDish(restaurantId, dishId, sender);
                break;
            case "OPTIONS":
                sender.send(HttpUtils.OK, "", null);
                break;
            default:
                sender.send(HttpUtils.BAD_REQUEST, "Method not supported", null);
        }
    }

    private void getMenu(HttpExchange exchange, UUID restaurantId, ResponseSender sender) throws IOException {
        sender.send(HttpUtils.OK,
                JaxsonUtils.toJson(restaurantService.getRestaurantMenu(restaurantId)),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void addDish(HttpExchange exchange, UUID restaurantId, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        DishDto dto = JaxsonUtils.fromJson(json, DishDto.class);

        assert dto != null;
        restaurantService.addDishToRestaurant(
                restaurantId,
                dto.name(),
                dto.description(),
                dto.price() != null ? dto.price() : BigDecimal.ZERO,
                dto.category()
        );

        // Retourner le menu complet en JSON au lieu de texte
        sender.send(HttpUtils.CREATED,
                JaxsonUtils.toJson(restaurantService.getRestaurantMenu(restaurantId)),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void updateDish(HttpExchange exchange, UUID restaurantId, UUID dishId, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        DishDto dto = JaxsonUtils.fromJson(json, DishDto.class);

        assert dto != null;
        if (dto.name() != null) restaurantService.updateDishName(restaurantId, dishId, dto.name());
        if (dto.description() != null) restaurantService.updateDishDescription(restaurantId, dishId, dto.description());
        if (dto.price() != null) restaurantService.updateDishPrice(restaurantId, dishId, dto.price());
        if (dto.category() != null) restaurantService.updateDishCategory(restaurantId, dishId, dto.category());

        // Retourner le menu complet en JSON au lieu de texte
        sender.send(HttpUtils.OK,
                JaxsonUtils.toJson(restaurantService.getRestaurantMenu(restaurantId)),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void removeDish(UUID restaurantId, UUID dishId, ResponseSender sender) throws IOException {
        restaurantService.removeDishFromRestaurant(restaurantId, dishId);
        sender.send(HttpUtils.NO_CONTENT, "", null);
    }
}
