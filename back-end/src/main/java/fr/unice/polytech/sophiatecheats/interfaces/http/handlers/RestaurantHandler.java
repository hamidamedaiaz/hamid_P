package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.RestaurantDto;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.HttpUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * HTTP handler pour les opérations liées aux restaurants.
 */
public class RestaurantHandler implements RouteHandler {

    private final SophiaTechEatsFacade facade;

    public RestaurantHandler(SophiaTechEatsFacade facade) {
        this.facade = facade;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        // CORS is handled by ApiRegistry
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    if (pathParams.containsKey("id")) {
                        getRestaurantById(UUID.fromString(pathParams.get("id")), sender);
                    } else {
                        handleFilteredRestaurantList(exchange, sender);
                    }
                    break;
                case "POST":
                    createRestaurant(exchange, sender);
                    break;
                case "PUT":
                    if (pathParams.containsKey("id")) {
                        updateRestaurant(exchange, UUID.fromString(pathParams.get("id")), sender);
                    }
                    break;
                case "DELETE":
                    if (pathParams.containsKey("id")) {
                        deleteRestaurant(UUID.fromString(pathParams.get("id")), sender);
                    }
                    break;
                case "OPTIONS":
                    sender.send(HttpUtils.OK, "", null);
                    break;
                default:
                    sender.send(HttpUtils.BAD_REQUEST, "Method not supported", null);
                    break;
            }
        } catch (Exception e) {
            GlobalExceptionHandler.callWithGlobalExceptionHandling(exchange, () -> {
                throw e;
            });
        }
    }

    // GET /restaurants + filters ou pas
    private void handleFilteredRestaurantList(HttpExchange exchange, ResponseSender sender) throws IOException {
        URI requestUri = exchange.getRequestURI();
        Map<String, String> queryParams = parseQueryParams(requestUri.getQuery());

        List<RestaurantDto> restaurants = facade.browseAllRestaurants().restaurants();

        // Filter: cuisineType
        if (queryParams.containsKey("cuisineType")) {
            try {
                DietType category = DietType.valueOf(queryParams.get("cuisineType").toUpperCase());
                restaurants = restaurants.stream()
                        .filter(r -> r.dishes().stream().anyMatch(d -> d.dietTypes().contains(category)))
                        .toList();
            } catch (IllegalArgumentException e) {
                sender.send(HttpUtils.BAD_REQUEST, "Invalid cuisineType value", null);
                return;
            }
        }

        // Filter: restaurantType
        if (queryParams.containsKey("restaurantType")) {
            try {
                RestaurantType type = RestaurantType.valueOf(queryParams.get("restaurantType").toUpperCase());
                restaurants = restaurants.stream()
                        .filter(r -> type.equals(r.restaurantType()))
                        .toList();
            } catch (IllegalArgumentException e) {
                sender.send(HttpUtils.BAD_REQUEST, "Invalid restaurantType value", null);
                return;
            }
        }

        // Filter: isOpen
        if (queryParams.containsKey("isOpen")) {
            boolean open = Boolean.parseBoolean(queryParams.get("isOpen"));
            restaurants = restaurants.stream()
                    .filter(r -> r.isOpen() == open)
                    .toList();
        }

        List<RestaurantDto> restaurantDtos = restaurants.stream()
                .map(r -> new RestaurantDto(
                        r.id(), r.name(), r.address(),
                        r.openingTime(), r.closingTime(), r.isOpen(),
                        r.dishes().stream().map(d -> new DishDto(
                                d.id(), d.name(), d.description(), d.price(),
                                d.category(), d.available(), d.dietTypes())
                        ).toList(), r.restaurantType())
                )
                .toList();

        sender.send(
                HttpUtils.OK, JaxsonUtils.toJson(restaurantDtos),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
        );
    }

    private Map<String, String> parseQueryParams(String query) {
        if (query == null || query.isEmpty()) return Map.of();

        return Arrays.stream(query.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    private void getRestaurantById(UUID id, ResponseSender sender) throws IOException {
        RestaurantDto r = facade.browseAllRestaurants().restaurants().stream()
                .filter(rest -> rest.id().equals(id))
                .findFirst()
                .orElse(null);
        if (r == null) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Restaurant not found", null);
            return;
        }
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(r), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void createRestaurant(HttpExchange exchange, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        RestaurantDto dto = JaxsonUtils.fromJson(json, RestaurantDto.class);
        if (dto == null) throw new IllegalArgumentException("Invalid restaurant JSON");

        Restaurant r = facade.createRestaurant(dto.name(), dto.address());

        sender.send(HttpUtils.CREATED, "Restaurant created with ID: " + r.getId(), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }

    private void updateRestaurant(HttpExchange exchange, UUID id, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        RestaurantDto dto = JaxsonUtils.fromJson(json, RestaurantDto.class);
        if (dto == null) throw new IllegalArgumentException("Invalid restaurant JSON");

        if (dto.name() != null) facade.updateRestaurantName(id, dto.name());
        if (dto.address() != null) facade.updateRestaurantAddress(id, dto.address());
        if (dto.openingTime() != null && dto.closingTime() != null)
            facade.updateRestaurantOpeningHours(id, dto.openingTime(), dto.closingTime());

        sender.send(HttpUtils.OK, "Restaurant updated successfully.", Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }

    private void deleteRestaurant(UUID id, ResponseSender sender) throws IOException {
        facade.deleteRestaurant(id);
        sender.send(HttpUtils.NO_CONTENT, "", null);
    }
}