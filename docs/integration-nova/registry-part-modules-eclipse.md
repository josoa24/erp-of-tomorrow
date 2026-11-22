# Registry draft — module engine + Eclipse (orchestrator decisions)

## C1. module-service (owner I6) — gateway prefix /api/modules/** (prefix stripped), port 8088, schema `module`, DB user `module_svc`
Roles: every authenticated role may read and write records; definitions and adaptations writes require X-User-Role ADMIN, checked in module-service (403 FORBIDDEN otherwise).

Definitions
- GET  /definitions -> 200 [ModuleSummary {id, slug, name, icon, createdAt}] ordered by createdAt
- GET  /definitions/{slug} -> 200 ModuleDefinition | 404
- POST /definitions (ADMIN) body ModuleDefinitionRequest -> 201 ModuleDefinition | 400 | 403 | 409 (slug exists)
- DELETE /definitions/{slug} (ADMIN) -> 204 | 403 | 404 | 422 BUSINESS (module has records)

ModuleDefinitionRequest {name (2..40), icon (allow-list: truck, wrench, calendar-days, package, clipboard-list, layout-grid, users, receipt), description? (<=280),
  columns[1..12]: {key (snake, unique), label, type: TEXT|NUMBER|MONEY|DATE|TIME|DATETIME|CHOICE|REFERENCE, required: bool, listed: bool (default true, see registry D2), choices?: string[] (CHOICE), reference?: CUSTOMER|SALE|EMPLOYEE|PRODUCT|SUPPLIER (REFERENCE)},
  statuses[2..6]: {key, label, tone: SUCCESS|WARNING|CRITICAL|INFO|NEUTRAL, final: bool} (ordered workflow; first = initial),
  indicators[0..4]: {key, label, icon, kind: COUNT|SUM|RATIO_TODAY, statuses?: string[], column?: string (MONEY column for SUM), todayColumn?: string (DATE/DATETIME/TIME-of-day scope), detailColumn?: string (list distinct labels, e.g. livreur names)}}
ModuleDefinition = request fields + {id, slug, createdBy, createdAt}.
Tables: module_definition (id PK, slug UNIQUE NOT NULL, name, icon, description, created_by, created_at), module_column (id, definition_id FK NOT NULL ON DELETE CASCADE, position, key, label, type, required, choices TEXT(json), reference_target, UNIQUE(definition_id,key)), module_status (id, definition_id FK cascade, position, key, label, tone, final, UNIQUE(definition_id,key)), module_indicator (id, definition_id FK cascade, position, key, label, icon, kind, statuses json, column_key, today_column_key, detail_column_key). Cardinality definition 1–N columns/statuses/indicators.

Records
- GET  /definitions/{slug}/records?search=&status=&page=0&size=20 -> 200 [ModuleRecord] + X-Total-Count
- POST /definitions/{slug}/records body {values: {colKey: value}} -> 201 (status = initial); REFERENCE values sent as {id} and stored as {id, label} after existence check through the owning service (CUSTOMER -> vente /customers/{id}, SALE -> vente /sales/{id}, EMPLOYEE -> rh /employees/{id}, PRODUCT -> stock /products/{id}, SUPPLIER -> stock /suppliers/{id}) with the caller's X-User-* headers; 404 -> 422 BUSINESS "référence introuvable", 503 if service down.
- POST /definitions/{slug}/records/{id}/advance -> 200 moves to the next status; record in a final status is immutable (409 IMMUTABLE on advance).
- GET  /definitions/{slug}/indicators -> 200 [{key, label, icon, kind, value (number), total? (RATIO_TODAY denominator), money: bool, detail: string[]}]
- GET  /definitions/{slug}/status-counts -> 200 {all: n, byStatus: {key: n}}
- GET  /definitions/{slug}/sales-import -> 200 {available: n} (only for modules having a REFERENCE SALE column; else 422)
- POST /definitions/{slug}/sales-import -> 200 {created: n}: creates one record per VALIDATED sale of the current ISO week with toDeliver=true not already referenced by a record of this module; fills SALE ref, CUSTOMER ref (if column), MONEY column (first MONEY column = sale total); other columns empty.
ModuleRecord {id, values (json object), status, createdBy, createdAt, updatedAt}. Table module_record (id, definition_id FK NOT NULL ON DELETE RESTRICT, values jsonb NOT NULL, status NOT NULL, created_by, created_at, updated_at, INDEX(definition_id,status)). Cardinality definition 1–N records; record N–1 logical refs to other services (no physical FK across schemas).

Adaptation activations (catalog fixed in code: stock.lots-peremption, ventes.mobile-money-credit, relations.nif-plafond-credit, equipe.avances-salaire)
- GET  /adaptations -> 200 [{moduleKey, adaptationKey, activatedBy, activatedAt}]
- POST /adaptations (ADMIN) {moduleKey, adaptationKey} -> 201 | 400 unknown key | 403 | 409 BUSINESS already active
- DELETE /adaptations/{adaptationKey} (ADMIN) -> 204 | 404
Table adaptation_activation (id, module_key NOT NULL, adaptation_key UNIQUE NOT NULL, activated_by NOT NULL, activated_at NOT NULL).

## C2. Eclipse proposals (owner I7, ai-service) — /api/ai/eclipse/**
- POST /eclipse/adaptations/propose {moduleKey: stock|ventes|relations|equipe|<custom slug>, request (1..500)} -> 200 {outcome: ADAPTATION|ALREADY_ACTIVE|QUESTION|UNSUPPORTED|CREATE_MODULE (see registry D1), adaptationKey?, request, title?, summary? ("Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt"), message? (honest text for UNSUPPORTED), canApply: role == ADMIN, targetModuleKey? (set when moduleKey is "accueil": the base module the adaptation belongs to)}. Recognition: Gemini structured classification when GEMINI_API_KEY is set, deterministic keyword matcher otherwise (and as safety net). QUESTION means "answer as a normal chat question" (front then streams /agent/chat/stream).
- POST /eclipse/modules/propose {description (1..500)} -> 200 {proposal: ModuleDefinitionRequest, accessText: "Toute l'équipe. Le gérant peut modifier ou supprimer le module.", source: GEMINI|TEMPLATE, canCreate: role == ADMIN}. Templates (deterministic fallback): Livraisons (client REF CUSTOMER, vente d'origine REF SALE, quartier TEXT, livreur REF EMPLOYEE, heure prévue TIME, montant à encaisser MONEY, signature du client TEXT optional unchecked by default; statuses À préparer -> En route -> Livrée; indicators Livrées aujourd'hui RATIO_TODAY, En route COUNT detail livreur, À encaisser SUM montant not Livrée), Réparations, Rendez-vous; unknown -> a minimal generic proposal built from the description + honest note.
- GET /eclipse/suggestions -> 200 [{adaptationKey, moduleKey, title, reason}] catalog adaptations not yet active and visible to the caller's role (used by Accueil "À décider").
- Apply / Create are done by the front directly on module-service (C1) with the user's token, never by ai-service.

## C3. Front contracts (owner I7 unless noted)
- [OWNER CHANGED TO I0 socle, decision 2026-11-22 06:00 to unblock module screens] features/eclipse/hooks/useModuleAdaptation(moduleKey) -> {active: boolean, preview: AdaptationProposal | null, isApplying, apply(), cancel(), canApply}. Module screens render adapted columns/filters/KPIs when `active || preview`, with highlight (isNew / highlighted) only while `preview`.
- features/eclipse/hooks/useEclipseRequest(moduleKey) used by EclipseDock: sends to /eclipse/adaptations/propose; ADAPTATION -> sets the preview for that module; QUESTION -> streams chat answer in an answer panel; UNSUPPORTED/ALREADY_ACTIVE -> shows Eclipse's message in the answer panel (informational text, not an error).
- Adaptation keys and module keys constants: features/eclipse/constants/adaptations.ts (owner I0 socle, same decision).
- EclipseAdaptationProvider (preview state per module: startPreview(moduleKey, proposal), clearPreview(moduleKey); URL `?eclipse=<key>` sync) in features/eclipse/ (owner I0 socle). I7 calls startPreview from useEclipseRequest; module screens only call useModuleAdaptation.
- AdaptationProposal type: {adaptationKey, moduleKey, request, title, summary} (owner I0 socle).
