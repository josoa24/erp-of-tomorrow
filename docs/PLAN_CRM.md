# Plan CRM : gestion complète des clients et des fournisseurs

Ce plan complète [PLAN_API.md](PLAN_API.md). Le plan de tout ce qui manque à l'ERP (finance, achats, stock avancé, RH avancé, IA, applications) est dans [PLAN_ERP.md](PLAN_ERP.md).

Statut : Fait ou À faire (précisé sur chaque tâche). Phases 0 à 2 faites ; phases 3 à 8 à faire. Taille : S (moins d'une demi-journée), M (une journée), L (plusieurs jours).

Chaque tâche suit [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md) : Reader / Creator / Updater / Deleter, 100 lignes maximum par fichier, DTO, erreurs typées, tests unitaires, données validées immuables.

---

## 1. Point de départ

Déjà en place :
- Clients dans `vente-service` : nom, téléphone, adresse (CRUD, suppression refusée si le client a des ventes).
- Fournisseurs dans `stock-service` : nom, téléphone, email, adresse (CRUD, suppression refusée s'il a des bons de commande).
- Liens avec l'activité : `GET /sales?customerId=` et `GET /purchase-orders?supplierId=`.

Manque : fiche complète, historique des échanges, relances, pipeline commercial, segmentation, créances.

---

## 2. Décisions d'architecture proposées

| # | Décision | Pourquoi |
|---|----------|----------|
| D1 | Nouveau service `crm-service` (port 8085, schéma `crm`) pour les interactions, tâches, opportunités et segments | Chaque module garde son rôle ; le CRM ne grossit pas Vente ni Stock |
| D2 | Les clients restent dans `vente-service`, les fournisseurs dans `stock-service` (pas de migration) | Aucune donnée à déplacer, mise en place progressive |
| D3 | `crm-service` référence un tiers par `partyType` (`CUSTOMER` ou `SUPPLIER`) et `partyId`, et appelle les autres services par client HTTP | Même méthode que `StockClient` dans Vente |
| D4 | Les échanges enregistrés (interactions) sont créés une fois et jamais modifiés ; une correction est une nouvelle note | Règle des données immuables |
| D5 | Aucune donnée personnelle réelle dans le dépôt (il est public) | Sécurité |

## 3. Questions à trancher avant de démarrer

1. Qui utilise le CRM : un nouveau rôle `CRM`, ou le rôle `VENTE` (et `STOCK` pour les fournisseurs) ?
2. Les factures et paiements (créances) sont-ils dans le périmètre ? Voir la phase 6 et [PLAN_ERP.md](PLAN_ERP.md).
3. Envoi réel d'emails ou de SMS, ou seulement un journal des échanges ?
4. Les écrans du CRM : un dossier `novaui/src/features/crm/` dans l'application web unique (décidé : plus d'application par module).

---

## Phase 0. Socle

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-1 | Squelette `crm-service` | pom, classe principale, `application.yml`, profil `h2`, port 8085, schéma `crm` dans `docker-compose.yml`, erreurs typées | M | | Fait |
| CRM-2 | Rôle `CRM` dans `auth-service` | Nouvelle valeur de l'énumération `Role` | S | Q1 | Fait |
| CRM-3 | Route gateway `/api/crm/**` | Route et règle d'accès par rôle, tests de `RoleAccessPolicy` | S | CRM-2 | Fait |
| CRM-4 | Clients HTTP vers Vente et Stock | `CustomerClient` et `SupplierClient`, 404 en erreur métier, 503 si indisponible | M | CRM-1 | Fait |
| CRM-5 | Vérification d'un tiers | Refuser `partyId` inexistant avant d'enregistrer une interaction, une tâche ou une opportunité | S | CRM-4 | Fait |

## Phase 1. Fiches enrichies (dans Vente et Stock, sans attendre `crm-service`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-6 | Clients : email, type (particulier ou entreprise), ville, notes | Nouveaux champs optionnels, validation de l'email | S | | Fait |
| CRM-7 | Clients : recherche et pagination | `GET /customers?search=&page=&size=` | S | | Fait |
| CRM-8 | Fournisseurs : recherche, conditions de paiement, délai de livraison habituel | Nouveaux champs et `GET /suppliers?search=` | S | | Fait |
| CRM-9 | Fiche client 360 | `GET /customers/{id}/profile` : total acheté, nombre de ventes, panier moyen, date du dernier achat, produits les plus achetés, 5 dernières ventes | M | | Fait |
| CRM-10 | Fiche fournisseur 360 | `GET /suppliers/{id}/profile` : total commandé, nombre de commandes, délai moyen de réception, produits fournis, derniers bons | M | | Fait |
| CRM-11 | Tests des deux fiches | Client sans vente, fournisseur sans commande, plusieurs ventes | S | CRM-9, CRM-10 | Fait |

## Phase 2. Historique des échanges

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-12 | Interactions | Type (`APPEL`, `EMAIL`, `REUNION`, `VISITE`, `NOTE`), tiers, date, résumé, auteur pris dans `X-User-Email` ; création et lecture uniquement | M | CRM-5 | Fait |
| CRM-13 | Liste des interactions d'un tiers | `GET /parties/{type}/{id}/interactions` | S | CRM-12 | Fait |
| CRM-14 | Chronologie unifiée | `GET /parties/{type}/{id}/timeline` : interactions, ventes (clients) ou bons de commande (fournisseurs), triés par date | M | CRM-12, CRM-4 | Fait |
| CRM-15 | Tests | Ordre de la chronologie, tiers inconnu, service Vente indisponible | S | CRM-14 | Fait |

## Phase 3. Tâches et relances

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-16 | Tâches | Titre, échéance, priorité, responsable, tiers, statut `A_FAIRE`, `FAITE`, `ANNULEE` | M | CRM-5 | À faire |
| CRM-17 | CRUD des tâches | Reader / Creator / Updater / Deleter ; une tâche faite ou annulée devient immuable | M | CRM-16 | À faire |
| CRM-18 | Recherche des tâches | `GET /tasks?assignee=&status=&overdue=true` | S | CRM-17 | À faire |
| CRM-19 | Clients à relancer | `GET /follow-ups?days=30` : clients sans achat depuis N jours et sans tâche ouverte | M | CRM-9, CRM-17 | À faire |
| CRM-20 | Création d'une tâche de relance depuis la liste | `POST /follow-ups/{customerId}/task` | S | CRM-19 | À faire |
| CRM-21 | Tests | Tâche en retard, relance sans doublon, tâche faite non modifiable | S | CRM-20 | À faire |

## Phase 4. Pipeline commercial

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-22 | Opportunités | Titre, client, montant estimé, probabilité, étape (`NOUVEAU`, `QUALIFIE`, `PROPOSITION`, `GAGNE`, `PERDU`), date de clôture prévue | M | CRM-5 | À faire |
| CRM-23 | CRUD et changement d'étape | Transitions autorisées uniquement ; `GAGNE` et `PERDU` sont immuables ; motif obligatoire pour `PERDU` | M | CRM-22 | À faire |
| CRM-24 | Conversion en vente | Une opportunité gagnée crée une vente en brouillon dans `vente-service` | M | CRM-23, CRM-4 | À faire |
| CRM-25 | Résumé du pipeline | `GET /pipeline/summary` : nombre et montant par étape, valeur pondérée (prévision) | S | CRM-23 | À faire |
| CRM-26 | Statistiques des pertes | Répartition des motifs de perte | S | CRM-23 | À faire |
| CRM-27 | Tests | Transitions interdites, conversion échouée, prévision pondérée | S | CRM-25 | À faire |

## Phase 5. Segmentation et analyse

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-28 | Segments RFM | Récence, fréquence, montant calculés depuis les ventes validées : `VIP`, `FIDELE`, `OCCASIONNEL`, `ENDORMI`, `A_RISQUE` ; règles fixes et documentées | L | CRM-9 | À faire |
| CRM-29 | Liste par segment | `GET /customers/segments` (effectifs) et `GET /customers?segment=` | M | CRM-28 | À faire |
| CRM-30 | Meilleurs clients | `GET /customers/top?by=revenue&limit=` | S | CRM-9 | À faire |
| CRM-31 | Risque de départ | Comparer le délai habituel entre achats au temps écoulé depuis le dernier achat | M | CRM-28 | À faire |
| CRM-32 | Classement des fournisseurs | Note selon les délais de réception et la stabilité des prix, `GET /suppliers/ranking` | M | CRM-10 | À faire |
| CRM-33 | Tableau de bord CRM | `GET /crm/dashboard` : clients actifs, nouveaux du mois, tâches en retard, pipeline pondéré, clients à relancer | M | CRM-19, CRM-25 | À faire |
| CRM-34 | Tests | Cas limites des segments (client sans achat, un seul achat) | S | CRM-28 | À faire |

## Phase 6. Créances et dettes (optionnel, gros chantier)

Dépend de la facturation décrite dans [PLAN_ERP.md](PLAN_ERP.md) (tâches FIN).

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-35 | Solde d'un client | `GET /customers/{id}/balance` : facturé, payé, reste à payer | M | FIN-1, FIN-2 | À faire |
| CRM-36 | Clients en retard de paiement | `GET /receivables/overdue` | M | CRM-35 | À faire |
| CRM-37 | Solde d'un fournisseur | `GET /suppliers/{id}/balance` : reçu, payé, reste à payer | M | FIN-3, FIN-4 | À faire |
| CRM-38 | Tests | Paiement partiel, surpaiement refusé | S | CRM-37 | À faire |

## Phase 7. Recherche et assistant

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-39 | Recherche unifiée | `GET /crm/search?q=` sur clients et fournisseurs | S | CRM-4 | À faire |
| CRM-40 | Saisie rapide d'une interaction | `POST /interactions/quick` avec un texte libre : le service extrait client, type et résumé, puis demande confirmation avant d'enregistrer | M | CRM-12, AI-2 | À faire |
| CRM-41 | Questions fréquentes pour l'assistant | Réponses prêtes : meilleurs clients, clients à relancer, pipeline du mois | M | CRM-33, AI-3 | À faire |

## Phase 8. Écrans (NovaUI, `features/crm`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| CRM-42 | Liste et recherche des tiers | Clients et fournisseurs | M | CRM-7, CRM-8 | À faire |
| CRM-43 | Fiche 360 avec chronologie | Client et fournisseur | L | CRM-14 | À faire |
| CRM-44 | Tâches et relances | Liste, filtre, création, fin de tâche | M | CRM-18 | À faire |
| CRM-45 | Pipeline | Colonnes par étape, changement d'étape | L | CRM-25 | À faire |
| CRM-46 | Tableau de bord CRM | Indicateurs et alertes | M | CRM-33 | À faire |
| CRM-47 | Page d'erreur dédiée et lecture seule des éléments validés | Règles de [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md) | S | | À faire |

---

## Version minimale pour la démonstration

Pour montrer un CRM utile avec un petit nombre de tâches : CRM-6, CRM-7, CRM-9, CRM-1, CRM-4, CRM-12, CRM-13, CRM-16 à CRM-19, CRM-28, CRM-30, CRM-33.

Résultat visible : une fiche client 360, l'historique des échanges, la liste des clients à relancer et un tableau de bord.

## Ordre conseillé et répartition

1. **Vente** (en parallèle, sans attendre `crm-service`) : CRM-6, CRM-7, CRM-9, puis CRM-28 à CRM-31.
2. **Stock** (en parallèle) : CRM-8, CRM-10, puis CRM-32.
3. **CRM** : socle CRM-1 à CRM-5, puis phases 2, 3 et 4.
4. **Écrans** : phase 8 une fois les endpoints disponibles.
5. **Créances** : phase 6 après la facturation de l'ERP.

## Définition de « terminé » pour une tâche

- [ ] Reader / Creator / Updater / Deleter séparés, avec interfaces.
- [ ] Aucun fichier au-dessus de 100 lignes.
- [ ] DTO en entrée et en sortie, erreurs avec `type` explicite.
- [ ] Données validées ou historiques immuables.
- [ ] Tests unitaires passés (`mvn test`) et essai réel via la gateway.
- [ ] Tâche cochée dans ce document, via une Pull Request.
