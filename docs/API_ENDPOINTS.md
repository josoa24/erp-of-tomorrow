# Documentation des endpoints

Référence de tous les endpoints de l'ERP, avec le rôle nécessaire pour chacun. Une version interactive (Swagger) est disponible quand les services tournent.

## 1. Swagger interactif

Une seule interface regroupe les 5 services : **http://localhost:8080/swagger-ui.html**

1. Démarrer les services et la gateway (voir le [README](../README.md#lancer-le-projet)).
2. Ouvrir l'adresse ci-dessus. La liste « Select a definition » en haut à droite permet de choisir Auth, Stock, Vente, RH ou CRM.
3. Dans la définition **Auth**, exécuter `POST /login` (« Try it out ») avec un email et un mot de passe. Copier le champ `token` de la réponse.
4. Cliquer sur **Authorize**, coller le token, valider.
5. Tester les endpoints des autres définitions : les appels passent par la gateway, qui contrôle le rôle.

Chaque endpoint affiche « Rôles autorisés : ... » dans sa description. Les spécifications OpenAPI brutes sont aussi disponibles :

| Service | Spécification (via la gateway) |
|---------|-------------------------------|
| Auth | `/api/auth/v3/api-docs` |
| Stock | `/api/stock/v3/api-docs` |
| Vente | `/api/vente/v3/api-docs` |
| RH | `/api/rh/v3/api-docs` |
| CRM | `/api/crm/v3/api-docs` |

Les spécifications et l'interface Swagger sont publiques (sans token) pour faciliter le développement. Elles ne donnent accès à aucune donnée : les endpoints métier restent protégés. À restreindre avant une mise en production.

## 2. Authentification et rôles

Toutes les requêtes passent par la gateway (`http://localhost:8080`) avec l'en-tête :

```
Authorization: Bearer <token>
```

Seul `POST /api/auth/login` est public. Sans token ou avec un token invalide ou expiré : **401**. Avec un rôle qui n'a pas accès au module : **403**.

| Rôle | `/api/stock` | `/api/vente` | `/api/rh` | `/api/crm` | `/api/auth/users` |
|------|:-----------:|:------------:|:---------:|:----------:|:-----------------:|
| `STOCK` | oui | non | non | non | non |
| `VENTE` | non | oui | non | non | non |
| `RH` | non | non | oui | non | non |
| `CRM` | non | non | non | oui | non |
| `ADMIN` | oui | oui | oui | oui | oui |

Exception : `/api/auth/users/me/...` (son propre compte) est ouvert à tous les rôles connectés.

La gateway transmet l'identité aux services dans les en-têtes `X-User-Id`, `X-User-Email` et `X-User-Role` (utilisés par le CRM pour enregistrer l'auteur d'un échange).

## 3. Format des erreurs

Toutes les erreurs ont la même forme :

```json
{ "status": 422, "type": "BUSINESS", "message": "Stock insuffisant pour Riz blanc : disponible 14, demandé 50", "timestamp": "2026-11-21T12:00:00Z" }
```

| `type` | Statut | Signification |
|--------|:------:|---------------|
| `VALIDATION` | 400 | Corps ou paramètre invalide (champ manquant, format incorrect) |
| `UNAUTHORIZED` | 401 | Token absent, invalide ou expiré ; identifiants incorrects |
| `FORBIDDEN` | 403 | Rôle non autorisé pour ce module |
| `NOT_FOUND` | 404 | Ressource inexistante |
| `IMMUTABLE` | 409 | Modification d'une donnée déjà validée |
| `BUSINESS` | 409 ou 422 | Règle métier refusée (doublon, stock insuffisant, solde de congés insuffisant, suppression d'un élément utilisé) |
| `SERVER` | 503 | Un service appelé est indisponible (par exemple Stock depuis Vente) |

Une donnée validée (vente, bon de commande, congé) ne peut plus être modifiée ni supprimée : on corrige par une nouvelle écriture.

---

## 4. Auth : `/api/auth`

| Méthode | Chemin | Rôles | Description | Statuts |
|---------|--------|-------|-------------|---------|
| POST | `/api/auth/login` | public | Connexion. Corps : `email`, `password`. Renvoie `token`, `email`, `role` | 200, 400, 401 |
| GET | `/api/auth/users` | ADMIN | Liste des comptes | 200 |
| POST | `/api/auth/users` | ADMIN | Crée un compte. `email` unique, `password` de 8 caractères minimum, `role` parmi ADMIN, STOCK, VENTE, RH, CRM | 201, 400, 409 |
| PUT | `/api/auth/users/{id}` | ADMIN | Change le rôle d'un compte. Corps : `role`. Refusé sur son propre compte | 200, 400, 404, 422 |
| POST | `/api/auth/users/{id}/disable` | ADMIN | Désactive un compte : il ne peut plus se connecter. Refusé sur son propre compte | 200, 404, 422 |
| POST | `/api/auth/users/{id}/enable` | ADMIN | Réactive un compte | 200, 404, 422 |
| POST | `/api/auth/users/me/password` | tous les rôles | Change son propre mot de passe. Corps : `currentPassword`, `newPassword` (8 caractères minimum, différent de l'actuel) | 204, 400, 422 |

Un compte désactivé reçoit 401 à la connexion. Un token déjà émis reste valable jusqu'à son expiration (`JWT_TTL_MINUTES`).

## 5. Stock : `/api/stock` (rôles STOCK, ADMIN)

### Produits et état du stock

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/stock/products` | Liste des produits. Filtres : `search` (nom ou SKU), `lowStock` (`true` ou `false`) | 200, 400 |
| GET | `/api/stock/products/low-stock` | Produits dont la quantité est inférieure ou égale au seuil | 200 |
| GET | `/api/stock/products/{id}` | Détail d'un produit | 200, 404 |
| POST | `/api/stock/products` | Crée un produit (`name`, `sku` unique, `unit`, `quantity`, `minThreshold`, `unitPrice`) | 201, 400, 409 |
| PUT | `/api/stock/products/{id}` | Modifie nom, unité, seuil et prix. La quantité ne change que par les mouvements | 200, 400, 404 |
| DELETE | `/api/stock/products/{id}` | Supprime un produit | 204, 404 |
| GET | `/api/stock/products/{id}/autonomy` | Moyenne vendue par jour (7 jours), jours restants et risque `OK`, `WARNING`, `CRITICAL` | 200, 404 |
| GET | `/api/stock/status` | État de stock complet : tous les produits avec autonomie et risque, du plus urgent au moins urgent. Filtre `risk` | 200, 400 |
| GET | `/api/stock/summary` | Nombre de produits, nombre en alerte, valeur totale du stock | 200 |

### Mouvements de stock

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/stock/products/{productId}/movements` | Historique des entrées et sorties d'un produit | 200, 404 |
| POST | `/api/stock/products/{productId}/movements` | Enregistre une entrée (`IN`) ou une sortie (`OUT`) et met la quantité à jour. Refusée si la sortie dépasse le stock. Les mouvements ne se modifient pas | 201, 400, 404, 422 |

### Fournisseurs

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/stock/suppliers` | Liste. Filtre `search` (nom, téléphone, email) | 200 |
| GET | `/api/stock/suppliers/{id}` | Détail | 200, 404 |
| POST | `/api/stock/suppliers` | Crée un fournisseur (nom unique, email, conditions de paiement, délai de livraison) | 201, 400, 409 |
| PUT | `/api/stock/suppliers/{id}` | Remplace les champs du fournisseur (les champs omis sont effacés) | 200, 400, 404, 409 |
| DELETE | `/api/stock/suppliers/{id}` | Supprime. Refusé s'il a des bons de commande | 204, 404, 422 |
| GET | `/api/stock/suppliers/{id}/profile` | Fiche 360 : commandes, total commandé, délai moyen de réception, produits fournis, dernières commandes | 200, 404 |

### Bons de commande

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/stock/purchase-orders` | Liste. Filtres `supplierId`, `status` (`DRAFT`, `VALIDATED`, `RECEIVED`) | 200, 400 |
| GET | `/api/stock/purchase-orders/{id}` | Détail avec lignes | 200, 404 |
| POST | `/api/stock/purchase-orders` | Crée un bon en brouillon (fournisseur, lignes produit, quantité, prix) | 201, 400, 404 |
| POST | `/api/stock/purchase-orders/{id}/validate` | Valide le bon, qui devient immuable | 200, 404, 409 |
| POST | `/api/stock/purchase-orders/{id}/receive` | Réceptionne un bon validé : une entrée de stock par ligne | 200, 404, 409, 422 |

## 6. Vente : `/api/vente` (rôles VENTE, ADMIN)

### Clients

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/vente/customers` | Liste. Filtre `search` (nom, téléphone, email, ville). Pagination optionnelle `page` et `size` ; le total est dans l'en-tête `X-Total-Count` | 200, 400 |
| GET | `/api/vente/customers/{id}` | Détail | 200, 404 |
| POST | `/api/vente/customers` | Crée un client (`name`, téléphone, email, adresse, ville, `type` PARTICULIER ou ENTREPRISE, notes) | 201, 400 |
| PUT | `/api/vente/customers/{id}` | Remplace les champs du client (les champs omis sont effacés) | 200, 400, 404 |
| DELETE | `/api/vente/customers/{id}` | Supprime. Refusé si le client a des ventes | 204, 404, 422 |
| GET | `/api/vente/customers/{id}/profile` | Fiche 360 : ventes validées, total acheté, panier moyen, dernier achat, produits les plus achetés, 5 dernières ventes | 200, 404 |

### Ventes

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/vente/sales` | Liste. Filtres `customerId`, `status` (`DRAFT`, `VALIDATED`), `from` et `to` (dates `AAAA-MM-JJ`) | 200, 400 |
| GET | `/api/vente/sales/{id}` | Détail avec lignes | 200, 404 |
| POST | `/api/vente/sales` | Crée une vente en brouillon. Le nom et le prix de chaque produit sont copiés depuis le Stock | 201, 400, 404, 422, 503 |
| PUT | `/api/vente/sales/{id}` | Remplace client et lignes d'une vente en brouillon | 200, 400, 404, 409, 422, 503 |
| DELETE | `/api/vente/sales/{id}` | Supprime une vente en brouillon | 204, 404, 409 |
| POST | `/api/vente/sales/{id}/validate` | Valide : une sortie de stock par ligne. Si une ligne échoue, les lignes déjà débitées sont restaurées et la vente reste en brouillon | 200, 404, 409, 422, 503 |
| GET | `/api/vente/sales/summary` | Chiffre d'affaires, nombre de ventes, panier moyen et détail par jour. Filtres `from`, `to` | 200, 400 |
| GET | `/api/vente/sales/top-products` | Produits les plus vendus. Paramètre `limit` (5 par défaut, 50 au maximum) | 200 |

## 7. RH : `/api/rh` (rôles RH, ADMIN)

### Départements et employés

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/rh/departments` | Liste des départements | 200 |
| GET | `/api/rh/departments/{id}` | Détail | 200, 404 |
| POST | `/api/rh/departments` | Crée un département (nom unique, sans tenir compte de la casse) | 201, 400, 409 |
| PUT | `/api/rh/departments/{id}` | Renomme | 200, 400, 404, 409 |
| DELETE | `/api/rh/departments/{id}` | Supprime. Refusé s'il a des employés | 204, 404, 422 |
| GET | `/api/rh/employees` | Liste. Filtres `search` (nom, prénom, email), `departmentId` | 200 |
| GET | `/api/rh/employees/{id}` | Détail avec solde de congés | 200, 404 |
| POST | `/api/rh/employees` | Crée un employé (solde de congés initial : 25 jours ; email unique). `userId` optionnel : compte de connexion lié, un compte par employé au plus | 201, 400, 404, 409 |
| PUT | `/api/rh/employees/{id}` | Modifie un employé (le solde ne change que par les congés) | 200, 400, 404, 409 |
| DELETE | `/api/rh/employees/{id}` | Supprime. Refusé s'il a des congés ou des pointages | 204, 404, 422 |

### Congés

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/rh/leaves` | Liste. Filtres `employeeId`, `status` (`DRAFT`, `VALIDATED`, `REJECTED`) | 200, 400 |
| GET | `/api/rh/leaves/{id}` | Détail | 200, 404 |
| POST | `/api/rh/leaves` | Crée une demande en brouillon. Refusée si le solde est insuffisant, si la fin précède le début ou si la période chevauche une autre demande | 201, 400, 404, 422 |
| POST | `/api/rh/leaves/{id}/validate` | Valide : décompte le solde, la demande devient immuable | 200, 404, 409, 422 |
| POST | `/api/rh/leaves/{id}/reject` | Rejette : le solde ne change pas, la demande devient immuable | 200, 404, 409 |

### Pointage et résumé

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/rh/attendances` | Liste. Filtres `employeeId`, `date` | 200, 400 |
| POST | `/api/rh/attendances/check-in` | Pointe l'entrée. Refusé si un pointage est déjà ouvert | 201, 404, 422 |
| POST | `/api/rh/attendances/check-out` | Pointe la sortie et calcule la durée travaillée | 200, 422 |
| GET | `/api/rh/summary` | Effectif, effectif par département, personnes en congé aujourd'hui | 200 |

## 8. CRM : `/api/crm` (rôles CRM, ADMIN)

`{partyType}` vaut `CUSTOMER` (client, dans Vente) ou `SUPPLIER` (fournisseur, dans Stock). Le CRM vérifie que le tiers existe en interrogeant le service concerné : si ce service est arrêté, la réponse est 503.

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/crm/parties/{partyType}/{partyId}/interactions` | Échanges enregistrés avec ce tiers, du plus récent au plus ancien | 200, 400, 404, 503 |
| POST | `/api/crm/parties/{partyType}/{partyId}/interactions` | Enregistre un échange : `type` (`APPEL`, `EMAIL`, `REUNION`, `VISITE`, `NOTE`), `summary` (2000 caractères maximum), `occurredAt` optionnel (pas dans le futur). L'auteur est l'email de l'utilisateur connecté. Un échange ne se modifie pas | 201, 400, 404, 503 |
| GET | `/api/crm/parties/{partyType}/{partyId}/timeline` | Chronologie : échanges, ventes validées (client) ou bons de commande validés ou reçus (fournisseur) | 200, 400, 404, 503 |

---

## 9. Pour aller plus loin

- Plans de développement : [PLAN_API.md](PLAN_API.md), [PLAN_CRM.md](PLAN_CRM.md), [PLAN_ERP.md](PLAN_ERP.md).
- Règles de code : [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md).
