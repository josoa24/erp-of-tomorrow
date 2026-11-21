# ERP of Tomorrow — Monorepo

ERP intelligent en architecture **microservices**, un module = un service Spring Boot + une application React Native.

## Architecture

```
Hackaton/
├── gateway/              # API Gateway (Spring Cloud Gateway) — port 8080
├── services/
│   ├── rh-service/       # Ressources Humaines — port 8081 — DB 5431
│   ├── vente-service/    # Ventes              — port 8082 — DB 5432
│   ├── stock-service/    # Stock               — port 8083 — DB 5433
│   └── auth-service/     # Utilisateurs, login, JWT — port 8084 — DB 5434
├── apps/
│   ├── rh-app/           # Front RH (React Native / Expo)
│   ├── vente-app/        # Front Ventes
│   └── stock-app/        # Front Stock
├── docs/
│   └── REGLES_DEVELOPPEMENT.md
└── docker-compose.yml    # 1 base PostgreSQL par service
```

Chaque service possède **sa propre base de données** : aucun service ne lit la base d'un autre.
La communication inter-services passe par l'API (via la gateway).

## Routage de la gateway

| Préfixe | Service |
|---------|---------|
| `/api/rh/**` | rh-service (8081) |
| `/api/vente/**` | vente-service (8082) |
| `/api/stock/**` | stock-service (8083) |
| `/api/auth/**` | auth-service (8084) |

Le préfixe est retiré avant d'atteindre le service (`/api/stock/products` → `/products`).

## Sécurité et rôles

Tout passe par la gateway, qui vérifie le JWT (`Authorization: Bearer <token>`) puis le rôle :

| Rôle | `/api/stock` | `/api/vente` | `/api/rh` | `/api/auth/users` |
|------|:-----------:|:------------:|:---------:|:-----------------:|
| `STOCK` | oui | non | non | non |
| `VENTE` | non | oui | non | non |
| `RH` | non | non | oui | non |
| `ADMIN` | oui | oui | oui | oui |

- Seul `POST /api/auth/login` est public.
- Pas de token ou token invalide : **401** (`UNAUTHORIZED`). Rôle non autorisé : **403** (`FORBIDDEN`).
- Un compte `ADMIN` est créé au premier démarrage de l'auth-service (`ADMIN_EMAIL` / `ADMIN_PASSWORD`, valeurs de développement par défaut à changer).
- Le secret `JWT_SECRET` doit être identique dans la gateway et l'auth-service.
- Les services ne doivent pas être exposés directement : seule la gateway est accessible de l'extérieur.

## Démarrage

Bases de données :

```powershell
docker compose up -d
```

Un service (exemple : stock) :

```powershell
cd services/stock-service
mvn spring-boot:run
```

Gateway :

```powershell
cd gateway
mvn spring-boot:run
```

Une application (exemple : stock) :

```powershell
cd apps/stock-app
npm install
npm start
```

## Règles d'équipe

Lire impérativement avant de coder :

- [docs/REGLES_DEVELOPPEMENT.md](docs/REGLES_DEVELOPPEMENT.md) : règles de code
- [docs/WORKFLOW_GIT.md](docs/WORKFLOW_GIT.md) : branches, commits, Pull Requests
- [docs/PLAN_API.md](docs/PLAN_API.md) : tâches par module et leur avancement

Test sans PostgreSQL ni Docker : démarrer `auth-service` ou `stock-service` avec le profil `h2` (`mvn spring-boot:run -Dspring-boot.run.profiles=h2`).

## Organisation interne

**Service Spring Boot** : `<entité>/{controller, service/{reader,creator,updater,deleter}, repository, entity, dto, mapper}` + `common/{exception, config}`.

**App React Native** : `src/{components/common, screens, hooks, services/api, types, utils, constants, navigation}`.
