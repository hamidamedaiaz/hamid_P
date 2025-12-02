/**
 * Service pour la gestion du PANIER (Cart Management)
 * - Ajouter des plats au panier
 * - Modifier les quantités
 * - Supprimer des articles
 * - Vider le panier
 * - Récupérer le panier
 */

const API_BASE_URL = "http://localhost:8080";

// UUID de l'utilisateur (à remplacer par un vrai système d'auth)
const DEFAULT_USER_ID = "550e8400-e29b-41d4-a716-446655440000";

// =================== RÉCUPÉRATION PANIER ===================

/**
 * Récupère le panier actif d'un utilisateur
 */
export async function fetchCart(userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}`);

        if (response.status === 404) {
            // Pas de panier actif - c'est normal, retourner un panier vide
            console.log('ℹ️ Pas de panier actif pour cet utilisateur');
            return null;
        }

        if (!response.ok) {
            throw new Error(`Failed to fetch cart: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        // Si c'est une erreur réseau (pas une erreur 404), on log
        if (error.message && !error.message.includes('404')) {
            console.error('Error fetching cart:', error);
        }
        // Ne pas throw si c'est juste qu'il n'y a pas de panier
        return null;
    }
}

// =================== AJOUT AU PANIER ===================

/**
 * Ajoute un plat au panier
 */
export async function addDishToCart(dishId, quantity = 1, userId = DEFAULT_USER_ID) {
    try {
        const payload = {
            userId,
            dishId,
            quantity
        };

        console.log('🔵 [cartService] Envoi requête POST /api/cart/items:', payload);

        const response = await fetch(`${API_BASE_URL}/api/cart/items`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        console.log('🔵 [cartService] Réponse du serveur:', response.status, response.statusText);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('❌ [cartService] Erreur serveur:', errorText);
            let error;
            try {
                error = JSON.parse(errorText);
            } catch {
                error = { error: errorText };
            }
            throw new Error(error.error || 'Failed to add dish to cart');
        }

        const result = await response.json();
        console.log('✅ [cartService] Plat ajouté avec succès:', result);
        return result;
    } catch (error) {
        console.error('❌ [cartService] Error adding dish to cart:', error);
        throw error;
    }
}

// =================== MODIFICATION PANIER ===================

/**
 * Met à jour la quantité d'un article dans le panier
 */
export async function updateCartItemQuantity(dishId, newQuantity, userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}/items`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId,
                dishId,
                newQuantity
            }),
        });

        if (!response.ok) throw new Error('Failed to update cart item');

        // Le backend retourne du texte, pas du JSON
        const text = await response.text();
        return { success: true, message: text };
    } catch (error) {
        console.error('Error updating cart item:', error);
        throw error;
    }
}

/**
 * Supprime un article du panier
 */
export async function removeFromCart(dishId, userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}/items/${dishId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to remove item from cart');
        return true;
    } catch (error) {
        console.error('Error removing from cart:', error);
        throw error;
    }
}

/**
 * Vide complètement le panier
 */
export async function clearCart(userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to clear cart');
        return true;
    } catch (error) {
        console.error('Error clearing cart:', error);
        throw error;
    }
}

/**
 * Annule le panier (supprime complètement)
 */
export async function cancelCart(userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}/cancel`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to cancel cart');
        return true;
    } catch (error) {
        console.error('Error canceling cart:', error);
        throw error;
    }
}

// =================== CRÉNEAU DE LIVRAISON ===================

/**
 * Sélectionne un créneau de livraison pour le panier
 */
export async function selectDeliverySlotForCart(deliverySlotId, userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}/delivery-slot`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ deliverySlotId }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to select delivery slot');
        }

        return await response.json();
    } catch (error) {
        console.error('Error selecting delivery slot:', error);
        throw error;
    }
}

// =================== PAIEMENT & TRANSFORMATION EN COMMANDE ===================

/**
 * Transforme le panier en commande (avec paiement)
 */
export async function processCartPayment(paymentData, userId = DEFAULT_USER_ID) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${userId}/payment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(paymentData),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Payment failed');
        }

        return await response.json();
    } catch (error) {
        console.error('Error processing payment:', error);
        throw error;
    }
}

// =================== UTILITAIRES ===================

/**
 * Calcule le total du panier localement
 */
export function calculateCartTotal(cartItems) {
    if (!cartItems || cartItems.length === 0) return 0;

    return cartItems.reduce((total, item) => {
        return total + (item.unitPrice * item.quantity);
    }, 0);
}

/**
 * Compte le nombre total d'articles dans le panier
 */
export function getCartItemCount(cartItems) {
    if (!cartItems || cartItems.length === 0) return 0;

    return cartItems.reduce((count, item) => count + item.quantity, 0);
}

/**
 * Vérifie si le panier est vide
 */
export function isCartEmpty(cart) {
    return !cart || !cart.items || cart.items.length === 0;
}