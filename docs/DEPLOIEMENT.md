# Déploiement avec Dokploy

Le fichier [docker-compose.yml](../docker-compose.yml) est celui de la production. **Aucun port n'est publié** : les conteneurs se parlent sur le réseau interne du projet, et seul le front est atteint depuis Internet, par le domaine que Dokploy route.

## Architecture déployée

```
Internet ──HTTPS──► Traefik (Dokploy) ──► novaui (nginx :8080)
                                              │  /api/*
                                              ▼
                                          gateway :8080 ──► auth, stock, vente, rh, crm, module, ai (voice)
                                                                         │
                                                                     erp-db (PostgreSQL, un schéma par service)
```

| Service | Rôle | Joignable depuis |
|---|---|---|
| `novaui` | Front web (fichiers statiques) et relais `/api/` vers la gateway, sur la même origine | Internet (via le domaine) |
| `gateway` | Vérifie le jeton et le rôle, route vers les services | `novaui` |
| `auth-service`, `stock-service`, `vente-service`, `rh-service`, `crm-service`, `module-service` | Services Spring Boot | gateway et services concernés |
| `ai-service` | Assistant IA (Gemini). Sort vers Internet seulement pour joindre Gemini | gateway |
| `voice-service` | Transcription vocale (profil `voice`, facultatif) | gateway |
| `erp-db` | PostgreSQL 16, volume `erp-data` | services Java |
| `migrate` | Migrations SQL, exécutée puis arrêtée | — |

## Étapes dans Dokploy

1. Créer un projet, puis un service de type **Compose** (source : le dépôt Git, fichier `docker-compose.yml`, branche de production).
2. Onglet **Environment** : renseigner toutes les variables de [.env.production.example](../.env.production.example). Les variables obligatoires font échouer le déploiement si elles manquent ; aucune valeur de développement n'est utilisée.
3. Onglet **Domains** : ajouter le domaine avec *Service Name* = `novaui`, *Port* = `8080`, HTTPS activé (Let's Encrypt). Ne créer aucun autre domaine : la gateway, la base et les services ne doivent pas être exposés.
4. Pour activer la voix, ajouter la variable `COMPOSE_PROFILES=voice` (le premier démarrage télécharge le modèle Whisper, conservé dans le volume `hf-cache`).
5. Déployer. Le premier démarrage prend plusieurs minutes (compilation des services Java).

## Base de données et migrations

- À la création du volume, `erp-db` exécute [docker/postgres/init/01-schemas.sh](../docker/postgres/init/01-schemas.sh) : un schéma et un utilisateur par service, avec les mots de passe des variables `*_DB_PASSWORD`. Cet ordre n'est rejoué que sur un volume vide : changer un mot de passe ensuite demande un `ALTER ROLE`.
- Les tables sont créées et complétées par Hibernate (`ddl-auto: update`) au démarrage de chaque service.
- Le service `migrate` applique ensuite, à **chaque déploiement**, les scripts de [docker/postgres/migrations/](../docker/postgres/migrations/) (idempotents : colonnes, contraintes CHECK des énumérations, rattrapages de données). Il réessaie toutes les 10 secondes (30 fois) tant que les tables n'existent pas encore, puis s'arrête. Un échec définitif apparaît dans ses journaux et ne bloque pas les services.
- Nouvelle migration : ajouter un fichier `NNN_<service>_<description>.sql` idempotent dans ce dossier (préfixes réservés par domaine : 100 modules, 200 stock, 300 vente, 400 CRM, 500 RH, 600 auth).

## Sauvegardes

Dokploy sait sauvegarder un volume. Sauvegarder au minimum `erp-data` (base) et `ai-data` (journal de l'assistant). Test de restauration conseillé avant la mise en production.

## Sécurité à vérifier avant l'ouverture

- `JWT_SECRET` long, aléatoire, jamais réutilisé ailleurs ; mot de passe administrateur changé.
- Aucune ligne `ports:` ajoutée au Compose, aucun domaine sur un autre service que `novaui`.
- Le fichier `docker-compose.dev.yml` ne sert qu'en développement : il publie base, gateway et front sur `127.0.0.1` uniquement.
