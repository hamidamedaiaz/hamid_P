package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.HttpUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.util.Map;

public class OrderHandler implements RouteHandler {

    private final SophiaTechEatsFacade facade;

    public OrderHandler(SophiaTechEatsFacade facade) {
        this.facade = facade;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        // CORS is handled by ApiRegistry

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "POST":
                    handlePost(exchange, pathParams, path, sender);
                    break;
                case "GET":
                    handleGet(pathParams, sender);
                    break;
                case "OPTIONS":
                    sender.send(HttpUtils.OK, "", null);
                    break;
                default:
                    sender.send(HttpUtils.BAD_REQUEST, "Method not supported", null);
            }
        } catch (Exception e) {
            GlobalExceptionHandler.callWithGlobalExceptionHandling(exchange, () -> {
                throw e;
            });
        }
    }

    private void handlePost(HttpExchange exchange, Map<String, String> pathParams, String path, ResponseSender sender) throws IOException {
        if (path.endsWith("/delivery-slot")) {
            selectDeliverySlot(exchange, pathParams.get("id"), sender);
        } else if (path.endsWith("/payment")) {
            initiatePayment(pathParams.get("id"), sender);
        } else if (path.endsWith("/confirm")) {
            confirmOrder(exchange, pathParams.get("id"), sender);
        } else {
            createOrder(exchange, sender);
        }
    }

    private void handleGet(Map<String, String> pathParams, ResponseSender sender) throws IOException {
        if (pathParams.containsKey("id")) {
            getOrder(pathParams.get("id"), sender);
        }
    }

    private void createOrder(HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        PlaceOrderRequest request = JaxsonUtils.fromJson(json, PlaceOrderRequest.class);
        var response = facade.placeOrder(request);
        sender.send(HttpUtils.CREATED, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void selectDeliverySlot(HttpExchange exchange, String orderId, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        SelectDeliverySlotRequest request = JaxsonUtils.fromJson(json, SelectDeliverySlotRequest.class);
        var response = facade.selectDeliverySlot(request);
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void initiatePayment(String orderId, ResponseSender sender) throws IOException {
        facade.initiatePayment(orderId);
        sender.send(HttpUtils.OK, "Paiement initié", null);
    }

    private void confirmOrder(HttpExchange exchange, String orderId, ResponseSender sender) throws IOException {
        ConfirmOrderRequest request = new ConfirmOrderRequest(orderId);
        var response = facade.confirmOrder(request);
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void getOrder(String orderId, ResponseSender sender) throws IOException {
        var order = facade.getOrder(orderId);
        if (order.items().isEmpty()) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Commande non trouvée", null);
            return;
        }
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(order),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }
}