package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.InitiatePaymentUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.SelectDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.infrastructure.api.ApiRegistry;
import fr.unice.polytech.sophiatecheats.infrastructure.api.httphandlers.*;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Point d'entrée principal - TOUT EN UN
 * Service unifié contenant Restaurant + Order & Payment sur le port 8080
 */
@Command(name = "sophiatech-eats",
         description = "Système de commande et livraison de repas pour le campus SophiaTech",
         version = "1.0.0")
public class SophiaTechEatsApplication implements Runnable {

    public static final int PORT = 8080;  // UN SEUL PORT
    private static final String RESTAURANT_PATH = "/restaurants";
    private static final String RESTAURANT_BY_ID_PATH = RESTAURANT_PATH + "/{id}";

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String GET = "GET";

    private final ApplicationConfig config;
    private static final Logger logger = Logger.getLogger(SophiaTechEatsApplication.class.getName());

    public SophiaTechEatsApplication() {
        this.config = new ApplicationConfig();
    }

    @Override
    public void run() {
        logger.info(CYAN + "╔══════════════════════════════════════════════════════════╗" + RESET);
        logger.info(CYAN + "║    🍽️  SOPHIA TECH EATS - SERVEUR UNIFIÉ  🍽️           ║" + RESET);
        logger.info(CYAN + "╚══════════════════════════════════════════════════════════╝" + RESET);
        logger.info(GREEN + "Démarrage de SophiaTechEats (Restaurant + Orders + Cart)..." + RESET);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            final ApiRegistry registry = getApiRegistry();

            server.createContext("/", registry::dispatch);
            server.start();

            logger.info(GREEN + "✓ Serveur démarré sur http://localhost:" + PORT + "/" + RESET);
            logger.info(CYAN + "📡 Toutes les fonctionnalités disponibles :" + RESET);
            logger.info(CYAN + "  • Restaurants & Menus" + RESET);
            logger.info(CYAN + "  • Panier (Cart)" + RESET);
            logger.info(CYAN + "  • Commandes (Orders)" + RESET);
            logger.info(CYAN + "  • Paiements (Payments)" + RESET);

        } catch (Exception e) {
            logger.severe(RED + "Erreur critique au démarrage du serveur : " + e + RESET);
        }

        logger.info(GREEN + "✓ Application prête à recevoir des requêtes (Ctrl+C pour arrêter)" + RESET);
    }

    private ApiRegistry getApiRegistry() {
        RestaurantService restaurantService = config.getInstance(RestaurantService.class);
        RestaurantApiHandler restaurantHandler = new RestaurantApiHandler(restaurantService);
        MenuApiHandler menuHandler = new MenuApiHandler(restaurantService);
        DeliverySlotApiHandler deliverySlotHandler = new DeliverySlotApiHandler(restaurantService);

        // Handler pour les plats - permet d'interroger les infos d'un plat
        RestaurantRepository restaurantRepository = config.getInstance(RestaurantRepository.class);
        DishApiHandler dishHandler = new DishApiHandler(restaurantRepository);

        AddDishToCartUseCase addDishToCartUseCase = config.getInstance(AddDishToCartUseCase.class);
        ClearCartUseCase clearCartUseCase = config.getInstance(ClearCartUseCase.class);
        CancelCartUseCase cancelCartUseCase = config.getInstance(CancelCartUseCase.class);
        CartRepository cartRepository = config.getInstance(CartRepository.class);

        PlaceOrderUseCase placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);
        SelectDeliverySlotUseCase selectDeliverySlotUseCase = config.getInstance(SelectDeliverySlotUseCase.class);
        InitiatePaymentUseCase initiatePaymentUseCase = config.getInstance(InitiatePaymentUseCase.class);
        ConfirmOrderUseCase confirmOrderUseCase = config.getInstance(ConfirmOrderUseCase.class);
        OrderRepository orderRepository = config.getInstance(OrderRepository.class);
        RemoveDishFromCartUseCase removeDishFromCartUseCase = config.getInstance(RemoveDishFromCartUseCase.class);
        UpdateCartItemUseCase updateCartItemUseCase = config.getInstance(UpdateCartItemUseCase.class);

        ApiRegistry registry = new ApiRegistry();

        OrderApiHandler orderHandler = new OrderApiHandler(
                placeOrderUseCase, selectDeliverySlotUseCase, initiatePaymentUseCase,
                confirmOrderUseCase, orderRepository
        );

        CartApiHandler cartHandler = new CartApiHandler(
                addDishToCartUseCase, clearCartUseCase, cancelCartUseCase,
                removeDishFromCartUseCase, updateCartItemUseCase, cartRepository
        );

        // Enregistrer TOUTES les routes sur le MÊME serveur
        registerOrderAndCartRoutes(registry, orderHandler, cartHandler);
        registerRestaurantRelatedRoutes(registry, menuHandler, deliverySlotHandler, restaurantHandler);

        return registry;
    }

    private static void registerRestaurantRelatedRoutes(ApiRegistry registry, MenuApiHandler menuHandler,
                                                        DeliverySlotApiHandler deliverySlotHandler,
                                                        RestaurantApiHandler restaurantHandler) {
        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH + "/menu", menuHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/menu", menuHandler);
        registry.registerRoute(PUT, RESTAURANT_BY_ID_PATH + "/menu/{dishId}", menuHandler);
        registry.registerRoute(DELETE, RESTAURANT_BY_ID_PATH + "/menu/{dishId}", menuHandler);

        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH + "/delivery-slots", deliverySlotHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/delivery-slots", deliverySlotHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/delivery-slots/{slotId}", deliverySlotHandler);

        registry.registerRoute(GET, RESTAURANT_PATH, restaurantHandler);
        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH, restaurantHandler);
        registry.registerRoute(POST, RESTAURANT_PATH, restaurantHandler);
        registry.registerRoute(PUT, RESTAURANT_BY_ID_PATH, restaurantHandler);
        registry.registerRoute(DELETE, RESTAURANT_BY_ID_PATH, restaurantHandler);
    }

    static void registerOrderAndCartRoutes(ApiRegistry registry, OrderApiHandler orderHandler, CartApiHandler cartHandler) {
        registry.registerRoute(POST, "/api/cart/items", cartHandler);
        registry.registerRoute(GET, "/api/cart/{userId}", cartHandler);
        registry.registerRoute(PUT, "/api/cart/{userId}/items", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}/cancel", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}/items/{dishId}", cartHandler);

        registry.registerRoute(POST, "/api/orders", orderHandler);
        registry.registerRoute(GET, "/api/orders/{id}", orderHandler);
        registry.registerRoute(GET, "/api/orders/user/{userId}", orderHandler);
        registry.registerRoute(POST, "/api/orders/{id}/delivery-slot", orderHandler);
        registry.registerRoute(POST, "/api/orders/{id}/payment", orderHandler);
        registry.registerRoute(POST, "/api/orders/{id}/confirm", orderHandler);
    }

    public static void main(String[] args) {
        SophiaTechEatsApplication app = new SophiaTechEatsApplication();
        CommandLine cmd = new CommandLine(app);
        int executeCode = cmd.execute(args);
        String msg = executeCode != 0 ? RED+ "Échec du démarrage de l'application. Code : " + executeCode + RESET
                                      : GREEN + "Application démarrée avec succès." + RESET;
        if (executeCode != 0) { logger.severe(msg); }
        else { logger.info(msg); }
    }

    public ApplicationConfig getConfig() {
        return config;
    }
}
