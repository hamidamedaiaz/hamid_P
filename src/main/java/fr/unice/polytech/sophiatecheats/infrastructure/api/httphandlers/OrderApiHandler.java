package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.*;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.request.*;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.api.*;
import java.io.IOException;
import java.util.Map;

public class OrderApiHandler implements RouteHandler {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final SelectDeliverySlotUseCase selectDeliverySlotUseCase;
    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final OrderRepository orderRepository;

    public OrderApiHandler(
            PlaceOrderUseCase placeOrderUseCase,
            SelectDeliverySlotUseCase selectDeliverySlotUseCase,
            InitiatePaymentUseCase initiatePaymentUseCase,
            ConfirmOrderUseCase confirmOrderUseCase,
            OrderRepository orderRepository
    ) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.selectDeliverySlotUseCase = selectDeliverySlotUseCase;
        this.initiatePaymentUseCase = initiatePaymentUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.orderRepository = orderRepository;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        // CORS headers sont déjà ajoutés globalement dans ApiRegistry

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
        } else if (pathParams.containsKey("userId")) {
            getUserOrders(pathParams.get("userId"), sender);
        }
    }

    private void createOrder(HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        PlaceOrderRequest request = JaxsonUtils.fromJson(json, PlaceOrderRequest.class);
        var response = placeOrderUseCase.execute(request);
        sender.send(HttpUtils.CREATED, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void selectDeliverySlot(HttpExchange exchange, String orderId, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        SelectDeliverySlotRequest request = JaxsonUtils.fromJson(json, SelectDeliverySlotRequest.class);
        var response = selectDeliverySlotUseCase.execute(request);
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void initiatePayment(String orderId, ResponseSender sender) throws IOException {
        initiatePaymentUseCase.execute(orderId);
        sender.send(HttpUtils.OK, "Paiement initié", null);
    }

    private void confirmOrder(HttpExchange exchange, String orderId, ResponseSender sender) throws IOException {
        ConfirmOrderRequest request = new ConfirmOrderRequest(orderId);
        var response = confirmOrderUseCase.execute(request);
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void getOrder(String orderId, ResponseSender sender) throws IOException {
        var order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Commande non trouvée", null);
            return;
        }
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(order.get()),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void getUserOrders(String userId, ResponseSender sender) throws IOException {
        var orders = orderRepository.findAll();
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(orders),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }
}