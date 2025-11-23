import api from './api';

const cartService = {
    addDishToCart: async (userId, dishId, quantity = 1) => {
        try {
            const response = await api.post('/cart/items', {
                userId,
                dishId,
                quantity
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de l\'ajout au panier:', error);
            throw error;
        }
    },

    getCart: async (userId) => {
        try {
            const response = await api.get(`/cart/${userId}`);
            return response.data;
        } catch (error) {
            // Ne pas logger l'erreur si le panier n'existe pas (404), c'est normal
            if (error.response?.status !== 404) {
                console.error('Erreur lors de la récupération du panier:', error);
            }
            throw error;
        }
    },


    updateCartItem: async (userId, dishId, quantity) => {
        try {
            const response = await api.put(`/cart/${userId}/items`, {
                dishId,
                quantity
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la mise à jour du panier:', error);
            throw error;
        }
    },

    removeDishFromCart: async (userId, dishId) => {
        try {
            const response = await api.delete(`/cart/${userId}/items/${dishId}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la suppression du plat:', error);
            throw error;
        }
    },

    clearCart: async (userId) => {
        try {
            const response = await api.delete(`/cart/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors du vidage du panier:', error);
            throw error;
        }
    },

    cancelCart: async (userId) => {
        try {
            const response = await api.delete(`/cart/${userId}/cancel`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de l\'annulation du panier:', error);
            throw error;
        }
    }
};

export default cartService;
