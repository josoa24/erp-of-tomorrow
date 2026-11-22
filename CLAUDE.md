# Instructions pour les assistants IA

Ce fichier s'applique à tout assistant IA (Claude Code, Copilot, Cursor, etc.) qui travaille sur ce dépôt.

## Règles Git (obligatoires)

1. **Jamais de commit sur `main`.** Toujours créer une branche : `<module>/<type>-<description>` (ex. `stock/feat-supplier-crud`).
2. **Toujours passer par une Pull Request (MR)** vers `main`, relue par un humain avant le merge.
3. **Aucune mention de l'IA dans les commits** : pas de `Co-Authored-By`, pas de « Generated with », pas de signature d'outil. Le commit est au nom du développeur qui le valide.
4. **Ne jamais committer de fichier `.env`**, de mot de passe, de clé API, de token ou de secret. Utiliser des variables d'environnement et un `.env.example` sans valeurs réelles.
5. Ne jamais pousser avec `--force` sur une branche partagée.
6. Ne pas pousser ni créer de PR sans demande explicite du développeur.
7. **Supprimer la branche dès que sa PR est mergée**, en local (`git branch -D`, puis `git fetch --prune`) et sur le dépôt distant s'il reste. Ne jamais réutiliser une branche mergée. Ne supprimer que des branches dont la PR est bien au statut MERGED.

## Règles de code

Lire et respecter [docs/REGLES_DEVELOPPEMENT.md](docs/REGLES_DEVELOPPEMENT.md). En résumé :

- Réutiliser un composant, service ou hook existant avant d'en créer un.
- 100 lignes maximum par fichier ; une responsabilité par fichier.
- Front : aucun service ni appel API dans un composant (services, hooks et API dans des fichiers dédiés).
- Back : séparer Reader / Creator / Updater / Deleter, avec des interfaces (SOLID) ; DTO uniquement, jamais d'entité exposée.
- Front : les erreurs s'affichent sur une page d'erreur dédiée, jamais en popup ni dans le détail.
- Une donnée validée est immuable : on corrige par une nouvelle écriture.
- Tout nouveau code a ses tests unitaires.

## Contexte du projet

Monorepo microservices (une seule base PostgreSQL, un schéma par service) : `gateway/`, `services/{auth,stock,vente,rh,crm}-service/`, `services/ai-service/` (Python), `novaui/` (application web unique, un dossier `src/features/<module>/` par module). Chaque équipe reste dans son dossier ([docs/WORKFLOW_GIT.md](docs/WORKFLOW_GIT.md)). Les tâches sont dans [docs/PLAN_API.md](docs/PLAN_API.md).
