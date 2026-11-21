# ai-service — Assistant IA de l'ERP (port 8086)

Assistant en langage naturel (Google Gemini, function calling natif) branché sur les modules
de l'ERP. **Les calculs restent dans les services métier** : l'IA comprend la demande, appelle
les endpoints existants **via la gateway avec le token de l'utilisateur** (ses droits sont
donc respectés), puis met en forme la réponse. **Aucune écriture sans confirmation.**

```
Front ──► gateway :8080 ──/api/ai/**──► ai-service :8086 ──► Gemini
              ▲                               │
              └──── /api/stock, /api/vente… ◄─┘  (Authorization: Bearer <token de l'utilisateur>)
```

## Démarrer

Prérequis : les bases, les services Java et la gateway tournent (voir le README racine).

### Option A — en local (Python 3.10+)

```powershell
cd services/ai-service
python -m venv venv
venv\Scripts\activate
pip install -r requirements-dev.txt
copy .env.example .env        # puis renseigner GEMINI_API_KEY
uvicorn app.main:app --reload --port 8086
```

### Option B — Docker (profil `ai`, les services Java restent sur la machine)

```powershell
# à la racine du dépôt, fichier .env (jamais commité) contenant GEMINI_API_KEY=...
docker compose --profile ai up -d --build
```

## Tester

```powershell
# 1. Données de démonstration (idempotent) : comptes stock/vente, produits, clients, ventes
python scripts/seed_demo.py

# 2. Test de bout en bout comme un front : lecture, contexte, écritures confirmées/refusées, rôles
python scripts/test_erp_agent.py

# 3. Tests unitaires (sans réseau : faux Gemini + fausse gateway)
python -m pytest -q
```

Page de test (streaming, connexion, boutons de confirmation) : **http://localhost:8080/api/ai/ui**
Swagger : http://localhost:8080/swagger-ui.html → définition **AI**.

## API (toujours via la gateway, en-tête `Authorization: Bearer <token>`)

| Méthode | Chemin | Rôle |
|---|---|---|
| POST | `/api/ai/agent/chat` | Réponse complète (JSON) |
| POST | `/api/ai/agent/chat/stream` | Réponse en Server-Sent Events |
| GET | `/api/ai/tools` | Outils disponibles pour le rôle connecté |
| GET / DELETE | `/api/ai/agent/conversations/{id}` | Historique / réinitialisation |
| POST | `/api/ai/agent/conversations/{id}/actions/{actionId}/confirm` | Confirmer une écriture (bouton) |
| POST | `/api/ai/agent/conversations/{id}/actions/{actionId}/cancel` | Annuler une écriture (bouton) |
| GET | `/api/ai/ui` | Page de test (publique, sans donnée) |

Corps : `{"message": "...", "conversation_id": "<id reçu, optionnel>"}`.

Réponse `/chat` :

```json
{
  "response": "Le riz n'a plus qu'environ 2 jours de stock…",
  "conversation_id": "3f9c…",
  "history_turns": 1,
  "tool_calls": [{"name": "get_stock_status", "arguments": {}, "success": true, "duration_ms": 12.3, "turn": 1}],
  "pending_actions": [],
  "metrics": {"total_ms": 1800, "first_token_ms": 900, "gemini_call_count": 2}
}
```

Flux `/chat/stream` (événements SSE) : `start` {conversation_id} → `token` {text} (×N) →
`tool_call` / `tool_result` → `done` {response, tool_calls, pending_actions, history_turns, metrics}
— ou `error` {type, code, message}. `EventSource` ne gère pas POST : lire le flux avec `fetch`
(exemple complet dans `app/static/chat.html`).

Erreurs HTTP : même format que l'ERP `{status, type, message, timestamp}` (401, 400, 404, 503, 504).

### Écritures : confirmation obligatoire

Outils d'écriture : `record_stock_movement`, `create_purchase_order`, `validate_purchase_order`,
`receive_purchase_order`, `create_sale`, `validate_sale`.

1. L'IA appelle l'outil → **rien n'est écrit** : une action en attente est créée et renvoyée dans
   `pending_actions` (`action_id`, `summary`) et dans `tool_calls[].status = "pending_confirmation"`.
2. L'utilisateur confirme : bouton (`.../actions/{id}/confirm`, sans passer par l'IA) ou « oui »
   dans le chat (l'IA appelle `confirm_action`, refusé dans le tour même de la proposition).
3. L'écriture part vers l'ERP avec le token de l'utilisateur ; l'ERP revalide tout (stock, droits).

Les actions expirent après `CONFIRMATION_TTL_MIN` minutes et ne s'exécutent qu'une fois.

## Outils par module

| Module (rôles) | Lecture | Écriture (confirmée) |
|---|---|---|
| Stock (STOCK, ADMIN) | `search_products`, `get_stock_status`, `get_product_autonomy`, `get_stock_summary`, `get_product_movements`, `search_suppliers`, `list_purchase_orders` | `record_stock_movement`, `create_purchase_order`, `validate_purchase_order`, `receive_purchase_order` |
| Vente (VENTE, ADMIN) | `get_sales_summary`, `get_top_products`, `search_sales`, `search_customers`, `get_customer_profile` | `create_sale`, `validate_sale` |
| RH (RH, ADMIN) | `get_hr_summary`, `search_employees` | — |
| CRM (CRM, ADMIN) | `get_party_timeline` | — |

Gemini ne reçoit que les outils du rôle connecté ; la gateway revérifie chaque appel.

## Structure

```
app/
├── main.py                 Application, erreurs au format ERP, /ui, /tools
├── config.py               Variables d'environnement
├── api/                    chat_routes (JSON + SSE), conversation_routes (historique, boutons), identity, schemas
├── agent/                  orchestrator (boucle Gemini), prompts, memory (RAM), confirmation (garde-fou)
├── erp/                    client (HTTP gateway + token), context (utilisateur courant), periods
├── tools/                  spec, registry, stock_tools, vente_tools, rh_crm_tools
└── static/chat.html        Page de test
scripts/                    seed_demo.py, test_erp_agent.py, erp_http.py (bibliothèque standard uniquement)
tests/                      pytest (fake_gemini, fake_erp)
```

## Ajouter un outil

1. Dans `app/tools/<module>_tools.py` : un modèle d'arguments (`Args`), une fonction
   `async def mon_outil(erp, a)` qui appelle `erp.call("GET", "/api/<module>/...")`, et un `ToolSpec`
   dans `SPECS` (nom, description courte, paramètres).
2. Écriture ? Ajouter un 5ᵉ argument `summarize` au `ToolSpec` : la confirmation devient obligatoire.
3. Un test dans `tests/`.

## Limites connues

- Pas de coût d'achat dans les produits : l'IA donne le chiffre d'affaires, pas le bénéfice.
- Conversations et actions en attente en mémoire vive (perdues au redémarrage, une seule instance).
- `/sales/top-products` n'a pas de filtre de période côté Vente.
