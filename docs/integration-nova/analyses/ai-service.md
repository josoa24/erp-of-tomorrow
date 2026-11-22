# ai-service, voice-service and Eclipse integration: analysis (read-only)

I did not change anything in the repo: `git status` is unchanged and the branch is still `songomby_d2`. Running pytest may have refreshed some `__pycache__` folders, which git ignores. Scratch venvs are in `docs/integration-nova/analyses/`.

## 1. ai-service (`services/ai-service`, FastAPI, port 8086, gateway route `/api/ai/**` with StripPrefix=2)

**Structure (EXISTS)**
- `app/main.py:42-75` `create_app(settings, client, erp_http_client)` is injectable for tests. Its lifespan builds `ToolRegistry`, `ConversationStore`, `AuditStore`, `SlidingWindowLimiter`, `GeminiAgent` and `InvoiceExtractor`.
- Packages:
  - `agent/`: orchestrator, streaming, confirmation, memory, prompts, gemini_*
  - `api/`: routes, schemas, errors, identity, rate_limit
  - `tools/`: catalog, registry, spec, runner, and one file per module
  - `trust/`, `audit/`, `erp/` (gateway client + user ContextVar), `invoices/`
- Identity: `api/identity.py:12-20` requires `Authorization: Bearer` plus `X-User-Role` (one of ADMIN/STOCK/VENTE/RH/CRM) and `X-User-Id`. The gateway injects these (`GatewayAuthFilter.java:20-22,61-64`). `RoleAccessPolicy.java:22` allows `/api/ai` for all business roles plus ADMIN.

**Endpoints (EXISTS)**

| Route | File | Notes |
|---|---|---|
| `POST /agent/chat` | `api/chat_routes.py:21-43` | Body `{message ≤4000, conversation_id?}` (`schemas.py:15-20`). Returns `ChatResponse` `{response, conversation_id, history_turns, tool_calls[], pending_actions[], max_turns_reached, metrics, trust}` (`schemas.py:34-42`). |
| `POST /agent/chat/stream` | `chat_routes.py:46-83` | SSE events: `start{conversation_id}`, `token{text}`×N, `tool_call`, `tool_result` (with `status`/`action`), `done{…same as JSON + pending_actions}`, or `error{type,code,message}`. Sets `X-Accel-Buffering: no`. |
| `GET/DELETE /agent/conversations/{id}` | `conversation_routes.py:26-39` | Owner-only. |
| `POST …/actions/{aid}/confirm` / `cancel` | `conversation_routes.py:42-86` | Human button. Confirm runs the write with the user's token and does not go through Gemini. |
| `GET /tools` | `system_routes.py:27-32` | Tools for the caller's role, including `requires_confirmation`. |
| `GET /audit` | `audit_routes.py:9-20` | ADMIN only. |
| `POST /invoices/extract` | `invoice_routes.py` | Multipart. Stock role. |
| `GET /health`, `GET /ui` (test page), `/v3/api-docs` | | |

**Conversation memory (EXISTS)**: `agent/memory.py`. Stored in RAM only.
- Only user text and final model text are kept. There are no function calls in history.
- Limits: `MAX_HISTORY_TURNS=6`, `HISTORY_MAX_CHARS=1500`, TTL 30 min, LRU cap of 1000 conversations.
- Each conversation has an `owner`, an `asyncio.Lock` and a `pending` dict.
- Everything is lost on restart.

**Tools per role (EXISTS)**: `tools/catalog.py:10` `MODULE_ROLES` mirrors the gateway: one role per module, ADMIN gets everything, `system` tools are open to all.
- stock (`stock_tools.py:125-158`): search_products, get_stock_status, get_product_autonomy, get_stock_summary, get_product_movements, search_suppliers, list_purchase_orders. Writes: record_stock_movement, create/validate/receive_purchase_order. Invoice tools: check_supplier_invoice, register_supplier_invoice (write).
- vente (`vente_tools.py:123-144`): get_sales_summary, get_top_products, search_sales, search_customers, get_customer_profile. Writes: create_sale, validate_sale.
- rh/crm, read-only (`rh_crm_tools.py:36-44`): get_hr_summary, search_employees, get_party_timeline.
- system: confirm_action, cancel_action.
- Gemini only receives the role's tools (`registry.py:38-43`). The role is checked again when a tool runs (`registry.py:79-80`).

**Write confirmation (EXISTS)**: `agent/confirmation.py` (`ActionGuard`).
- A write tool (any `ToolSpec` with `summarize`, see `spec.py:29-37`) is not executed. `propose()` stores a `PendingAction{id(8 hex), tool, arguments, summary, created_turn}` and returns `CONFIRMATION_REQUIRED` (`:54-65`).
- `take(by_user=False)` refuses a confirmation made in the same turn as the proposal (`:80-84`).
- Actions expire after `CONFIRMATION_TTL_MIN` (10 min) and run only once.
- `execute_pending` re-checks the role and re-validates the stored args (`registry.py:59-69`).
- Pending actions reach the client through `tool_result.action`, `done.pending_actions` and `GET conversation`.

**Trust layer (EXISTS)**: `trust/assessment.py:22-40`. Label is RECOMMENDATION (a write was proposed), ESTIMATION (forecast tools), FACT or NONE. It also returns `verifiedNumbers`/`unverifiedNumbers`: numbers in the answer are matched against the question and the tool results (`trust/numbers.py`).

**Audit (EXISTS)**: `audit/store.py`, append-only SQLite at `AI_AUDIT_DB=data/ai_audit.db`.
- Each row holds user, role, conversation, question (max 300 chars), tool names, actions `{proposed, confirmed, refused, failed}` as `{tool, action_id}`, and the trust label. It never stores args or tokens (`audit/entry.py`).
- `turn_support.py:32-39` makes sure an audit failure never breaks a response.
- `docker-compose.yml:141-156` mounts **no volume** for `/app/data`, so the audit is lost when the container is recreated.

**Rate limit (EXISTS)**: `api/rate_limit.py`. Sliding window per `X-User-Id`, in memory, `AI_RATE_LIMIT_PER_MINUTE=20`. Applies to the chat and invoice routers. The confirm/cancel routes are not limited. Over the limit it returns 429 with `Retry-After`.

**Config (EXISTS)**: `app/config.py`, reading `.env` and env vars.
- `GEMINI_API_KEY` is a `SecretStr`. `has_api_key` is false when the key is empty or equals `ma_cle_api`.
- `GEMINI_MODEL=gemini-3.5-flash-lite`. Thinking level or budget, `MAX_TOOL_TURNS=5`, timeouts, retries.
- `CORS_ORIGINS` is defined but no CORS middleware uses it; the gateway handles CORS.
- `requirements.txt` is **unpinned**. google-genai 3.6.1 was installed.

**Error format (EXISTS)**: `{status, type, message, timestamp}` (`schemas.py:45-49`, `error_handlers.py`). Types: 401 UNAUTHORIZED, 429 RATE_LIMIT, 400 VALIDATION, SERVER (with `AgentError` mapped to 502/503/504, `gemini_errors.py:29-53`), 403 FORBIDDEN, 404 NOT_FOUND. During a stream, errors arrive as an SSE `error` event while the HTTP status stays 200.

**Without `GEMINI_API_KEY`**: `main.py:62-63` logs an error. `agent` and `invoice_extractor` are `None`, so chat and stream return **503 SERVER** "GEMINI_API_KEY manquante" (`chat_routes.py:25-26,52-53`). The other routes (`/tools`, `/audit`, conversations) still work. **There is no mock or deterministic mode in production code.** The only deterministic path is the test `FakeClient` (`tests/fake_gemini.py`), which returns real SDK response objects from a script and is injected through `create_app(client=…)`.

**Structured output**:
- **EXISTS** only in `invoices/extractor.py:33-35`, which uses `response_mime_type="application/json"` and `response_schema=ExtractedInvoice`, then validates with Pydantic (`:46-49`) and raises 502 `EXTRACTION_INVALID`.
- Function calling uses hand-built `types.Schema` (`spec.py:39-42`).
- The installed SDK supports `FunctionDeclaration.parameters_json_schema` and `GenerateContentConfig.response_json_schema` (checked), so nested Pydantic schemas can be used directly.
- `gemini_client.py` itself only builds the client and checks the model.

**Tests (EXISTS)**: 14 files and 61 tests: confirmation 7, api 7, registry 6, trust 6, orchestrator 5, invoices 9, memory 4, audit 4, stream 2, a wire-format test against the real SDK with a fake HTTP server, among others.
- Command used: `python3 -I -m venv …/venv-ai && pip install -r requirements-dev.txt && GEMINI_API_KEY= AI_AUDIT_DB=:memory: python -m pytest -q -p no:cacheprovider`
- Result: **61 passed in 2.47 s** (Python 3.13.5, fastapi 0.165.0, google-genai 3.6.1, pydantic 2.14.4).

**Rule deviations (EXISTS)**: these files exceed the 100-line rule: `tools/stock_tools.py` (158), `tools/vente_tools.py` (144) and `static/chat.html` (348).

## 2. voice-service (high level)

- FastAPI on port 8087, gateway `/api/voice/**`, faster-whisper running locally.
- `POST /transcribe` (multipart `audio`, `language`) returns `{text, language, durationSeconds}`.
- `WS /stream?language=` takes PCM16 at 16 kHz and returns `partial`/`final` JSON.
- Errors use the ERP format. Settings come from `VOICE_*` env vars. The compose profile is `voice`.
- Tests: **46 passed in 3.27 s**. Whisper is faked, nothing is downloaded.
- Risk: README line ~45 says NovaUI can reach `ws://gateway/api/voice/stream` "avec son token dans les en-têtes". Browsers cannot set headers on a WebSocket, and `GatewayAuthFilter.java:53` only reads `Authorization`. As things stand, the browser can only use `POST /transcribe` through the gateway.
- Voice and chat are not wired together; that is AI-19, still "À faire".

## 3. The two Eclipse capabilities from Figma

**What the backend can serve today**
- MISSING: none of the "after AI" data exists in the Java services. grep found nothing in stock-service for lot or expiry, nothing in vente-service for payment method, MVola or credit, nothing in crm/vente for NIF, STAT or credit limit, nothing in rh-service for salary advances, and no generic custom-field or JSON column anywhere.
- So "Ajoute le n° de lot…" cannot become real values without new storage. Figma 05 shows filled `LOT-2610-C` values immediately after the proposal, which only seeded data could produce.
- MISSING: `ChatRequest` has no screen or module context (`schemas.py:15-20`). The Eclipse bar cannot currently tell the agent which screen it is on or which columns it already shows.

**Which existing pattern can carry "proposal → apply" (DERIVABLE)**
- **ActionGuard plus the confirm/cancel endpoints already matches screen 05 exactly** ("Annuler" / "Appliquer pour l'équipe", no editing).
- How to derive it: add one `ToolSpec("propose_screen_adaptation", args_model=ScreenAdaptation, handler=POST to a persistence API through ErpClient, summarize=…)`.
- Everything else is reused unchanged: role filtering, the SSE `tool_result{status:"pending_confirmation", action:{arguments=full spec}}` the front needs for the preview, the 10-min TTL, single execution, the confirm/cancel buttons, audit proposed/confirmed/refused, and trust=RECOMMENDATION.
- **Screen 04b ("Créer le module") does not fit as-is.** The user can uncheck columns or indicators and rename the module, but `take()` runs the *stored* arguments (`confirmation.py:69-87`, `registry.py:65-69`). Minimal change, either one:
  - (a) `confirm` accepts an optional body `{name?, exclude: [keys]}`, validated server-side as a subset of the proposal so nothing can be added; or
  - (b) the AI only drafts, and "Créer le module" is an ordinary human write sent by the front to the persistence API.
- I recommend (a) so the audit trail stays in one place.
- For 04a (a dedicated modal where the intent is already known), a direct structured-output endpoint following the `InvoiceExtractor` pattern is cleaner than chat routing. One example: `POST /eclipse/module-drafts` with `response_json_schema=ModuleSpec`, temperature 0, no tools, then register the draft with `ActionGuard.propose` on a conversation.

**Where proposals and applied adaptations should live**
- Proposals: in `Conversation.pending` (RAM, 10 min). This is acceptable for a preview.
- Applied adaptations: **not in ai-service**. Reasons:
  - ADR-005 states "sans base".
  - It runs as a single instance.
  - The compose `ai` profile is optional, so screens would lose their configuration when Gemini or ai-service is down.
  - The SQLite store is an append-only audit store with no volume.
- Proposed MISSING component: a small Spring service, e.g. `services/workspace-service`, with its own PostgreSQL schema per ADR-006, Reader/Creator/Updater/Deleter interfaces and DTOs. Tables:
  - `screen_adaptation(id, module, version, spec jsonb, status, created_by, created_at)`
  - `custom_module(id, slug, name, icon, spec jsonb, version, …)`
  - `custom_record(module_id, data jsonb, status…)` for "Livraisons"
  - `extension_value(entity_module, entity_id, field_key, value…)` for lot and expiry on existing products
- To respect immutability, "Annuler" after apply (the 04c toast) writes a new version with status REVERTED rather than a delete.
- It also needs a gateway route and a `RoleAccessPolicy` entry. Read access is for all roles of the module; writes are ADMIN or "gérant", which needs a decision because 04b says "Le gérant peut modifier".
- Alternative: real fields in stock, vente, crm and rh (lot/expiry, payment method, NIF, advances). That is more correct for the business, but it has to be done per team and per field, and it is not generic.

## 4. Frontend side

**novaui on `songomby_d2` (EXISTS)**
- `components/ui/ai-button.tsx` is a secondary button with a Sparkle icon and no logic.
- `AiOrb`/`EclipseLogo` are visual only.
- **There is no assistant API, hook or SSE code.**
- `services/api/httpClient.ts:24-42` has `request`/`httpGet`/`httpPost` only, with no timeout, no AbortSignal and no `send`/`httpDelete`.
- The Vite proxy `/api → :8080` is a plain http-proxy, which passes SSE through.
- `constants/appInfo.ts:1` has `APP_NAME='Eclipse'` and `sessionStore` uses the key `eclipse.session`. Both conflict with the decision that the product is NOVA and Eclipse is only the AI.

**Branch `web/fix-frontend-stabilization` (DERIVABLE)**
- It is 9 commits ahead of merge-base `cd6eafe`, which predates the Tailwind and TanStack migration. `diff --stat` shows 176 files and +10 411 lines because all of novaui appears as new.
- The assistant data layer has no screen and is router-agnostic:
  - `types/agent.ts` (70)
  - `utils/sseParser.ts` (49), incremental
  - `services/api/sseStream.ts` (28)
  - `services/agentEvents.ts` (64), snake_case wire to typed events
  - `services/agentStreamReducer.ts` (81) and `agentChatReducer.ts` (55)
  - `services/api/agentApi.ts` (47): streamAgentChat, confirmAgentAction, cancelAgentAction, deleteAgentConversation
  - `hooks/useAgentChat.ts` (73) and `useAgentActions.ts` (48), with double-click protection
  - tests for each of these
- To port it, also bring the branch's `httpClient.ts` (`send`, timeouts, `httpDelete`), `requestSignal.ts`, `responseReader.ts` and `utils/abortError.ts`.
- `useApiError` on `songomby_d2` already uses TanStack and has a compatible `(error) => void` signature.
- **Rule conflict**: `useAgentChat.ts:9-12,35` keeps AI errors on the message and sends only 401/403 to the error page. `REGLES_DEVELOPPEMENT.md:138-146` forbids showing errors in the detail view. Someone has to decide.

**How the Eclipse bar would work**: it sends `{message, conversation_id, context:{module}}` to `streamAgentChat`. When it receives a `tool_result` with `status==="pending_confirmation"` and `tool==="propose_screen_adaptation"`, the screen renders a preview from `action.arguments` (the dashed "Nouveau" columns, the KPI, the filter). The bar's "Annuler"/"Appliquer" buttons call `cancel`/`confirm`. On success, React Query invalidates `['adaptations', module]`. Screens read their effective column, filter and KPI configuration from workspace-service through services, hooks and components.

## 5. Recommended minimal architecture

1. **ai-service**:
   - Add an optional `context: {module: Literal[...], screen?}` to `ChatRequest` and pass it into `build_system_instruction`.
   - New `app/adapt/` package, one file per responsibility:
     - `models.py`: closed Pydantic catalog. Column types are text, date, number, money and enum, plus `link∈{crm,vente,rh}`. Filter kinds: equals, date_within_days. KPI kinds: count and sum with an optional date window. Alert rule: `{field, days_before}`. Icon comes from an enum. `ModuleSpec` has `statuses[]` as an ordered workflow.
     - `adapt_tools.py`: two ToolSpecs, `propose_screen_adaptation` (module = target module) and `propose_module` (module="system" or ADMIN-only), declared with `parameters_json_schema`.
     - `summaries.py`.
   - Confirm handlers call `/api/workspace/...` through `ErpClient` with the user's token.
   - Optional confirm body `exclude/name` for 04b.
2. **workspace-service** (Java) as described in §3, plus gateway route and policy.
3. **novaui**: port the branch data layer, then add `adaptationApi.ts`, `useScreenAdaptations(module)` (React Query) and `EclipseBar` (presentational) with `useEclipseBar` (state).
4. **Tests**: use `FakeClient` scripts with `fc_response(("propose_screen_adaptation", {...}))`, covering role, invalid spec, confirm/cancel/expiry and subset-only override. Add JUnit tests for workspace-service and Vitest tests for the event and reducer mapping.
5. **No-key behaviour**: keep the 503, routed to the error page. If a demo fallback is wanted, an explicit `ECLIPSE_DEMO_SCRIPT` flag could inject a scripted client (the `FakeClient` pattern) limited to the 4 Figma prompts, and the UI must label it as a demo.

## 6. Risks

- **Data gap**: adaptations add *empty* columns unless values are seeded or captured. The Figma "after" screens show data that does not exist anywhere.
- **Gemini nested args**: deep nested function arguments are less reliable on `flash-lite`. Server-side Pydantic validation plus a closed catalog are required, along with a retry or clarifying question.
- **RAM state**: pending proposals live in RAM with a 10-min TTL and a single instance. A restart or a TTL expiry between preview and "Appliquer" leads to 404 `ACTION_NOT_FOUND`.
- **Permissions**: "Appliquer pour l'équipe" is a team-wide schema change with no defined role (ADMIN vs gérant). `MODULE_ROLES` has no manager concept.
- **Unpinned SDK**: `google-genai` and fastapi are unpinned, so a Docker build can pick up breaking changes.
- **Error-display rule**: the inline AI error handling on the branch conflicts with the rule.
- **Prompt injection**: a free-text request becomes a schema change. The closed catalog must ensure no raw labels are executed and no formulas or code are accepted.
- **Rule debt**: files over 100 lines in ai-service.
- **Lost audit**: no volume, so the audit is lost when the container is recreated.
- **Voice**: WebSocket auth through the gateway does not work from a browser.
