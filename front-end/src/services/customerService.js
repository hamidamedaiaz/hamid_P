
import api, { API_BASE_URL } from './api.js';

// =================== CONSULTATION RESTAURANTS ===================

/**
 * Récupère tous les restaurants disponibles
 * @returns {Promise<Array>} Liste des restaurants
 */
export async function fetchRestaurants() {
    try {
        const response = await api.get('/restaurants');
        return response.data;
    } catch (error) {
        console.error('Error fetching restaurants:', error);
        throw new Error('Impossible de charger les restaurants. Veuillez réessayer.');
    }
}

/**
 * Récupère les détails d'un restaurant spécifique
 * @param {string} restaurantId - UUID du restaurant
 * @returns {Promise<Object>} Détails du restaurant
 */
export async function fetchRestaurantDetails(restaurantId) {
    try {
        const response = await api.get(`/restaurants/${restaurantId}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching restaurant details:', error);

        if (error.response?.status === 404) {
            throw new Error('Restaurant non trouvé.');
        }

        throw new Error('Impossible de charger les détails du restaurant.');
    }
}

/**
 * Filtre les restaurants selon des critères multiples
 * @param {Object} filters - Critères de filtrage
 * @param {string} filters.cuisineType - Type de cuisine (VEGETARIAN, VEGAN, etc.)
 * @param {string} filters.restaurantType - Type d'établissement (RESTAURANT, FOOD_TRUCK, etc.)
 * @param {boolean} filters.isOpen - Statut ouvert/fermé
 * @returns {Promise<Array>} Liste filtrée des restaurants
 */
export async function filterRestaurants(filters = {}) {
    try {
        const params = new URLSearchParams();

        // Construire les paramètres de requête
        if (filters.cuisineType) {
            params.append('cuisineType', filters.cuisineType);
        }
        if (filters.restaurantType) {
            params.append('restaurantType', filters.restaurantType);
        }
        if (filters.isOpen !== undefined && filters.isOpen !== '') {
            params.append('isOpen', filters.isOpen);
        }

        const queryString = params.toString();
        const url = queryString ? `/restaurants?${queryString}` : '/restaurants';

        const response = await api.get(url);
        return response.data;
    } catch (error) {
        console.error('Error filtering restaurants:', error);
        throw new Error('Impossible de filtrer les restaurants.');
    }
}

/**
 * Récupère uniquement les restaurants ouverts
 * @returns {Promise<Array>} Liste des restaurants ouverts
 */
export async function fetchOpenRestaurants() {
    try {
        return await filterRestaurants({ isOpen: true });
    } catch (error) {
        console.error('Error fetching open restaurants:', error);
        throw error;
    }
}

/**
 * Recherche de restaurants par nom ou adresse (côté client)
 * @param {string} searchTerm - Terme de recherche
 * @returns {Promise<Array>} Restaurants correspondants
 */
export async function searchRestaurants(searchTerm) {
    try {
        const restaurants = await fetchRestaurants();

        if (!searchTerm || searchTerm.trim() === '') {
            return restaurants;
        }

        const term = searchTerm.toLowerCase().trim();

        return restaurants.filter(restaurant =>
            restaurant.name.toLowerCase().includes(term) ||
            restaurant.address.toLowerCase().includes(term)
        );
    } catch (error) {
        console.error('Error searching restaurants:', error);
        throw new Error('Erreur lors de la recherche.');
    }
}

// =================== CONSULTATION MENU ===================

/**
 * Récupère le menu complet d'un restaurant
 * @param {string} restaurantId - UUID du restaurant
 * @returns {Promise<Array>} Liste des plats du menu
 */
export async function fetchMenu(restaurantId) {
    try {
        const response = await api.get(`/restaurants/${restaurantId}/menu`);
        return response.data;
    } catch (error) {
        console.error('Error fetching menu:', error);

        if (error.response?.status === 404) {
            throw new Error('Menu non trouvé.');
        }

        throw new Error('Impossible de charger le menu.');
    }
}

/**
 * Récupère uniquement les plats disponibles d'un restaurant
 * @param {string} restaurantId - UUID du restaurant
 * @returns {Promise<Array>} Liste des plats disponibles
 */
export async function fetchAvailableDishes(restaurantId) {
    try {
        const menu = await fetchMenu(restaurantId);
        return menu.filter(dish => dish.available === true);
    } catch (error) {
        console.error('Error fetching available dishes:', error);
        throw error;
    }
}

/**
 * Récupère un plat spécifique par son ID
 * @param {string} restaurantId - UUID du restaurant
 * @param {string} dishId - UUID du plat
 * @returns {Promise<Object|null>} Le plat ou null si non trouvé
 */
export async function fetchDishById(restaurantId, dishId) {
    try {
        const menu = await fetchMenu(restaurantId);
        return menu.find(dish => dish.id === dishId) || null;
    } catch (error) {
        console.error('Error fetching dish:', error);
        throw error;
    }
}

// =================== CRÉNEAUX DE LIVRAISON ===================

/**
 * Récupère les créneaux de livraison pour un restaurant
 * @param {string} restaurantId - UUID du restaurant
 * @param {string|null} date - Date au format YYYY-MM-DD (optionnel)
 * @returns {Promise<Array>} Liste des créneaux
 */
export async function fetchDeliverySlots(restaurantId, date = null) {
    try {
        const params = date ? `?date=${date}` : '';
        const response = await api.get(`/restaurants/${restaurantId}/delivery-slots${params}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching delivery slots:', error);
        throw new Error('Impossible de charger les créneaux de livraison.');
    }
}

/**
 * Récupère uniquement les créneaux disponibles (non pleins)
 * @param {string} restaurantId - UUID du restaurant
 * @param {string|null} date - Date au format YYYY-MM-DD (optionnel)
 * @returns {Promise<Array>} Liste des créneaux disponibles
 */
export async function fetchAvailableDeliverySlots(restaurantId, date = null) {
    try {
        const slots = await fetchDeliverySlots(restaurantId, date);

        return slots.filter(slot =>
            slot.available === true &&
            slot.reservedCount < slot.maxCapacity
        );
    } catch (error) {
        console.error('Error fetching available delivery slots:', error);
        throw error;
    }
}

/**
 * Récupère les créneaux pour aujourd'hui
 * @param {string} restaurantId - UUID du restaurant
 * @returns {Promise<Array>} Créneaux d'aujourd'hui
 */
export async function fetchTodayDeliverySlots(restaurantId) {
    try {
        const today = new Date().toISOString().split('T')[0];
        return await fetchAvailableDeliverySlots(restaurantId, today);
    } catch (error) {
        console.error('Error fetching today slots:', error);
        throw error;
    }
}

// =================== UTILITAIRES ===================

/**
 * Vérifie si un restaurant est actuellement ouvert
 * @param {Object} restaurant - Objet restaurant
 * @returns {boolean} True si ouvert
 */
export function isRestaurantOpen(restaurant) {
    if (!restaurant) return false;

    // Vérifier d'abord le statut isOpen
    if (!restaurant.isOpen) return false;

    // Vérifier les horaires si disponibles
    if (restaurant.openingTime && restaurant.closingTime) {
        const now = new Date();
        const currentTime = now.getHours() * 60 + now.getMinutes();

        const [openHour, openMin] = restaurant.openingTime.split(':').map(Number);
        const [closeHour, closeMin] = restaurant.closingTime.split(':').map(Number);

        const openingMinutes = openHour * 60 + openMin;
        const closingMinutes = closeHour * 60 + closeMin;

        return currentTime >= openingMinutes && currentTime <= closingMinutes;
    }

    return restaurant.isOpen;
}

/**
 * Calcule la distance entre deux points (si coordonnées GPS disponibles)
 * @param {number} lat1 - Latitude point 1
 * @param {number} lon1 - Longitude point 1
 * @param {number} lat2 - Latitude point 2
 * @param {number} lon2 - Longitude point 2
 * @returns {number} Distance en kilomètres
 */
export function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Rayon de la Terre en km
    const dLat = toRad(lat2 - lat1);
    const dLon = toRad(lon2 - lon1);

    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

function toRad(value) {
    return value * Math.PI / 180;
}

/**
 * Groupe les plats par catégorie
 * @param {Array} dishes - Liste des plats
 * @returns {Object} Plats groupés par catégorie
 */
export function groupDishesByCategory(dishes) {
    return dishes.reduce((acc, dish) => {
        const category = dish.category || 'OTHER';
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(dish);
        return acc;
    }, {});
}

/**
 * Formate le prix avec le symbole euro
 * @param {number} price - Prix à formater
 * @returns {string} Prix formaté
 */
export function formatPrice(price) {
    return `${price.toFixed(2)} €`;
}