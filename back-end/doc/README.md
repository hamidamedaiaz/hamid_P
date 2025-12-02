# SophiaTech Eats - Projet Fil Rouge Backend

## 📋 Table des Matières

1. [Équipe et Rôles](#équipe-et-rôles)
2. [Installation et Lancement](#installation-et-lancement)
3. [Structure du Projet](#structure-du-projet)
4. [Gestion de Projet](#gestion-de-projet)

---

## 👥 Équipe et Rôles

### **Team P - 2025-2026**

| Rôle                        | Nom                           | Email                                                   | Responsabilités                                                                                                                                                                                                                               |
|-----------------------------|-------------------------------|---------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Product Owner (PO)**      | Saad BENAQA                   | saad.benaqa@etu.unice.fr                                | Gestion du backlog, priorisation des exigences fonctionnelles, validation des critères d'acceptation, coordination avec les encadrants, Infrastructure, configuration CI/CD, gestion des environnements, documentation technique, déploiement 
|
| **Software Architect (SA)** | Hamid AMEDIAZ<br>Guilaye DIOP | hamid.amediaz@etu.unice.fr<br>guilaye.diop@etu.unice.fr | Architecture du système, design patterns, diagrammes UML, conception des couches (Domain/Application/Infrastructure), revues techniques                                                                                                       |
| **Quality Assurance (QA)**  | Guilaye DIOP<br>Adam BOTTERO  | guilaye.diop@etu.unice.fr<br>adam.bottero@etu.unice.fr  | Stratégie de tests (unitaires/BDD/intégration), couverture de code, intégration continue (CI), analyse SonarQube, validation de la qualité                                                                                                    |
| **Operations (Ops)**        | Othmane GARTANI               | othmane.gartani@etu.unice.fr                            | Infrastructure, configuration CI/CD, gestion des environnements, documentation technique, déploiement                                                                                                                                         |

### Encadrants du Projet

- **Mme Blay Mireille**
- **M. Collet Philippe**
- **Mme Anne-Marie Dery Pinna**

---

## 🚀 Installation et Lancement

### Prérequis

Assurez-vous d'avoir les outils suivants installés sur votre machine :

- **Java JDK 21** ou supérieur ([Télécharger](https://adoptium.net/))
- **Maven 3.9+** ([Télécharger](https://maven.apache.org/download.cgi))
- **Git** ([Télécharger](https://git-scm.com/downloads))
- **Docker** (optionnel, pour SonarQube) ([Télécharger](https://www.docker.com/get-started))

### Installation

#### 1. Cloner le dépôt

```bash
git clone https://github.com/votre-organisation/ste-25-26-team-p-1.git
cd ste-25-26-team-p-1
```

#### 2. Installer les dépendances Maven

```bash
mvn clean install
```

Cette commande va :

- Télécharger toutes les dépendances nécessaires
- Compiler le code source
- Exécuter les tests unitaires et d'intégration
- Générer le package JAR

### Lancement du Projet

#### Option 1 : Exécution via Maven

```bash
mvn exec:java -Dexec.mainClass="fr.unice.polytech.sophiatecheats.SophiaTechEatsApplication"
```

#### Option 2 : Exécution du JAR

```bash
java -jar target/sophiatech-eats-1.0-SNAPSHOT.jar
```

#### Option 3 : Démo End-to-End

Pour lancer une démonstration complète du système :

```bash
mvn exec:java -Dexec.mainClass="fr.unice.polytech.sophiatecheats.EndToEndUserFlowDemo"
```

### Commandes Maven Utiles

#### Compilation et Tests

```bash
# Compilation uniquement
mvn compile

# Exécuter tous les tests
mvn test

# Exécuter tests unitaires uniquement
mvn test -Dtest="**/*Test.java"

# Exécuter tests BDD Cucumber uniquement
mvn test -Dtest="**/*CucumberTest.java"

# Ignorer les tests
mvn clean install -DskipTests
```

#### Génération de Rapports

```bash
# Générer le rapport de couverture JaCoCo
mvn clean test jacoco:report

# Le rapport sera disponible dans : target/site/jacoco/index.html
```

#### Analyse de Qualité avec SonarQube

##### 1. Démarrer SonarQube avec Docker

```bash
docker-compose -f docker-compose-sonar.yml up -d
```

##### 2. Lancer l'analyse

```bash
mvn clean verify sonar:sonar
```

##### 3. Accéder au dashboard

Ouvrez votre navigateur : [http://localhost:9000](http://localhost:9000)

**Identifiants par défaut :**

- Username: `admin`
- Password: `admin`

#### Package et Distribution

```bash
# Créer le JAR exécutable
mvn clean package

# Créer le JAR avec dépendances
mvn clean package assembly:single
```

### Vérification de l'Installation

Pour vérifier que tout fonctionne correctement :

```bash
# Vérifier la version Java
java -version

# Vérifier la version Maven
mvn -version

# Compiler et tester le projet
mvn clean test

# Si tous les tests passent, l'installation est réussie ✅
```

---

## 📂 Structure du Projet

Le projet suit les principes de la **Clean Architecture** avec une séparation stricte des responsabilités en couches
concentriques.

### Architecture Générale

```
sophiatech-eats/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── fr/unice/polytech/sophiatecheats/
│   │           ├── domain/                 # 🔵 Couche Domain (Cœur métier)
│   │           ├── application/            # 🟢 Couche Application (Use Cases)
│   │           ├── infrastructure/         # 🟡 Couche Infrastructure (Technique)
│   │           └── SophiaTechEatsApplication.java
│   │
│   └── test/
│       ├── java/                           # Tests unitaires et BDD
│       └── resources/
│           └── features/                   # Scénarios Cucumber (.feature)
│
├── target/                                 # Fichiers compilés et rapports
├── doc/                                    # Documentation du projet
├── pom.xml                                 # Configuration Maven
├── sonar-project.properties               # Configuration SonarQube
└── README.md                              # Ce fichier
```

### 🔵 Couche Domain (Cœur Métier)

**Principe :** Contient la logique métier pure, indépendante de toute technologie externe.

```
domain/
├── entities/                              # Entités métier avec logique
│   ├── user/
│   │   ├── User.java                     # Classe abstraite utilisateur
│   │   ├── CampusUser.java               # Utilisateur avec crédit étudiant
│   │   └── AdminUser.java                # Administrateur système
│   │
│   ├── restaurant/
│   │   ├── Restaurant.java               # Restaurant avec Builder Pattern
│   │   ├── Dish.java                     # Plat avec catégories et allergènes
│   │   ├── Schedule.java                 # Horaires d'ouverture
│   │   └── DeliverySchedule.java         # Planning de livraison
│   │
│   ├── order/
│   │   ├── Order.java                    # Commande validée
│   │   ├── OrderItem.java                # Ligne de commande (immutable)
│   │   └── OrderStatus.java              # États : PENDING, PAID, CONFIRMED...
│   │
│   ├── cart/
│   │   ├── Cart.java                     # Panier utilisateur
│   │   └── CartItem.java                 # Ligne de panier (mutable)
│   │
│   └── delivery/
│       ├── TimeSlot.java                 # Créneau de livraison (Builder)
│       └── CapacitySlot.java             # Gestion de la capacité
│
├── valueobjects/                          # Objets valeur immuables
│   ├── Money.java                        # Montant avec devise (BigDecimal)
│   ├── Address.java                      # Adresse structurée
│   ├── DishCategory.java                 # STARTER, MAIN_COURSE, DESSERT...
│   ├── DietType.java                     # VEGETARIAN, VEGAN, HALAL...
│   ├── CuisineType.java                  # FRENCH, ITALIAN, ASIAN...
│   └── RestaurantType.java               # CROUS, RESTAURANT, FOOD_TRUCK...
│
├── services/                              # Services métier (Domain Services)
│   ├── CartService.java                  # Logique de gestion du panier
│   ├── OrderService.java                 # Logique de commande
│   └── PaymentService.java               # Orchestration des paiements
│
└── repositories/                          # Interfaces de persistance (DIP)
    ├── Repository.java                   # Interface générique <T, ID>
    ├── UserRepository.java
    ├── RestaurantRepository.java
    ├── CartRepository.java
    ├── OrderRepository.java
    └── TimeSlotRepository.java
```

**Caractéristiques :**

- ✅ Aucune dépendance vers les couches externes
- ✅ Entités avec logique métier (validation, calculs)
- ✅ Interfaces de repositories (Dependency Inversion Principle)
- ✅ Value Objects immuables (pattern DDD)

### 🟢 Couche Application (Use Cases)

**Principe :** Orchestre la logique métier pour répondre aux exigences fonctionnelles.

```
application/
├── facade/
│   └── SophiaTechEatsFacade.java         # 🎯 Point d'entrée unique (Facade Pattern)
│
├── usecases/                              # Cas d'utilisation (1 use case = 1 exigence)
│   ├── user/
│   │   ├── BrowseRestaurantsUseCase.java         # C1: Consulter restaurants
│   │   ├── AddDishToCartUseCase.java             # C2: Ajouter au panier
│   │   ├── ViewCartDetailsUseCase.java           # C5: Voir détails panier
│   │   ├── UpdateCartItemUseCase.java            # C5: Modifier quantité
│   │   ├── RemoveFromCartUseCase.java            # C5: Retirer du panier
│   │   ├── PlaceOrderUseCase.java                # C6: Valider commande
│   │   ├── SelectDeliverySlotUseCase.java        # C7: Choisir créneau
│   │   └── ClearCartUseCase.java                 # C8: Vider panier
│   │
│   ├── restaurant/
│   │   ├── AddDishToRestaurantUseCase.java       # R2: Ajouter plat
│   │   ├── UpdateDishUseCase.java                # R4: Modifier plat
│   │   └── RemoveDishFromRestaurantUseCase.java  # R5: Retirer plat
│   │
│   ├── order/
│   │   ├── ConfirmOrderUseCase.java              # P2: Confirmer paiement
│   │   └── CancelOrderUseCase.java               # P3: Annuler commande
│   │
│   └── payment/
│       ├── ProcessPaymentUseCase.java            # P1: Traiter paiement
│       └── RefundPaymentUseCase.java             # P6: Rembourser
│
├── dto/                                   # Data Transfer Objects (entrée/sortie)
│   ├── user/
│   │   ├── BrowseRestaurantsRequest.java
│   │   ├── AddDishToCartRequest.java
│   │   └── CartDetailResponse.java
│   │
│   ├── restaurant/
│   │   ├── RestaurantDto.java
│   │   ├── DishDto.java
│   │   └── AddDishToRestaurantRequest.java
│   │
│   └── order/
│       ├── PlaceOrderRequest.java
│       └── SelectDeliverySlotRequest.java
│
└── exceptions/                            # Exceptions métier
    ├── InsufficientCreditException.java
    ├── SlotNotFoundException.java
    ├── OrderExpiredException.java
    └── ValidationException.java
```

**Caractéristiques :**

- ✅ Un Use Case = Un fichier = Une exigence fonctionnelle
- ✅ DTO pour découpler l'API de la logique métier
- ✅ Facade Pattern pour simplifier l'accès aux Use Cases
- ✅ Exceptions métier pour la gestion d'erreurs

### 🟡 Couche Infrastructure (Technique)

**Principe :** Implémentations concrètes des interfaces définies par le domain.

```
infrastructure/
├── persistence/                           # Implémentations des repositories
│   ├── inmemory/                         # Stockage en mémoire (D1)
│   │   ├── InMemoryUserRepository.java
│   │   ├── InMemoryRestaurantRepository.java
│   │   ├── InMemoryCartRepository.java
│   │   ├── InMemoryOrderRepository.java
│   │   └── InMemoryTimeSlotRepository.java
│   │
│   └── jpa/                              # Implémentations JPA (D2 - À venir)
│       └── (à implémenter)
│
├── payment/                               # Gestion des paiements (Strategy Pattern)
│   ├── PaymentStrategy.java             # Interface stratégie
│   ├── StudentCreditStrategy.java       # Paiement crédit étudiant
│   ├── ExternalCardStrategy.java        # Paiement carte externe (mock)
│   ├── PaymentStrategyFactory.java      # Factory pour créer les stratégies
│   └── PaymentResult.java               # Record du résultat
│
├── api/                                   # API REST (D2 - À venir)
│   └── (à implémenter)
│
└── config/                                # Configuration et injection
    ├── DependencyInjectionConfig.java    # Configuration des dépendances
    └── DataInitializer.java              # Initialisation des données de test
```

**Caractéristiques :**

- ✅ Strategy Pattern pour les paiements (extensible)
- ✅ InMemory repositories pour le prototype (facile à tester)
- ✅ Prêt pour migration vers JPA/Hibernate (D2)
- ✅ Configuration centralisée

### 🧪 Tests (src/test/)

```
test/
├── java/
│   └── fr/unice/polytech/sophiatecheats/
│       ├── domain/                        # Tests unitaires domaine
│       │   ├── entities/
│       │   │   ├── UserTest.java
│       │   │   ├── RestaurantTest.java
│       │   │   └── OrderTest.java
│       │   └── services/
│       │       └── CartServiceTest.java
│       │
│       ├── application/                   # Tests Use Cases
│       │   ├── usecases/
│       │   │   ├── BrowseRestaurantsUseCaseTest.java
│       │   │   ├── AddDishToCartUseCaseTest.java
│       │   │   └── PlaceOrderUseCaseTest.java
│       │   └── facade/
│       │       └── SophiaTechEatsFacadeTest.java
│       │
│       ├── infrastructure/                # Tests infrastructure
│       │   ├── persistence/
│       │   │   └── InMemoryRepositoryTest.java
│       │   └── payment/
│       │       ├── StudentCreditStrategyTest.java
│       │       └── PaymentStrategyFactoryTest.java
│       │
│       └── bdd/                          # Tests BDD Cucumber
│           ├── CucumberRunnerTest.java   # Runner JUnit 5
│           └── stepdefinitions/          # Implémentations Given/When/Then
│               ├── BrowseRestaurantsSteps.java
│               ├── CartManagementSteps.java
│               └── OrderPlacementSteps.java
│
└── resources/
    └── features/                          # Scénarios Gherkin
        ├── browse_restaurants.feature     # C1: Consulter restaurants
        ├── add_to_cart.feature           # C2: Ajouter au panier
        ├── cart_management.feature       # C5: Gestion panier
        ├── place_order.feature           # C6: Passer commande
        └── delivery_slots.feature        # C7: Créneaux de livraison
```

**Couverture de tests :**

- ✅ Tests unitaires JUnit 5 : 85%+
- ✅ Tests BDD Cucumber : 12 scénarios
- ✅ Tests d'intégration : Use Cases complets
- ✅ Rapport JaCoCo : `target/site/jacoco/index.html`

### Design Patterns Utilisés

| Pattern        | Localisation                                           | Objectif                                 |
|----------------|--------------------------------------------------------|------------------------------------------|
| **Repository** | `domain/repositories/` + `infrastructure/persistence/` | Abstraction de la persistance            |
| **Strategy**   | `infrastructure/payment/`                              | Algorithmes de paiement interchangeables |
| **Builder**    | `domain/entities/` (Restaurant, Dish, TimeSlot)        | Construction fluide d'objets complexes   |
| **Facade**     | `application/facade/SophiaTechEatsFacade.java`         | Point d'entrée simplifié                 |
| **Factory**    | `infrastructure/payment/PaymentStrategyFactory.java`   | Création de stratégies de paiement       |

### Principes SOLID Appliqués

- **S**ingle Responsibility : Chaque classe a une responsabilité unique
- **O**pen/Closed : Extensions sans modifications (Strategy Pattern)
- **L**iskov Substitution : Interfaces respectées par toutes implémentations
- **I**nterface Segregation : Interfaces spécifiques et ciblées
- **D**ependency Inversion : Dépendances vers abstractions (repositories)

### Documentation Complémentaire

Consultez les documents suivants dans le dossier `doc/` :

- **[FACADE_PATTERN.md](./FACADE_PATTERN.md)** : Guide détaillé du Facade Pattern
- **[FACADE_USAGE_GUIDE.md](./FACADE_USAGE_GUIDE.md)** : Utilisation de la façade
- **[SONARQUBE_GUIDE.md](./SONARQUBE_GUIDE.md)** : Configuration et analyse SonarQube
- **[D1_EVALUATION_REPORT.md](../D1_EVALUATION_REPORT.md)** : Rapport d'évaluation complet

---

## 📊 Gestion de Projet

### Méthodologie Agile - Scrum

Le projet suit la méthodologie **Scrum** avec les rituels suivants :

#### Sprints

- **Durée :** 2 semaines
- **Sprint Planning :** Planification en début de sprint
- **Daily Stand-up :** Synchronisation quotidienne (Discord)
- **Sprint Review :** Démonstration des fonctionnalités
- **Sprint Retrospective :** Amélioration continue

#### Backlog

- **Product Backlog** : Priorisé par le Product Owner
- **User Stories** : Avec critères d'acceptation clairs
- **Estimation** : En Story Points (Planning Poker)

### Tableau Kanban - GitHub Projects

**Organisation du tableau :**

```
📋 TODO  →  🔄 IN PROGRESS  →  👁️ REVIEW  →  ✅ DONE
```

**Lien vers le Kanban :** [GitHub Projects - Team P](https://github.com/orgs/PNS-Conception/projects/111)

### Stratégie de Gestion des Branches (Git Flow)

```
main (production)
 └── feature/* (fonctionnalités)
      ├── feature/C1-browse-restaurants
      ├── feature/C2-add-to-cart
      └── feature/R2-add-dish
```

**Règles de contribution :**

1. **Branche `main`** : Version stable, protégée
2. **Branches de fonctionnalités** : `feature/[ID]-[description]`
3. **Pull Request obligatoire** : Minimum 1 reviewer
4. **CI/CD** : Tests automatiques avant merge
5. **Code Review** : Checklist de qualité respectée

### Outils de Communication

| Outil               | Usage                                |
|---------------------|--------------------------------------|
| **Discord**         | Communication quotidienne, stand-ups |
| **GitHub Issues**   | Suivi des bugs et user stories       |
| **GitHub Projects** | Tableau Kanban et planification      |
| **Google Drive**    | Documents partagés et maquettes      |

### Intégration Continue (CI)

**Pipeline GitHub Actions :**

```yaml
1. ✅ Compilation Maven
2. ✅ Tests unitaires (JUnit 5)
3. ✅ Tests BDD (Cucumber)
4. ✅ Analyse SonarQube
5. ✅ Rapport de couverture JaCoCo
6. ✅ Package JAR
```

**Triggers :**

- Push sur `main`
- Pull Request vers `main`
- Tag de version

### Qualité du Code

**Outils utilisés :**

- **SonarQube** : Analyse statique, détection de bugs, code smells
- **JaCoCo** : Couverture de tests (objectif : 85%+)
- **Checkstyle** : Respect des conventions Java
- **SpotBugs** : Détection de bugs potentiels

**Métriques cibles :**

- **Couverture de tests** : 85%+
- **Code Smells** : < 50
- **Bugs** : 0
- **Vulnerabilités** : 0
- **Note SonarQube** : A

---

## 📞 Contact et Support

Pour toute question ou problème :

1. **Issues GitHub** : Créer une issue sur le dépôt
2. **Discord Team P** : Canal `#sophiatech-eats`
3. **Email Product Owner** : saad.benaqa@etu.unice.fr

---

## 📜 Licence

Ce projet est développé dans le cadre du **Projet Fil Rouge** de 4ème année à **Polytech Nice Sophia**.

**Formation :** Ingénieur Sciences Informatiques - Parcours SI  
**Année académique :** 2025-2026  
**Équipe :** Team P

---

**© 2025 SophiaTech Eats - Team P - Polytech Nice Sophia**

