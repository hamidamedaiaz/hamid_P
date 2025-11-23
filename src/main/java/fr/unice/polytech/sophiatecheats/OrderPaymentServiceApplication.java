package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.application.usecases.order.*;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.api.ApiRegistry;
import fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers.*;
import fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers.OrderApiHandler;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

import java.net.InetSocketAddress;
import java.util.logging.Logger;


public class OrderPaymentServiceApplication {

    public static final int PORT = 8082;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    private static final Logger logger = Logger.getLogger(OrderPaymentServiceApplication.class.getName());

    private final ApplicationConfig config;

    public OrderPaymentServiceApplication() {
        // Utilise la config partagée - les repositories sont partagés avec le service Restaurant
        this.config = new ApplicationConfig();
    }

    public void start() {
        logger.info(CYAN + "  ORDER & PAYMENT SERVICE" + RESET);
        logger.info(GREEN + "Démarrage du service Order & Payment..." + RESET);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            ApiRegistry registry = configureApiRoutes();

            server.createContext("/", registry::dispatch);
            server.start();

            logger.info(GREEN + " Service démarré sur http://localhost:" + PORT + "/" + RESET);
            logger.info(YELLOW + " Routes disponibles :" + RESET);
            logger.info(YELLOW + "  - POST   /api/cart/items" + RESET);
            logger.info(YELLOW + "  - GET    /api/cart/{userId}" + RESET);
            logger.info(YELLOW + "  - PUT    /api/cart/{userId}/items" + RESET);
            logger.info(YELLOW + "  - DELETE /api/cart/{userId}" + RESET);
            logger.info(YELLOW + "  - DELETE /api/cart/{userId}/cancel" + RESET);
            logger.info(YELLOW + "  - DELETE /api/cart/{userId}/items/{dishId}" + RESET);
            logger.info(YELLOW + "  - POST   /api/orders" + RESET);
            logger.info(YELLOW + "  - GET    /api/orders/{id}" + RESET);
            logger.info(YELLOW + "  - GET    /api/orders/user/{userId}" + RESET);
            logger.info(YELLOW + "  - POST   /api/orders/{id}/delivery-slot" + RESET);
            logger.info(YELLOW + "  - POST   /api/orders/{id}/payment" + RESET);
            logger.info(YELLOW + "  - POST   /api/orders/{id}/confirm" + RESET);
            logger.info(CYAN + "========================================" + RESET);
            logger.info(GREEN + "Service prêt à recevoir des requêtes." + RESET);

            Thread.currentThread().join();

        } catch (Exception e) {
            logger.severe(" Erreur critique au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure les routes API pour le service Order & Payment
     */
    private ApiRegistry configureApiRoutes() {
        // Récupération des use cases depuis ApplicationConfig
        AddDishToCartUseCase addDishToCartUseCase = config.getInstance(AddDishToCartUseCase.class);
        ClearCartUseCase clearCartUseCase = config.getInstance(ClearCartUseCase.class);
        CancelCartUseCase cancelCartUseCase = config.getInstance(CancelCartUseCase.class);
        RemoveDishFromCartUseCase removeDishFromCartUseCase = config.getInstance(RemoveDishFromCartUseCase.class);
        UpdateCartItemUseCase updateCartItemUseCase = config.getInstance(UpdateCartItemUseCase.class);
        CartRepository cartRepository = config.getInstance(CartRepository.class);

        PlaceOrderUseCase placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);
        SelectDeliverySlotUseCase selectDeliverySlotUseCase = config.getInstance(SelectDeliverySlotUseCase.class);
        InitiatePaymentUseCase initiatePaymentUseCase = config.getInstance(InitiatePaymentUseCase.class);
        ConfirmOrderUseCase confirmOrderUseCase = config.getInstance(ConfirmOrderUseCase.class);
        OrderRepository orderRepository = config.getInstance(OrderRepository.class);

        CartApiHandler cartHandler = new CartApiHandler(
                addDishToCartUseCase,
                clearCartUseCase,
                cancelCartUseCase,
                removeDishFromCartUseCase,
                updateCartItemUseCase,
                cartRepository
        );

        OrderApiHandler orderHandler = new OrderApiHandler(
                placeOrderUseCase,
                selectDeliverySlotUseCase,
                initiatePaymentUseCase,
                confirmOrderUseCase,
                orderRepository
        );

        ApiRegistry registry = new ApiRegistry();

        SophiaTechEatsApplication.registerOrderAndCartRoutes(registry, orderHandler, cartHandler);

        return registry;
    }

    public static void main(String[] args) {
        OrderPaymentServiceApplication service = new OrderPaymentServiceApplication();
        service.start();
    }

    public ApplicationConfig getConfig() {
        return config;
    }
}
