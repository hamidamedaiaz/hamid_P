# 🏗️ Architecture Microservices SophiaTech Eats

## 📋 Vue d'ensemble

L'application SophiaTech Eats utilise une architecture microservices avec 3 services indépendants:

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React)                          │
│                   http://localhost:5173                      │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                  API Gateway (Port 8080)                     │
│              Point d'entrée unique pour les clients          │
│                                                              │
│  • Route intelligemment les requêtes vers les services       │
│  • Gère CORS et headers communs                             │
│  • Fait office de reverse proxy                             │
└────────────────┬────────────────────────┬───────────────────┘
                 │                        │
        ┌────────▼────────┐      ┌───────▼────────┐
        │                 │      │                 │
┌───────┴─────────────────┴──────┴─────────────────┴──────────┐
│                                                               │
│   Consumer Service          Restaurant Service               │
│     (Port 8082)                (Port 8081)                   │
│                                                               │
│  • Parcourir restaurants    • Gérer restaurants              │
│  • Gérer panier            • Gérer menu                      │
│  • Passer commandes        • Gérer créneaux livraison        │
│  • Consulter commandes     • Interface restaurant            │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

## 🎯 Les 3 Services

### 1. 🔷 API Gateway (Port 8080)

**Responsabilité:** Point d'entrée unique - Routage des requêtes

**Classe:** `ApiGatewayApplication.java`

**Règles de routage:**
- `/api/cart/*` → Consumer Service (8082)
- `/api/orders/*` → Consumer Service (8082)
- `/restaurants` (GET) → Consumer Service (8082)
- `/restaurants/*` (POST/PUT/DELETE) → Restaurant Service (8081)

**Avantages:**
- ✅ Simplifie l'intégration frontend (un seul point d'entrée)
- ✅ Centralise CORS et sécurité
- ✅ Permet de changer l'implémentation des services sans impact client
- ✅ Facilite le load balancing futur

---

### 2. 🟢 Restaurant Service (Port 8081)

**Responsabilité:** Gestion de l'interface restaurant

**Classe:** `RestaurantServiceApplication.java`

**Endpoints:**

#### Gestion Restaurant
- `POST /restaurants` - Créer un restaurant
- `PUT /restaurants/{id}` - Modifier un restaurant
- `DELETE /restaurants/{id}` - Supprimer un restaurant

#### Gestion Menu
- `GET /restaurants/{id}/menu` - Voir le menu
- `POST /restaurants/{id}/menu` - Ajouter un plat
- `PUT /restaurants/{id}/menu/{dishId}` - Modifier un plat
- `DELETE /restaurants/{id}/menu/{dishId}` - Supprimer un plat

#### Gestion Créneaux
- `GET /restaurants/{id}/delivery-slots` - Voir les créneaux
- `POST /restaurants/{id}/delivery-slots` - Créer des créneaux
- `POST /restaurants/{id}/delivery-slots/{slotId}` - Réserver/Libérer un créneau

**Utilisateurs:** Propriétaires de restaurants

---

### 3. 🔵 Consumer Service (Port 8082)

**Responsabilité:** Gestion des actions clients

**Classe:** `ConsumerServiceApplication.java`

**Endpoints:**

#### Navigation Restaurants (Clients)
- `GET /restaurants` - Parcourir les restaurants
- `GET /restaurants/{id}` - Détails d'un restaurant

#### Gestion Panier
- `POST /api/cart/items` - Ajouter au panier
- `GET /api/cart/{userId}` - Voir le panier
- `PUT /api/cart/{userId}/items` - Modifier un article
- `DELETE /api/cart/{userId}` - Vider le panier
- `DELETE /api/cart/{userId}/cancel` - Annuler le panier
- `DELETE /api/cart/{userId}/items/{dishId}` - Retirer un article

#### Gestion Commandes
- `POST /api/orders` - Passer une commande
- `GET /api/orders/{id}` - Voir une commande
- `GET /api/orders/user/{userId}` - Mes commandes
- `POST /api/orders/{id}/delivery-slot` - Sélectionner un créneau
- `POST /api/orders/{id}/payment` - Payer
- `POST /api/orders/{id}/confirm` - Confirmer la commande

**Utilisateurs:** Clients (étudiants)

---

## 📊 Flux de Données

### Exemple: Un client commande un plat

```
1. Frontend → GET http://localhost:8080/restaurants
   ↓
2. Gateway → Consumer Service (8082)
   ↓
3. Consumer Service retourne la liste des restaurants
   ↓
4. Frontend → POST http://localhost:8080/api/cart/items
   ↓
5. Gateway → Consumer Service (8082)
   ↓
6. Consumer Service ajoute au panier
```

### Exemple: Un restaurant ajoute un plat

```
1. Frontend Restaurant → POST http://localhost:8080/restaurants/{id}/menu
   ↓
2. Gateway → Restaurant Service (8081)
   ↓
3. Restaurant Service ajoute le plat
```

---

## ✅ Avantages de cette Architecture

### Séparation des Responsabilités
- ✅ Chaque service a une responsabilité claire
- ✅ Facilite la maintenance et l'évolution
- ✅ Respect des principes SOLID

### Scalabilité
- ✅ Possibilité de scaler chaque service indépendamment
- ✅ Restaurant Service peut avoir moins d'instances (moins de traffic)
- ✅ Consumer Service peut avoir plus d'instances (plus de clients)

### Isolation des Pannes
- ✅ Si Restaurant Service tombe, les clients peuvent toujours commander
- ✅ Si Consumer Service tombe, les restaurants peuvent gérer leur menu

### Déploiement Indépendant
- ✅ Mise à jour du Restaurant Service sans redémarrer Consumer Service
- ✅ Déploiement progressif (rolling updates)

### Développement en Équipe
- ✅ Équipes différentes peuvent travailler sur chaque service
- ✅ Moins de conflits Git
- ✅ Tests indépendants

---

## 🏗️ Respect de Clean Architecture

```
┌─────────────────────────────────────────────┐
│           Interfaces Layer                   │
│  (Handlers HTTP, API Gateway)               │
│                                              │
│  • ApiGatewayApplication                    │
│  • RestaurantServiceApplication             │
│  • ConsumerServiceApplication               │
│  • Handlers (Cart, Order, Restaurant...)    │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│         Application Layer                    │
│  (Use Cases, DTOs, Facade)                  │
│                                              │
│  • SophiaTechEatsFacade                     │
│  • Use Cases                                │
│  • DTOs                                     │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│           Domain Layer                       │
│  (Entities, Services, Value Objects)         │
│                                              │
│  • Restaurant, Order, Cart                  │
│  • RestaurantService, OrderService          │
│  • Business Rules                           │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│        Infrastructure Layer                  │
│  (Repositories, Config, External Services)   │
│                                              │
│  • InMemoryRepositories                     │
│  • ApplicationConfig                        │
└─────────────────────────────────────────────┘
```

**Dépendances:** Toutes les flèches pointent vers le bas
- ✅ Le domaine ne dépend de rien
- ✅ L'application dépend du domaine
- ✅ Les interfaces dépendent de l'application
- ✅ L'infrastructure dépend du domaine

---

### Voir les logs

Chaque service affiche ses logs dans son terminal respectif avec des couleurs:
- 🟣 **Magenta**: Restaurant Service (8081)
- 🔵 **Cyan**: Consumer Service (8082)
- 🔷 **Blue**: API Gateway (8080)
