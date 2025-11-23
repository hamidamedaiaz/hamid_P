package fr.unice.polytech.sophiatecheats;

import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.sophiatecheats.gateway.routing.GatewayRouter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Application Gateway - Point d'entrée unique pour toutes les requêtes API
 *
 * Route automatiquement vers :
 * - Service Restaurant (port 8081) : /api/restaurants/**
 * - Service Order & Payment (port 8082) : /api/cart/**, /api/orders/**, /api/payments/**
 *
 * Les services partagent les mêmes données en mémoire grâce aux SharedRepositories
 * Pas besoin de client HTTP - les repositories communiquent via un Singleton partagé
 */
public class GatewayApplication {

    public static final int GATEWAY_PORT = 8080;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";

    private static final Logger logger = Logger.getLogger(GatewayApplication.class.getName());

    public static void main(String[] args) {
        try {
            GatewayApplication gateway = new GatewayApplication();
            gateway.start();
        } catch (Exception e) {
            logger.severe("❌ Erreur fatale lors du démarrage de la Gateway : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws IOException {
        printBanner();

        // Créer le serveur HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(GATEWAY_PORT), 0);

        // Créer le router
        GatewayRouter router = new GatewayRouter();

        // Configurer le contexte pour toutes les routes
        server.createContext("/", router::handle);

        // Démarrer le serveur
        server.setExecutor(null);
        server.start();

        printStartupInfo();
    }

    private void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║         🌐  SOPHIA TECH EATS - API GATEWAY  🌐          ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void printStartupInfo() {
        System.out.println(GREEN + BOLD + "✓ Gateway démarrée avec succès!" + RESET);
        System.out.println();
        System.out.println(YELLOW + "📡 Configuration du routage :" + RESET);
        System.out.println("   ┌─────────────────────────────────────────────────────┐");
        System.out.println("   │ Gateway Port     : " + GATEWAY_PORT + "                              │");
        System.out.println("   ├─────────────────────────────────────────────────────┤");
        System.out.println("   │ Routes :                                            │");
        System.out.println("   │   /api/restaurants/**  → Service Restaurant (8081)  │");
        System.out.println("   │   /restaurants/**      → Service Restaurant (8081)  │");
        System.out.println("   │   /api/cart/**         → Service Order (8082)       │");
        System.out.println("   │   /api/orders/**       → Service Order (8082)       │");
        System.out.println("   │   /api/payments/**     → Service Order (8082)       │");
        System.out.println("   │   /api/delivery/**     → Service Order (8082)       │");
        System.out.println("   └─────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println(CYAN + "🌍 Gateway accessible sur : " + RESET + BOLD + "http://localhost:" + GATEWAY_PORT + RESET);
        System.out.println();
        System.out.println(YELLOW + "💡 Les services partagent les données via SharedRepositories" + RESET);
        System.out.println(YELLOW + "   Pas de client HTTP - Communication directe en mémoire" + RESET);
        System.out.println();
        System.out.println(YELLOW + "⚠️  Assurez-vous que les services suivants sont démarrés :" + RESET);
        System.out.println("   • Service Restaurant (port 8081)");
        System.out.println("   • Service Order & Payment (port 8082)");
        System.out.println();
        System.out.println(GREEN + "✓ Prêt à recevoir des requêtes..." + RESET);
        System.out.println();
    }
}

