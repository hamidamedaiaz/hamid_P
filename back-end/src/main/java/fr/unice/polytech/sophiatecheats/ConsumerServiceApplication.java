package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import fr.unice.polytech.sophiatecheats.interfaces.http.ApiRegistry;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.CartHandler;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.OrderHandler;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Consumer Service - Gère les actions des clients.
 * Permet aux clients de:
 * - Parcourir les restaurants (GET /restaurants)
 * - Gérer leur panier
 * - Passer des commandes
 * - Consulter leurs commandes
 *
 * Port: 8082
 */
public class ConsumerServiceApplication {

    public static final int PORT = 8082;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    private static final Logger logger = Logger.getLogger(ConsumerServiceApplication.class.getName());

    private final ApplicationConfig config;
    private final SophiaTechEatsFacade facade;

    public ConsumerServiceApplication() {
        this.config = new ApplicationConfig();
        this.facade = new SophiaTechEatsFacade(config);
    }

    public static void main(String[] args) {
        ConsumerServiceApplication service = new ConsumerServiceApplication();
        service.start();
    }

    public void start() {
        logger.info(CYAN + "╔════════════════════════════════════════╗" + RESET);
        logger.info(CYAN + "║     CONSUMER SERVICE - PORT 8082       ║" + RESET);
        logger.info(CYAN + "╚════════════════════════════════════════╝" + RESET);

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
        CartHandler cartHandler = new CartHandler(facade);
        OrderHandler orderHandler = new OrderHandler(facade);

        ApiRegistry registry = new ApiRegistry();

        // Routes pour le panier
        registry.registerRoute(POST, "/api/cart/items", cartHandler);
        registry.registerRoute(GET, "/api/cart/{userId}", cartHandler);
        registry.registerRoute(PUT, "/api/cart/{userId}/items", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}/cancel", cartHandler);
        registry.registerRoute(DELETE, "/api/cart/{userId}/items/{dishId}", cartHandler);

        // Sélectionner un créneau de livraison (ajouté au panier)
        registry.registerRoute(POST, "/api/cart/{userId}/delivery-slot", cartHandler);

        // Payer le panier (transforme en commande)
        registry.registerRoute(POST, "/api/cart/{userId}/payment", cartHandler);

        registry.registerRoute(GET, "/api/orders/{id}", orderHandler);
        registry.registerRoute(GET, "/api/orders/user/{userId}", orderHandler);

        registry.registerRoute(POST, "/api/orders/{id}/confirm", orderHandler);

        return registry;
    }
}


