package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.RestaurantDto;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.infrastructure.api.HttpUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.infrastructure.api.ResponseSender;
import fr.unice.polytech.sophiatecheats.infrastructure.api.RouteHandler;

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
 * Pour le moment, on a juste la gestion des restaurants (CRUD).
 * Les autres opérations sur les restaurants (gestion des plats, des créneaux, etc.)
 * peuvent être ajoutées ici ultérieurement.
 */
public class RestaurantApiHandler implements RouteHandler {

    private final RestaurantService restaurantService;

    public RestaurantApiHandler(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        // CORS headers sont déjà ajoutés globalement dans ApiRegistry, pas besoin de les re-ajouter ici

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

        List<Restaurant> restaurants = restaurantService.listRestaurants();

        // Filter: cuisineType
        if (queryParams.containsKey("cuisineType")) {
            try {
                DietType category = DietType.valueOf(queryParams.get("cuisineType").toUpperCase());
                restaurants = restaurants.stream()
                        .filter(r -> r.getMenu().stream().anyMatch(d -> d.hasDietType(category)))
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
                        .filter(r -> type.equals(r.getRestaurantType()))
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
                        r.getId(), r.getName(), r.getAddress(),
                        r.getOpeningTime(), r.getClosingTime(), r.isOpen(),
                        r.getMenu().stream().map(d -> new DishDto(
                                d.getId(), d.getName(), d.getDescription(), d.getPrice(),
                                d.getCategory(), d.isAvailable(), d.getDietTypes())
                        ).toList()
                ))
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
        Restaurant r = restaurantService.getRestaurantById(id);
        if (r == null) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Restaurant not found", null);
            return;
        }
        RestaurantDto dto = new RestaurantDto(
                r.getId(), r.getName(), r.getAddress(),
                r.getOpeningTime(), r.getClosingTime(), r.isOpen(),
                r.getMenu().stream().map(d -> new DishDto(
                        d.getId(), d.getName(), d.getDescription(), d.getPrice(),
                        d.getCategory(), d.isAvailable(), d.getDietTypes())
                ).toList()
        );
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(dto), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void createRestaurant(HttpExchange exchange, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        RestaurantDto dto = JaxsonUtils.fromJson(json, RestaurantDto.class);
        if (dto == null) throw new IllegalArgumentException("Invalid restaurant JSON");

        Restaurant r = restaurantService.createRestaurant(dto.name(), dto.address());

        sender.send(HttpUtils.CREATED, "Restaurant created with ID: " + r.getId(), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }

    private void updateRestaurant(HttpExchange exchange, UUID id, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        RestaurantDto dto = JaxsonUtils.fromJson(json, RestaurantDto.class);
        if (dto == null) throw new IllegalArgumentException("Invalid restaurant JSON");

        if (dto.name() != null) restaurantService.updateRestaurantName(id, dto.name());
        if (dto.address() != null) restaurantService.updateRestaurantAddress(id, dto.address());
        if (dto.openingTime() != null && dto.closingTime() != null)
            restaurantService.updateRestaurantOpeningHours(id, dto.openingTime(), dto.closingTime());

        sender.send(HttpUtils.OK, "Restaurant updated successfully.", Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }

    private void deleteRestaurant(UUID id, ResponseSender sender) throws IOException {
        restaurantService.deleteRestaurant(id);
        sender.send(HttpUtils.NO_CONTENT, "", null);
    }
}
