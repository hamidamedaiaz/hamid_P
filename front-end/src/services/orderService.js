

const API_BASE_URL = "http://localhost:8080";

const DEFAULT_USER_ID = "550e8400-e29b-41d4-a716-446655440000";

// =================== RÉCUPÉRATION COMMANDES ===================

/**
 * Récupère une commande par son ID
 */
export async function fetchOrder(orderId) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/${orderId}`);

        if (!response.ok) throw new Error('Failed to fetch order');
        return await response.json();
    } catch (error) {
        console.error('Error fetching order:', error);
        throw error;
    }
}

/**
 * Récupère toutes les commandes d'un utilisateur
 */
export async function fetchUserOrders(userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/user/${userId}`);

        if (!response.ok) throw new Error('Failed to fetch user orders');
        return await response.json();
    } catch (error) {
        console.error('Error fetching user orders:', error);
        throw error;
    }
}