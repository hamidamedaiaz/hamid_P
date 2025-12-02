import axios from "axios";
import { apiLogger } from "../utils/logger.js";
import errorHandler from "../utils/errorHandler.js";

// URL de base de l'API Gateway
const API_BASE_URL = "http://localhost:8080";

// Instance Axios configurée avec des options professionnelles
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json"
    },
    timeout: 15000, // 15 secondes
    validateStatus: (status) => status >= 200 && status < 500, // Ne pas rejeter les erreurs 4xx pour les gérer manuellement
});

// Intercepteur pour les requêtes - Logging et préparation
api.interceptors.request.use(
    (config) => {
        const requestId = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
        config.metadata = {
            requestId,
            startTime: Date.now()
        };

        // Log de la requête
        apiLogger.apiCall(
            config.method.toUpperCase(),
            config.url,
            config.params ? `?${new URLSearchParams(config.params).toString()}` : ''
        );

        // Ajouter un header de traçabilité
        config.headers['X-Request-ID'] = requestId;

        // Ajouter le token d'authentification si disponible
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        apiLogger.error('Request configuration error', error);
        return Promise.reject(error);
    }
);

// Intercepteur pour les réponses - Logging et gestion d'erreurs
api.interceptors.response.use(
    (response) => {
        const { config } = response;
        const duration = config.metadata ? Date.now() - config.metadata.startTime : 0;

        // Log de la réponse réussie
        apiLogger.apiCall(
            config.method.toUpperCase(),
            config.url,
            `${response.status} (${duration}ms)`
        );

        // Vérifier si c'est vraiment un succès (certaines API retournent des erreurs avec 200)
        if (response.status >= 400) {
            const error = new Error(response.data?.message || 'Request failed');
            error.response = response;
            throw error;
        }

        return response;
    },
    (error) => {
        const { config } = error;

        // Log détaillé de l'erreur
        if (error.response) {
            // Le serveur a répondu avec un code d'erreur
            const { status, data } = error.response;
            const duration = config?.metadata ? Date.now() - config.metadata.startTime : 0;

            apiLogger.apiError(
                config?.method?.toUpperCase() || 'UNKNOWN',
                config?.url || 'UNKNOWN',
                {
                    status,
                    message: data?.message || error.message,
                    duration: `${duration}ms`,
                    data: data
                }
            );
        } else if (error.request) {
            // La requête a été envoyée mais pas de réponse (problème réseau)
            apiLogger.error('Network error - No response received', {
                url: config?.url,
                method: config?.method,
                message: error.message
            });
        } else {
            // Erreur lors de la configuration de la requête
            apiLogger.error('Request setup error', {
                message: error.message,
                config: config
            });
        }

        // Rejeter avec l'erreur pour qu'elle soit gérée par les composants
        return Promise.reject(error);
    }
);

/**
 * Wrapper pour les appels API avec gestion d'erreur intégrée
 * @param {Function} apiCall - Fonction qui effectue l'appel API
 * @param {string} endpoint - Endpoint de l'API pour le logging
 * @param {string} method - Méthode HTTP pour le logging
 * @returns {Promise} - Promesse avec les données ou erreur gérée
 */
export async function apiRequest(apiCall, endpoint, method = 'GET') {
    try {
        const response = await apiCall();
        return response.data;
    } catch (error) {
        // Utiliser le gestionnaire d'erreurs centralisé
        throw errorHandler.handleApiError(error, endpoint, method);
    }
}

export default api;
export { API_BASE_URL };