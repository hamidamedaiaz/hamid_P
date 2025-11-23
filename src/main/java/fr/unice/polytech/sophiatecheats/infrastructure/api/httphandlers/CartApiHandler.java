package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.RemoveFromCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.UpdateCartItemRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.api.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CartApiHandler implements RouteHandler {

    private final AddDishToCartUseCase addDishToCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CancelCartUseCase cancelCartUseCase;
    private final RemoveDishFromCartUseCase removeDishFromCartUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final CartRepository cartRepository;

    public CartApiHandler(
            AddDishToCartUseCase addDishToCartUseCase,
            ClearCartUseCase clearCartUseCase,
            CancelCartUseCase cancelCartUseCase,
            RemoveDishFromCartUseCase removeDishFromCartUseCase,
            UpdateCartItemUseCase updateCartItemUseCase,
            CartRepository cartRepository) {

        this.addDishToCartUseCase = addDishToCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.cancelCartUseCase = cancelCartUseCase;
        this.removeDishFromCartUseCase = removeDishFromCartUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
        this.cartRepository = cartRepository;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws Exception {
        // CORS headers sont déjà ajoutés globalement dans ApiRegistry

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {

                case "POST":
                    if (path.contains("/items")) {
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
            GlobalExceptionHandler.callWithGlobalExceptionHandling(exchange, () -> { throw e; });
        }
    }

    private void addItemToCart(HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        AddDishToCartRequest request = JaxsonUtils.fromJson(json, AddDishToCartRequest.class);
        AddDishToCartResponse response = addDishToCartUseCase.execute(request);

        sender.send(HttpUtils.CREATED, JaxsonUtils.toJson(response),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void getCart(String userId, ResponseSender sender) throws IOException {
        var cart = cartRepository.findActiveCartByUserId(UUID.fromString(userId));

        if (cart.isEmpty()) {
            sender.send(HttpUtils.RESOURCE_NOT_FOUND, "Panier non trouvé", null);
            return;
        }

        sender.send(HttpUtils.OK, JaxsonUtils.toJson(cart.get()),
                Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void clearCart(String userId, ResponseSender sender) throws IOException {
        clearCartUseCase.execute(UUID.fromString(userId));
        sender.send(HttpUtils.OK, "Panier vidé", null);
    }

    private void cancelCart(String userId, ResponseSender sender) throws IOException {
        cancelCartUseCase.execute(UUID.fromString(userId));
        sender.send(HttpUtils.OK, "Panier annulé", null);
    }

    private void removeItemFromCart(Map<String, String> params, HttpExchange exchange, ResponseSender sender) throws IOException {
        String userId = params.get("userId");
        String dishId = params.get("dishId");

        RemoveFromCartRequest request = new RemoveFromCartRequest(UUID.fromString(userId), UUID.fromString(dishId));
        removeDishFromCartUseCase.execute(request);

        sender.send(HttpUtils.OK, "Article supprimé du panier", null);
    }

    private void updateCartItem(Map<String, String> params, HttpExchange exchange, ResponseSender sender) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes());
        UpdateCartItemRequest request = JaxsonUtils.fromJson(json, UpdateCartItemRequest.class);

        updateCartItemUseCase.execute(request);

        sender.send(HttpUtils.OK, "Article du panier mis à jour", null);
    }
}
