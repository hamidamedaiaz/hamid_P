package fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.domain.exceptions.*;
import fr.unice.polytech.sophiatecheats.infrastructure.api.HttpUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler for HTTP requests.
 * Maps domain exceptions to appropriate HTTP status codes and JSON error responses.
 */
public class GlobalExceptionHandler {

    private GlobalExceptionHandler() {
        logger.setLevel(Level.OFF);
    }

    static Logger logger = Logger.getLogger("GlobalExceptionHandler");

    public static void handleException(HttpExchange exchange, RestaurantNotFoundException e) throws IOException {
        logger.info("RestaurantNotFoundException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.RESOURCE_NOT_FOUND, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, DishNotFoundException e) throws IOException {
        logger.info("DishNotFoundException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.RESOURCE_NOT_FOUND, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, SlotNotFoundException e) throws IOException {
        logger.info("SlotNotFoundException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.RESOURCE_NOT_FOUND, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, UserNotFoundException e) throws IOException {
        logger.info("UserNotFoundException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.RESOURCE_NOT_FOUND, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, DuplicateRestaurantException e) throws IOException {
        logger.info("DuplicateRestaurantException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.CONFLICT, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, RestaurantValidationException e) throws IOException {
        logger.info("RestaurantValidationException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, DishValidationException e) throws IOException {
        logger.info("DishValidationException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, CannotMixRestaurantsException e) throws IOException {
        logger.info("CannotMixRestaurantsException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, InsufficientCreditException e) throws IOException {
        logger.info("InsufficientCreditException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, InvalidCartOperationException e) throws IOException {
        logger.info("InvalidCartOperationException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, InvalidCuisineException e) throws IOException {
        logger.info("InvalidCuisineException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, OrderAlreadyConfirmedException e) throws IOException {
        logger.info("OrderAlreadyConfirmedException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.CONFLICT, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, OrderExpiredException e) throws IOException {
        logger.info("OrderExpiredException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.GONE, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, PaymentTimeoutException e) throws IOException {
        logger.info("PaymentTimeoutException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.GATEWAY_TIMEOUT, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, ValidationException e) throws IOException {
        logger.info("ValidationException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, IllegalArgumentException e) throws IOException {
        logger.info("IllegalArgumentException: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.BAD_REQUEST, e.getMessage());
    }

    public static void handleException(HttpExchange exchange, Exception e) throws IOException {
        logger.info("Unhandled Exception: " + e.getMessage());
        sendErrorResponse(exchange, HttpUtils.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public static <R> R callWithGlobalExceptionHandling(HttpExchange exchange, Callable<R> callable) throws IOException {
        try {
            return callable.call();
        } catch (RestaurantNotFoundException e) {
            handleException(exchange, e);
        } catch (DishNotFoundException e) {
            handleException(exchange, e);
        } catch (SlotNotFoundException e) {
            handleException(exchange, e);
        } catch (UserNotFoundException e) {
            handleException(exchange, e);
        } catch (DuplicateRestaurantException e) {
            handleException(exchange, e);
        } catch (RestaurantValidationException e) {
            handleException(exchange, e);
        } catch (DishValidationException e) {
            handleException(exchange, e);
        } catch (CannotMixRestaurantsException e) {
            handleException(exchange, e);
        } catch (InsufficientCreditException e) {
            handleException(exchange, e);
        } catch (InvalidCartOperationException e) {
            handleException(exchange, e);
        } catch (InvalidCuisineException e) {
            handleException(exchange, e);
        } catch (OrderAlreadyConfirmedException e) {
            handleException(exchange, e);
        } catch (OrderExpiredException e) {
            handleException(exchange, e);
        } catch (PaymentTimeoutException e) {
            handleException(exchange, e);
        } catch (ValidationException e) {
            handleException(exchange, e);
        } catch (IllegalArgumentException e) {
            handleException(exchange, e);
        } catch (Exception e) {
            handleException(exchange, e);
        }
        return null;
    }

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
        String response = "{\"error\": \"" + message + "\"}";
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
