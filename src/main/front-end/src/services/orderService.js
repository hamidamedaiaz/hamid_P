import api from './api';

/**
 * Service pour gérer les commandes (Orders) et le paiement
 * Communique avec le backend Order & Payment Service
 */
const orderService = {
    /**
     * Créer une commande à partir du panier
     * @param {string} userId - ID de l'utilisateur
     */
    createOrder: async (userId) => {
        try {
            const response = await api.post('/orders', { userId });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la création de la commande:', error);
            throw error;
        }
    },

    /**
     * Récupérer les détails d'une commande
     * @param {string} orderId - ID de la commande
     */
    getOrder: async (orderId) => {
        try {
            const response = await api.get(`/orders/${orderId}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération de la commande:', error);
            throw error;
        }
    },

    /**
     * Récupérer toutes les commandes d'un utilisateur
     * @param {string} userId - ID de l'utilisateur
     */
    getUserOrders: async (userId) => {
        try {
            const response = await api.get(`/orders/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des commandes:', error);
            throw error;
        }
    },

    /**
     * Sélectionner un créneau de livraison
     * @param {string} orderId - ID de la commande
     * @param {string} deliverySlot - Créneau horaire (format ISO 8601)
     */
    selectDeliverySlot: async (orderId, deliverySlot) => {
        try {
            const response = await api.post(`/orders/${orderId}/delivery-slot`, {
                deliverySlot
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la sélection du créneau:', error);
            throw error;
        }
    },

    /**
     * Effectuer le paiement d'une commande (mocké)
     * @param {string} orderId - ID de la commande
     * @param {object} paymentData - Données de paiement (method, amount, etc.)
     */
    processPayment: async (orderId, paymentData) => {
        try {
            const response = await api.post(`/orders/${orderId}/payment`, paymentData);
            return response.data;
        } catch (error) {
            console.error('Erreur lors du paiement:', error);
            throw error;
        }
    },

    /**
     * Confirmer la commande après paiement
     * @param {string} orderId - ID de la commande
     */
    confirmOrder: async (orderId) => {
        try {
            const response = await api.post(`/orders/${orderId}/confirm`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la confirmation:', error);
            throw error;
        }
    }
};

export default orderService;

