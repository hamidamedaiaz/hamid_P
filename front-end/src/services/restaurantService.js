import api from './api.js';

const API_BASE_URL = "http://localhost:8080/";

export async function fetchRestaurants() {
    const response = await fetch(`${API_BASE_URL}restaurants`);
    if (!response.ok) throw new Error('Failed to fetch restaurants');
    return await response.json();
}

export async function fetchRestaurantDetails(restaurantId) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}`);
    if (!response.ok) throw new Error('Failed to fetch restaurant details');
    return await response.json();
}

export async function createRestaurant(restaurantData) {
    const response = await fetch(`${API_BASE_URL}restaurants`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(restaurantData),
    });
    if (!response.ok) throw new Error('Failed to create restaurant');

    const slotData = {
        date: new Date().toISOString().split('T')[0],
        start: restaurantData.startTime,
        end: restaurantData.endTime,
        maxCapacity: 5,
    }

    await createSlots(response.id, slotData);

    return await response.json();
}

export async function updateRestaurant(restaurantId, restaurantData) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(restaurantData),
    });
    if (!response.ok) throw new Error('Failed to update restaurant');
    return await response.json();
}

export async function deleteRestaurant(restaurantId) {
    if (globalThis.confirm("Are you sure you want to delete this restaurant? This action cannot be undone.")) {
        const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}`, {method: 'DELETE'});
        if (!response.ok) throw new Error('Failed to delete restaurant');
        return true;
    }
    return false;
}

export async function addDishToMenu(restaurantId, dishData) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/menu`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(dishData),
    });
    if (!response.ok) throw new Error('Failed to add dish to menu');
    return await response.json();
}

export async function removeDishFromMenu(restaurantId, dishId) {
    if (globalThis.confirm("Are you sure you want to remove this dish from the menu? This action cannot be undone.")) {
        const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/menu/${dishId}`, {
            method: 'DELETE',
        });
        if (!response.ok) throw new Error('Failed to remove dish from menu');
        return true;
    }
    return false;
}

export async function updateDishInMenu(restaurantId, dishId, dishData) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/menu/${dishId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(dishData),
    });
    if (!response.ok) throw new Error('Failed to update dish in menu');
    return await response.json();
}

export async function fetchMenu(restaurantId) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/menu`);
    if (!response.ok) throw new Error('Failed to fetch menu');
    return await response.json();
}

export async function fetchDeliverySlots(restaurantId) {
    try {
        const response = await api.get(`/restaurants/${restaurantId}/delivery-slots`);
        return response.data;
    } catch (error) {
        console.error('Error fetching delivery slots:', error);
        throw new Error('Impossible de charger les créneaux de livraison');
    }
}

export async function createSlots(restaurantId, slotData) {
    try {
        const response = await api.post(`/restaurants/${restaurantId}/delivery-slots`, slotData);
        return response.data;
    } catch (error) {
        console.error('Error creating delivery slots:', error);
        throw new Error('Impossible de créer les créneaux de livraison');
    }
}

export async function reserveOrReleaseSlot(restaurantId, slotId, action = 'reserve') {
    try {
        const response = await api.post(`/restaurants/${restaurantId}/delivery-slots/${slotId}`, action);
        return response.data;
    } catch (error) {
        console.error('Error reserving/releasing delivery slot:', error);
        throw new Error('Impossible de réserver/libérer le créneau');
    }
}

/**
 * Récupère toutes les commandes d'un restaurant
 * @param {string} restaurantId - L'ID du restaurant
 */
export async function fetchRestaurantOrders(restaurantId) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/orders`);
    if (!response.ok) throw new Error('Failed to fetch restaurant orders');
    return await response.json();
}

/**
 * Filtrage dynamique des restaurants par catégorie, type ou ouverture
 * @param {Object} filters - {cuisineType, restaurantType, isOpen}
 */
export async function filterRestaurants(filters = {}) {
    const params = new URLSearchParams();

    if (filters.cuisineType) params.append('cuisineType', filters.cuisineType);
    if (filters.restaurantType) params.append('restaurantType', filters.restaurantType);
    if (filters.isOpen !== undefined) params.append('isOpen', filters.isOpen);

    const queryString = params.toString() ? `?${params.toString()}` : '';
    const response = await fetch(`${API_BASE_URL}restaurants${queryString}`);
    if (!response.ok) throw new Error('Failed to filter restaurants');
    return await response.json();
}

// Default export for convenience
const restaurantService = {
    fetchRestaurants,
    fetchRestaurantDetails,
    createRestaurant,
    updateRestaurant,
    deleteRestaurant,
    addDishToMenu,
    removeDishFromMenu,
    updateDishInMenu,
    fetchMenu,
    fetchDeliverySlots,
    createSlots,
    reserveOrReleaseSlot,
    filterRestaurants,
    getRestaurantById: fetchRestaurantDetails
};

export default restaurantService;
