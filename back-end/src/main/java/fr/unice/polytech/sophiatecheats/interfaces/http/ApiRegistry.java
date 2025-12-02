package fr.unice.polytech.sophiatecheats.interfaces.http;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.GlobalExceptionHandler;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.RouteHandler;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe pour enregistrer les routes et dispatcher les requêtes HTTP.
 * Gère automatiquement CORS pour toutes les routes.
 */
public class ApiRegistry {

    // Map des routes : méthode HTTP → liste des entrées de route (eg. GET → [RouteEntry1, RouteEntry2, ...])
    private final Map<String, List<RouteEntry>> routes = new HashMap<>();

    /****
     * Enregistre une nouvelle route avec la méthode HTTP, le chemin et le gestionnaire de la requete
     * @param method la méthode HTTP (GET, POST, etc.)
     * @param path le chemin de la route, pouvant contenir des paramètres entre accolades (e.g. /users/{id})
     * @param handler le gestionnaire de la route, une fonction qui prend HttpExchange, les paramètres extraits et un ResponseSender
     */
    public void registerRoute(String method, String path, RouteHandler handler) {
        Pattern paramPattern = Pattern.compile("\\{([^/]+)}");
        //On crée un "matcher" pour trouver les paramètres.
        Matcher paramMatcher = paramPattern.matcher(path);
        List<String> paramNames = new ArrayList<>();
        while (paramMatcher.find()) {
            paramNames.add(paramMatcher.group(1));
        }
        //paramNames contient maintenant la liste des noms de paramètres dans l'ordre d'apparition dans le chemin

        // Create a regex pattern to match the values
        //On l'utilisera pour extraire les valeurs des paramètres dans le chemin de la requête
        String regexPath = path.replaceAll("\\{[^/]+}", "([^/]+)");
        Pattern pattern = Pattern.compile(regexPath);

        routes.computeIfAbsent(method.toUpperCase(), k -> new ArrayList<>())
                .add(new RouteEntry(pattern, paramNames, handler));
    }

    public void dispatch(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod().toUpperCase();

        if ("OPTIONS".equals(requestMethod)) {
            exchange.sendResponseHeaders(204, -1); // No content
            exchange.close();
            return;
        }

        // Ici, continue le matching normal pour GET, POST, PUT, DELETE
        String requestPath = exchange.getRequestURI().getPath();
        List<RouteEntry> methodRoutes = routes.getOrDefault(requestMethod, Collections.emptyList());

        for (RouteEntry entry : methodRoutes) {
            Matcher matcher = entry.pattern().matcher(requestPath);
            if (matcher.matches()) {
                Map<String, String> pathParams = new HashMap<>();
                for (int i = 0; i < entry.paramNames().size(); i++) {
                    pathParams.put(entry.paramNames().get(i), matcher.group(i + 1));
                }

                GlobalExceptionHandler.callWithGlobalExceptionHandling(exchange, () -> {
                    ResponseSender sender = (statusCode, response, headers) ->
                            sendResponse(exchange, statusCode, response, headers);
                    entry.handler().handle(exchange, pathParams, sender);
                    return null;
                });
                return;
            }
        }

        // Si aucune route ne correspond
        sendResponse(exchange, 404, "{\"error\":\"Route not found\"}", null);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response, Map<String, String> headers) throws IOException {

        if (headers != null) {
            headers.forEach(exchange.getResponseHeaders()::set);
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public record RouteEntry(Pattern pattern, List<String> paramNames, RouteHandler handler) {
    }
}
