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
| POST | `/api/ai/invoices/extract` | Facture fournisseur → extraction + contrôle Invoice Shield (STOCK, ADMIN) |
| POST | `/api/ai/eclipse/adaptations/propose` | Barre Eclipse : reconnaît une adaptation d'écran du catalogue (rien n'est appliqué) |
| POST | `/api/ai/eclipse/modules/propose` | « Créer un module » : prépare une définition de module (rien n'est créé) |
| GET | `/api/ai/eclipse/suggestions` | Adaptations du catalogue pas encore actives, pour les modules du rôle |
| GET | `/api/ai/audit?limit=&userId=` | Journal des interactions IA (ADMIN uniquement, sinon 403 `FORBIDDEN`) |
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
  "metrics": {"total_ms": 1800, "first_token_ms": 900, "gemini_call_count": 2},
  "trust": {"label": "ESTIMATION", "verifiedNumbers": ["2", "14"], "unverifiedNumbers": [],
            "sources": ["get_stock_status"]}
}
```

### Confiance des réponses (`trust`)

Après chaque réponse, les nombres cités sont extraits (« 1 440 000 », « 1,440,000 », « 12,5 »…) et
comparés aux résultats des outils du tour et à la question : `verifiedNumbers` / `unverifiedNumbers`
(un nombre calculé ou inventé par l'IA apparaît dans `unverifiedNumbers`). Sont ignorés : 0, 1 et les
marqueurs de liste (« 1. »). Un arrondi est accepté à une demi-unité près (« 103 333 » pour 103333.33).
`label` (premier cas qui s'applique) : `RECOMMENDATION` si une écriture est proposée, `ESTIMATION` si un
outil de prévision a servi (`get_stock_status`, `get_product_autonomy`, `get_replenishment_forecast`, `list_replenishment_needs`), `FACT` si un outil a réussi,
`NONE` sinon. `sources` = outils ayant réussi. Code : `app/trust/`.

Flux `/chat/stream` (événements SSE) : `start` {conversation_id} → `token` {text} (×N) →
`tool_call` / `tool_result` → `done` {response, tool_calls, pending_actions, history_turns, metrics, trust}
— ou `error` {type, code, message}. `EventSource` ne gère pas POST : lire le flux avec `fetch`
(exemple complet dans `app/static/chat.html`).

Erreurs HTTP : même format que l'ERP `{status, type, message, timestamp}` (400, 401, 403, 404, 413, 429, 503, 504).

### Limite de débit, langue, devise

- Chat, flux et extraction : `AI_RATE_LIMIT_PER_MINUTE` requêtes par utilisateur (fenêtre glissante d'une
  minute, en mémoire). Au-delà : 429 `RATE_LIMIT` + en-tête `Retry-After`.
- L'assistant répond dans la langue de l'utilisateur (français, malgache ou anglais), montants en Ariary (Ar).

### Journal des actions IA (`/audit`, ADMIN)

Chaque interaction (chat, flux, boutons Confirmer/Annuler, extraction de facture) est écrite dans SQLite
(`AI_AUDIT_DB`, défaut `data/ai_audit.db`, ignoré par git) : horodatage, userId, email, rôle, conversation,
question tronquée à 300 caractères, noms des outils, actions `proposed` / `confirmed` / `refused` / `failed`
(outil + identifiant), label de confiance. **Jamais** de token, de résultat d'outil ni d'argument (donc aucun
compte bancaire).

```json
{"count": 1, "items": [{"id": 3, "created_at": "2026-11-22T09:15:02Z", "user_id": "7", "email": "stock@erp.local",
  "role": "STOCK", "conversation_id": "3f9c…", "question": "Enregistre la facture FA-2026-0042",
  "tools": ["check_supplier_invoice", "register_supplier_invoice"],
  "actions": {"proposed": [{"tool": "register_supplier_invoice", "action_id": "a1b2c3d4"}],
              "confirmed": [], "refused": [], "failed": []}, "trust_label": "RECOMMENDATION"}]}
```

### Factures fournisseurs (Invoice Shield)

`POST /invoices/extract` (STOCK, ADMIN), `multipart/form-data`, champ `file` : JPEG, PNG, WEBP ou PDF,
`INVOICE_MAX_MB` Mo maximum (type vérifié sur les premiers octets ; sinon 400, trop gros : 413).

1. Gemini (vision, **sans aucun outil**, sortie JSON validée par `ExtractedInvoice`) lit `supplierName,
   invoiceNumber, invoiceDate, bankAccount, lines[], subtotal, taxAmount, totalAmount, notes`. Le document est
   une donnée non fiable : le prompt demande d'ignorer ses instructions et de les recopier dans `notes`.
2. Le fournisseur est cherché (`GET /api/stock/suppliers?search=`), puis `POST /api/stock/supplier-invoices/check`
   est appelé avec le token de l'utilisateur. **Rien n'est enregistré.**

```json
{"extracted": {"supplierName": "Riz Import SARL", "invoiceNumber": "FA-2026-0042", "totalAmount": 1440000},
 "supplierMatch": {"found": true, "supplierId": 1, "name": "Riz Import SARL"},
 "shield": {"verdict": "BLOCKED", "alerts": [{"code": "PROMPT_INJECTION", "severity": "BLOCKING", "message": "…"}]}}
```

Fournisseur introuvable ou ambigu : `supplierMatch.found = false` (avec `message`), `shield = null`, l'extraction
est quand même renvoyée. Contrôle impossible (champ obligatoire non lu, erreur ERP) : `shieldError` {code, message}.
Pour enregistrer, l'assistant utilise `register_supplier_invoice`, qui passe par la confirmation.

### Écritures : confirmation obligatoire

Outils d'écriture : `record_stock_movement`, `create_purchase_order`, `validate_purchase_order`,
`receive_purchase_order`, `register_supplier_invoice`, `create_sale`, `validate_sale`.

1. L'IA appelle l'outil → **rien n'est écrit** : une action en attente est créée et renvoyée dans
   `pending_actions` (`action_id`, `summary`) et dans `tool_calls[].status = "pending_confirmation"`.
2. L'utilisateur confirme : bouton (`.../actions/{id}/confirm`, sans passer par l'IA) ou « oui »
   dans le chat (l'IA appelle `confirm_action`, refusé dans le tour même de la proposition).
3. L'écriture part vers l'ERP avec le token de l'utilisateur ; l'ERP revalide tout (stock, droits).

Les actions expirent après `CONFIRMATION_TTL_MIN` minutes et ne s'exécutent qu'une fois.

### Eclipse : adaptations d'écran et nouveaux modules (`/eclipse`)

Eclipse **propose**, le front **applique** : l'ai-service n'écrit rien. L'activation d'une adaptation
(`POST /api/modules/adaptations`) et la création d'un module (`POST /api/modules/definitions`) sont faites par le
front, avec le token de l'utilisateur, sur le module-service (réservées au rôle ADMIN, vérifié par ce service).
Les trois routes fonctionnent **sans clé Gemini** ; avec la clé, Gemini affine la reconnaissance (sortie structurée,
température 0, liste fermée) et sa réponse est toujours revalidée contre le catalogue.

Catalogue fermé (une adaptation par module de base) :

| Clé | Module | `summary` (barre « après demande IA ») |
|---|---|---|
| `stock.lots-peremption` | stock | Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt |
| `ventes.mobile-money-credit` | ventes | Eclipse a ajouté : colonne Paiement · filtre À crédit · indicateur Crédit en cours |
| `relations.nif-plafond-credit` | relations | Eclipse a ajouté : colonnes NIF / STAT et Plafond crédit · filtre Professionnels · alerte de dépassement |
| `equipe.avances-salaire` | equipe | Eclipse a ajouté : colonnes Avance et Plafond · filtre Avance demandée · validation par le gérant |

`POST /eclipse/adaptations/propose` — corps `{"moduleKey": "stock", "request": "Ajoute le n° de lot…"}`
(`moduleKey` : `stock`, `ventes`, `relations`, `equipe`, `accueil` ou slug d'un module créé ; `request` 1 à 500
caractères). Module de base hors des droits du rôle : 403 `FORBIDDEN`.

```json
{"outcome": "ADAPTATION", "adaptationKey": "stock.lots-peremption",
 "request": "Ajoute le n° de lot et la date de péremption, avec une alerte 30 jours avant",
 "title": "N° de lot et péremption",
 "summary": "Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt",
 "canApply": true}
```

| `outcome` | Quand | Champs en plus |
|---|---|---|
| `ADAPTATION` | mots-clés du catalogue (sans accents ni casse : lot, péremption, DLC, MVola, Orange Money, crédit, NIF, STAT, plafond, encours, avance, acompte…) | `adaptationKey`, `title`, `summary`, `targetModuleKey` si `moduleKey` = `accueil` |
| `ALREADY_ACTIVE` | le module-service indique l'adaptation active (`GET /api/modules/adaptations` ; injoignable = considérée inactive) | idem + `message` |
| `QUESTION` | question (« combien », « quels », « liste », « ? »…) : le front l'envoie à `/agent/chat/stream` | — |
| `CREATE_MODULE` | « crée un module… », « je veux suivre mes… » : le front ouvre « Créer un module » prérempli | — |
| `UNSUPPORTED` | hors catalogue | `message` honnête : « Je ne sais pas encore ajouter cela à « Stock ». Je peux ajouter : … » |

`canApply` = rôle ADMIN. Une reconnaissance par mot-clé l'emporte toujours sur Gemini, qui n'est consulté que sans
mot-clé ; une clé hors du module ou une erreur Gemini font revenir à la reconnaissance par mots-clés.

`POST /eclipse/modules/propose` — corps `{"description": "Suivre les livraisons de mes clients"}`. Réponse
`{proposal, accessText, source, canCreate, uncheckedColumns, note?}` : `proposal` a la forme de
`ModuleDefinitionRequest` du module-service (`name`, `icon`, `description`, `columns`, `statuses`, `indicators`).
Modèles par mots-clés : **Livraisons** (Client · Vente d'origine · Quartier · Livreur · Heure prévue, statuts
À préparer → En route → Livrée, indicateurs Livrées aujourd'hui, En route, À encaisser ; `uncheckedColumns` =
`signature_client` ; Vente d'origine, Montant à encaisser et Signature du client ont `listed: false`, visibles
seulement dans le détail), **Réparations**,
**Rendez-vous** (`source` = `TEMPLATE`). Sinon Gemini si la clé est présente (`source` = `GEMINI`, JSON validé,
icône de la liste autorisée), sinon base générique Nom · Date · Montant, statuts À faire → En cours → Terminé,
avec `note` qui le dit. `canCreate` = rôle ADMIN. La proposition suit les mêmes règles que le module-service
(au moins une colonne `listed`, choix distincts, indicateurs cohérents, nom non réservé : `stock`, `ventes`,
`relations`, `equipe`).

`GET /eclipse/suggestions` — `[{adaptationKey, moduleKey, title, reason}]` (ex. `title` « Eclipse suggère un champ
« N° de lot » dans Stock »), adaptations non actives des modules accessibles au rôle ; module-service injoignable :
liste vide.

Les deux `propose` comptent dans la limite de débit et sont journalisées (`/audit`) : outil
`eclipse_propose_adaptation` ou `eclipse_propose_module`, action `proposed` (clé d'adaptation ou slug du module),
label `RECOMMENDATION` (`NONE` quand rien n'est proposé). `suggestions` n'est pas limitée.

## Outils par module

| Module (rôles) | Lecture | Écriture (confirmée) |
|---|---|---|
| Stock (STOCK, ADMIN) | `search_products`, `get_stock_status`, `get_product_autonomy`, `get_replenishment_forecast` (quantité à commander, stock de sécurité, hypothèses), `list_replenishment_needs` (produits à commander maintenant), `get_stock_summary`, `get_product_movements`, `search_suppliers`, `list_purchase_orders`, `check_supplier_invoice` | `record_stock_movement`, `create_purchase_order`, `validate_purchase_order`, `receive_purchase_order`, `register_supplier_invoice` (une facture `BLOCKED` est signalée, jamais présentée comme sûre) |
| Vente (VENTE, ADMIN) | `get_sales_summary`, `get_top_products`, `search_sales`, `search_customers`, `get_customer_profile` | `create_sale`, `validate_sale` |
| RH (RH, ADMIN) | `get_hr_summary`, `search_employees` | — |
| CRM (CRM, ADMIN) | `get_party_timeline` | — |

Gemini ne reçoit que les outils du rôle connecté ; la gateway revérifie chaque appel.

## Structure

```
app/
├── main.py                 Application (état, routeurs)
├── config.py               Variables d'environnement (même nom que le champ, en majuscules)
├── api/                    chat_routes (JSON + SSE), conversation_routes (boutons), invoice_routes, audit_routes,
│                           eclipse_routes, system_routes (/ui, /health, /tools), error_handlers, rate_limit,
│                           turn_support, identity, schemas
├── agent/                  orchestrator (boucle Gemini), streaming, agent_base, gemini_client/config/errors, records,
│                           prompts, memory (RAM), confirmation (garde-fou)
├── trust/                  numbers (extraction des nombres), assessment (label + vérification)
├── audit/                  store (SQLite), entry (ligne de journal sans donnée sensible)
├── invoices/               upload (multipart, taille, type), models, extractor (Gemini sans outil), shield_check
├── eclipse/                catalog, matcher (mots-clés), gemini_intent, templates, generic_module, gemini_designer,
│                           definition (ModuleDefinitionRequest), adaptation_flow, module_flow, suggestions
├── erp/                    client (HTTP gateway + token), context (utilisateur courant), periods
├── tools/                  spec, catalog (liste blanche + rôles), registry, runner, stock_tools, invoice_tools/args,
│                           vente_tools, rh_crm_tools
└── static/chat.html        Page de test
scripts/                    seed_demo.py, test_erp_agent.py, erp_http.py (bibliothèque standard uniquement)
tests/                      pytest (fake_gemini, fake_erp)
```

## Ajouter un outil

1. Dans `app/tools/<module>_tools.py` : un modèle d'arguments (`Args`), une fonction
   `async def mon_outil(erp, a)` qui appelle `erp.call("GET", "/api/<module>/...")`, et un `ToolSpec`
   dans `SPECS` (nom, description courte, paramètres). Nouveau fichier : l'ajouter à `ALL_SPECS` (`app/tools/catalog.py`).
2. Écriture ? Ajouter un 5ᵉ argument `summarize` au `ToolSpec` : la confirmation devient obligatoire.
3. Un test dans `tests/`.

## Limites connues

- Pas de coût d'achat dans les produits : l'IA donne le chiffre d'affaires, pas le bénéfice.
- Conversations, actions en attente et compteurs de débit en mémoire vive (perdus au redémarrage, une seule instance).
- Vérification des chiffres textuelle : un total calculé par l'IA (somme, pourcentage) est signalé non vérifié.
- `/invoices/extract` dépend de l'API Invoice Shield du stock-service (`/api/stock/supplier-invoices`).
- `/sales/top-products` n'a pas de filtre de période côté Vente.
