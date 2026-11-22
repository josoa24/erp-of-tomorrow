# ADR-001 : microservices Spring Boot, un schéma par service dans une base unique, une gateway unique

Statut : acceptée. Le point « une base par service » est remplacé par l'[ADR-006](ADR-006-instance-postgresql-unique.md) : une instance, un schéma et un utilisateur par service.

## Contexte

L'ERP couvre plusieurs métiers (stock, vente, RH, CRM, comptes) développés en parallèle par des équipes différentes, pendant un hackathon. Chaque équipe doit pouvoir avancer, tester et démarrer son module sans attendre les autres, et sans casser le travail des autres ([WORKFLOW_GIT.md](../WORKFLOW_GIT.md) : une équipe, un dossier).

## Décision

- Un module métier = un service Spring Boot dans `services/<module>-service`, avec son port (rh 8081, vente 8082, stock 8083, auth 8084, crm 8085).
- Une seule instance PostgreSQL (base `erp`) avec un schéma et un utilisateur par service (`rh`, `vente`, `stock`, `auth`, `crm`), voir [ADR-006](ADR-006-instance-postgresql-unique.md). Aucun service ne lit le schéma d'un autre.
- Un service qui a besoin d'une donnée d'un autre module l'appelle en HTTP derrière une interface : `StockClient` (vente vers stock), `CustomerClient` et `SupplierClient` (crm vers vente et stock).
- Une gateway unique (Spring Cloud Gateway, port 8080) route `/api/<module>/**` vers le bon service en retirant le préfixe. C'est le seul point d'entrée publié par `docker-compose.yml`, et elle agrège le Swagger de tous les services.

## Conséquences

- Les équipes travaillent dans des dossiers séparés ; un module se démarre seul (`mvn spring-boot:run`, ou profil `h2` sans PostgreSQL).
- Pas de transaction entre deux services. Une opération qui touche deux services doit compenser elle-même en cas d'échec : la validation d'une vente remet en stock ce qui a déjà été débité (`SaleValidatorService`).
- Pas de clé étrangère entre modules : `productId` dans une vente n'est pas vérifié par la base. Les règles d'intégrité inter-modules sont dans le code, et certaines manquent encore (la suppression d'un produit ne vérifie pas les ventes).
- Un service indisponible donne une erreur claire chez l'appelant (503 `SERVER`, via `ServiceUnavailableException`).
- Plus de processus à lancer qu'un monolithe : `docker compose up -d --build` démarre l'ensemble.

## Alternatives écartées

- **Monolithe unique** : plus simple à déployer, mais toutes les équipes dans le même code et la même base, avec plus de conflits pendant le hackathon.
- **Base partagée entre services** : plus simple pour les jointures, mais couple les modules par le schéma ; un changement de table casse les autres.
- **Bus de messages (événements)** pour la synchronisation : utile à plus grande échelle, mais trop d'infrastructure pour le besoin actuel ; les appels HTTP synchrones avec compensation suffisent.
