package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.sophiatecheats.application.facade.SophiaTechEatsFacade;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import fr.unice.polytech.sophiatecheats.interfaces.http.ApiRegistry;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.CatalogHandler;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.DeliverySlotApiHandler;
import fr.unice.polytech.sophiatecheats.interfaces.http.handlers.RestaurantHandler;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Restaurant Service - Gère l'interface restaurant.
 * Permet aux restaurants de:
 * - Gérer leur menu (ajouter/modifier/supprimer des plats)
 * - Gérer leurs créneaux de livraison
 * - Gérer leurs informations (horaires, disponibilité, etc.)
 *
 * Port: 8081
 */
public class RestaurantServiceApplication {

    public static final int PORT = 8081;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String RESTAURANT_PATH = "/restaurants";
    private static final String RESTAURANT_BY_ID_PATH = RESTAURANT_PATH + "/{id}";

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String MAGENTA = "\u001B[35m";

    private static final Logger logger = Logger.getLogger(RestaurantServiceApplication.class.getName());

    private final ApplicationConfig config;
    private final SophiaTechEatsFacade facade;

    public RestaurantServiceApplication() {
        this.config = new ApplicationConfig();
        this.facade = new SophiaTechEatsFacade(config);
    }

    public static void main(String[] args) {
        RestaurantServiceApplication service = new RestaurantServiceApplication();
        service.start();
    }

    public void start() {
        logger.info(MAGENTA + "╔════════════════════════════════════════╗" + RESET);
        logger.info(MAGENTA + "║    RESTAURANT SERVICE - PORT 8081      ║" + RESET);
        logger.info(MAGENTA + "╚════════════════════════════════════════╝" + RESET);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            ApiRegistry registry = configureRestaurantRoutes();

            server.createContext("/", registry::dispatch);
            server.start();

            logger.info(GREEN + "✓ Restaurant Service démarré sur http://localhost:" + PORT + RESET);
            logger.info(YELLOW + "Routes disponibles:" + RESET);
            logger.info(CYAN + "  [RESTAURANTS]" + RESET);
            logger.info(CYAN + "  - GET    /restaurants                  (Lister restaurants)" + RESET);
            logger.info(CYAN + "  - GET    /restaurants/{id}             (Détails restaurant)" + RESET);
            logger.info(CYAN + "  - POST   /restaurants                  (Créer restaurant)" + RESET);
            logger.info(CYAN + "  - PUT    /restaurants/{id}             (Modifier restaurant)" + RESET);
            logger.info(CYAN + "  - DELETE /restaurants/{id}             (Supprimer restaurant)" + RESET);
            logger.info(CYAN + "  [MENU MANAGEMENT]" + RESET);
            logger.info(CYAN + "  - GET    /restaurants/{id}/menu        (Voir menu)" + RESET);
            logger.info(CYAN + "  - POST   /restaurants/{id}/menu        (Ajouter plat)" + RESET);
            logger.info(CYAN + "  - PUT    /restaurants/{id}/menu/{dishId} (Modifier plat)" + RESET);
            logger.info(CYAN + "  - DELETE /restaurants/{id}/menu/{dishId} (Supprimer plat)" + RESET);
            logger.info(CYAN + "  [DELIVERY SLOTS]" + RESET);
            logger.info(CYAN + "  - GET    /restaurants/{id}/delivery-slots (Voir créneaux)" + RESET);
            logger.info(CYAN + "  - POST   /restaurants/{id}/delivery-slots (Créer créneaux)" + RESET);
            logger.info(CYAN + "  - POST   /restaurants/{id}/delivery-slots/{slotId} (Réserver/Libérer)" + RESET);
            logger.info(MAGENTA + "════════════════════════════════════════" + RESET);
            logger.info(GREEN + "Service prêt à recevoir des requêtes." + RESET);

            Thread.currentThread().join();

        } catch (Exception e) {
            logger.severe("Erreur critique au démarrage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure les routes pour l'interface restaurant
     */
    private ApiRegistry configureRestaurantRoutes() {
        RestaurantService restaurantService = config.getInstance(RestaurantService.class);

        RestaurantHandler restaurantHandler = new RestaurantHandler(facade);
        CatalogHandler catalogHandler = new CatalogHandler(restaurantService);
        DeliverySlotApiHandler deliverySlotHandler = new DeliverySlotApiHandler(facade);

        ApiRegistry registry = new ApiRegistry();
        // Routes de liste des restaurants (lecture seule)
        registry.registerRoute(GET, RESTAURANT_PATH, restaurantHandler);
        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH, restaurantHandler);
        // Routes de gestion du menu

        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH + "/menu", catalogHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/menu", catalogHandler);
        registry.registerRoute(PUT, RESTAURANT_BY_ID_PATH + "/menu/{dishId}", catalogHandler);
        registry.registerRoute(DELETE, RESTAURANT_BY_ID_PATH + "/menu/{dishId}", catalogHandler);

        // Routes de gestion des créneaux de livraison
        registry.registerRoute(GET, RESTAURANT_BY_ID_PATH + "/delivery-slots", deliverySlotHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/delivery-slots", deliverySlotHandler);
        registry.registerRoute(POST, RESTAURANT_BY_ID_PATH + "/delivery-slots/{slotId}", deliverySlotHandler);

        // Routes de gestion du restaurant (CRUD)
        registry.registerRoute(POST, RESTAURANT_PATH, restaurantHandler);
        registry.registerRoute(PUT, RESTAURANT_BY_ID_PATH, restaurantHandler);
        registry.registerRoute(DELETE, RESTAURANT_BY_ID_PATH, restaurantHandler);

        return registry;
    }
}

