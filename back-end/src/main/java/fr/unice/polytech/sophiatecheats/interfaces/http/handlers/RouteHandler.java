package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.util.Map;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws Exception;
}
