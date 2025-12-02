package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.GetRestaurantOrdersResponse;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.HttpUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * HTTP handler pour les opérations liées aux commandes d'un restaurant.
 */
public class RestaurantOrdersHandler implements RouteHandler {

    private final SophiaTechEatsFacade facade;

    public RestaurantOrdersHandler(SophiaTechEatsFacade facade) {
        this.facade = facade;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                getRestaurantOrders(pathParams, sender);
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

    private void getRestaurantOrders(Map<String, String> pathParams, ResponseSender sender) throws IOException {
        if (!pathParams.containsKey("id")) {
            sender.send(HttpUtils.BAD_REQUEST, "Restaurant ID is required", null);
            return;
        }

        try {
            UUID restaurantId = UUID.fromString(pathParams.get("id"));
            GetRestaurantOrdersResponse response = facade.getRestaurantOrders(restaurantId);

            sender.send(
                    HttpUtils.OK,
                    JaxsonUtils.toJson(response),
                    Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
            );
        } catch (IllegalArgumentException e) {
            sender.send(HttpUtils.BAD_REQUEST, "Invalid restaurant ID format", null);
        }
    }
}

