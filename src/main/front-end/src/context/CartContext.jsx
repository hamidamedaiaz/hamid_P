import { createContext, useContext, useState, useCallback } from 'react';
import cartService from '../services/cartService';
import { useUser } from './UserContext';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const { userId } = useUser();
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const loadCart = useCallback(async () => {
        if (!userId) return;

        setLoading(true);
        setError(null);
        try {
            const cartData = await cartService.getCart(userId);
            setCart(cartData);
        } catch (err) {
            // Si le panier n'existe pas encore (404), c'est normal
            if (err.response?.status === 404) {
                setCart(null);
            } else {
                setError(err.message);
            }
        } finally {
            setLoading(false);
        }
    }, [userId]);

    const addToCart = async (dishId, quantity = 1) => {
        if (!userId) {
            throw new Error('Utilisateur non connecté');
        }

        try {
            const response = await cartService.addDishToCart(userId, dishId, quantity);

            if (response && response.success !== false && response.cartId) {
                // Recharger le panier pour avoir les données à jour
                await loadCart();
                return response;
            }
            const error = new Error('Échec de l\'ajout au panier');
            setError(error.message);
            throw error;
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const updateCartItem = async (dishId, quantity) => {
        if (!userId) return;

        try {
            await cartService.updateCartItem(userId, dishId, quantity);
            await loadCart();
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const removeFromCart = async (dishId) => {
        if (!userId) return;

        try {
            await cartService.removeDishFromCart(userId, dishId);
            await loadCart();
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const clearCart = async () => {
        if (!userId) return;

        try {
            await cartService.clearCart(userId);
            setCart(null);
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const cancelCart = async () => {
        if (!userId) return;

        try {
            await cartService.cancelCart(userId);
            setCart(null);
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const getCartItemCount = () => {
        return cart?.totalItems || 0;
    };

    const getCartTotal = () => {
        return cart?.total || 0;
    };

    const value = {
        cart,
        loading,
        error,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        cancelCart,
        loadCart,
        getCartItemCount,
        getCartTotal
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart doit être utilisé dans un CartProvider');
    }
    return context;
};

export default CartContext;
