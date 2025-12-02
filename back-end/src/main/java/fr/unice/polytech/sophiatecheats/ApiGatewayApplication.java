package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.logging.Logger;

/**
 * API Gateway - Point d'entrée unique pour tous les clients.
 * Route les requêtes vers les microservices appropriés:
 * - Consumer Service (8082): /api/cart/*, /api/orders/*, /restaurants (GET only)
 * - Restaurant Service (8081): /restaurants/* (POST/PUT/DELETE management)
 */
public class ApiGatewayApplication {

    public static final int GATEWAY_PORT = 8080;
    public static final String CONSUMER_SERVICE_URL = "http://localhost:8082";
    public static final String RESTAURANT_SERVICE_URL = "http://localhost:8081";

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";

    private static final Logger logger = Logger.getLogger(ApiGatewayApplication.class.getName());

   public static void main(String[] args) {
        ApiGatewayApplication gateway = new ApiGatewayApplication();
        gateway.start();
    }

    public void start() {
        logger.info(BLUE + "╔════════════════════════════════════════╗" + RESET);
        logger.info(BLUE + "║       API GATEWAY - PORT 8080          ║" + RESET);
        logger.info(BLUE + "╚════════════════════════════════════════╝" + RESET);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(GATEWAY_PORT), 0);
            server.createContext("/", this::routeRequest);
            server.start();

            logger.info(GREEN + "✓ API Gateway démarré sur http://localhost:" + GATEWAY_PORT + RESET);
            logger.info(YELLOW + "Routes configurées:" + RESET);
            logger.info(CYAN + "  → /api/cart/*        → Consumer Service (8082)" + RESET);
            logger.info(CYAN + "  → /api/orders/*      → Consumer Service (8082)" + RESET);
            logger.info(CYAN + "  → /restaurants/*     → Restaurant Service (8081)" + RESET);
            logger.info(BLUE + "════════════════════════════════════════" + RESET);
            logger.info(GREEN + "Gateway prêt à router les requêtes." + RESET);

            Thread.currentThread().join();

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.severe("Erreur critique au démarrage du Gateway: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void routeRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        // CORS headers pour toutes les réponses
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, x-request-id");


        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String targetServiceUrl = determineTargetService(path);

        if (targetServiceUrl == null) {
            sendErrorResponse(exchange, 404, "Service not found for path: " + path);
            return;
        }

        try {
            proxyRequest(exchange, targetServiceUrl);
        } catch (Exception e) {
            logger.warning("Erreur lors du routage: " + e.getMessage());
            sendErrorResponse(exchange, 503, "Service temporairement indisponible");
        }
    }

    private String determineTargetService(String path) {
        // Routes Consumer Service (8082) - Actions clients
        if (path.startsWith("/api/cart") || path.startsWith("/api/orders")) {
            return CONSUMER_SERVICE_URL;
        }

        // Routes Restaurant Management → Restaurant Service (8081)
        if (path.startsWith("/restaurants")) {
            return RESTAURANT_SERVICE_URL;
        }

        return null;
    }

    private void proxyRequest(HttpExchange exchange, String targetServiceUrl) throws IOException {
        String path = exchange.getRequestURI().toString();
        URL url = new URL(targetServiceUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure la connexion
        String method = exchange.getRequestMethod();
        connection.setRequestMethod(method);
        connection.setDoInput(true);

        // Copie les headers de la requête (sauf Host)
        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!"Host".equalsIgnoreCase(key)) {
                values.forEach(value -> connection.setRequestProperty(key, value));
            }
        });

        // Envoie le body si présent (seulement pour POST, PUT, etc.)
        if (!"GET".equals(method) && !"DELETE".equals(method) && exchange.getRequestBody().available() > 0) {
            connection.setDoOutput(true);
            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            connection.getOutputStream().write(requestBody);
            connection.getOutputStream().flush();
        }

        // Récupère la réponse du service
        int responseCode = connection.getResponseCode();
        byte[] responseBody;

        try {
            responseBody = connection.getInputStream().readAllBytes();
        } catch (IOException e) {
            // En cas d'erreur, lire l'error stream
            responseBody = connection.getErrorStream() != null
                    ? connection.getErrorStream().readAllBytes()
                    : "Service Error".getBytes();
        }

        connection.getHeaderFields().forEach((key, values) -> {
            if (key != null &&
                    !"Transfer-Encoding".equalsIgnoreCase(key) &&
                    !key.toLowerCase().startsWith("access-control-")) {
                values.forEach(value -> exchange.getResponseHeaders().add(key, value));
            }
        });

        // Envoie la réponse au client
        exchange.sendResponseHeaders(responseCode, responseBody.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBody);
        os.close();

        connection.disconnect();
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = ("{\"error\":\"" + message + "\"}").getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}

