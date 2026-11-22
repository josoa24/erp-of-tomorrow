# ADR-006 : une instance PostgreSQL, un schéma et un utilisateur par service

Statut : acceptée. Remplace le point « une base PostgreSQL par service » de l'[ADR-001](ADR-001-microservices-base-par-service.md).

## Contexte

Jusqu'ici, `docker-compose.yml` démarrait cinq serveurs PostgreSQL, un par service (ports 5431 à 5435), avec chacun son processus, sa mémoire partagée, son volume et ses connexions. Chaque service ouvrait aussi un pool de 10 connexions par défaut. Sur un poste de développement, une machine de démonstration ou un petit serveur de PME, c'est beaucoup de mémoire et de processus pour peu de données.

L'objectif reste l'isolation des microservices : aucun service ne doit lire ni modifier les données d'un autre.

## Décision

- **Une seule instance PostgreSQL** (`erp-db`, port 5432, base `erp`).
- **Un schéma par service** (`auth`, `stock`, `vente`, `rh`, `crm`) et **un utilisateur par service** (`auth_svc`, `stock_svc`, …), créés au premier démarrage par [docker/postgres/init/01-schemas.sh](../../docker/postgres/init/01-schemas.sh).
- Chaque utilisateur est propriétaire de son schéma et n'a aucun droit sur les autres schémas ni sur `public`. L'isolation est donc **garantie par la base**, alors qu'avant elle n'était qu'une convention : n'importe quel service pouvait se connecter à n'importe quelle base avec l'utilisateur `postgres`.
- Chaque service se connecte avec son utilisateur (`DB_USER`, `DB_PASSWORD`) au schéma défini par `DB_URL` (`currentSchema`) et `hibernate.default_schema`.
- Sobriété :
  - pool de 5 connexions par service (`DB_POOL_SIZE`) ;
  - 50 threads Tomcat au plus ;
  - `max_connections=60` côté PostgreSQL ;
  - JVM adaptée aux conteneurs (`MaxRAMPercentage`, ramasse-miettes série, compilation JIT limitée au niveau C1) ;
  - limite mémoire par conteneur (`JAVA_SERVICE_MEMORY`, 512 Mo par défaut).

## Mesures (docker compose, poste de développement, 8 Go)

| | Avant | Après |
|---|---|---|
| Serveurs PostgreSQL | 5 | 1 (environ 90 Mo) |
| Connexions ouvertes au repos | jusqu'à 50 (pools de 10) | 7 |
| Démarrage d'un service Java | 39 à 46 s | 19 à 23 s |
| Mémoire des 6 JVM | 1,39 Go | 1,08 Go |
| Isolation entre schémas | convention | imposée : `permission denied` si `stock_svc` lit `vente` ou écrit dans `public` |

Les deux mesures JVM sont faites dans les mêmes conditions (base unique, pools et threads réduits dans les deux cas) : la seule différence est `-XX:TieredStopAtLevel=1`. Le gain des pools et des threads réduits n'est pas mesuré séparément.

## Conséquences

- Moins de ressources : un serveur PostgreSQL au lieu de cinq, des pools plus petits, une mémoire bornée par conteneur.
- Le code des services ne change pas : chaque service a toujours son propre modèle de données, ses tables et ses appels HTTP vers les autres. Un service peut être extrait vers son propre serveur PostgreSQL plus tard en changeant seulement `DB_URL`.
- Une panne de l'instance arrête tous les services. C'est acceptable pour un ERP de PME ; une entreprise plus grande peut redonner un serveur à un service critique sans modifier le code.
- Les anciens volumes (`rh-data`, `vente-data`, …) ne sont plus utilisés : les données de développement repartent de zéro dans `erp-data`.
- Le script d'initialisation ne tourne qu'à la création du volume. Pour ajouter un service plus tard, ajouter une ligne au script et recréer le volume (ou exécuter les mêmes ordres SQL à la main).

## Alternatives écartées

- **Garder cinq instances** : isolation physique, mais coût en mémoire et en processus sans bénéfice à cette échelle.
- **Une base unique avec un seul utilisateur et le schéma `public`** : la plus simple, mais elle supprime l'isolation et mélange les tables de tous les modules.
- **Plusieurs bases dans une même instance** : possible, mais les schémas suffisent, simplifient les sauvegardes (une seule base) et gardent la même isolation par les droits.
