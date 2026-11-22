# ERP of Tomorrow — Monorepo

ERP intelligent en architecture **microservices**, un module = un service Spring Boot + une application React Native.

## Architecture

```
Hackaton/
├── gateway/              # API Gateway (Spring Cloud Gateway) — port 8080
├── services/
│   ├── rh-service/       # Ressources Humaines — port 8081 — schéma rh
│   ├── vente-service/    # Ventes              — port 8082 — schéma vente
│   ├── stock-service/    # Stock               — port 8083 — schéma stock
│   ├── auth-service/     # Utilisateurs, login, JWT — port 8084 — schéma auth
│   ├── crm-service/      # Échanges, relances, pipeline — port 8085 — schéma crm
│   └── ai-service/       # Assistant IA (Python, Gemini) — port 8086 — sans base
│   └── voice-service/    # Voix vers texte (Python, Whisper) — port 8087
├── apps/
│   ├── rh-app/           # Front RH (React Native / Expo)
│   ├── vente-app/        # Front Ventes
│   └── stock-app/        # Front Stock
├── docs/
│   └── REGLES_DEVELOPPEMENT.md
└── docker-compose.yml    # 1 instance PostgreSQL (base erp), 1 schéma par service
```

Une seule instance PostgreSQL (port 5432, base `erp`) héberge **un schéma et un utilisateur par service** (`auth_svc`, `stock_svc`, `vente_svc`, `rh_svc`, `crm_svc`), créés par [docker/postgres/init/01-schemas.sh](docker/postgres/init/01-schemas.sh). Chaque service ne peut lire et écrire que son propre schéma : l'isolation des microservices est garantie par la base, sans le coût de cinq serveurs (voir [ADR-006](docs/adr/ADR-006-instance-postgresql-unique.md)).
La communication inter-services passe par l'API (via la gateway).

## Routage de la gateway

| Préfixe | Service |
|---------|---------|
| `/api/rh/**` | rh-service (8081) |
| `/api/vente/**` | vente-service (8082) |
| `/api/stock/**` | stock-service (8083) |
| `/api/auth/**` | auth-service (8084) |
| `/api/crm/**` | crm-service (8085) |
| `/api/ai/**` | ai-service (8086) |
| `/api/voice/**` | voice-service (8087) |

Le préfixe est retiré avant d'atteindre le service (`/api/stock/products` → `/products`).

## Sécurité et rôles

Tout passe par la gateway, qui vérifie le JWT (`Authorization: Bearer <token>`) puis le rôle :

| Rôle | `/api/stock` | `/api/vente` | `/api/rh` | `/api/ai` | `/api/voice` | `/api/auth/users` |
|------|:-----------:|:------------:|:---------:|:---------:|:------------:|:-----------------:|
| `STOCK` | oui | non | non | oui | non | non |
| `VENTE` | non | oui | non | oui | non | non |
| `RH` | non | non | oui | oui | oui | non |
| `CRM` | non | non | non | oui | non | non |
| `ADMIN` | oui | oui | oui | oui | oui | oui |

L'assistant (`/api/ai`) est ouvert à tous les rôles : il appelle les autres modules avec le token de
l'utilisateur, il ne voit donc que ce que ce rôle peut voir.

- Seuls `POST /api/auth/login` et la page de test `GET /api/ai/ui` sont publics.
- Pas de token ou token invalide : **401** (`UNAUTHORIZED`). Rôle non autorisé : **403** (`FORBIDDEN`).
- Un compte `ADMIN` est créé au premier démarrage de l'auth-service (`ADMIN_EMAIL` / `ADMIN_PASSWORD`, valeurs de développement par défaut à changer).
- Le secret `JWT_SECRET` doit être identique dans la gateway et l'auth-service.
- Les services ne doivent pas être exposés directement : seule la gateway est accessible de l'extérieur.

## Démarrage

Base de données (une seule instance pour tous les services) :

```powershell
docker compose up -d erp-db
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
| `AI_URL` | gateway | Adresse de l'ai-service. |
| `CORS_ALLOWED_ORIGINS` | gateway | Origines web autorisées, séparées par des virgules (`http://localhost:*,http://127.0.0.1:*` par défaut). |
| `GEMINI_API_KEY`, `GEMINI_MODEL` | ai-service | Clé et modèle Gemini (jamais commités). |
| `ERP_GATEWAY_URL` | ai-service | Adresse de la gateway vue par l'ai-service. |
| `VOICE_MODEL_SIZE`, `VOICE_PARTIAL_MODEL_SIZE`, `VOICE_DEVICE`, `VOICE_COMPUTE_TYPE`, `VOICE_LANGUAGE`, `VOICE_MAX_AUDIO_BYTES` | voice-service | Modèle Whisper (`small`), modèle rapide pour le texte provisoire (facultatif), `cpu` ou `cuda`, précision (`int8`), langue par défaut (`fr`), taille maximale de l'audio (10 Mo). |
| `EXPO_PUBLIC_API_URL` | apps React Native | Adresse de la gateway. |

Définir une variable dans PowerShell, puis lancer le service **dans le même terminal** :

```powershell
$env:DB_PASSWORD = "mon_mot_de_passe"
```

Dans IntelliJ : Run Configuration, Environment variables.

Attention : `DB_URL`, `DB_USER` et `DB_PASSWORD` ont le même nom dans tous les services. Les définir service par service, dans le terminal de chacun, sinon un service utiliserait la base d'un autre.

Le dépôt est public : ne jamais y mettre de vrai secret. Les valeurs par défaut (`admin1234`, secret JWT de test) ne servent qu'en développement et doivent être remplacées avant toute mise en production.

## Lancer le projet

1. La base : `docker compose up -d erp-db` (ou tout l'ERP conteneurisé : `docker compose up -d --build`).
2. Chaque service, dans son dossier : `mvn spring-boot:run` (auth 8084, stock 8083, vente 8082, rh 8081).
3. La gateway : `cd gateway`, puis `mvn spring-boot:run`.
4. Une app : `cd apps/<module>-app`, puis `npm install` et `npm start`.
5. L'assistant IA (optionnel) : voir [services/ai-service/README.md](services/ai-service/README.md)
   (`uvicorn` en local, ou `docker compose --profile ai up -d --build`), puis
   `python services/ai-service/scripts/seed_demo.py` et `python services/ai-service/scripts/test_erp_agent.py`.
3. Le voice-service (facultatif) : voir [services/voice-service/README.md](services/voice-service/README.md).
4. La gateway : `cd gateway`, puis `mvn spring-boot:run`.
5. Une app : `cd apps/<module>-app`, puis `npm install` et `npm start`.

Sous Windows, si `docker compose up --build` échoue avec « additional privileges requested: pass --allow=fs.read=docker\Dockerfile.java », construire les images une par une puis démarrer sans reconstruire :

```powershell
foreach ($m in "services/auth-service","services/stock-service","services/vente-service","services/rh-service","services/crm-service","gateway") {
  docker build -f docker/Dockerfile.java --build-arg MODULE_PATH=$m -t "erp-$(Split-Path $m -Leaf)" .
}
docker compose up -d --no-build
```

Sans PostgreSQL ni Docker, le profil `h2` démarre un service avec une base en mémoire :

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

Si le port 8081 est déjà pris (Docker/WSL sur certains postes), lancer le service RH sur un autre port :

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8091"
```

## Documentation

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) : composants, chemin d'une requête, sécurité, tests, place de l'IA
- [docs/adr/](docs/adr/README.md) : décisions d'architecture et leurs raisons
- [docs/API_ENDPOINTS.md](docs/API_ENDPOINTS.md) : tous les endpoints et les rôles autorisés
- [docs/FONCTIONNALITES.md](docs/FONCTIONNALITES.md) : ce que l'ERP sait faire aujourd'hui

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

## Mode développement avec rechargement à chaud

```bash
docker compose -f docker-compose.dev.yml up                                # bases, services Java, gateway, NovaUI
docker compose -f docker-compose.dev.yml --profile ai --profile voice up   # + assistant IA et voix
```

Le code source est monté dans les conteneurs. Java : recompilation automatique puis redémarrage par spring-boot-devtools. Python : `uvicorn --reload`. NovaUI : Vite (http://localhost:5173). Gateway sur http://localhost:8080. Le premier démarrage est long (téléchargement des dépendances Maven, mises en cache dans un volume). Les apps Expo se lancent sur la machine (`npm start`).
