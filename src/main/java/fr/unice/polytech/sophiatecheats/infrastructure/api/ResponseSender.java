package fr.unice.polytech.sophiatecheats.infrastructure.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@FunctionalInterface
public interface ResponseSender {
    void send(int statusCode, String response, Map<String, String> headers) throws IOException;

    static ResponseSender fromExchange(HttpExchange exchange) {
        return (statusCode, response, headers) -> {
            if (headers != null) {
                headers.forEach(exchange.getResponseHeaders()::set);
            }
            byte[] bytes = response != null ? response.getBytes(StandardCharsets.UTF_8) : new byte[0];
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        };
    }
}

