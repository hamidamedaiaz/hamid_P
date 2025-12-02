package fr.unice.polytech.sophiatecheats.application.facade;

import fr.unice.polytech.sophiatecheats.application.dto.FindCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.FindCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.GetAvailableDeliverySlotsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.delivery.DeliverySlotDTO;
import fr.unice.polytech.sophiatecheats.application.dto.order.OrderDto;
import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.GetRestaurantOrdersResponse;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.SelectDeliverySlotResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.AddDishToRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.UpdateDishRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.AddDishToRestaurantResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.UpdateDishResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.*;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.InitiatePaymentUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.SelectDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.AddDishToRestaurantUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.GetRestaurantOrdersUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.UpdateDishUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.GetAvailableDeliverySlotsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.GetOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

import java.util.List;
import java.util.UUID;

public class SophiaTechEatsFacade {

    private final BrowseRestaurantsUseCase browseRestaurantsUseCase;
    private final AddDishToCartUseCase addDishToCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CancelCartUseCase cancelCartUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final AddDishToRestaurantUseCase addDishToRestaurantUseCase;
    private final UpdateDishUseCase updateDishUseCase;
    private final RemoveDishFromCartUseCase removeDishFromCartUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final SelectDeliverySlotUseCase selectDeliverySlotUseCase;
    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final FindActiveCartUseCase findActiveCartUseCase;
    private final GetAvailableDeliverySlotsUseCase getAvailableDeliverySlotsUseCase;
    private final GetRestaurantOrdersUseCase getRestaurantOrdersUseCase;
    private final fr.unice.polytech.sophiatecheats.domain.services.RestaurantService restaurantService;

    public SophiaTechEatsFacade(ApplicationConfig config) {
        this.browseRestaurantsUseCase = config.getInstance(BrowseRestaurantsUseCase.class);
        this.addDishToCartUseCase = config.getInstance(AddDishToCartUseCase.class);
        this.clearCartUseCase = config.getInstance(ClearCartUseCase.class);
        this.cancelCartUseCase = config.getInstance(CancelCartUseCase.class);
        this.placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);
        this.confirmOrderUseCase = config.getInstance(ConfirmOrderUseCase.class);
        this.addDishToRestaurantUseCase = config.getInstance(AddDishToRestaurantUseCase.class);
        this.updateDishUseCase = config.getInstance(UpdateDishUseCase.class);
        this.removeDishFromCartUseCase = config.getInstance(RemoveDishFromCartUseCase.class);
        this.updateCartItemUseCase = config.getInstance(UpdateCartItemUseCase.class);
        this.selectDeliverySlotUseCase = config.getInstance(SelectDeliverySlotUseCase.class);
        this.initiatePaymentUseCase = config.getInstance(InitiatePaymentUseCase.class);
        this.getOrderUseCase = config.getInstance(GetOrderUseCase.class);
        this.findActiveCartUseCase = config.getInstance(FindActiveCartUseCase.class);
        this.getAvailableDeliverySlotsUseCase = config.getInstance(GetAvailableDeliverySlotsUseCase.class);
        this.getRestaurantOrdersUseCase = config.getInstance(GetRestaurantOrdersUseCase.class);
        this.restaurantService = config.getInstance(fr.unice.polytech.sophiatecheats.domain.services.RestaurantService.class);
    }

    public OrderDto getOrder(String orderId) {
        return getOrderUseCase.execute(orderId);
    }

    public void initiatePayment(String orderId) {
        initiatePaymentUseCase.execute(orderId);
    }

    public BrowseRestaurantsResponse browseRestaurants(BrowseRestaurantsRequest request) {
        return browseRestaurantsUseCase.execute(request);
    }

    public FindCartResponse findCart(FindCartRequest request) {
        return findActiveCartUseCase.execute(request);
    }

    public void updateCartItem(UpdateCartItemRequest request) {
        updateCartItemUseCase.execute(request);
    }

    public void removeDishFromCart(RemoveFromCartRequest request) {
        removeDishFromCartUseCase.execute(request);
    }

    public AddDishToCartResponse addDishToCart(AddDishToCartRequest request) {
        return addDishToCartUseCase.execute(request);
    }

    public void clearCart(java.util.UUID userId) {
        clearCartUseCase.execute(userId);
    }

    public void cancelCart(java.util.UUID userId) {
        cancelCartUseCase.execute(userId);
    }

    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        return placeOrderUseCase.execute(request);
    }

    public ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request) {
        return confirmOrderUseCase.execute(request);
    }

    public AddDishToRestaurantResponse addDishToRestaurant(AddDishToRestaurantRequest request) {
        return addDishToRestaurantUseCase.execute(request);
    }

    public UpdateDishResponse updateDish(UpdateDishRequest request) {
        return updateDishUseCase.execute(request);
    }

    public BrowseRestaurantsResponse browseAllRestaurants() {
        return browseRestaurantsUseCase.execute(
                new BrowseRestaurantsRequest(null, null, null, null, null, null)
        );
    }

    public BrowseRestaurantsResponse browseAvailableRestaurants() {
        return browseRestaurantsUseCase.execute(
                new BrowseRestaurantsRequest(null, true, null, null, null, null)
        );
    }

    public SelectDeliverySlotResponse selectDeliverySlot(SelectDeliverySlotRequest request) {
        return selectDeliverySlotUseCase.execute(request);
    }

    public List<DeliverySlotDTO> getAvailableDeliverySlots(GetAvailableDeliverySlotsRequest request) {
        return getAvailableDeliverySlotsUseCase.execute(request);
    }

    public void generateDeliverySlots(java.util.UUID restaurantId, java.time.LocalDate date, java.time.LocalTime start, java.time.LocalTime end, int maxCapacityPerSlot) {
        restaurantService.generateDeliverySlots(restaurantId, date, start, end, maxCapacityPerSlot);
    }

    public void reserveDeliverySlot(java.util.UUID restaurantId, java.util.UUID slotId) {
        restaurantService.reserveDeliverySlot(restaurantId, slotId);
    }

    public void releaseDeliverySlot(java.util.UUID restaurantId, java.util.UUID slotId) {
        restaurantService.releaseDeliverySlot(restaurantId, slotId);
    }

    public Restaurant createRestaurant(String name, String address) {
        return restaurantService.createRestaurant(name, address);
    }

    public void updateRestaurantName(java.util.UUID id, String newName) {
        restaurantService.updateRestaurantName(id, newName);
    }

    public void updateRestaurantAddress(java.util.UUID id, String newAddress) {
        restaurantService.updateRestaurantAddress(id, newAddress);
    }

    public void updateRestaurantOpeningHours(java.util.UUID id, java.time.LocalTime opening, java.time.LocalTime closing) {
        restaurantService.updateRestaurantOpeningHours(id, opening, closing);
    }

    public void deleteRestaurant(java.util.UUID id) {
        restaurantService.deleteRestaurant(id);
    }

    public void removeDishFromRestaurant(java.util.UUID restaurantId, java.util.UUID dishId) {
        restaurantService.removeDishFromRestaurant(restaurantId, dishId);
    }

    public List<Dish> getRestaurantMenu(java.util.UUID restaurantId) {
        return restaurantService.getRestaurantMenu(restaurantId);
    }

    public void setDeliverySlotToCart(UUID userId, UUID deliverySlotId) {
        selectDeliverySlotUseCase.execute(
                new SelectDeliverySlotRequest(userId.toString(), deliverySlotId)
        );
    }

    public GetRestaurantOrdersResponse getRestaurantOrders(UUID restaurantId) {
        return getRestaurantOrdersUseCase.execute(restaurantId);
    }
}
