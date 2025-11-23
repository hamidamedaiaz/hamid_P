package fr.unice.polytech.sophiatecheats.gateway.routing;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Router pour l'API Gateway
 *
 * Route les requêtes HTTP vers les services appropriés :
 * - /api/restaurants/** -> Service Restaurant (Port 8081)
 * - /api/cart/**, /api/orders/**, /api/payments/** -> Service Order & Payment (Port 8082)
 */
public class GatewayRouter {

    private static final Logger logger = Logger.getLogger(GatewayRouter.class.getName());

    private static final String RESTAURANT_SERVICE_URL = "http://localhost:8081";
    private static final String ORDER_SERVICE_URL = "http://localhost:8082";

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    public void handle(HttpExchange exchange) throws IOException {
        // Configuration CORS
        configureCORS(exchange);

        // Gestion des requêtes OPTIONS (preflight)
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        logger.info(CYAN + "🌐 Gateway: " + method + " " + path + RESET);

        try {
            // Déterminer le service cible
            String targetServiceUrl = determineTargetService(path);

            if (targetServiceUrl == null) {
                sendError(exchange, 404, "Service non trouvé pour le chemin : " + path);
                return;
            }

            // Construire l'URL complète
            String targetUrl = targetServiceUrl + path;
            if (exchange.getRequestURI().getQuery() != null) {
                targetUrl += "?" + exchange.getRequestURI().getQuery();
            }

            logger.info(YELLOW + "  ➜ Redirection vers: " + targetUrl + RESET);

            // Proxy de la requête
            proxyRequest(exchange, targetUrl);

        } catch (Exception e) {
            logger.severe("❌ Erreur lors du routage : " + e.getMessage());
            e.printStackTrace();
            sendError(exchange, 500, "Erreur interne du gateway : " + e.getMessage());
        }
    }

    /**
     * Détermine vers quel service router la requête
     */
    private String determineTargetService(String path) {
        // Routes pour le service Restaurant (avec ou sans /api/)
        if (path.startsWith("/api/restaurants") || path.startsWith("/restaurants")) {
            return RESTAURANT_SERVICE_URL;
        }

        // Routes pour le service Order & Payment (avec ou sans /api/)
        if (path.startsWith("/api/cart") || path.startsWith("/cart") ||
            path.startsWith("/api/orders") || path.startsWith("/orders") ||
            path.startsWith("/api/payments") || path.startsWith("/payments") ||
            path.startsWith("/api/delivery") || path.startsWith("/delivery")) {
            return ORDER_SERVICE_URL;
        }

        return null;
    }

    /**
     * Proxy la requête vers le service cible
     */
    private void proxyRequest(HttpExchange exchange, String targetUrl) throws IOException {
        HttpURLConnection connection = null;

        try {
            // Créer la connexion vers le service cible
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(exchange.getRequestMethod());
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            // Copier les headers (sauf Host)
            copyHeaders(exchange, connection);

            // Copier le body pour POST/PUT/PATCH
            if ("POST".equals(exchange.getRequestMethod()) ||
                "PUT".equals(exchange.getRequestMethod()) ||
                "PATCH".equals(exchange.getRequestMethod())) {
                connection.setDoOutput(true);
                copyRequestBody(exchange, connection);
            }

            // Obtenir la réponse
            int responseCode = connection.getResponseCode();

            // Copier les headers de réponse
            copyResponseHeaders(connection, exchange);

            // Copier le body de réponse
            InputStream responseStream = (responseCode >= 200 && responseCode < 400)
                ? connection.getInputStream()
                : connection.getErrorStream();

            if (responseStream != null) {
                byte[] responseBody = responseStream.readAllBytes();
                exchange.sendResponseHeaders(responseCode, responseBody.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBody);
                os.close();
            } else {
                exchange.sendResponseHeaders(responseCode, -1);
            }

        } catch (Exception e) {
            logger.severe("❌ Erreur lors du proxy : " + e.getMessage());
            throw new IOException("Erreur de proxy", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Copie les headers de la requête
     */
    private void copyHeaders(HttpExchange exchange, HttpURLConnection connection) {
        for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
            String key = header.getKey();
            // Ignorer certains headers qui seront gérés automatiquement
            if (!"Host".equalsIgnoreCase(key) &&
                !"Connection".equalsIgnoreCase(key) &&
                !"Content-Length".equalsIgnoreCase(key)) {
                for (String value : header.getValue()) {
                    connection.setRequestProperty(key, value);
                }
            }
        }
    }

    /**
     * Copie le body de la requête
     */
    private void copyRequestBody(HttpExchange exchange, HttpURLConnection connection) throws IOException {
        InputStream is = exchange.getRequestBody();
        OutputStream os = connection.getOutputStream();
        is.transferTo(os);
        os.close();
    }

    /**
     * Copie les headers de la réponse
     */
    private void copyResponseHeaders(HttpURLConnection connection, HttpExchange exchange) {
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                String headerKey = header.getKey();
                // Ignorer les headers CORS qui viennent du service backend
                // La Gateway gère déjà le CORS
                if (!headerKey.equalsIgnoreCase("Access-Control-Allow-Origin") &&
                    !headerKey.equalsIgnoreCase("Access-Control-Allow-Methods") &&
                    !headerKey.equalsIgnoreCase("Access-Control-Allow-Headers") &&
                    !headerKey.equalsIgnoreCase("Access-Control-Max-Age")) {
                    for (String value : header.getValue()) {
                        exchange.getResponseHeaders().add(headerKey, value);
                    }
                }
            }
        }
    }

    /**
     * Configure CORS pour permettre les requêtes depuis le frontend
     */
    private void configureCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
    }

    /**
     * Envoie une réponse d'erreur
     */
    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = String.format("{\"error\": \"%s\", \"status\": %d}", message, statusCode);
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
