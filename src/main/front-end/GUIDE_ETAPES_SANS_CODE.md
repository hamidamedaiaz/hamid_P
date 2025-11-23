# 🎯 PLAN D'ACTION - Frontend Order & Payment (Sans Code)

## 📌 Vue d'ensemble : Qu'est-ce qu'on va faire ?

Vous allez connecter votre **interface React** (frontend) à votre **serveur Java** (backend) pour permettre à un utilisateur de :
1. Ajouter des plats dans un panier
2. Voir et modifier son panier
3. Créer une commande
4. Choisir un créneau horaire
5. Payer (simulé)
6. Recevoir une confirmation

---

## 🏗️ Architecture : Comment ça fonctionne ?

```
USER (Navigateur)
    ↓
REACT (Interface visuelle)
    ↓
SERVICES (Fonctions qui parlent au serveur)
    ↓
AXIOS (Bibliothèque qui envoie des requêtes HTTP)
    ↓
BACKEND JAVA (Port 8082)
```

**Analogie** : 
- React = Le restaurant avec les serveurs
- Services = Les serveurs qui prennent les commandes
- Axios = Le téléphone pour appeler la cuisine
- Backend = La cuisine qui prépare les plats

---

## ✅ ÉTAPE 1 : Configurer la "ligne téléphonique" (api.js)

### 🎯 Objectif
Dire à React où se trouve votre serveur backend.

### 📝 Ce que vous devez comprendre

Actuellement, votre fichier `api.js` dit :
- "Le serveur est sur le port 8080"

Mais votre backend Order & Payment tourne sur le **port 8082**.

### 🔧 Ce que vous devez faire

1. **Ouvrir** le fichier : `src/main/front-end/src/services/api.js`
2. **Trouver** la ligne qui dit `baseURL: "http://localhost:8080/"`
3. **Changer** 8080 en 8082
4. **Ajouter** `/api` à la fin pour que ça devienne `http://localhost:8082/api`

### ❓ Pourquoi ?

Parce que toutes vos routes backend commencent par `/api` :
- `/api/cart/items`
- `/api/orders`
- etc.

### ✅ Validation

Après cette étape, Axios saura que quand vous appelez `/cart/items`, il doit contacter `http://localhost:8082/api/cart/items`.

---

## ✅ ÉTAPE 2 : Créer le "menu des fonctions panier" (cartService.js)

### 🎯 Objectif
Créer un fichier qui contient TOUTES les fonctions pour gérer le panier.

### 📝 Ce que vous devez comprendre

Vous ne voulez PAS écrire 10 fois le même code pour appeler le backend. Vous créez UN fichier avec TOUTES les fonctions réutilisables.

### 🔧 Ce que vous devez faire

1. **Créer** le fichier : `src/main/front-end/src/services/cartService.js`

2. **Importer** axios (la bibliothèque de communication) :
   - Elle est déjà installée dans votre projet
   - Vous l'importez depuis `./api.js` (le fichier de l'étape 1)

3. **Créer un objet** appelé `cartService` qui contient 5 fonctions :

#### Fonction 1 : `addDishToCart`
- **Rôle** : Ajouter un plat au panier
- **Paramètres** : userId, dishId, quantity
- **Action** : Envoyer une requête POST à `/cart/items`
- **Retour** : Les données du panier mis à jour

#### Fonction 2 : `getCart`
- **Rôle** : Récupérer le panier d'un utilisateur
- **Paramètres** : userId
- **Action** : Envoyer une requête GET à `/cart/{userId}`
- **Retour** : La liste des articles dans le panier

#### Fonction 3 : `updateCartItem`
- **Rôle** : Changer la quantité d'un article
- **Paramètres** : userId, dishId, quantity
- **Action** : Envoyer une requête PUT à `/cart/{userId}/items`
- **Retour** : Le panier mis à jour

#### Fonction 4 : `removeDishFromCart`
- **Rôle** : Supprimer un article du panier
- **Paramètres** : userId, dishId
- **Action** : Envoyer une requête DELETE à `/cart/{userId}/items/{dishId}`
- **Retour** : Le panier mis à jour

#### Fonction 5 : `clearCart`
- **Rôle** : Vider complètement le panier
- **Paramètres** : userId
- **Action** : Envoyer une requête DELETE à `/cart/{userId}`
- **Retour** : Confirmation que le panier est vide

### 💡 Concept important : async/await

Les requêtes HTTP prennent du temps (comme commander au téléphone). Vous devez :
- Marquer vos fonctions comme `async` (asynchrone)
- Utiliser `await` pour attendre la réponse du serveur
- Gérer les erreurs avec `try/catch`

### ✅ Validation

Après cette étape, vous pouvez appeler `cartService.addDishToCart(...)` depuis n'importe où dans votre application React.

---

## ✅ ÉTAPE 3 : Créer le "menu des fonctions commande" (orderService.js)

### 🎯 Objectif
Créer un fichier qui contient TOUTES les fonctions pour gérer les commandes.

### 📝 Ce que vous devez comprendre

C'est la même logique que l'étape 2, mais pour les commandes.

### 🔧 Ce que vous devez faire

1. **Créer** le fichier : `src/main/front-end/src/services/orderService.js`

2. **Créer un objet** `orderService` avec 6 fonctions :

#### Fonction 1 : `createOrder`
- **Rôle** : Transformer le panier en commande
- **Paramètres** : userId
- **Action** : POST à `/orders`
- **Retour** : L'ID de la nouvelle commande

#### Fonction 2 : `getOrder`
- **Rôle** : Récupérer les détails d'une commande
- **Paramètres** : orderId
- **Action** : GET à `/orders/{orderId}`
- **Retour** : Tous les détails (montant, articles, statut, etc.)

#### Fonction 3 : `getUserOrders`
- **Rôle** : Voir toutes les commandes d'un utilisateur
- **Paramètres** : userId
- **Action** : GET à `/orders/user/{userId}`
- **Retour** : Liste de toutes les commandes

#### Fonction 4 : `selectDeliverySlot`
- **Rôle** : Choisir l'heure de livraison
- **Paramètres** : orderId, deliverySlot (date et heure)
- **Action** : POST à `/orders/{orderId}/delivery-slot`
- **Retour** : Confirmation du créneau

#### Fonction 5 : `processPayment`
- **Rôle** : Effectuer le paiement
- **Paramètres** : orderId, paymentData (méthode, montant)
- **Action** : POST à `/orders/{orderId}/payment`
- **Retour** : Statut du paiement

#### Fonction 6 : `confirmOrder`
- **Rôle** : Finaliser la commande
- **Paramètres** : orderId
- **Action** : POST à `/orders/{orderId}/confirm`
- **Retour** : Confirmation finale

### ✅ Validation

Maintenant vous avez **toutes les fonctions** pour communiquer avec le backend !

---

## ✅ ÉTAPE 4 : Créer le "magasin central du panier" (CartContext.jsx)

### 🎯 Objectif
Créer un endroit où TOUTE l'application peut accéder au panier sans passer des données partout.

### 📝 Ce que vous devez comprendre

**Problème sans Context** :
```
HomePage → RestaurantPage → DishCard → Cart
         ↓ props        ↓ props     ↓ props
```
Vous devez passer `cartItems` à travers 4 composants !

**Solution avec Context** :
```
CartContext (magasin central)
    ↓ accès direct
HomePage, DishCard, Cart peuvent tous accéder au panier
```

### 🔧 Ce que vous devez faire

1. **Créer** le fichier : `src/main/front-end/src/context/CartContext.jsx`

2. **Créer le Context** :
   - Utiliser `createContext()` de React
   - C'est comme créer une "boîte magique" accessible partout

3. **Créer le Provider** (le fournisseur) :
   - C'est un composant qui enveloppe toute votre application
   - Il contient les DONNÉES et les FONCTIONS du panier

4. **Définir les données à stocker** :
   - `cartItems` : Liste des articles dans le panier
   - `userId` : L'utilisateur actuel (pour l'instant, en dur : "user-123")
   - `loading` : Est-ce qu'on est en train de charger ?

5. **Définir les fonctions** :

#### `loadCart()`
- **Rôle** : Charger le panier depuis le backend
- **Logique** : 
  1. Appeler `cartService.getCart(userId)`
  2. Stocker le résultat dans `cartItems`

#### `addToCart(dishId, quantity)`
- **Rôle** : Ajouter un plat
- **Logique** :
  1. Appeler `cartService.addDishToCart(userId, dishId, quantity)`
  2. Recharger le panier avec `loadCart()`

#### `updateQuantity(dishId, quantity)`
- **Rôle** : Changer la quantité
- **Logique** :
  1. Appeler `cartService.updateCartItem(...)`
  2. Recharger le panier

#### `removeFromCart(dishId)`
- **Rôle** : Supprimer un article
- **Logique** :
  1. Appeler `cartService.removeDishFromCart(...)`
  2. Recharger le panier

#### `clearCart()`
- **Rôle** : Vider le panier
- **Logique** :
  1. Appeler `cartService.clearCart(userId)`
  2. Mettre `cartItems` à tableau vide

#### `getTotalAmount()`
- **Rôle** : Calculer le prix total
- **Logique** : Additionner (prix × quantité) de tous les articles

#### `getTotalItems()`
- **Rôle** : Compter le nombre total d'articles
- **Logique** : Additionner toutes les quantités

6. **Charger le panier au démarrage** :
   - Utiliser `useEffect()` de React
   - Quand l'application démarre, appeler `loadCart()`

7. **Fournir les données** :
   - Mettre toutes les fonctions et données dans un objet `value`
   - Passer cet objet au `Provider`

### ✅ Validation

Après cette étape, n'importe quel composant peut faire :
```javascript
const { cartItems, addToCart } = useContext(CartContext);
```

---

## ✅ ÉTAPE 5 : Envelopper l'application avec le Context

### 🎯 Objectif
Activer le Context dans toute l'application.

### 📝 Ce que vous devez comprendre

Le Context ne fonctionne que si vous "enveloppez" votre application avec le `Provider`.

**Analogie** : C'est comme installer le Wi-Fi dans une maison. Si vous ne l'installez pas, personne ne peut s'y connecter.

### 🔧 Ce que vous devez faire

1. **Ouvrir** le fichier : `src/main/front-end/src/main.jsx` (ou `App.jsx`)

2. **Importer** le `CartProvider` depuis le fichier que vous venez de créer

3. **Envelopper** le composant `<App />` avec `<CartProvider>`

**Avant** :
```
<App />
```

**Après** :
```
<CartProvider>
    <App />
</CartProvider>
```

### ✅ Validation

Maintenant, TOUS les composants de votre application peuvent accéder au panier.

---

## ✅ ÉTAPE 6 : Corriger le composant Cart.jsx

### 🎯 Objectif
Réparer le bug dans le composant qui affiche le panier.

### 📝 Ce que vous devez comprendre

**Le problème actuel** :
```javascript
const {...} = useContext(); // ❌ ERREUR : useContext de QUEL contexte ?
```

C'est comme dire "Je veux me connecter au Wi-Fi" sans dire QUEL réseau Wi-Fi.

### 🔧 Ce que vous devez faire

1. **Ouvrir** : `src/main/front-end/src/components/cart/Cart.jsx`

2. **Importer** le CartContext :
   - Depuis `../../context/CartContext`

3. **Corriger** la ligne `useContext()` :
   - Passer `CartContext` en paramètre

4. **Récupérer** les données et fonctions :
   - `cartItems` : Les articles du panier
   - `removeFromCart` : Fonction pour supprimer
   - `updateQuantity` : Fonction pour changer la quantité
   - `getTotalAmount` : Fonction pour le total
   - `loading` : Savoir si on charge

5. **Afficher** :
   - Si `loading` est true → Afficher "Chargement..."
   - Si `cartItems` est vide → Afficher "Panier vide"
   - Sinon → Afficher la liste des articles

6. **Pour chaque article** :
   - Afficher le nom du plat
   - Afficher le prix
   - Afficher un champ nombre pour la quantité
   - Quand on change la quantité → Appeler `updateQuantity()`
   - Afficher un bouton "Supprimer"
   - Quand on clique → Appeler `removeFromCart()`

7. **En bas** :
   - Afficher le total avec `getTotalAmount()`

### ✅ Validation

Le panier s'affiche correctement et vous pouvez modifier/supprimer des articles.

---

## ✅ ÉTAPE 7 : Créer le résumé du panier (CartSummary.jsx)

### 🎯 Objectif
Créer un petit composant qui affiche un résumé rapide du panier.

### 📝 Ce que vous devez comprendre

Ce composant sera affiché en haut de page (ou dans un coin) pour montrer :
- Nombre d'articles
- Prix total
- Bouton "Passer commande"

### 🔧 Ce que vous devez faire

1. **Créer** : `src/main/front-end/src/components/cart/CartSummary.jsx`

2. **Importer** :
   - Le CartContext
   - `useNavigate` de React Router (pour la navigation)

3. **Récupérer** du Context :
   - `getTotalItems()`
   - `getTotalAmount()`
   - `cartItems`

4. **Logique** :
   - Si le panier est vide → Ne rien afficher
   - Sinon → Afficher le résumé

5. **Afficher** :
   - "Articles : X"
   - "Total : XX.XX€"
   - Bouton "Passer commande"

6. **Quand on clique sur le bouton** :
   - Utiliser `navigate('/checkout')` pour aller à la page de validation

### ✅ Validation

Un petit résumé s'affiche et le bouton redirige vers la page checkout.

---

## ✅ ÉTAPE 8 : Créer la page du panier (CartPage.jsx)

### 🎯 Objectif
Créer une page complète dédiée au panier.

### 📝 Ce que vous devez comprendre

Cette page affiche :
- Le composant `<Cart />` (que vous avez corrigé)
- Un bouton pour vider le panier
- Un bouton pour valider et passer à la commande

### 🔧 Ce que vous devez faire

1. **Créer** : `src/main/front-end/src/pages/CartPage.jsx`

2. **Importer** :
   - Le CartContext
   - Le composant `Cart`
   - `useNavigate` pour la navigation

3. **Récupérer** du Context :
   - `cartItems`
   - `getTotalAmount`
   - `clearCart`

4. **Afficher** :
   - Un titre "Mon Panier"
   - Le composant `<Cart />`
   - Si le panier n'est pas vide :
     - Bouton "Vider le panier" → Appelle `clearCart()`
     - Bouton "Valider (XX€)" → Redirige vers `/checkout`

### ✅ Validation

Vous avez une page complète pour gérer le panier.

---

## ✅ ÉTAPE 9 : Créer la page de validation (CheckoutPage.jsx)

### 🎯 Objectif
Créer la page qui gère : création commande → créneau → paiement → confirmation.

### 📝 Ce que vous devez comprendre

Cette page passe par **3 étapes** :

**Étape 1** : Créer la commande
- Quand la page s'ouvre, créer automatiquement une commande
- Récupérer l'ID de la commande

**Étape 2** : Sélectionner le créneau
- Afficher un champ pour choisir date et heure
- Quand validé → Enregistrer le créneau

**Étape 3** : Payer
- Afficher le montant
- Choisir le mode de paiement (Carte, PayPal, etc.)
- Quand validé → Effectuer le paiement
- Puis confirmer la commande
- Rediriger vers la page de confirmation

### 🔧 Ce que vous devez faire

1. **Créer** : `src/main/front-end/src/pages/CheckoutPage.jsx`

2. **Importer** :
   - CartContext (pour le userId et le total)
   - orderService
   - useState, useEffect
   - useNavigate

3. **Créer les états** :
   - `orderId` : L'ID de la commande (null au début)
   - `deliverySlot` : Le créneau choisi (vide au début)
   - `paymentMethod` : Le mode de paiement (par défaut "CREDIT_CARD")
   - `step` : L'étape actuelle (1, 2 ou 3)

4. **Au chargement de la page** (useEffect) :
   - Appeler `orderService.createOrder(userId)`
   - Stocker l'ID dans `orderId`
   - Passer à l'étape 2

5. **Affichage selon l'étape** :

#### Si step === 1
- Afficher "Création de la commande..."

#### Si step === 2
- Afficher "Sélectionner un créneau horaire"
- Afficher un champ `<input type="datetime-local">`
- Lier le champ à `deliverySlot`
- Bouton "Continuer"
- Quand on clique :
  - Appeler `orderService.selectDeliverySlot(orderId, deliverySlot)`
  - Passer à l'étape 3

#### Si step === 3
- Afficher "Paiement"
- Afficher le montant total
- Afficher un menu déroulant pour choisir le mode de paiement
- Lier le menu à `paymentMethod`
- Bouton "Payer"
- Quand on clique :
  - Appeler `orderService.processPayment(orderId, { method: paymentMethod, amount: ... })`
  - Appeler `orderService.confirmOrder(orderId)`
  - Rediriger vers `/order-confirmation/{orderId}`

### ✅ Validation

Le flux complet fonctionne : panier → commande → créneau → paiement → confirmation.

---

## ✅ ÉTAPE 10 : Créer la page de confirmation (OrderConfirmationPage.jsx)

### 🎯 Objectif
Afficher un récapitulatif de la commande validée.

### 📝 Ce que vous devez comprendre

Cette page :
- Reçoit l'ID de la commande dans l'URL
- Charge les détails depuis le backend
- Affiche un message de succès avec le récapitulatif

### 🔧 Ce que vous devez faire

1. **Créer** : `src/main/front-end/src/pages/OrderConfirmationPage.jsx`

2. **Importer** :
   - orderService
   - useState, useEffect
   - `useParams` de React Router (pour récupérer l'ID de l'URL)

3. **Récupérer l'ID** de l'URL :
   - Utiliser `const { orderId } = useParams()`

4. **Créer un état** :
   - `order` : Les détails de la commande (null au début)

5. **Au chargement** (useEffect) :
   - Appeler `orderService.getOrder(orderId)`
   - Stocker le résultat dans `order`

6. **Afficher** :
   - Si `order` est null → "Chargement..."
   - Sinon :
     - Titre "✅ Commande confirmée !"
     - Numéro de commande
     - Total
     - Créneau de livraison
     - Statut

### ✅ Validation

La page de confirmation affiche toutes les informations de la commande.

---

## ✅ ÉTAPE 11 : Configurer les routes

### 🎯 Objectif
Faire en sorte que les URLs fonctionnent.

### 📝 Ce que vous devez comprendre

Vous devez dire à React Router :
- `/cart` → Affiche CartPage
- `/checkout` → Affiche CheckoutPage
- `/order-confirmation/:orderId` → Affiche OrderConfirmationPage

### 🔧 Ce que vous devez faire

1. **Ouvrir** : `src/main/front-end/src/App.jsx` (ou où sont définies vos routes)

2. **Importer** les 3 pages que vous venez de créer

3. **Ajouter les routes** :
   - `<Route path="/cart" element={<CartPage />} />`
   - `<Route path="/checkout" element={<CheckoutPage />} />`
   - `<Route path="/order-confirmation/:orderId" element={<OrderConfirmationPage />} />`

### ✅ Validation

Les URLs fonctionnent et affichent les bonnes pages.

---

## ✅ ÉTAPE 12 : Ajouter un bouton "Ajouter au panier" sur les plats

### 🎯 Objectif
Permettre d'ajouter un plat au panier depuis la page des restaurants.

### 📝 Ce que vous devez comprendre

Quand un utilisateur voit un plat (dans DishCard par exemple), il doit pouvoir cliquer sur "Ajouter au panier".

### 🔧 Ce que vous devez faire

1. **Ouvrir** : Le composant qui affiche un plat (probablement `DishCard.jsx`)

2. **Importer** le CartContext

3. **Récupérer** la fonction `addToCart`

4. **Ajouter un bouton** "Ajouter au panier"

5. **Quand on clique** :
   - Appeler `addToCart(plat.id, 1)`
   - Afficher un message "Ajouté au panier !"

### ✅ Validation

Vous pouvez ajouter des plats au panier depuis la liste des restaurants.

---

## 🚨 ÉTAPE 13 : Gérer CORS (Très important !)

### 🎯 Objectif
Permettre au frontend (port 5173) de communiquer avec le backend (port 8082).

### 📝 Ce que vous devez comprendre

**Le problème** : Par défaut, les navigateurs bloquent les requêtes entre différents ports (sécurité).

**La solution** : Dire au backend "J'autorise les requêtes depuis le port 5173".

### 🔧 Ce que vous devez faire (CÔTÉ BACKEND JAVA)

1. **Ouvrir** votre handler ou registry côté Java

2. **Ajouter des headers CORS** dans chaque réponse :
   - `Access-Control-Allow-Origin: http://localhost:5173`
   - `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`
   - `Access-Control-Allow-Headers: Content-Type`

3. **Gérer les requêtes OPTIONS** :
   - Les navigateurs envoient d'abord une requête OPTIONS
   - Vous devez répondre avec les headers CORS sans traiter la logique

### ✅ Validation

Les requêtes depuis le frontend fonctionnent sans erreur CORS.

---

## 🧪 ÉTAPE 14 : Tester tout le flux

### 🎯 Objectif
Vérifier que tout fonctionne de bout en bout.

### 🔧 Ce que vous devez faire

1. **Lancer le backend** :
   - Run `OrderPaymentServiceApplication.java`
   - Vérifier qu'il démarre sur port 8082

2. **Lancer le frontend** :
   - `cd src/main/front-end`
   - `npm run dev`
   - Ouvrir `http://localhost:5173`

3. **Ouvrir la console du navigateur** (F12)

4. **Tester le flux** :
   - ✅ Ajouter un plat au panier
   - ✅ Voir le panier
   - ✅ Modifier la quantité
   - ✅ Supprimer un article
   - ✅ Cliquer sur "Passer commande"
   - ✅ Sélectionner un créneau
   - ✅ Effectuer le paiement
   - ✅ Voir la confirmation

5. **Vérifier dans la console** :
   - Pas d'erreurs rouges
   - Les requêtes réussissent (status 200)

### ✅ Validation

Tout fonctionne de A à Z !

---

## 📊 Récapitulatif : Ordre de création

1. ✅ Modifier `api.js` (port 8082)
2. ✅ Créer `cartService.js` (5 fonctions)
3. ✅ Créer `orderService.js` (6 fonctions)
4. ✅ Créer `CartContext.jsx` (le magasin central)
5. ✅ Envelopper l'app avec `<CartProvider>`
6. ✅ Corriger `Cart.jsx`
7. ✅ Créer `CartSummary.jsx`
8. ✅ Créer `CartPage.jsx`
9. ✅ Créer `CheckoutPage.jsx`
10. ✅ Créer `OrderConfirmationPage.jsx`
11. ✅ Configurer les routes
12. ✅ Ajouter bouton "Ajouter au panier"
13. ✅ Gérer CORS (backend)
14. ✅ Tester tout

---

## 🎓 Concepts React à comprendre

### useState
- Stocke des données qui peuvent changer
- Quand les données changent → React re-rend le composant

### useEffect
- Exécute du code quand le composant s'affiche
- Utile pour charger des données au démarrage

### useContext
- Accède aux données du Context
- Évite de passer des props partout

### async/await
- Attend qu'une requête HTTP se termine
- Permet d'écrire du code asynchrone de façon lisible

### try/catch
- Gère les erreurs
- Si la requête échoue → On peut afficher un message

---

## 🎯 Points clés à retenir

1. **Services** = Fonctions qui parlent au backend
2. **Context** = Magasin central de données
3. **Components** = Morceaux d'interface réutilisables
4. **Pages** = Écrans complets
5. **Routes** = Associer URL → Page
6. **CORS** = Autoriser frontend à parler au backend

---

Bonne chance ! 🚀

