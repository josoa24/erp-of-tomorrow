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

## Scripts

| Commande | Rôle |
|----------|------|
| `npm run dev` | Serveur de développement |
| `npm run build` | Vérification TypeScript + build de production |
| `npm test` | Tests unitaires (Vitest + Testing Library) |
| `npm run lint` | ESLint |

## Structure

```
src/
├── components/common/  # Composants réutilisables
├── screens/            # Écrans (assemblage composants + hooks)
├── hooks/              # Hooks personnalisés
├── services/api/       # Appels HTTP
├── types/              # Types TypeScript
├── utils/              # Fonctions pures
├── constants/          # Constantes
├── navigation/         # Routage
└── test/               # Configuration des tests
```

Règles : voir [docs/REGLES_DEVELOPPEMENT.md](../docs/REGLES_DEVELOPPEMENT.md).
