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

Chaque service refuse (401 `UNAUTHORIZED`) un appel sans `X-User-Id` et `X-User-Role` : il faut passer par la gateway. Restent ouverts en direct : `/v3/api-docs`, `/swagger-ui`, `/actuator` et `POST /login` (auth). Les appels entre services (Vente vers Stock, CRM vers Vente et Stock) recopient ces en-têtes.

## 3. Format des erreurs

Toutes les erreurs ont la même forme :

```json
{ "status": 422, "type": "BUSINESS", "message": "Stock insuffisant pour Riz blanc : disponible 14, demandé 50", "timestamp": "2026-11-21T12:00:00Z" }
```

| `type` | Statut | Signification |
|--------|:------:|---------------|
| `VALIDATION` | 400 | Corps ou paramètre invalide (champ manquant, format incorrect, JSON mal formé, id non numérique, valeur d'énumération inconnue) |
| `VALIDATION` | 405, 415 | Méthode HTTP non prévue sur cette adresse ; corps qui n'est pas en JSON |
| `UNAUTHORIZED` | 401 | Token absent, invalide ou expiré ; identifiants incorrects |
| `FORBIDDEN` | 403 | Rôle non autorisé pour ce module |
| `NOT_FOUND` | 404 | Ressource ou adresse inexistante |
| `IMMUTABLE` | 409 | Modification d'une donnée déjà validée |
| `BUSINESS` | 409 ou 422 | Règle métier refusée (doublon, stock insuffisant, solde de congés insuffisant, suppression d'un élément utilisé) |
| `SERVER` | 503 | Un service appelé est indisponible (par exemple Stock depuis Vente) |
| `SERVER` | 500 | Erreur inattendue du service (le détail est dans ses logs, jamais dans la réponse) |

Les statuts 400 dus à un paramètre ou un id mal formé valent pour tous les endpoints et ne sont pas répétés dans les tableaux ci-dessous.

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
| DELETE | `/api/stock/products/{id}` | Supprime un produit. Refusé s'il a des mouvements ou figure dans un bon de commande | 204, 404, 422 |
| GET | `/api/stock/products/{id}/autonomy` | Moyenne vendue par jour (7 jours), jours restants et risque `OK`, `WARNING`, `CRITICAL` | 200, 404 |
| GET | `/api/stock/status` | État de stock complet : tous les produits avec autonomie et risque, du plus urgent au moins urgent. Filtre `risk` | 200, 400 |
| GET | `/api/stock/summary` | Nombre de produits, nombre en alerte, valeur totale du stock | 200 |

### Prévision de réapprovisionnement

Calcul fait par le code (pas par l'IA) sur les sorties `OUT` des 28 derniers jours, par jour calendaire UTC (jour sans sortie = 0) : moyenne `averageDailyOut` (μ), écart-type `dailyOutStdDev` (σ), délai `leadTimeDays` (L) = délai habituel du fournisseur du dernier bon contenant le produit (`leadTimeSource` `SUPPLIER`), sinon `replenishment.default-lead-days` (7, `DEFAULT`). Stock de sécurité `safetyStock` = ⌈1,65 × σ × √L⌉ (service 95 %), point de commande `reorderPoint` = μ × L + SS, `recommendedQuantity` = max(0, ⌈μ × (L + 30) + SS − stock⌉) (30 = `replenishment.coverage-days`), `coverageDays` = stock / μ (null si aucune sortie). `needsReorder` = stock ≤ point de commande, avec des sorties et assez d'historique. Moins de 14 jours depuis le premier mouvement : `confidence` = `INSUFFICIENT_DATA` et `recommendedQuantity` = null. `suggestedSupplierId` et la liste `assumptions` expliquent le résultat.

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/stock/products/{id}/replenishment` | Prévision d'un produit | 200, 400, 404 |
| GET | `/api/stock/replenishment` | Prévisions de tous les produits, à commander d'abord puis par jours de couverture croissants. `onlyNeeded` (`true` par défaut) : seulement les produits à commander | 200, 400 |

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
| POST | `/api/stock/suppliers` | Crée un fournisseur (nom unique, email, conditions de paiement, délai de livraison, `bankAccount` : coordonnées de paiement de référence, renvoyées seulement sous forme `bankAccountLast4`, `city` 80 caractères, `nif` 20 caractères, `stat` 30 caractères, tous trois optionnels) | 201, 400, 409 |
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

### Factures fournisseurs et Invoice Shield

Chaque facture est contrôlée par Invoice Shield avant toute décision. Codes d'alerte : `UNKNOWN_SUPPLIER`, `DUPLICATE_INVOICE`, `BANK_ACCOUNT_MISMATCH`, `BANK_ACCOUNT_UNVERIFIED`, `TOTALS_MISMATCH`, `INVALID_DATE`, `PROMPT_INJECTION`, `PURCHASE_ORDER_MISMATCH`, `OVER_INVOICED`, `AMOUNT_ANOMALY` ; gravité `BLOCKING` ou `WARNING`. Verdict : `CLEAR`, `WARNING` ou `BLOCKED`. Détail dans [NEW_FEATURES_IMPLEMENTATION.md](NEW_FEATURES_IMPLEMENTATION.md).

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| POST | `/api/stock/supplier-invoices/check` | Contrôle une facture sans l'enregistrer. Renvoie `{verdict, alerts[]}` | 200, 400 |
| POST | `/api/stock/supplier-invoices` | Enregistre une facture (`supplierId`, `invoiceNumber`, `invoiceDate`, `lines[]`, `totalAmount` obligatoires ; `purchaseOrderId`, `bankAccount`, `subtotal`, `taxAmount`, `notes` optionnels) après contrôle : statut `CLEARED`, ou `BLOCKED` si une alerte est bloquante | 201, 400 |
| GET | `/api/stock/supplier-invoices` | Liste, de la plus récente à la plus ancienne. Filtres `status` (`CLEARED`, `BLOCKED`, `APPROVED`, `REJECTED`), `supplierId` | 200, 400 |
| GET | `/api/stock/supplier-invoices/{id}` | Détail avec lignes et alertes. Le compte bancaire n'est jamais renvoyé en clair (`bankAccountLast4`) | 200, 404 |
| POST | `/api/stock/supplier-invoices/{id}/approve` | Approuve. Une facture `BLOCKED` exige le rôle `ADMIN`, un autre utilisateur que son auteur, et `{"justification": "..."}`. Devient immuable | 200, 400, 403, 404, 409 |
| POST | `/api/stock/supplier-invoices/{id}/reject` | Rejette avec `{"reason": "..."}` obligatoire. Devient immuable | 200, 400, 404, 409 |
| GET | `/api/stock/supplier-invoices/{id}/history` | Journal d'audit : création, contrôle (verdict et codes), décision (qui, quand, justification) | 200, 404 |

## 6. Vente : `/api/vente` (rôles VENTE, ADMIN)

### Clients

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/vente/customers` | Liste. Filtres `search` (nom, téléphone, email, ville) et `type` (`PARTICULIER`, qui inclut les clients sans type, ou `ENTREPRISE`). Pagination optionnelle `page` et `size` ; le total est dans l'en-tête `X-Total-Count` | 200, 400 |
| GET | `/api/vente/customers/{id}` | Détail | 200, 404 |
| POST | `/api/vente/customers` | Crée un client (`name`, téléphone, email, adresse, ville, `type` PARTICULIER ou ENTREPRISE, notes, `nif` 20 caractères, `stat` 30 caractères, `creditLimit` plafond de crédit en Ariary, positif ou nul, 2 décimales ; absent = pas de plafond) | 201, 400 |
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
| GET | `/api/vente/sales/top-products` | Produits les plus vendus. Paramètre `limit` (5 par défaut ; une valeur hors de 1 à 50 est ramenée dans ces bornes) | 200 |

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
| POST | `/api/rh/attendances/check-in` | Pointe l'entrée. Refusé si un pointage est déjà ouvert | 201, 400, 404, 422 |
| POST | `/api/rh/attendances/check-out` | Pointe la sortie et calcule la durée travaillée. Sans pointage d'entrée ouvert (ou employé inconnu) : 422 | 200, 400, 422 |
| GET | `/api/rh/summary` | Effectif, effectif par département, personnes en congé aujourd'hui | 200 |

## 8. CRM : `/api/crm` (rôles CRM, ADMIN)

`{partyType}` vaut `CUSTOMER` (client, dans Vente) ou `SUPPLIER` (fournisseur, dans Stock). Le CRM vérifie que le tiers existe en interrogeant le service concerné : si ce service est arrêté, la réponse est 503.

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/crm/parties/{partyType}/{partyId}/interactions` | Échanges enregistrés avec ce tiers, du plus récent au plus ancien | 200, 400, 404, 503 |
| POST | `/api/crm/parties/{partyType}/{partyId}/interactions` | Enregistre un échange : `type` (`APPEL`, `EMAIL`, `REUNION`, `VISITE`, `NOTE`), `summary` (2000 caractères maximum), `occurredAt` optionnel (pas dans le futur). L'auteur est l'email de l'utilisateur connecté. Un échange ne se modifie pas | 201, 400, 404, 503 |
| GET | `/api/crm/parties/{partyType}/{partyId}/timeline` | Chronologie : échanges, ventes validées (client) ou bons de commande validés ou reçus (fournisseur) | 200, 400, 404, 503 |

### Contacts (écran Relations)

Un contact réunit un client de Vente ou un fournisseur de Stock et son dernier échange enregistré au CRM. Le CRM appelle Vente puis Stock l'un après l'autre avec l'identité de l'utilisateur : si l'un des deux est arrêté, la réponse est 503 `SERVER`.

Un contact est « à relancer » (`FOLLOW_UP`) quand la date la plus récente entre son dernier échange et sa création est plus vieille que `crm.follow-up-days` jours (30 par défaut), jours comptés dans le fuseau `app.zone` (Indian/Antananarivo) : 30 jours reste « actif », 31 jours passe « à relancer ».

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| GET | `/api/crm/contacts` | Liste triée par dernier échange (les contacts sans échange en dernier, puis par nom). Filtres `kind` (`ALL` par défaut, `CUSTOMER`, `SUPPLIER`), `search` (nom, téléphone, email, ville, NIF, STAT), `followUp=true` (à relancer seulement), `professional=true` (clients `ENTREPRISE` seulement). Chaque contact : `kind`, `partyId`, `name`, `initials`, `typeLabel` (« Client · professionnel », « Client · particulier » ou « Fournisseur »), `city`, `phone`, `email`, `lastInteractionAt`, `status` (`ACTIVE` ou `FOLLOW_UP`), `nif`, `stat`, `creditLimit`, `outstanding` (encours crédit du client, 0 sans crédit, absent pour un fournisseur), `overLimit` (encours supérieur au plafond), `professional` | 200, 400, 503 |
| GET | `/api/crm/contacts/counts` | Nombre de contacts par onglet : `all`, `customers`, `suppliers`, `followUp`, `professional`. Filtre `search` optionnel, le même que la liste | 200, 503 |
| GET | `/api/crm/contacts/summary` | Indicateurs : `activeCustomers` (clients distincts ayant une vente validée depuis le début du trimestre), `totalCustomers`, `suppliers`, `openPurchaseOrders` (bons validés non reçus), `followUp`, `creditOutstanding` (somme des encours), `overLimitCustomers` | 200, 503 |
| POST | `/api/crm/contacts` | Crée le contact chez son propriétaire : `kind` `CUSTOMER` (vers `POST /api/vente/customers`) ou `SUPPLIER` (vers `POST /api/stock/suppliers`), `name`, `phone`, `email`, `city` (80 caractères), `nif` (20), `stat` (30) ; pour un client seulement : `type` (`PARTICULIER` par défaut ou `ENTREPRISE`) et `creditLimit`. Un refus du service propriétaire (nom de fournisseur déjà pris par exemple) revient en 422 `BUSINESS` avec son message | 201, 400, 422, 503 |

Tant que Vente n'expose pas `GET /sales/credit-by-customer`, l'encours vaut 0 pour tous les clients.

---

## 9. Voix : `/api/voice` (rôles STOCK, VENTE, RH, CRM, ADMIN)

Service Python (FastAPI), hors Swagger de la gateway.

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/api/voice/health` | État du service |
| POST | `/api/voice/transcribe` | Transcrit un fichier audio (multipart) en texte |
| WS | `/api/voice/stream` | Transcription en temps réel (audio PCM envoyé en continu ; messages `partial`, `processing`, `final`) |
| GET | `/api/voice/demo` | Page de démonstration |

## 10. Assistant IA : `/api/ai` (tous les rôles connectés)

L'assistant appelle les autres modules via la gateway **avec le token de l'utilisateur** : il n'a jamais plus de droits que lui, et Gemini ne reçoit que les outils du rôle connecté. Détails et exemples : [services/ai-service/README.md](../services/ai-service/README.md).

| Méthode | Chemin | Description | Statuts |
|---------|--------|-------------|---------|
| POST | `/api/ai/agent/chat` | Réponse complète. Corps `message`, `conversation_id` optionnel. La réponse contient `trust` : `label` (`FACT`, `ESTIMATION`, `RECOMMENDATION`, `NONE`), `verifiedNumbers`, `unverifiedNumbers`, `sources` | 200, 400, 401, 404, 429, 503, 504 |
| POST | `/api/ai/agent/chat/stream` | Même chose en Server-Sent Events ; `trust` est dans l'événement `done` | 200, 401, 404, 429, 503 |
| GET | `/api/ai/tools` | Outils disponibles pour le rôle connecté | 200, 401 |
| GET, DELETE | `/api/ai/agent/conversations/{id}` | Historique, réinitialisation | 200, 404 |
| POST | `/api/ai/agent/conversations/{id}/actions/{actionId}/confirm` | Confirme une écriture proposée par l'IA (exécutée avec le token de l'utilisateur) | 200, 404, 4xx de l'ERP |
| POST | `/api/ai/agent/conversations/{id}/actions/{actionId}/cancel` | Annule une écriture proposée | 200, 404 |
| POST | `/api/ai/invoices/extract` | Rôles STOCK, ADMIN. `multipart/form-data`, champ `file` : image JPEG, PNG, WEBP ou PDF (8 Mo par défaut). Extrait la facture fournisseur, retrouve le fournisseur et renvoie le contrôle Invoice Shield `{extracted, supplierMatch, shield}`. Rien n'est enregistré | 200, 400, 403, 413, 429, 503 |
| GET | `/api/ai/audit` | ADMIN. Journal des interactions avec l'IA (plus récentes d'abord). Filtres `limit` (1 à 500, défaut 50), `userId` | 200, 400, 403 |

Au-delà de `AI_RATE_LIMIT_PER_MINUTE` requêtes par minute et par utilisateur (chat, flux, extraction) : **429** `RATE_LIMIT`, avec l'en-tête `Retry-After`.

---

## 11. Pour aller plus loin

- Plans de développement : [PLAN_API.md](PLAN_API.md), [PLAN_CRM.md](PLAN_CRM.md), [PLAN_ERP.md](PLAN_ERP.md).
- Règles de code : [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md).
