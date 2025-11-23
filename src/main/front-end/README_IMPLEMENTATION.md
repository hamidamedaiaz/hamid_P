# 📘 Guide d'Implémentation Frontend - Service Order & Payment

## 🎯 Objectif

Ce guide vous explique **comment implémenter vous-même** le frontend pour le service Order & Payment, étape par étape.

---

## 📋 Table des matières

1. [Prérequis](#prérequis)
2. [Architecture Frontend](#architecture-frontend)
3. [Étape 1 : Configuration de base](#étape-1--configuration-de-base)
4. [Étape 2 : Créer les Services API](#étape-2--créer-les-services-api)
5. [Étape 3 : Créer le Context React](#étape-3--créer-le-context-react)
6. [Étape 4 : Créer les Composants](#étape-4--créer-les-composants)
7. [Étape 5 : Créer les Pages](#étape-5--créer-les-pages)
8. [Étape 6 : Tester l'application](#étape-6--tester-lapplication)
9. [Problèmes courants](#problèmes-courants)

---

## Prérequis

- ✅ Backend Order & Payment Service fonctionnel sur **port 8082**
- ✅ Node.js et npm installés
- ✅ Connaissances de base en React (useState, useEffect, useContext)
- ✅ Connaissances de base en API REST

---

## Architecture Frontend

```
src/
├── services/           # Communication avec le backend
│   ├── api.js         # Configuration Axios
│   ├── cartService.js # Fonctions pour le panier
│   └── orderService.js # Fonctions pour les commandes
│
├── context/           # État global de l'application
│   ├── CartContext.jsx # Gestion du panier
│   └── UserContext.jsx # Gestion de l'utilisateur
│
├── components/        # Composants réutilisables
│   └── cart/
│       ├── Cart.jsx
│       ├── CartItem.jsx
│       └── CartSummary.jsx
│
├── pages/            # Pages principales
│   ├── CartPage.jsx
│   ├── CheckoutPage.jsx
│   └── OrderConfirmationPage.jsx
│
└── App.jsx           # Point d'entrée de l'application
```

---

## Étape 1 : Configuration de base

### 1.1 Vérifier que Axios est installé

```bash
cd src/main/front-end
npm install axios
```

### 1.2 Modifier `src/services/api.js`

**Pourquoi ?** Votre backend tourne sur le port **8082**, pas 8080.

**Fichier : `src/services/api.js`**

```javascript
import axios from "axios";

// Configuration de l'API pour le service Order & Payment
const api = axios.create({
    baseURL: "http://localhost:8082/api", // ⚠️ Port 8082 !
    headers: {"Content-Type": "application/json"},
    timeout: 10000
});

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error:", error.response?.data || error.message);
        return Promise.reject(error);
    }
);

export default api;
```

**✅ Checkpoint** : Votre frontend peut maintenant communiquer avec le backend.

---

## Étape 2 : Créer les Services API

Ces fichiers contiennent les fonctions qui appellent votre backend.

### 2.1 Créer `src/services/cartService.js`

**Pourquoi ?** Pour centraliser tous les appels API liés au panier.

**Ce que vous devez faire :**

1. Créer le fichier `src/services/cartService.js`
2. Importer `api` depuis `./api.js`
3. Créer un objet `cartService` avec les méthodes suivantes :

```javascript
import api from './api';

const cartService = {
    // Ajouter un plat au panier
    addDishToCart: async (userId, dishId, quantity) => {
        const response = await api.post('/cart/items', {
            userId,
            dishId,
            quantity
        });
        return response.data;
    },

    // Récupérer le panier
    getCart: async (userId) => {
        const response = await api.get(`/cart/${userId}`);
        return response.data;
    },

    // Mettre à jour la quantité
    updateCartItem: async (userId, dishId, quantity) => {
        const response = await api.put(`/cart/${userId}/items`, {
            dishId,
            quantity
        });
        return response.data;
    },

    // Supprimer un plat
    removeDishFromCart: async (userId, dishId) => {
        const response = await api.delete(`/cart/${userId}/items/${dishId}`);
        return response.data;
    },

    // Vider le panier
    clearCart: async (userId) => {
        const response = await api.delete(`/cart/${userId}`);
        return response.data;
    }
};

export default cartService;
```

**📝 Explication :**
- Chaque méthode appelle une route de votre backend
- `async/await` pour gérer les requêtes asynchrones
- `response.data` contient la réponse du serveur

**🧪 Comment tester ?**

Créez un fichier de test temporaire :

```javascript
import cartService from './services/cartService';

// Test d'ajout au panier
cartService.addDishToCart('user-123', 'dish-456', 2)
    .then(data => console.log('Succès:', data))
    .catch(err => console.error('Erreur:', err));
```

### 2.2 Créer `src/services/orderService.js`

**Pourquoi ?** Pour centraliser tous les appels API liés aux commandes.

**Ce que vous devez faire :**

```javascript
import api from './api';

const orderService = {
    // Créer une commande
    createOrder: async (userId) => {
        const response = await api.post('/orders', { userId });
        return response.data;
    },

    // Récupérer une commande
    getOrder: async (orderId) => {
        const response = await api.get(`/orders/${orderId}`);
        return response.data;
    },

    // Récupérer toutes les commandes d'un user
    getUserOrders: async (userId) => {
        const response = await api.get(`/orders/user/${userId}`);
        return response.data;
    },

    // Sélectionner un créneau de livraison
    selectDeliverySlot: async (orderId, deliverySlot) => {
        const response = await api.post(`/orders/${orderId}/delivery-slot`, {
            deliverySlot
        });
        return response.data;
    },

    // Effectuer le paiement
    processPayment: async (orderId, paymentData) => {
        const response = await api.post(`/orders/${orderId}/payment`, paymentData);
        return response.data;
    },

    // Confirmer la commande
    confirmOrder: async (orderId) => {
        const response = await api.post(`/orders/${orderId}/confirm`);
        return response.data;
    }
};

export default orderService;
```

**✅ Checkpoint** : Vous avez maintenant tous les appels API nécessaires.

---

## Étape 3 : Créer le Context React

**Pourquoi ?** Pour partager l'état du panier entre tous les composants sans passer les props manuellement.

### 3.1 Créer `src/context/CartContext.jsx`

**Concept :**
- Le Context stocke les données du panier en mémoire
- N'importe quel composant peut accéder/modifier le panier
- Synchronisation avec le backend

**Ce que vous devez faire :**

```javascript
import React, { createContext, useState, useEffect } from 'react';
import cartService from '../services/cartService';

// Créer le contexte
export const CartContext = createContext();

// Provider qui enveloppe l'application
export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([]);
    const [userId, setUserId] = useState('user-123'); // TODO: Remplacer par vraie auth
    const [loading, setLoading] = useState(false);

    // Charger le panier au démarrage
    useEffect(() => {
        loadCart();
    }, [userId]);

    // Charger le panier depuis le backend
    const loadCart = async () => {
        try {
            setLoading(true);
            const data = await cartService.getCart(userId);
            setCartItems(data.items || []);
        } catch (error) {
            console.error('Erreur chargement panier:', error);
        } finally {
            setLoading(false);
        }
    };

    // Ajouter un plat
    const addToCart = async (dishId, quantity = 1) => {
        try {
            await cartService.addDishToCart(userId, dishId, quantity);
            await loadCart(); // Recharger pour avoir les données à jour
        } catch (error) {
            console.error('Erreur ajout panier:', error);
            throw error;
        }
    };

    // Mettre à jour la quantité
    const updateQuantity = async (dishId, quantity) => {
        try {
            await cartService.updateCartItem(userId, dishId, quantity);
            await loadCart();
        } catch (error) {
            console.error('Erreur mise à jour:', error);
            throw error;
        }
    };

    // Supprimer un plat
    const removeFromCart = async (dishId) => {
        try {
            await cartService.removeDishFromCart(userId, dishId);
            await loadCart();
        } catch (error) {
            console.error('Erreur suppression:', error);
            throw error;
        }
    };

    // Vider le panier
    const clearCart = async () => {
        try {
            await cartService.clearCart(userId);
            setCartItems([]);
        } catch (error) {
            console.error('Erreur vidage panier:', error);
            throw error;
        }
    };

    // Calculer le total
    const getTotalAmount = () => {
        return cartItems.reduce((total, item) => {
            return total + (item.price * item.quantity);
        }, 0);
    };

    const getTotalItems = () => {
        return cartItems.reduce((total, item) => total + item.quantity, 0);
    };

    // Valeur fournie à tous les composants
    const value = {
        cartItems,
        userId,
        loading,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        loadCart,
        getTotalAmount,
        getTotalItems
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};
```

**📝 Explication :**
- `useState` : stocke les données du panier
- `useEffect` : charge le panier au démarrage
- `CartContext.Provider` : rend les données disponibles à tous les enfants

### 3.2 Envelopper l'application avec le Provider

**Fichier : `src/main.jsx` ou `src/App.jsx`**

```javascript
import { CartProvider } from './context/CartContext';

// Dans votre composant principal
<CartProvider>
    <App />
</CartProvider>
```

**✅ Checkpoint** : Le panier est maintenant accessible partout dans l'application.

---

## Étape 4 : Créer les Composants

### 4.1 Corriger `src/components/cart/Cart.jsx`

**Problème actuel :** `useContext()` est appelé sans paramètre.

**Solution :**

```javascript
import React, { useContext } from 'react';
import { CartContext } from '../../context/CartContext'; // ⚠️ Important
import './Cart.css';

export default function Cart() {
    const { 
        cartItems, 
        removeFromCart, 
        updateQuantity,
        getTotalAmount,
        loading 
    } = useContext(CartContext); // ✅ Passer le contexte

    if (loading) {
        return <div>Chargement du panier...</div>;
    }

    if (!cartItems || cartItems.length === 0) {
        return <div>Votre panier est vide</div>;
    }

    return (
        <div className="cart">
            <h2>Mon Panier</h2>
            {cartItems.map((item) => (
                <div key={item.dishId} className="cart-item">
                    <h3>{item.dishName}</h3>
                    <p>Prix: {item.price}€</p>
                    <input 
                        type="number" 
                        value={item.quantity}
                        onChange={(e) => updateQuantity(item.dishId, parseInt(e.target.value))}
                        min="1"
                    />
                    <button onClick={() => removeFromCart(item.dishId)}>
                        Supprimer
                    </button>
                </div>
            ))}
            <div className="cart-total">
                <strong>Total: {getTotalAmount()}€</strong>
            </div>
        </div>
    );
}
```

### 4.2 Créer `src/components/cart/CartSummary.jsx`

**Pourquoi ?** Pour afficher un résumé rapide du panier (nombre d'articles, total).

```javascript
import React, { useContext } from 'react';
import { CartContext } from '../../context/CartContext';
import { useNavigate } from 'react-router-dom';

export default function CartSummary() {
    const { getTotalItems, getTotalAmount, cartItems } = useContext(CartContext);
    const navigate = useNavigate();

    const handleCheckout = () => {
        navigate('/checkout');
    };

    if (!cartItems || cartItems.length === 0) {
        return null;
    }

    return (
        <div className="cart-summary">
            <p>Articles: {getTotalItems()}</p>
            <p>Total: {getTotalAmount().toFixed(2)}€</p>
            <button onClick={handleCheckout}>
                Passer commande
            </button>
        </div>
    );
}
```

**✅ Checkpoint** : Le panier peut être affiché et modifié.

---

## Étape 5 : Créer les Pages

### 5.1 Page du Panier - `src/pages/CartPage.jsx`

```javascript
import React, { useContext } from 'react';
import { CartContext } from '../context/CartContext';
import Cart from '../components/cart/Cart';
import { useNavigate } from 'react-router-dom';

export default function CartPage() {
    const { cartItems, getTotalAmount, clearCart } = useContext(CartContext);
    const navigate = useNavigate();

    const handleProceedToCheckout = async () => {
        // Créer la commande et rediriger
        navigate('/checkout');
    };

    return (
        <div className="cart-page">
            <h1>Mon Panier</h1>
            <Cart />
            
            {cartItems.length > 0 && (
                <div className="cart-actions">
                    <button onClick={clearCart}>Vider le panier</button>
                    <button onClick={handleProceedToCheckout} className="btn-primary">
                        Valider ({getTotalAmount().toFixed(2)}€)
                    </button>
                </div>
            )}
        </div>
    );
}
```

### 5.2 Page Checkout - `src/pages/CheckoutPage.jsx`

**Cette page gère :**
- Création de la commande
- Sélection du créneau horaire
- Paiement
- Confirmation

```javascript
import React, { useState, useContext, useEffect } from 'react';
import { CartContext } from '../context/CartContext';
import orderService from '../services/orderService';
import { useNavigate } from 'react-router-dom';

export default function CheckoutPage() {
    const { userId, getTotalAmount } = useContext(CartContext);
    const [orderId, setOrderId] = useState(null);
    const [deliverySlot, setDeliverySlot] = useState('');
    const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
    const [step, setStep] = useState(1); // 1: créer ordre, 2: créneau, 3: paiement
    const navigate = useNavigate();

    // Étape 1 : Créer la commande
    useEffect(() => {
        createOrder();
    }, []);

    const createOrder = async () => {
        try {
            const order = await orderService.createOrder(userId);
            setOrderId(order.id);
            setStep(2);
        } catch (error) {
            console.error('Erreur création commande:', error);
        }
    };

    // Étape 2 : Sélectionner le créneau
    const handleSelectSlot = async () => {
        try {
            await orderService.selectDeliverySlot(orderId, deliverySlot);
            setStep(3);
        } catch (error) {
            console.error('Erreur sélection créneau:', error);
        }
    };

    // Étape 3 : Payer
    const handlePayment = async () => {
        try {
            await orderService.processPayment(orderId, {
                method: paymentMethod,
                amount: getTotalAmount()
            });
            
            // Confirmer la commande
            await orderService.confirmOrder(orderId);
            
            // Rediriger vers la page de confirmation
            navigate(`/order-confirmation/${orderId}`);
        } catch (error) {
            console.error('Erreur paiement:', error);
        }
    };

    return (
        <div className="checkout-page">
            <h1>Finaliser la commande</h1>

            {step === 1 && <p>Création de la commande...</p>}

            {step === 2 && (
                <div className="delivery-slot">
                    <h2>Sélectionner un créneau horaire</h2>
                    <input 
                        type="datetime-local" 
                        value={deliverySlot}
                        onChange={(e) => setDeliverySlot(e.target.value)}
                    />
                    <button onClick={handleSelectSlot}>Continuer</button>
                </div>
            )}

            {step === 3 && (
                <div className="payment">
                    <h2>Paiement</h2>
                    <p>Total: {getTotalAmount().toFixed(2)}€</p>
                    <select 
                        value={paymentMethod}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                    >
                        <option value="CREDIT_CARD">Carte bancaire</option>
                        <option value="PAYPAL">PayPal</option>
                    </select>
                    <button onClick={handlePayment}>Payer</button>
                </div>
            )}
        </div>
    );
}
```

### 5.3 Page Confirmation - `src/pages/OrderConfirmationPage.jsx`

```javascript
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import orderService from '../services/orderService';

export default function OrderConfirmationPage() {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);

    useEffect(() => {
        loadOrder();
    }, [orderId]);

    const loadOrder = async () => {
        try {
            const data = await orderService.getOrder(orderId);
            setOrder(data);
        } catch (error) {
            console.error('Erreur chargement commande:', error);
        }
    };

    if (!order) {
        return <div>Chargement...</div>;
    }

    return (
        <div className="order-confirmation">
            <h1>✅ Commande confirmée !</h1>
            <div className="order-details">
                <p>Numéro de commande: {order.id}</p>
                <p>Total: {order.totalAmount}€</p>
                <p>Livraison prévue: {order.deliverySlot}</p>
                <p>Statut: {order.status}</p>
            </div>
        </div>
    );
}
```

**✅ Checkpoint** : Le flux complet est implémenté.

---

## Étape 6 : Tester l'application

### 6.1 Lancer le backend

```bash
# Depuis IntelliJ IDEA
Run OrderPaymentServiceApplication.java

# Vérifier que le serveur démarre sur port 8082
```

### 6.2 Lancer le frontend

```bash
cd src/main/front-end
npm install
npm run dev
```

Le frontend démarre sur `http://localhost:5173`

### 6.3 Tester avec Postman d'abord

Avant de tester le frontend, vérifiez que le backend fonctionne :

```
POST http://localhost:8082/api/cart/items
Body: {
  "userId": "user-123",
  "dishId": "dish-456",
  "quantity": 2
}
```

### 6.4 Tester le frontend

1. Ouvrir la console du navigateur (F12)
2. Vérifier les requêtes dans l'onglet "Network"
3. Tester le flux complet :
   - Ajouter un plat au panier
   - Voir le panier
   - Modifier la quantité
   - Passer commande
   - Sélectionner créneau
   - Payer
   - Voir la confirmation

---

## Problèmes courants

### ❌ Erreur CORS

**Symptôme :** 
```
Access to XMLHttpRequest at 'http://localhost:8082/api/cart/items' from origin 'http://localhost:5173' has been blocked by CORS policy
```

**Solution :**

Dans votre backend Java, ajoutez un filtre CORS :

```java
// Dans votre ApiRegistry ou Handler
exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
```

### ❌ Erreur "useContext() requires a context"

**Solution :** Vérifiez que vous importez ET passez le contexte :

```javascript
import { CartContext } from '../context/CartContext';
const { cartItems } = useContext(CartContext); // ✅
```

### ❌ Le panier ne se charge pas

**Solution :**
1. Vérifier que le backend répond (Postman)
2. Vérifier la console du navigateur
3. Vérifier que `userId` est correct
4. Vérifier que le `baseURL` dans `api.js` est correct (port 8082)

---

## 🎉 Félicitations !

Vous avez maintenant un frontend complet connecté à votre backend Order & Payment Service.

**Prochaines étapes :**
- Améliorer le design avec CSS
- Ajouter des animations de chargement
- Gérer les erreurs utilisateur
- Ajouter des tests unitaires

---

## 📚 Ressources

- [React Context API](https://react.dev/reference/react/useContext)
- [Axios Documentation](https://axios-http.com/docs/intro)
- [React Router](https://reactrouter.com/)

