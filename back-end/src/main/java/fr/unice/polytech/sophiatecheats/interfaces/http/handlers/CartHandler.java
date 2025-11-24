package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.order.OrderDto;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.RemoveFromCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.UpdateCartItemRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.HttpUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CartHandler implements RouteHandler {
    private final SophiaTechEatsFacade facade;

    public CartHandler(SophiaTechEatsFacade facade) {
        this.facade = facade;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws Exception {
        // CORS is handled by ApiRegistry

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {

                case "POST":
                    if (path.contains("/payment")) {
                        //  Transformation du panier en commande
                        processPayment(exchange, pathParams.get("userId"), sender);
                    } else if (path.contains("/delivery-slot")) {
                        // SÉLECTION DU CRÉNEAU  Onn l Ajouter au panier
                        selectDeliverySlotForCart(exchange, pathParams.get("userId"), sender);
                    } else if (path.contains("/items")) {
                        addItemToCart(exchange, sender);
                    }
                    break;
                case "GET":
                    getCart(pathParams.get("userId"), sender);
                    break;

                case "PUT":
                    updateCartItem(pathParams, exchange, sender);
                    break;

                case "DELETE":
                    if (path.contains("/items")) {
                        removeItemFromCart(pathParams, exchange, sender);
                    } else if (path.contains("/cancel")) {
                        cancelCart(pathParams.get("userId"), sender);
                    } else {
                        clearCart(pathParams.get("userId"), sender);
                    }
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

    private void addItemToCart(HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        AddDishToCartRequest request = JaxsonUtils.fromJson(json, AddDishToCartRequest.class);
        AddDishToCartResponse response = facade.addDishToCart(request);

        sender.send(HttpUtils.CREATED, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void getCart(String userId, ResponseSender sender) throws IOException {
        try {
            FindCartRequest request = new FindCartRequest(UUID.fromString(userId));
            FindCartResponse response = facade.findCart(request);

            // Vérifier si le panier existe
            if (response == null || response.cartId() == null) {
                sender.send(
                        HttpUtils.RESOURCE_NOT_FOUND,
                        "{\"error\":\"Aucun panier actif trouvé pour cet utilisateur\",\"userId\":\"" + userId + "\"}",
                        Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                );
                return;
            }

            sender.send(
                    HttpUtils.OK,
                    JaxsonUtils.toJson(response),
                    Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
            );

        } catch (IllegalArgumentException e) {
            sender.send(
                    HttpUtils.BAD_REQUEST,
                    "{\"error\":\"UUID utilisateur invalide\"}",
                    Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
            );
        }
    }

    private void clearCart(String userId, ResponseSender sender) throws IOException {
        facade.clearCart(UUID.fromString(userId));
        sender.send(HttpUtils.OK, "Panier vidé", null);
    }

    private void cancelCart(String userId, ResponseSender sender) throws IOException {
        facade.cancelCart(UUID.fromString(userId));
        sender.send(HttpUtils.OK, "Panier annulé", null);
    }

    private void removeItemFromCart(Map<String, String> params, HttpExchange exchange, ResponseSender sender) throws IOException {
        String userId = params.get("userId");
        String dishId = params.get("dishId");

        RemoveFromCartRequest request = new RemoveFromCartRequest(UUID.fromString(userId), UUID.fromString(dishId));
        facade.removeDishFromCart(request);

        sender.send(HttpUtils.OK, "Article supprimé du panier", null);
    }

    private void updateCartItem(Map<String, String> params, HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        UpdateCartItemRequest request = JaxsonUtils.fromJson(json, UpdateCartItemRequest.class);

        facade.updateCartItem(request);

        sender.send(HttpUtils.OK, "Article du panier mis à jour", null);
    }


    private void processPayment(HttpExchange exchange, String userId, ResponseSender sender) throws IOException {
        try {

            String json = new String(exchange.getRequestBody().readAllBytes());
            PlaceOrderRequest placeOrderRequest = JaxsonUtils.fromJson(json, PlaceOrderRequest.class);


            PlaceOrderResponse orderResponse = facade.placeOrder(placeOrderRequest);


            OrderDto order = facade.getOrder(orderResponse.orderId());

            sender.send(
                    HttpUtils.CREATED,
                    JaxsonUtils.toJson(order),
                    Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
            );

        } catch (ValidationException e) {
            sendPaymentError(sender, e.getMessage(), HttpUtils.BAD_REQUEST);

        } catch (EntityNotFoundException e) {
            sendPaymentError(sender, e.getMessage(), HttpUtils.RESOURCE_NOT_FOUND);

        } catch (Exception e) {
            sendPaymentError(sender, "Erreur lors du traitement du paiement: " + e.getMessage(), HttpUtils.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Envoie une réponse d'erreur de paiement au client
     */
    private void sendPaymentError(ResponseSender sender, String errorMessage, int statusCode) throws IOException {
        String errorResponse = String.format(
                "{\"error\":\"Échec du paiement\",\"message\":\"%s\",\"status\":\"FAILED\"}",
                errorMessage.replace("\"", "\\\"")  // Échapper les guillemets
        );

        sender.send(
                statusCode,
                errorResponse,
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
        );
    }


    private void selectDeliverySlotForCart(HttpExchange exchange, String userId, ResponseSender sender) throws IOException {
        try {
            // Lire le body
            String json = new String(exchange.getRequestBody().readAllBytes());
            var request = JaxsonUtils.fromJson(json, java.util.Map.class);
            String deliverySlotId = (String) request.get("deliverySlotId");

            if (deliverySlotId == null) {
                sendPaymentError(sender, "deliverySlotId est requis", HttpUtils.BAD_REQUEST);
                return;
            }

            facade.setDeliverySlotToCart(UUID.fromString(userId), UUID.fromString(deliverySlotId));

            sender.send(
                    HttpUtils.OK,
                    JaxsonUtils.toJson(java.util.Map.of(
                            "success", true,
                            "message", "Créneau de livraison sélectionné",
                            "deliverySlotId", deliverySlotId
                    )),
                    java.util.Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
            );

        } catch (EntityNotFoundException e) {
            sendPaymentError(sender, e.getMessage(), HttpUtils.RESOURCE_NOT_FOUND);
        } catch (ValidationException e) {
            sendPaymentError(sender, e.getMessage(), HttpUtils.BAD_REQUEST);
        } catch (Exception e) {
            sendPaymentError(sender, "Erreur lors de la sélection du créneau: " + e.getMessage(), HttpUtils.INTERNAL_SERVER_ERROR);
        }
    }
}
