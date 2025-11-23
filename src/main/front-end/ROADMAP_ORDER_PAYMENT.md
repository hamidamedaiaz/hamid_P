# 🚀 ROADMAP - Frontend Order & Payment Service

## 📋 Vue d'ensemble

Ce document explique **étape par étape** comment connecter le frontend React au backend Order & Payment Service pour permettre à un utilisateur de **passer une commande complète de A à Z**.

---

## 🎯 Objectif Final

Permettre à l'utilisateur de :
1. ✅ Parcourir les restaurants et leurs plats
2. ✅ Ajouter des plats au panier
3. ✅ Modifier/Supprimer des articles du panier
4. ✅ Sélectionner un créneau horaire de livraison
5. ✅ Valider la commande
6. ✅ Effectuer le paiement (mocké)
7. ✅ Confirmer la commande
8. ✅ Voir la confirmation de commande

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (React + Vite)                  │
│                   http://localhost:5173                      │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Pages      │  │  Components  │  │   Context    │      │
│  │              │  │              │  │              │      │
│  │ - HomePage   │  │ - Cart       │  │ - CartContext│      │
│  │ - CartPage   │  │ - CartItem   │  │ - UserContext│      │
│  │ - Checkout   │  │ - Payment    │  │              │      │
│  │ - Confirm    │  │              │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
│         └──────────────────┴──────────────────┘              │
│                            │                                 │
│                  ┌─────────▼─────────┐                       │
│                  │     Services      │                       │
│                  │                   │                       │
│                  │ - cartService.js  │                       │
│                  │ - orderService.js │                       │
│                  │ - api.js          │                       │
│                  └─────────┬─────────┘                       │
└────────────────────────────┼─────────────────────────────────┘
                             │
                             │ HTTP/REST (Axios)
                             │
┌────────────────────────────▼─────────────────────────────────┐
│              BACKEND - Order & Payment Service               │
│                   http://localhost:8082                      │
│                                                              │
│  Routes disponibles :                                        │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ CART MANAGEMENT                                        │ │
│  │ POST   /api/cart/items           - Ajouter un plat     │ │
│  │ GET    /api/cart/{userId}        - Voir le panier      │ │
│  │ PUT    /api/cart/{userId}/items  - Modifier quantité   │ │
│  │ DELETE /api/cart/{userId}/items/{dishId} - Supprimer   │ │
│  │ DELETE /api/cart/{userId}        - Vider le panier     │ │
│  │ DELETE /api/cart/{userId}/cancel - Annuler le panier   │ │
│  │                                                         │ │
│  │ ORDER MANAGEMENT                                        │ │
│  │ POST   /api/orders               - Créer une commande  │ │
│  │ GET    /api/orders/{id}          - Détails commande    │ │
│  │ GET    /api/orders/user/{userId} - Commandes user      │ │
│  │ POST   /api/orders/{id}/delivery-slot - Créneau        │ │
│  │ POST   /api/orders/{id}/payment  - Paiement            │ │
│  │ POST   /api/orders/{id}/confirm  - Confirmer           │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

---

## 📦 Flux de données - Passer une commande

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUX COMPLET D'UNE COMMANDE                  │
└─────────────────────────────────────────────────────────────────┘

1️⃣ PARCOURIR LES RESTAURANTS
   User → HomePage → restaurantService.getRestaurants()
   ↓
   Affichage des restaurants disponibles

2️⃣ SÉLECTIONNER UN RESTAURANT
   User clique sur restaurant → RestaurantDetailPage
   ↓
   restaurantService.getRestaurantById(id)
   ↓
   Affichage des plats (menu)

3️⃣ AJOUTER DES PLATS AU PANIER
   User clique "Ajouter au panier"
   ↓
   CartContext.addToCart(dish)
   ↓
   cartService.addDishToCart(userId, dishId, quantity)
   ↓
   Backend: POST /api/cart/items
   ↓
   Mise à jour du state React

4️⃣ VOIR/MODIFIER LE PANIER
   User → CartPage
   ↓
   cartService.getCart(userId)
   ↓
   Backend: GET /api/cart/{userId}
   ↓
   Affichage du panier avec :
   - Liste des articles
   - Quantités modifiables (PUT /api/cart/{userId}/items)
   - Bouton supprimer (DELETE /api/cart/{userId}/items/{dishId})
   - Total

5️⃣ VALIDER LE PANIER → CRÉER LA COMMANDE
   User clique "Passer commande"
   ↓
   orderService.createOrder(userId)
   ↓
   Backend: POST /api/orders (convertit le panier en commande)
   ↓
   Redirection vers CheckoutPage avec orderId

6️⃣ SÉLECTIONNER CRÉNEAU DE LIVRAISON
   CheckoutPage affiche les créneaux disponibles
   ↓
   orderService.selectDeliverySlot(orderId, timeSlot)
   ↓
   Backend: POST /api/orders/{id}/delivery-slot
   ↓
   Créneau enregistré

7️⃣ PAIEMENT (MOCKÉ)
   User clique "Payer"
   ↓
   orderService.processPayment(orderId, paymentMethod)
   ↓
   Backend: POST /api/orders/{id}/payment
   ↓
   Simulation du paiement (toujours success)

8️⃣ CONFIRMATION
   orderService.confirmOrder(orderId)
   ↓
   Backend: POST /api/orders/{id}/confirm
   ↓
   Commande confirmée → Redirection OrderConfirmationPage
   ↓
   Affichage récapitulatif (numéro commande, total, créneau, etc.)
```

---

## 🛠️ Fichiers à créer/modifier

### ✅ Services API (Backend communication)

| Fichier | Status | Description |
|---------|--------|-------------|
| `src/services/api.js` | ⚠️ À MODIFIER | Configuration Axios (changer port 8080 → 8082) |
| `src/services/cartService.js` | ❌ VIDE | Appels API pour gestion du panier |
| `src/services/orderService.js` | ❌ VIDE | Appels API pour gestion des commandes |
| `src/services/restaurantService.js` | ❌ VIDE | Appels API pour les restaurants (si nécessaire) |

### ✅ Context React (State Management)

| Fichier | Status | Description |
|---------|--------|-------------|
| `src/context/CartContext.jsx` | ❌ VIDE | État global du panier |
| `src/context/UserContext.jsx` | ⚠️ À VÉRIFIER | État de l'utilisateur connecté |

### ✅ Components

| Fichier | Status | Description |
|---------|--------|-------------|
| `src/components/cart/Cart.jsx` | ⚠️ BUG | Manque le contexte dans useContext() |
| `src/components/cart/CartSummary.js` | ❌ VIDE | Résumé du panier |
| `src/components/order/...` | ⚠️ À VÉRIFIER | Composants de commande |

### ✅ Pages

| Fichier | Status | Description |
|---------|--------|-------------|
| `src/pages/CartPage.jsx` | ⚠️ À VÉRIFIER | Page du panier |
| `src/pages/CheckoutPage.jsx` | ⚠️ À VÉRIFIER | Page de validation (créneau + paiement) |
| `src/pages/OrderConfirmationPage.jsx` | ⚠️ À VÉRIFIER | Page de confirmation |

---

## 🔧 Étapes d'implémentation

### ÉTAPE 1 : Configuration de base ✅

**Fichier : `src/services/api.js`**
- ✅ Changer baseURL de `http://localhost:8080` → `http://localhost:8082`
- ✅ Ajouter gestion des erreurs globales
- ✅ Ajouter intercepteurs si nécessaire

### ÉTAPE 2 : Services API ✅

**Fichier : `src/services/cartService.js`**
Implémenter :
- `addDishToCart(userId, dishId, quantity)` → POST /api/cart/items
- `getCart(userId)` → GET /api/cart/{userId}
- `updateCartItem(userId, dishId, quantity)` → PUT /api/cart/{userId}/items
- `removeDishFromCart(userId, dishId)` → DELETE /api/cart/{userId}/items/{dishId}
- `clearCart(userId)` → DELETE /api/cart/{userId}

**Fichier : `src/services/orderService.js`**
Implémenter :
- `createOrder(userId)` → POST /api/orders
- `getOrder(orderId)` → GET /api/orders/{id}
- `getUserOrders(userId)` → GET /api/orders/user/{userId}
- `selectDeliverySlot(orderId, timeSlot)` → POST /api/orders/{id}/delivery-slot
- `processPayment(orderId, paymentData)` → POST /api/orders/{id}/payment
- `confirmOrder(orderId)` → POST /api/orders/{id}/confirm

### ÉTAPE 3 : Context React ✅

**Fichier : `src/context/CartContext.jsx`**
- État : `cartItems`, `totalAmount`, `userId`
- Actions : `addToCart`, `removeFromCart`, `updateQuantity`, `clearCart`, `loadCart`
- Synchronisation avec le backend via cartService

### ÉTAPE 4 : Composants ✅

**Fichier : `src/components/cart/Cart.jsx`**
- Corriger : `useContext()` → `useContext(CartContext)`
- Afficher les articles du panier
- Boutons : modifier quantité, supprimer, vider panier

**Fichier : `src/components/cart/CartSummary.js`**
- Afficher le résumé (total items, total prix)
- Bouton "Passer commande"

### ÉTAPE 5 : Pages ✅

**CartPage** : Affiche le panier complet
**CheckoutPage** : Sélection créneau + paiement
**OrderConfirmationPage** : Récapitulatif commande

### ÉTAPE 6 : Configuration CORS (Backend) ⚠️

**IMPORTANT** : Votre backend doit autoriser les requêtes depuis le frontend !

Dans votre backend Java, ajoutez un filtre CORS pour accepter `http://localhost:5173`

---

## 🚦 Relation avec les autres tâches

### ❌ NE PAS TOUCHER (autres développeurs)

| Task | Responsable | Description |
|------|-------------|-------------|
| **Service Restaurant** (#186) | Autre dev | Gestion des restaurants, menus, plats |
| **API Gateway** (#188) | Autre dev | Point d'entrée centralisé |

### ✅ CE QUE VOUS UTILISEZ des autres services

**Du Service Restaurant :**
- Liste des restaurants
- Détails d'un restaurant
- Liste des plats d'un restaurant

**Comment ?**
- Si le Service Restaurant est sur un autre port (ex: 8081), créez `restaurantService.js` qui pointe vers ce port
- Ou utilisez l'API Gateway si elle existe déjà

---

## 🧪 Tests à effectuer

### Test Postman (Backend seul)

```
Collection : Order & Payment Service Tests

1. Ajouter un plat au panier
   POST http://localhost:8082/api/cart/items
   Body: {
     "userId": "user-123",
     "dishId": "dish-456",
     "quantity": 2
   }

2. Voir le panier
   GET http://localhost:8082/api/cart/user-123

3. Modifier quantité
   PUT http://localhost:8082/api/cart/user-123/items
   Body: {
     "dishId": "dish-456",
     "quantity": 3
   }

4. Créer une commande
   POST http://localhost:8082/api/orders
   Body: {
     "userId": "user-123"
   }

5. Sélectionner créneau
   POST http://localhost:8082/api/orders/{orderId}/delivery-slot
   Body: {
     "deliverySlot": "2025-01-20T12:00:00"
   }

6. Payer
   POST http://localhost:8082/api/orders/{orderId}/payment
   Body: {
     "method": "CREDIT_CARD",
     "amount": 25.50
   }

7. Confirmer
   POST http://localhost:8082/api/orders/{orderId}/confirm
```

### Test Frontend + Backend

1. Lancer le backend : Run `OrderPaymentServiceApplication.java`
2. Lancer le frontend : `cd src/main/front-end && npm run dev`
3. Ouvrir `http://localhost:5173`
4. Tester le flux complet

---

## 📝 Checklist finale

- [ ] Backend démarré sur port 8082
- [ ] Frontend démarré sur port 5173
- [ ] CORS configuré dans le backend
- [ ] `api.js` pointe vers le bon port
- [ ] `cartService.js` implémenté
- [ ] `orderService.js` implémenté
- [ ] `CartContext.jsx` implémenté
- [ ] `Cart.jsx` corrigé (useContext)
- [ ] Flux complet testé (ajout panier → commande → paiement → confirmation)

---

## 🚀 Commandes de lancement

### Backend
```bash
# Depuis IntelliJ IDEA
Run OrderPaymentServiceApplication.java

# Ou via Maven
mvn clean install
mvn exec:java -Dexec.mainClass="fr.unice.polytech.sophiatecheats.OrderPaymentServiceApplication"
```

### Frontend
```bash
cd src/main/front-end
npm install
npm run dev
```

Votre application est prête ! 🎉

