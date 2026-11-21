# Workflow Git — travail entre équipes

Un seul dépôt (monorepo). Chaque équipe travaille dans **son dossier** pour éviter les conflits.

## Qui touche quoi

| Équipe | Dossiers autorisés |
|--------|--------------------|
| Stock | `services/stock-service/`, `apps/stock-app/` |
| Vente | `services/vente-service/`, `apps/vente-app/` |
| RH | `services/rh-service/`, `apps/rh-app/` |
| Auth / Gateway | `services/auth-service/`, `gateway/` |

Les fichiers communs (`README.md`, `docs/`, `docker-compose.yml`, `.gitignore`) se modifient par Pull Request, avec l'accord des autres équipes.

## Branches

- `main` : toujours stable, jamais de push direct.
- Une branche par tâche, nommée `<module>/<type>-<description>` :
  - `stock/feat-supplier-crud`
  - `vente/feat-sale-validation`
  - `rh/fix-leave-balance`
- Types : `feat`, `fix`, `refactor`, `test`, `docs`.

## Commits

Format : `<module>: <verbe à l'infinitif> <quoi>`

```
stock: ajouter la recherche de produits
vente: valider une vente et sortir le stock
```

Un commit = une idée. Pas de fichiers générés (`target/`, `node_modules/`).

## Pull Request

1. Mettre à jour sa branche : `git pull --rebase origin main`.
2. Lancer les tests du module (`mvn test` ou `npm test`).
3. Ouvrir la PR vers `main` avec la checklist de [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md).
4. Au moins **une relecture** d'un autre développeur avant le merge.
5. Merge en **squash** pour garder un historique lisible.
6. **Supprimer la branche dès que la PR est mergée** (voir la section suivante).

## Suppression des branches après merge

Une branche ne vit que le temps de sa PR. **Dès que la PR est mergée, la branche est supprimée.**

- **Sur GitHub** : la suppression est automatique (option « Automatically delete head branches » activée sur le dépôt). Si elle n'a pas eu lieu, cliquer sur « Delete branch » dans la PR.
- **En local**, chaque développeur nettoie sa copie :

```
git checkout main
git pull origin main
git branch -D <ma-branche>
git fetch --prune
```

Le merge en squash empêche `git branch -d` de reconnaître la branche comme fusionnée : utiliser `-D` une fois la PR mergée.

- Ne jamais réutiliser une branche mergée : pour une nouvelle tâche, créer une nouvelle branche depuis un `main` à jour.
- Les branches de longue durée n'existent pas dans ce projet : `main` est la seule branche permanente.

## Avant de commencer une tâche

1. Choisir une tâche dans [PLAN_API.md](PLAN_API.md) et la marquer comme prise.
2. Lire [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md).
3. Créer sa branche depuis un `main` à jour.

## Utilisation d'une IA

Quand un développeur utilise une IA (Claude Code, Copilot, Cursor…) :

1. L'IA travaille **toujours sur une branche** dédiée, jamais sur `main`.
2. Le résultat passe **obligatoirement par une Pull Request (MR)**, relue par un humain avant le merge.
3. Les commits **ne portent aucune mention de l'IA** : pas de `Co-Authored-By`, pas de « Generated with ». Le commit est au nom du développeur, qui reste responsable du code qu'il valide.
4. Le développeur relit et teste le code avant de le soumettre : les règles de [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md) s'appliquent de la même façon.

Les instructions destinées aux IA sont dans [../CLAUDE.md](../CLAUDE.md).

## Secrets et fichiers `.env`

- **Ne jamais commiter un fichier `.env`** (ni `.env.local`, ni `.env.*`), ni mot de passe, clé API ou token.
- Les valeurs sensibles (`JWT_SECRET`, `ADMIN_PASSWORD`, accès aux bases) se passent par variables d'environnement.
- Pour documenter les variables attendues, commiter un `.env.example` **sans valeurs réelles**.
- Si un secret est commité par erreur : le considérer comme compromis, le changer immédiatement, puis prévenir l'équipe.
