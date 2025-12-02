package fr.unice.polytech.sophiatecheats.infrastructure.config;

import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.InitiatePaymentUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.SelectDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.AddDishToRestaurantUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.RemoveDishFromRestaurantUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.UpdateDishUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.GetAvailableDeliverySlotsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.ValidateDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.GetOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.domain.repositories.*;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.domain.services.photoai.PhotoAnalysisService;
import fr.unice.polytech.sophiatecheats.infrastructure.external.MockAIPhotoAnalysisService;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.*;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;


/**
 * Central application configuration managing dependency injection for the SophiaTech Eats system.
 *
 * <p>This configuration class implements the Dependency Inversion Principle by wiring
 * concrete implementations to their corresponding interfaces. It uses PicoContainer
 * for lightweight dependency injection following Clean Architecture guidelines.</p>
 *
 * <h3>Dependency Flow:</h3>
 * <pre>
 * Infrastructure → Application → Domain
 * </pre>
 *
 * <p>All dependencies are configured to flow inward toward the domain layer,
 * ensuring business logic remains framework-independent.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class ApplicationConfig {

    private final MutablePicoContainer container;

    public ApplicationConfig() {
        this.container = new DefaultPicoContainer(new Caching());
        configure();
    }

    /**
     * Configure l'injection de dépendances.
     * Enregistre les implementations concrètes pour les interfaces.
     */
    private void configure() {
        // Repositories - using caching behavior for singleton instances
        container.addComponent(UserRepository.class, InMemoryUserRepository.class);
        container.addComponent(RestaurantRepository.class, InMemoryRestaurantRepository.class);
        container.addComponent(OrderRepository.class, InMemoryOrderRepository.class);
        container.addComponent(CartRepository.class, InMemoryCartRepository.class);
        container.addComponent(TimeSlotRepository.class, InMemoryTimeSlotRepository.class);

        // Services
        container.addComponent(PhotoAnalysisService.class, MockAIPhotoAnalysisService.class);
        container.addComponent(RestaurantService.class);
        container.addComponent(DeliveryService.class);

        // Use Cases
        container.addComponent(BrowseRestaurantsUseCase.class);
        container.addComponent(PlaceOrderUseCase.class);
        container.addComponent(GetOrderUseCase.class);

        // Delivery Slot Use Cases
        container.addComponent(ValidateDeliverySlotUseCase.class);
        container.addComponent(GetAvailableDeliverySlotsUseCase.class);

        // Order Flow Use Cases - Complete order→slot→payment sequence
        container.addComponent(SelectDeliverySlotUseCase.class);
        container.addComponent(ConfirmOrderUseCase.class);
        container.addComponent(InitiatePaymentUseCase.class);

        // Cart Use Cases
        container.addComponent(AddDishToCartUseCase.class);
        container.addComponent(FindActiveCartUseCase.class);
        container.addComponent(ClearCartUseCase.class);
        container.addComponent(CancelCartUseCase.class);
        container.addComponent(RemoveDishFromCartUseCase.class);
        container.addComponent(UpdateCartItemUseCase.class);

        // Dish Management Use Cases - Restaurant Administration
        container.addComponent(AddDishToRestaurantUseCase.class);
        container.addComponent(UpdateDishUseCase.class);
        container.addComponent(RemoveDishFromRestaurantUseCase.class);

        // Restaurant Order Management Use Cases
        container.addComponent(fr.unice.polytech.sophiatecheats.application.usecases.restaurant.GetRestaurantOrdersUseCase.class);
    }

    /**
     * Récupère une instance configurée d'une classe.
     */
    public <T> T getInstance(Class<T> clazz) {
        return container.getComponent(clazz);
    }

}
