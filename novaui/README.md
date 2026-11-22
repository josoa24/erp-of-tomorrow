# NovaUI

Frontend web de l'ERP (React 19 + TypeScript + Vite).

## Prérequis

- Node.js **20 ou plus** (`nvm use 23.9.0`).

## Démarrage

```bash
cp .env.example .env   # adresse de la gateway
npm install
npm run dev            # http://localhost:5173
```

Ou, sans Node installé, depuis la racine du dépôt : `docker compose up -d novaui`.

Les appels `/api/...` passent par le proxy du serveur Vite vers la gateway
(`API_PROXY_TARGET`, `http://localhost:8080` par défaut, `http://gateway:8080` dans Docker) :
la gateway n'a pas besoin d'autoriser le CORS.

## Écrans

| Route | Écran |
|-------|-------|
| `/connexion` | Connexion (e-mail + mot de passe, « Se souvenir de moi » garde la session dans le navigateur) |
| `/` | Tableau de bord (protégé) : ventes, stock, équipe et points d'attention selon le rôle |
| `/erreur` | Page d'erreur unique (réseau, accès refusé, session expirée…) |

## Scripts

| Commande | Rôle |
|----------|------|
| `npm run dev` | Serveur de développement |
| `npm run build` | Vérification TypeScript + build de production |
| `npm test` | Tests unitaires (Vitest + Testing Library) |
| `npm run lint` | ESLint |

## Stack

React 19, TypeScript, Vite, Tailwind CSS 4 (jetons du design system dans `src/styles.css`, thème Eclipse dans `src/eclipse.css`), composants de base type shadcn/ui (`src/components/ui`, `cva` + `clsx` + `tailwind-merge`), TanStack Query (`src/lib/queryClient.ts`), TanStack Router (installé, pas encore branché : le routage actuel est react-router), PWA (`vite-plugin-pwa`). Alias `@` = `src/`.

## Structure

```
src/
├── components/ui/      # Composants de base du design system (Button, Input, Badge...)
├── components/common/  # Composants réutilisables existants
├── features/<module>/  # Code propre à un module : stock, vente, rh, crm
│   └── components/ hooks/ services/ types/
├── screens/            # Écrans transverses (connexion, tableau de bord, erreur)
├── hooks/              # Hooks partagés
├── lib/                # utilitaires techniques (cn, queryClient)
├── services/api/       # Appels HTTP partagés
├── types/ utils/ constants/
├── navigation/         # Routage
└── test/               # Configuration des tests
```

Une seule application web pour tous les modules : chaque équipe travaille dans `src/features/<son module>/`.

Règles : voir [docs/REGLES_DEVELOPPEMENT.md](../docs/REGLES_DEVELOPPEMENT.md).
