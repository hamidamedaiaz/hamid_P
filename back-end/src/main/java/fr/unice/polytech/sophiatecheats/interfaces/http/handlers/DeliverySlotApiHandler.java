package fr.unice.polytech.sophiatecheats.interfaces.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.sophiatecheats.application.dto.GetAvailableDeliverySlotsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.delivery.DeliverySlotDTO;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.HttpUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.JaxsonUtils;
import fr.unice.polytech.sophiatecheats.interfaces.http.utils.ResponseSender;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliverySlotApiHandler implements RouteHandler {

    private final SophiaTechEatsFacade facade;

    public DeliverySlotApiHandler(SophiaTechEatsFacade facade) {
        this.facade = facade;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> pathParams, ResponseSender sender) throws IOException {
        // CORS is handled by ApiRegistry

        String method = exchange.getRequestMethod();
        UUID restaurantId = UUID.fromString(pathParams.get("id"));
        UUID slotId = pathParams.containsKey("slotId") ? UUID.fromString(pathParams.get("slotId")) : null;

        switch (method) {
            case "GET":
                LocalDate date = pathParams.containsKey("date") ? LocalDate.parse(pathParams.get("date")) : LocalDate.now();
                getSlots(restaurantId, date, sender);
                break;
            case "POST":
                if (slotId != null) reserveOrReleaseSlot(restaurantId, slotId, sender, exchange);
                else createSlots(exchange, restaurantId, sender);
                break;
            default:
                sender.send(HttpUtils.BAD_REQUEST, "Method not supported", null);
        }
    }

    private void getSlots(UUID restaurantId, LocalDate date, ResponseSender sender) throws IOException {
        GetAvailableDeliverySlotsRequest request = new GetAvailableDeliverySlotsRequest(restaurantId, date);
        List<DeliverySlotDTO> slots = facade.getAvailableDeliverySlots(request);
        sender.send(HttpUtils.OK, JaxsonUtils.toJson(slots), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    private void createSlots(HttpExchange exchange, UUID restaurantId, ResponseSender sender) throws IOException {
        InputStream is = exchange.getRequestBody();
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Map<?, ?> data = JaxsonUtils.fromJson(json, Map.class);

        assert data != null;
        LocalDate date = LocalDate.parse((String) data.get("date"));
        LocalTime start = LocalTime.parse((String) data.get("start"));
        LocalTime end = LocalTime.parse((String) data.get("end"));
        int maxCapacity = (Integer) data.get("maxCapacityPerSlot");

        facade.generateDeliverySlots(restaurantId, date, start, end, maxCapacity);
        sender.send(HttpUtils.CREATED, "Delivery slots created", Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }

    private void reserveOrReleaseSlot(UUID restaurantId, UUID slotId, ResponseSender sender, HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String action = new String(is.readAllBytes(), StandardCharsets.UTF_8).toLowerCase();

        if (action.contains("reserve")) facade.reserveDeliverySlot(restaurantId, slotId);
        else if (action.contains("release")) facade.releaseDeliverySlot(restaurantId, slotId);

        sender.send(HttpUtils.OK, "Action performed", Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.TEXT_PLAIN));
    }
}