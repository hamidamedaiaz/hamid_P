import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {"Content-Type": "application/json"},
    timeout: 10000
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Ne pas logger les erreurs 404 sur les requêtes GET du panier (cas normal quand le panier n'existe pas)
        const isCartNotFound = error.config?.url?.includes('/cart/') &&
                               error.response?.status === 404 &&
                               error.config?.method === 'get';

        if (!isCartNotFound) {
            console.error("API Error:", error.response?.data || error.message);
        }

        return Promise.reject(error);
    }
);

export default api;