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
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(restaurantData),
    });
    if (!response.ok) throw new Error('Failed to create restaurant');

    const slotData = {
        date : new Date().toISOString().split('T')[0],
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
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(restaurantData),
    });
    if (!response.ok) throw new Error('Failed to update restaurant');
    return await response.json();
}

export async function deleteRestaurant(restaurantId) {
    if (globalThis.confirm("Are you sure you want to delete this restaurant? This action cannot be undone.")) {
        const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete restaurant');
        return true;
    }
    return false;
}

export async function addDishToMenu(restaurantId, dishData) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/menu`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
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
        headers: { 'Content-Type': 'application/json' },
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
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/delivery-slots`);
    if (!response.ok) throw new Error('Failed to fetch delivery slots');
    return await response.json();
}

export async function createSlots(restaurantId, slotData) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/delivery-slots`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(slotData),
    });
    if (!response.ok) throw new Error('Failed to create delivery slots');
    return await response.json();
}

export async function reserveOrReleaseSlot(restaurantId, slotId) {
    const response = await fetch(`${API_BASE_URL}restaurants/${restaurantId}/delivery-slots/${slotId}/`, {
        method: 'POST',
    });
    if (!response.ok) throw new Error('Failed reserving or releasing a delivery slot');
    return true;
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
