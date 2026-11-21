# Plan d'implémentation des API par module

Légende : ✅ fait · ⬜ à faire · 🔗 dépend d'une autre tâche

Chaque tâche suit [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md) : séparation Reader / Creator / Updater / Deleter, 100 lignes max par fichier, DTO, tests unitaires obligatoires.

---

## 0. Socle commun (à faire en premier, valable pour les 3 services)

| # | Tâche | Détail | Statut |
|---|-------|--------|--------|
| C1 | Champ `type` dans `ApiError` | `NOT_FOUND`, `VALIDATION`, `IMMUTABLE`, `BUSINESS`, `UNAUTHORIZED`, `FORBIDDEN`, `SERVER` : permet au front d'ouvrir la bonne page d'erreur. Fait pour auth-service, stock-service et la gateway ; reste vente-service et rh-service. | ⬜ |
| C2 | `ImmutableResourceException` (409) | Levée quand on modifie ou supprime une entité `VALIDATED`. | ⬜ |
| C3 | Enum `DocumentStatus` (`DRAFT`, `VALIDATED`) | Réutilisé par les ventes, les bons de commande et les congés. | ⬜ |
| C4 | CORS sur la gateway | Autorise les apps React Native. | ⬜ |
| C5 | Client HTTP inter-services | `RestClient` + interface par service appelé (ex. `StockClient` dans vente-service). | ✅ |
| C6 | Tests d'intégration légers | `@WebMvcTest` par controller (statuts HTTP, validation). | ⬜ |

Les tâches C1 à C3 sont copiées dans chaque service (pas de bibliothèque partagée : chaque microservice reste indépendant).

---

## 0 bis. Module Auth — `services/auth-service` (port 8084)

Gère les **comptes de connexion** (différents des fiches employés du module RH). Rôles : `ADMIN`, `STOCK`, `VENTE`, `RH`.

| # | Tâche | Endpoint | Détail | Statut |
|---|-------|----------|--------|--------|
| A1 | Création de comptes | `POST /users` | Mot de passe haché (BCrypt), email unique, réservé à `ADMIN`. | ✅ |
| A2 | Liste des comptes | `GET /users` | Réservé à `ADMIN`. | ✅ |
| A3 | Connexion et JWT | `POST /login` | Renvoie un token signé avec l'email, l'id et le rôle. | ✅ |
| A4 | Compte admin initial | — | Créé au premier démarrage si la base est vide. | ✅ |
| A5 | Contrôle des rôles dans la gateway | — | 401 sans token, 403 si le rôle n'a pas accès au module (RH ne peut pas aller sur Vente ni Stock). | ✅ |
| A6 | Modification d'un compte | `PUT /users/{id}` | Changer le rôle, désactiver un compte. | ⬜ |
| A7 | Changement de mot de passe | `POST /users/me/password` | L'utilisateur change son propre mot de passe. | ⬜ |
| A8 | Désactivation d'un compte | `POST /users/{id}/disable` | Un compte désactivé ne peut plus se connecter. | ⬜ |
| A9 | Lier un compte à un employé | — | `userId` optionnel sur la fiche employé du module RH. | ⬜ 🔗 R2 |
| A10 | Services accessibles uniquement via la gateway | — | Chaque service refuse les appels sans les en-têtes `X-User-*` de la gateway. | ⬜ |
| A11 | Page de connexion et pages d'erreur 401/403 dans les 3 apps | — | Les erreurs `UNAUTHORIZED` et `FORBIDDEN` ouvrent la page d'erreur dédiée. | ⬜ |

La gateway transmet l'identité aux services (`X-User-Id`, `X-User-Email`, `X-User-Role`) : elle sert au journal d'audit (qui a validé quoi).

---

## 1. Module Stock — `services/stock-service` (port 8083)

| # | Tâche | Endpoint | Détail | Statut |
|---|-------|----------|--------|--------|
| S1 | CRUD produits | `GET/POST /products`, `GET/PUT/DELETE /products/{id}` | Reader / Creator / Updater / Deleter. | ✅ |
| S2 | Mouvements d'entrée / sortie | `POST/GET /products/{id}/movements` | Met à jour la quantité, refuse une sortie supérieure au stock. | ✅ |
| S3 | Recherche et filtre produits | `GET /products?search=&lowStock=` | Nouvelle méthode du `ProductReader`. | ✅ |
| S4 | Produits en alerte | `GET /products/low-stock` | Produits dont quantité ≤ seuil. | ✅ |
| S5 | Autonomie d'un produit | `GET /products/{id}/autonomy` | Moyenne des sorties sur 7 jours, jours restants, risque de rupture (`OK`, `WARNING`, `CRITICAL`). Calcul déterministe côté serveur : critique sous 3 jours, alerte sous 7 jours. | ✅ |
| S6 | Résumé du stock | `GET /summary` (donc `/api/stock/summary` via la gateway) | Nombre de produits, valeur totale du stock, nombre en alerte. | ✅ |
| S7 | Fournisseurs | `GET/POST/PUT/DELETE /suppliers` | CRUD complet, interdiction de supprimer un fournisseur lié à une commande. | ✅ |
| S8 | Bons de commande | `POST /purchase-orders`, `GET /purchase-orders[/{id}]` | Création en `DRAFT` avec lignes (produit, quantité, prix). | ✅ |
| S9 | Validation d'un bon de commande | `POST /purchase-orders/{id}/validate` | Passe en `VALIDATED`, devient immuable (C2). | ✅ |
| S10 | Réception d'une commande | `POST /purchase-orders/{id}/receive` | Crée automatiquement un mouvement `IN` par ligne. | ✅ |
| S11 | Tests | — | Tests du reader d'autonomie, de la validation et de la réception. | ✅ |

---

## 2. Module Vente — `services/vente-service` (port 8082)

| # | Tâche | Endpoint | Détail | Statut |
|---|-------|----------|--------|--------|
| V1 | CRUD clients | `GET/POST/PUT/DELETE /customers` | Nom, téléphone, adresse. | ✅ |
| V2 | Création d'une vente | `POST /sales` | Statut `DRAFT`, lignes (productId, quantité), le prix unitaire est copié au moment de la vente. | ✅ |
| V3 | Lecture des ventes | `GET /sales`, `GET /sales/{id}` | Filtres par date, client et statut. | ✅ |
| V4 | Modification d'une vente brouillon | `PUT/DELETE /sales/{id}` | Autorisé uniquement en `DRAFT`, sinon 409 (C2). | ✅ |
| V5 | Validation d'une vente | `POST /sales/{id}/validate` | Vérifie le stock, appelle `stock-service` pour créer une sortie par ligne, passe en `VALIDATED`. | ✅ |
| V6 | Gestion d'échec de stock | — | Si le stock est insuffisant (422 du stock-service), la vente reste `DRAFT` et l'erreur est de type `BUSINESS`. | ✅ |
| V7 | Chiffre d'affaires | `GET /sales/summary?from=&to=` | Total, nombre de ventes, panier moyen par jour. | ✅ |
| V8 | Meilleurs produits | `GET /sales/top-products?limit=` | Produits les plus vendus sur une période. | ✅ |
| V9 | Tests | — | Validation avec `StockClient` mocké (succès, stock insuffisant, service indisponible). | ✅ |

---

## 3. Module RH — `services/rh-service` (port 8081)

| # | Tâche | Endpoint | Détail | Statut |
|---|-------|----------|--------|--------|
| R1 | CRUD départements | `GET/POST/PUT/DELETE /departments` | Interdiction de supprimer un département qui a des employés. | ✅ |
| R2 | CRUD employés | `GET/POST/PUT/DELETE /employees` | Nom, poste, département, date d'embauche, salaire de base. | ✅ |
| R3 | Recherche d'employés | `GET /employees?search=&departmentId=` | Nouvelle méthode du `EmployeeReader`. | ✅ |
| R4 | Demande de congé | `POST /leaves` | Statut `DRAFT`, vérifie le solde de congés restant. | ✅ |
| R5 | Validation / refus de congé | `POST /leaves/{id}/validate`, `/reject` | Une fois validé, le congé est immuable (C2) et le solde est décompté. | ✅ |
| R6 | Pointage | `POST /attendances/check-in`, `/check-out`, `GET /attendances` | Un pointage enregistré ne se modifie pas : pas d'Updater ni de Deleter. | ✅ |
| R7 | Résumé RH | `GET /summary` (donc `/api/rh/summary` via la gateway) | Effectif total, effectif par département, personnes en congé aujourd'hui. | ✅ |
| R8 | Tests | — | Solde de congés, refus de modification d'un congé validé, pointage. | ✅ |

---

## 4. Ordre de réalisation conseillé

1. **Socle commun** C1 à C3 dans les trois services (une journée de travail en parallèle).
2. **En parallèle, une personne par module** :
   - Stock : S3 → S7 → S8 → S9 → S10 → S4 → S5 → S6 → S11
   - Vente : V1 → V2 → V3 → V4 → V5 → V6 → V7 → V8 → V9
   - RH : R1 → R2 → R3 → R4 → R5 → R6 → R7 → R8
3. **Intégration** : C5 (client inter-services) puis V5 (la vente validée modifie réellement le stock).
4. **Gateway** : C4, puis vérification de bout en bout via `/api/stock`, `/api/vente`, `/api/rh`.

## 5. Dépendances entre modules

```
vente-service  ──(sortie de stock à la validation)──►  stock-service
rh-service     (indépendant)
```

Seul le module Vente appelle un autre service. Le Stock et les RH restent autonomes, donc leurs équipes ne se bloquent pas entre elles.

## 6. Définition de « terminé » pour une tâche

- [ ] Reader / Creator / Updater / Deleter séparés, avec interfaces.
- [ ] Aucun fichier au-dessus de 100 lignes.
- [ ] DTO en entrée et en sortie, aucune entité JPA exposée.
- [ ] Erreurs avec `type` explicite.
- [ ] Données validées immuables.
- [ ] Tests unitaires du service qui passent (`mvn test`).
