# 🛒 ROADMAP - Passer une Commande de A à Z

## 📋 Ce qui EXISTE déjà dans le Backend

### ✅ Service Order & Payment (Port 8082)
Votre backend est **DÉJÀ COMPLET** avec toutes les routes nécessaires :

#### Routes Cart (Panier)
- `POST /api/cart/items` - Ajouter un plat au panier
- `GET /api/cart/{userId}` - Récupérer le panier d'un utilisateur
- `PUT /api/cart/{userId}/items` - Mettre à jour la quantité d'un plat
- `DELETE /api/cart/{userId}/items/{dishId}` - Supprimer un plat du panier
- `DELETE /api/cart/{userId}` - Vider le panier
- `DELETE /api/cart/{userId}/cancel` - Annuler le panier

#### Routes Order (Commande)
- `POST /api/orders` - Créer une commande depuis le panier
- `GET /api/orders/{id}` - Récupérer une commande
- `GET /api/orders/user/{userId}` - Récupérer toutes les commandes d'un user
- `POST /api/orders/{id}/delivery-slot` - Sélectionner un créneau de livraison
- `POST /api/orders/{id}/payment` - Effectuer le paiement (mocké)
- `POST /api/orders/{id}/confirm` - Confirmer la commande

### ✅ Services Frontend déjà créés
- `cartService.js` - Toutes les fonctions pour gérer le panier
- `orderService.js` - Toutes les fonctions pour gérer les commandes
- `api.js` - Configuration Axios pointant sur http://localhost:8082/api

---

## 🎯 Ce qu'il FAUT CRÉER dans le Frontend

### Le flux complet est :
1. **Parcourir les restaurants** → Déjà fait (RestaurantsPage.jsx existe)
2. **Voir les plats d'un restaurant** → Déjà fait (RestaurantDetailPage.jsx existe)
3. **Ajouter des plats au panier** → À CRÉER
4. **Visualiser le panier** → CartPage.jsx existe mais à vérifier/améliorer
5. **Passer à la commande (Checkout)** → CheckoutPage.jsx existe mais à vérifier/améliorer
6. **Sélectionner un créneau horaire** → À CRÉER dans CheckoutPage
7. **Effectuer le paiement (mocké)** → À CRÉER dans CheckoutPage
8. **Confirmer la commande** → À CRÉER
9. **Voir la confirmation** → OrderConfirmationPage.jsx existe mais à vérifier

---

## 📝 PLAN D'ACTION DÉTAILLÉ

### ÉTAPE 1 : Comprendre le flux de données

#### A. Comment fonctionne l'ajout au panier ?
```
User clique sur "Ajouter" → 
Frontend appelle cartService.addDishToCart(userId, dishId, quantity) →
API POST http://localhost:8082/api/cart/items →
Backend retourne une AddDishToCartResponse avec :
  - cartId
  - totalItems
  - totalAmount
  - success
```

#### B. Comment fonctionne la création de commande ?
```
User a un panier rempli →
User clique "Passer commande" →
Frontend appelle orderService.createOrder(userId) →
API POST http://localhost:8082/api/orders →
Backend crée une Order depuis le panier actif →
Backend retourne l'orderId
```

#### C. Comment fonctionne le paiement complet ?
```
Commande créée →
User sélectionne un créneau horaire →
Frontend appelle orderService.selectDeliverySlot(orderId, deliverySlot) →
User procède au paiement →
Frontend appelle orderService.processPayment(orderId, paymentData) →
Frontend appelle orderService.confirmOrder(orderId) →
Commande CONFIRMÉE → Affichage page de confirmation
```

---

### ÉTAPE 2 : Vérifier et améliorer les pages existantes

#### Page 1 : RestaurantDetailPage.jsx
**Ce qu'elle doit faire :**
- Afficher les plats du restaurant avec leurs détails
- Pour chaque plat, avoir un bouton "Ajouter au panier"
- Le bouton doit appeler `cartService.addDishToCart(userId, dish.id, 1)`
- Afficher un message de succès/erreur après l'ajout

**Actions à faire :**
1. Ouvrir le fichier `src/main/front-end/src/pages/RestaurantDetailPage.jsx`
2. Vérifier s'il y a un bouton "Ajouter au panier" sur chaque plat
3. Si non → Ajouter un composant Button avec onClick qui appelle cartService
4. Gérer le userId (soit depuis un Context UserContext, soit hardcodé pour le test)
5. Afficher un feedback visuel (toast, message) après l'ajout

---

#### Page 2 : CartPage.jsx
**Ce qu'elle doit faire :**
- Afficher tous les items du panier avec :
  - Nom du plat
  - Prix unitaire
  - Quantité (avec possibilité de modifier)
  - Sous-total
- Bouton "+" / "-" pour changer la quantité → appelle `cartService.updateCartItem()`
- Bouton "Supprimer" pour retirer un plat → appelle `cartService.removeDishFromCart()`
- Afficher le TOTAL général
- Bouton "Vider le panier" → appelle `cartService.clearCart()`
- Bouton "Passer la commande" → redirige vers CheckoutPage

**Actions à faire :**
1. Ouvrir `src/main/front-end/src/pages/CartPage.jsx`
2. Vérifier qu'elle appelle `cartService.getCart(userId)` au chargement
3. Vérifier qu'elle affiche correctement les items
4. Ajouter les boutons d'action (modifier quantité, supprimer item)
5. Ajouter le bouton "Passer la commande" qui navigue vers `/checkout`

---

#### Page 3 : CheckoutPage.jsx
**C'est la page la PLUS IMPORTANTE - Le cœur de votre task**

**Ce qu'elle doit faire :**
1. **Étape 1 : Récapitulatif du panier**
   - Afficher le résumé des plats commandés
   - Afficher le total

2. **Étape 2 : Créer la commande**
   - Appeler `orderService.createOrder(userId)`
   - Récupérer l'orderId retourné
   - Stocker cet orderId dans le state

3. **Étape 3 : Sélection du créneau horaire**
   - Afficher une liste de créneaux disponibles
   - User sélectionne un créneau
   - Appeler `orderService.selectDeliverySlot(orderId, selectedSlot)`

4. **Étape 4 : Paiement (mocké)**
   - Afficher un formulaire de paiement simple (même fictif)
   - User clique "Payer"
   - Appeler `orderService.processPayment(orderId, { method: "CARD", amount: total })`

5. **Étape 5 : Confirmation**
   - Appeler `orderService.confirmOrder(orderId)`
   - Rediriger vers `/order-confirmation?orderId={orderId}`

**Actions à faire :**
1. Ouvrir `src/main/front-end/src/pages/CheckoutPage.jsx`
2. Créer un state pour gérer les étapes : `[currentStep, setCurrentStep]`
3. Créer un state pour l'orderId : `[orderId, setOrderId]`
4. Créer un state pour le créneau : `[selectedSlot, setSelectedSlot]`
5. Implémenter chaque étape avec ses appels API
6. Gérer les erreurs et afficher des messages appropriés

---

#### Page 4 : OrderConfirmationPage.jsx
**Ce qu'elle doit faire :**
- Récupérer l'orderId depuis les paramètres URL
- Appeler `orderService.getOrder(orderId)`
- Afficher :
  - Numéro de commande
  - Détails des plats
  - Créneau de livraison
  - Montant payé
  - Statut de la commande
  - Message de confirmation

**Actions à faire :**
1. Ouvrir `src/main/front-end/src/pages/OrderConfirmationPage.jsx`
2. Utiliser `useSearchParams()` ou `useParams()` pour récupérer orderId
3. Appeler l'API pour récupérer les détails
4. Afficher toutes les informations de manière claire

---

### ÉTAPE 3 : Créer les composants manquants

#### Composant : AddToCartButton
**Localisation :** `src/main/front-end/src/components/cart/AddToCartButton.jsx`

**Responsabilité :**
- Bouton réutilisable pour ajouter un plat au panier
- Gère l'état de chargement pendant l'appel API
- Affiche un feedback visuel

**Ce qu'il contient :**
- Props : `dishId`, `userId`, `onSuccess`, `onError`
- State : `loading`
- Function : `handleAddToCart()` qui appelle `cartService.addDishToCart()`

---

#### Composant : CartItem
**Localisation :** `src/main/front-end/src/components/cart/CartItem.jsx`

**Responsabilité :**
- Afficher un item du panier
- Permettre de modifier la quantité
- Permettre de supprimer l'item

**Ce qu'il contient :**
- Props : `item`, `onUpdate`, `onRemove`
- Affichage : nom, prix, quantité, sous-total
- Boutons : +, -, supprimer

---

#### Composant : DeliverySlotSelector
**Localisation :** `src/main/front-end/src/components/order/DeliverySlotSelector.jsx`

**Responsabilité :**
- Afficher une liste de créneaux horaires disponibles
- Permettre de sélectionner un créneau
- Formater les dates/heures de manière lisible

**Ce qu'il contient :**
- Props : `slots`, `selectedSlot`, `onSelect`
- Liste de boutons ou radio buttons pour choisir
- Format : "Lundi 18 Nov - 12:00 - 13:00"

---

#### Composant : PaymentForm
**Localisation :** `src/main/front-end/src/components/order/PaymentForm.jsx`

**Responsabilité :**
- Formulaire de paiement mocké (pas de vraie validation)
- Simuler un paiement

**Ce qu'il contient :**
- Champs fictifs : numéro de carte, nom, date expiration, CVV
- Bouton "Payer"
- Message "Paiement sécurisé (simulation)"

---

### ÉTAPE 4 : Gérer le Context Utilisateur

#### UserContext
**Localisation :** `src/main/front-end/src/context/UserContext.jsx` (existe déjà)

**Vérifier :**
- Est-ce qu'il fournit un `userId` ?
- Si non, ajouter un userId hardcodé pour les tests
- Exemple : `const userId = "550e8400-e29b-41d4-a716-446655440000"`

**Actions :**
1. Ouvrir le fichier UserContext.jsx
2. Vérifier s'il exporte un userId
3. Si non, ajouter un userId de test
4. Utiliser ce Context dans toutes les pages

---

### ÉTAPE 5 : Tester le flux complet

#### Test 1 : Ajouter au panier
1. Lancer le backend : `OrderPaymentServiceApplication.java`
2. Lancer le frontend : `npm run dev` dans `src/main/front-end/`
3. Aller sur la page des restaurants
4. Cliquer sur un restaurant
5. Cliquer sur "Ajouter au panier" sur un plat
6. Vérifier que le panier est mis à jour

#### Test 2 : Voir le panier
1. Aller sur `/cart`
2. Vérifier que les plats ajoutés sont affichés
3. Tester la modification de quantité
4. Tester la suppression d'un item
5. Vérifier que le total est correct

#### Test 3 : Passer une commande complète
1. Depuis le panier, cliquer "Passer la commande"
2. Vérifier le récapitulatif
3. Créer la commande (automatique)
4. Sélectionner un créneau horaire
5. Remplir le formulaire de paiement
6. Confirmer
7. Voir la page de confirmation avec tous les détails

---

## 🔧 Outils de Test

### Postman (pour tester le backend seul)
Collections de tests déjà disponibles dans :
`src/test/resources/SophiaTechEats.postman_collection.json`

### Console du navigateur
- Ouvrir F12 → Console
- Vérifier les appels API dans l'onglet Network
- Vérifier les erreurs JavaScript

### React DevTools
- Installer l'extension React DevTools
- Voir l'état des composants en temps réel

---

## 🚀 Commandes pour lancer le projet

### Backend
```bash
# Depuis IntelliJ ou terminal
# Compiler
mvn clean package

# Lancer le service Order & Payment
# Clic droit sur OrderPaymentServiceApplication.java → Run
# OU en ligne de commande :
java -cp target/classes fr.unice.polytech.sophiatecheats.OrderPaymentServiceApplication
```

### Frontend
```bash
cd src/main/front-end
npm install
npm run dev
```

Le frontend sera accessible sur : http://localhost:5173
Le backend sur : http://localhost:8082

---

## 📊 Résumé des fichiers à modifier/créer

### À VÉRIFIER ET AMÉLIORER
- ✏️ `src/main/front-end/src/pages/RestaurantDetailPage.jsx`
- ✏️ `src/main/front-end/src/pages/CartPage.jsx`
- ✏️ `src/main/front-end/src/pages/CheckoutPage.jsx`
- ✏️ `src/main/front-end/src/pages/OrderConfirmationPage.jsx`
- ✏️ `src/main/front-end/src/context/UserContext.jsx`

### À CRÉER
- ➕ `src/main/front-end/src/components/cart/AddToCartButton.jsx`
- ➕ `src/main/front-end/src/components/cart/CartItem.jsx`
- ➕ `src/main/front-end/src/components/order/DeliverySlotSelector.jsx`
- ➕ `src/main/front-end/src/components/order/PaymentForm.jsx`

### DÉJÀ PRÊTS (ne pas toucher)
- ✅ `src/main/front-end/src/services/cartService.js`
- ✅ `src/main/front-end/src/services/orderService.js`
- ✅ `src/main/front-end/src/services/api.js`
- ✅ Backend complet avec toutes les routes

---

## 🎓 Concepts Importants à Comprendre

### 1. Séparation Frontend/Backend
- Frontend = Interface utilisateur (React)
- Backend = Logique métier + Base de données (Java)
- Communication via API REST (HTTP)

### 2. Le flux de données
```
User Action → Component → Service → API → Backend → Database
                ↓                                        ↓
              State Update ← Response ← Response ← Query Result
```

### 3. Les services frontend
- Ce sont des fichiers qui encapsulent les appels API
- Ils utilisent axios pour faire des requêtes HTTP
- Ils retournent des Promises
- À utiliser dans les composants avec async/await

### 4. Le panier vs la commande
- **Panier (Cart)** = temporaire, peut être modifié, pas encore payé
- **Commande (Order)** = créée depuis un panier, avec créneau + paiement, définitive

---

## ✅ Checklist finale

Avant de dire que la task est terminée, vérifier :

- [ ] Je peux ajouter des plats au panier depuis la page restaurant
- [ ] Je peux voir mon panier avec tous les items
- [ ] Je peux modifier les quantités dans le panier
- [ ] Je peux supprimer des items du panier
- [ ] Je vois le total correct
- [ ] Je peux créer une commande depuis mon panier
- [ ] Je peux sélectionner un créneau de livraison
- [ ] Je peux effectuer un paiement (même mocké)
- [ ] Je peux confirmer ma commande
- [ ] Je vois une page de confirmation avec tous les détails
- [ ] Toutes les routes API fonctionnent
- [ ] Le code est propre et commenté
- [ ] Les erreurs sont gérées correctement

---

## 🎯 FOCUS : Votre Task Principale

**Service Commande & Paiement (Epic)**
✅ Backend : DÉJÀ FAIT (toutes les routes fonctionnent)
🔨 Frontend : À FAIRE (créer les composants et pages pour l'interface utilisateur)

**User Story à implémenter :**
"En tant qu'utilisateur, je veux passer une commande complète avec sélection d'un créneau horaire et paiement"

**Cela signifie :**
1. Interface pour ajouter au panier ✏️
2. Interface pour voir le panier ✏️
3. Interface pour passer commande ✏️
4. Interface pour sélectionner un créneau ➕
5. Interface pour payer (mocké) ➕
6. Interface pour confirmer ✏️
7. Interface pour voir la confirmation ✏️

**Votre backend Order & Payment Service est 100% prêt, il ne reste QUE le frontend à faire !**

---

Bon courage ! 🚀

