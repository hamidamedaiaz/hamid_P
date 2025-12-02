import React, { createContext, useContext, useState, useEffect } from 'react';
import { fetchCart, addDishToCart, removeFromCart, clearCart, updateCartItemQuantity } from '../services/cartService.js';
import notificationService from '../utils/notificationService.jsx';
import errorHandler from '../utils/errorHandler.js';
import { apiLogger } from '../utils/logger.js';

const CartContext = createContext();

export function useCart() {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
}

export function CartProvider({ children }) {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Ne PAS charger le panier automatiquement au démarrage
    // Le panier sera chargé uniquement quand nécessaire (par exemple lors de l'ajout d'un article)
    // Cela évite les appels API inutiles sur les pages manager

    async function loadCart() {
        try {
            setLoading(true);
            const data = await fetchCart();
            setCart(data);
            setError(null);

            if (data) {
                apiLogger.info('Cart loaded successfully', {
                    itemCount: data.items?.length || 0,
                    total: data.totalAmount
                });
            }
        } catch (err) {
            // Ne logger que les vraies erreurs (pas le 404)
            if (!err.message || !err.message.includes('404')) {
                setError(err.message);
                errorHandler.handle(err, { context: 'loadCart' }, false);
            }
        } finally {
            setLoading(false);
        }
    }

    async function addToCart(dishId, quantity = 1) {
        const loadingToast = notificationService.loading('Ajout au panier...');

        try {
            setLoading(true);
            await addDishToCart(dishId, quantity);
            await loadCart(); // Recharger le panier
            setError(null);

            notificationService.dismiss(loadingToast);
            notificationService.success(`${quantity} article(s) ajouté(s) au panier`);

            apiLogger.userAction('Item added to cart', { dishId, quantity });
            return true;
        } catch (err) {
            notificationService.dismiss(loadingToast);
            errorHandler.handleApiError(err, '/api/cart/items', 'POST');
            setError(err.message);
            return false;
        } finally {
            setLoading(false);
        }
    }

    async function removeItem(dishId) {
        try {
            setLoading(true);
            await removeFromCart(dishId);
            await loadCart();
            setError(null);

            notificationService.success('Article retiré du panier');
            apiLogger.userAction('Item removed from cart', { dishId });
        } catch (err) {
            errorHandler.handleApiError(err, `/api/cart/items/${dishId}`, 'DELETE');
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function updateQuantity(dishId, newQuantity) {
        try {
            setLoading(true);
            await updateCartItemQuantity(dishId, newQuantity);
            await loadCart();
            setError(null);

            apiLogger.userAction('Cart quantity updated', { dishId, newQuantity });
        } catch (err) {
            errorHandler.handleApiError(err, '/api/cart/items', 'PUT');
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function emptyCart() {
        notificationService.confirmAction(
            'Êtes-vous sûr de vouloir vider votre panier ?',
            async () => {
                try {
                    setLoading(true);
                    await clearCart();
                    setCart(null);
                    setError(null);

                    notificationService.success('Panier vidé avec succès');
                    apiLogger.userAction('Cart cleared');
                } catch (err) {
                    errorHandler.handleApiError(err, '/api/cart', 'DELETE');
                    setError(err.message);
                } finally {
                    setLoading(false);
                }
            }
        );
    }

    const cartItemsCount = cart?.items?.length || 0;
    const cartTotal = cart?.totalAmount || 0;

    const value = {
        cart,
        loading,
        error,
        cartItemsCount,
        cartTotal,
        addToCart,
        removeItem,
        updateQuantity,
        emptyCart,
        refreshCart: loadCart
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
}

