# Bilan de la branche `songomby_d2`

Ce document compare l'ERP avant et après la série de PR #12 à #25, fusionnée dans `songomby_d2`. Il liste ce qui existait déjà, ce qui a été créé et ce qui a été corrigé. Le frontend (`apps/`) n'a pas été modifié.

L'état complet et à jour est dans [FONCTIONNALITES.md](FONCTIONNALITES.md) ; le détail des endpoints dans [API_ENDPOINTS.md](API_ENDPOINTS.md).

## 1. Avant cette série

Point de départ : `main` au commit `d7b7e72` (« dockeriser la gateway et les services »).

| Domaine | Fonctionnalités présentes |
|---------|---------------------------|
| Accès | Connexion JWT, création et liste des comptes (ADMIN), compte admin initial, contrôle des rôles par module dans la gateway |
| Stock | Produits, mouvements, alertes, autonomie et risque de rupture, état de stock, résumé, fournisseurs et fiche 360, bons de commande (brouillon, validation, réception) |
| Vente | Clients et fiche 360, ventes (brouillon, validation avec sortie de stock), chiffre d'affaires, meilleurs produits |
| RH | Départements, employés, congés (demande, validation, refus), pointage, résumé |
| CRM | Historique des échanges, chronologie d'un client ou d'un fournisseur |
| IA | Assistant (Gemini) qui lit les modules et écrit après confirmation |
| Outillage | Swagger unique, `docker compose`, tests unitaires (124 tests) |

Les applications React Native étaient (et restent) des squelettes vides.

## 2. Fonctionnalités créées

| PR | Tâche | Ce qui est nouveau |
|----|-------|--------------------|
| #14 | C4 | **CORS sur la gateway** : les applications web peuvent appeler l'API. Origines réglables par `CORS_ALLOWED_ORIGINS`. Les réponses 401 et 403 gardent l'en-tête CORS, pour que le front lise le type d'erreur. |
| #16 | A6 | **Changement de rôle** d'un compte : `PUT /api/auth/users/{id}` (ADMIN). |
| #16 | A8 | **Désactivation et réactivation** d'un compte : `POST /api/auth/users/{id}/disable` et `/enable`. Un compte désactivé ne peut plus se connecter (401). |
| #16 | A7 | **Changement de son mot de passe** : `POST /api/auth/users/me/password`, ouvert à tous les rôles, mot de passe actuel exigé. |
| #16 | — | Un admin ne peut ni désactiver ni changer le rôle de son propre compte (422), pour éviter de se bloquer. |
| #17 | A9 | **Lien compte et employé** : `userId` optionnel sur la fiche employé ; un compte ne peut être lié qu'à un seul employé (409). |
| #19 | A10 | **Accès uniquement par la gateway** : chaque service refuse (401) un appel sans les en-têtes `X-User-*`. Vente et CRM recopient ces en-têtes quand ils appellent les autres services. Swagger, `/actuator` et `/login` restent joignables en direct. |
| #15, #22 à #25 | C6 | **Tests `@WebMvcTest`** de tous les controllers des 5 services : statuts HTTP, validation, erreurs typées. |
| #13 | — | [FONCTIONNALITES.md](FONCTIONNALITES.md) : liste des fonctionnalités. |

## 3. Corrections

| PR | Problème | Correction |
|----|----------|------------|
| #21 | Supprimer un produit qui avait des mouvements ou des lignes de bon de commande **réussissait en silence** : `productId` n'est pas une clé étrangère, l'historique devenait orphelin. | Suppression refusée (422 `BUSINESS`), comme pour les fournisseurs. |
| #20, #16 | Certaines erreurs renvoyaient la réponse par défaut de Spring au lieu du format commun `{status, type, message, timestamp}` : paramètre manquant, méthode non supportée, corps non JSON, adresse inconnue, exception inattendue. Dans auth : JSON mal formé et rôle inconnu aussi. | Toutes ces erreurs ont maintenant le format commun : 400, 405, 415, 404, ou 500 `SERVER`. Le détail d'une erreur inattendue va dans les logs, jamais dans la réponse. |
| #20 | Statuts manquants dans la doc des endpoints : 400 pour un paramètre ou un id mal formé, 400 au pointage ; bornes de `limit` non précisées. | [API_ENDPOINTS.md](API_ENDPOINTS.md) complété. |
| #18 | Deux fichiers de test du rh-service dépassaient la limite de 100 lignes (152 et 126 lignes). | Découpés en sept classes, une par service testé, avec les mêmes 24 tests. |
| #12 | Les plans indiquaient « À faire » pour des tâches déjà faites (C1 à C3, AI-1 à AI-3, AI-5, AI-9, SYS-12). | Statuts corrigés dans PLAN_API, PLAN_ERP et PLAN_CRM. |

## 4. Tests

| Module | Avant | Après |
|--------|------:|------:|
| gateway | 23 | 29 |
| auth-service | 3 | 42 |
| stock-service | 38 | 110 |
| vente-service | 27 | 78 |
| rh-service | 19 | 88 |
| crm-service | 14 | 38 |
| **Total** | **124** | **385** |

Tous passent sur `songomby_d2` (`mvn test` dans chaque module). Les points sensibles ont aussi été vérifiés sur les services lancés : préflight CORS, gestion des comptes, appel direct refusé, vente validée via la gateway qui débite le stock, suppression de produit refusée, format des erreurs.

## 5. Limites connues

- Un compte désactivé garde un token valable jusqu'à son expiration (`JWT_TTL_MINUTES`). Couper l'accès tout de suite demande un renouvellement de token (SYS-2).
- Les en-têtes `X-User-*` peuvent être imités par quelqu'un qui atteint directement le port d'un service. En production, ne pas exposer ces ports, ou ajouter un secret partagé entre la gateway et les services.
- La suppression d'un produit ne vérifie pas les ventes du vente-service (autre service).

## 6. Suite

1. Relire et fusionner la PR `songomby_d2` vers `main`.
2. Prochaines tâches backend : CRM phases 3 à 5 (tâches, relances, pipeline, segments) et IA de démonstration (AI-4, AI-6, AI-7). Voir [PLAN_CRM.md](PLAN_CRM.md) et [PLAN_ERP.md](PLAN_ERP.md).
3. Frontend : APP-1 à APP-3 (composants, connexion, page d'erreur), maintenant possibles grâce au CORS.
