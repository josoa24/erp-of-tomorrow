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
│   ├── auth-service/     # Utilisateurs, login, JWT — port 8084 — DB 5434
│   ├── crm-service/      # Échanges, relances, pipeline — port 8085 — DB 5435
│   └── voice-service/    # Voix vers texte (Python, Whisper) — port 8087
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
| `/api/crm/**` | crm-service (8085) |
| `/api/voice/**` | voice-service (8087) |

Le préfixe est retiré avant d'atteindre le service (`/api/stock/products` → `/products`).

## Sécurité et rôles

Tout passe par la gateway, qui vérifie le JWT (`Authorization: Bearer <token>`) puis le rôle :

| Rôle | `/api/stock` | `/api/vente` | `/api/rh` | `/api/voice` | `/api/auth/users` |
|------|:-----------:|:------------:|:---------:|:-----------:|:-----------------:|
| `STOCK` | oui | non | non | non | non |
| `VENTE` | non | oui | non | non | non |
| `RH` | non | non | oui | oui | non |
| `CRM` | non | non | non | non | non |
| `ADMIN` | oui | oui | oui | oui | oui |

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

## Variables d'environnement

Il n'y a pas de fichier `.env` dans le projet. Chaque `application.yml` lit ses variables avec la syntaxe `${NOM:valeur_par_defaut}` : sans variable définie, la valeur de développement s'applique. **Il n'y a donc rien à modifier dans les fichiers** : on définit la variable avant de lancer.

[.env.example](.env.example) liste toutes les variables avec leur rôle (Spring Boot ne le lit pas, il sert de référence).

| Variable | Service | Rôle |
|----------|---------|------|
| `JWT_SECRET` | gateway **et** auth-service | Secret de signature des tokens. Doit être identique dans les deux. |
| `JWT_TTL_MINUTES` | auth-service | Durée de validité d'un token (480 par défaut). |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | auth-service | Compte admin créé au premier démarrage. |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | chaque service | Accès à sa base PostgreSQL. |
| `STOCK_URL` | vente-service, gateway | Adresse du service Stock. |
| `AUTH_URL`, `RH_URL`, `VENTE_URL`, `CRM_URL`, `VOICE_URL` | gateway | Adresse des autres services. |
| `VENTE_URL`, `STOCK_URL` | crm-service | Adresse des services Vente et Stock (fiches clients et fournisseurs). |
| `VOICE_MODEL_SIZE`, `VOICE_DEVICE`, `VOICE_COMPUTE_TYPE`, `VOICE_LANGUAGE`, `VOICE_MAX_AUDIO_BYTES` | voice-service | Modèle Whisper (`small`), `cpu` ou `cuda`, précision (`int8`), langue par défaut (`fr`), taille maximale de l'audio (10 Mo). |
| `EXPO_PUBLIC_API_URL` | apps React Native | Adresse de la gateway. |

Définir une variable dans PowerShell, puis lancer le service **dans le même terminal** :

```powershell
$env:DB_PASSWORD = "mon_mot_de_passe"
```

Dans IntelliJ : Run Configuration, Environment variables.

Attention : `DB_URL`, `DB_USER` et `DB_PASSWORD` ont le même nom dans tous les services. Les définir service par service, dans le terminal de chacun, sinon un service utiliserait la base d'un autre.

Le dépôt est public : ne jamais y mettre de vrai secret. Les valeurs par défaut (`admin1234`, secret JWT de test) ne servent qu'en développement et doivent être remplacées avant toute mise en production.

## Lancer le projet

1. Les bases : `docker compose up -d`.
2. Chaque service, dans son dossier : `mvn spring-boot:run` (auth 8084, stock 8083, vente 8082, rh 8081).
3. Le voice-service (facultatif) : voir [services/voice-service/README.md](services/voice-service/README.md).
4. La gateway : `cd gateway`, puis `mvn spring-boot:run`.
5. Une app : `cd apps/<module>-app`, puis `npm install` et `npm start`.

Sans PostgreSQL ni Docker, le profil `h2` démarre un service avec une base en mémoire :

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

Si le port 8081 est déjà pris (Docker/WSL sur certains postes), lancer le service RH sur un autre port :

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8091"
```

## Règles d'équipe

Lire impérativement avant de coder :

- [docs/REGLES_DEVELOPPEMENT.md](docs/REGLES_DEVELOPPEMENT.md) : règles de code
- [docs/WORKFLOW_GIT.md](docs/WORKFLOW_GIT.md) : branches, commits, Pull Requests
- [docs/API_ENDPOINTS.md](docs/API_ENDPOINTS.md) : tous les endpoints avec leur rôle, et Swagger interactif sur http://localhost:8080/swagger-ui.html
- [docs/PLAN_API.md](docs/PLAN_API.md) : tâches par module et leur avancement
- [docs/PLAN_CRM.md](docs/PLAN_CRM.md) : plan pour un CRM complet (clients et fournisseurs)
- [docs/PLAN_ERP.md](docs/PLAN_ERP.md) : plan de ce qu'il reste pour un ERP complet (IA, applications, finance, achats, stock, RH)

Test sans PostgreSQL ni Docker : démarrer `auth-service` ou `stock-service` avec le profil `h2` (`mvn spring-boot:run -Dspring-boot.run.profiles=h2`).

## Organisation interne

**Service Spring Boot** : `<entité>/{controller, service/{reader,creator,updater,deleter}, repository, entity, dto, mapper}` + `common/{exception, config}`.

**App React Native** : `src/{components/common, screens, hooks, services/api, types, utils, constants, navigation}`.
