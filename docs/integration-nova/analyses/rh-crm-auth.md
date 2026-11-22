# Report: rh, crm, auth and gateway compared with the NOVA Figma screens (branch songomby_d2, read-only)

## 0. Tests
All four suites pass, with no failures, errors or skips:
- **rh-service:** 88 tests
- **crm-service:** 38 tests
- **auth-service:** 42 tests
- **gateway:** 30 tests

How they were run:
- Image `maven:3.9-eclipse-temurin-17`, command `mvn -q -B test`.
- I ran them on tar copies under `docs/integration-nova/analyses/copies/`, not on the repo itself, so no `target/` folder was written into the repo.
- The Maven cache was `docs/integration-nova/analyses/m2`, not `~/.m2`, because `~/.m2` does not exist and Docker would have created it as root.
- Logs: `docs/integration-nova/analyses/test-*.log`.
- The stack traces in the logs are the expected `log.error` output of `GlobalExceptionHandlerFallbackTest`.
- `git status` of the repo is unchanged.

## 1. Gateway
- **Routes** (`gateway/src/main/resources/application.yml:19-61`): `/api/{auth,crm,rh,vente,stock,ai,voice}/**`, each with `StripPrefix=2`. Services are reached directly through `*_URL`. Only the gateway (8080) and the database are published in `docker-compose.yml`.
- **Role matrix** (`gateway/src/main/java/com/hackaton/gateway/security/RoleAccessPolicy.java`):
  - `:17-24` `MODULE_ROLES`, built with `Map.of`. It has 7 entries; `Map.of` accepts at most 10, so beyond that it must switch to `ofEntries`.
    - stock → STOCK, vente → VENTE, rh → RH, crm → CRM.
    - ai and voice → STOCK, VENTE, RH, CRM.
    - auth → nobody.
  - `:31` ADMIN is allowed on every path.
  - `:14,31` `/api/auth/users/me` and everything under it is open to every role. Any future endpoint placed under `/users/me/**` is automatically open to all roles.
  - `:26-28` public paths: login, `/api/ai/ui`, `v3/api-docs`.
  - Matching is by path prefix only, never by HTTP method or action. "RH may read but only ADMIN validates" cannot be expressed at the gateway.
- **Identity headers** (`GatewayAuthFilter.java:101-110`): the gateway strips any incoming `X-User-Id/Email/Role` and re-injects them from the JWT. The JWT carries the email as `sub` plus `userId` and `role` (`JwtTokenParser.java:146-153`); it has no name. The `enabled` flag is not checked after login, so a disabled account keeps access until its token expires (TTL 480 min).
- **CORS** (`application.yml:10-18`): `allowedMethods: GET,POST,PUT,DELETE,OPTIONS`, so **PATCH is not allowed**. Exposed header: `X-Total-Count`. In dev, NovaUI goes through the Vite proxy (`novaui/vite.config.ts:33-34`).
- **Gateway changes needed for the screens:** none, as long as the new endpoints stay under existing prefixes (`/api/rh/...`, `/api/crm/...`, `/api/auth/users/me`).
  - A new service (for example a separate backend for Livraisons) would need: a route in the yml, a `MODULE_ROLES` entry, a swagger URL, compose env vars and a `RoleAccessPolicyTest` case.
  - If Livraisons lives in vente-service (`/api/vente/deliveries`), VENTE and ADMIN get it automatically. That matches 00g, where the VENTE nav shows Livraisons.

## 2. auth-service: profile, avatar menu (00e/f/g)
- **What exists:**
  - `UserAccount` (`user/entity/UserAccount.java:22-48`): id, email (unique, not null), passwordHash, role (enum ADMIN/STOCK/VENTE/RH/CRM), enabled (default true), createdAt.
  - Endpoints:
    - `POST /login` returns `LoginResponse(token, email, role)`; the user id is not in the body.
    - `GET /users`, `POST /users`, `PUT /users/{id}` (role only), `POST /users/{id}/disable|enable`.
    - `POST /users/me/password`.
  - Reader, Creator, Updater and PasswordChanger services each have an interface.
- **Display name ("Hery Rabe"): MISSING.** No name is stored anywhere in auth.
- **`GET /api/auth/users/me`: MISSING in the service**, although the gateway already allows it. Today Spring only matches `PUT /users/{id}`, so a GET most likely returns 405.
- **Role label and "Mes accès": DERIVABLE in the front** from `role`, using a front copy of `MODULE_ROLES`. That copy can drift from the gateway; cover it with a test.
- **Password and logout:** the Figma says "contactez l'administrateur", so the existing endpoint goes unused, which is fine. Logout is client-side only: the JWT is stateless and there is no revocation.
- **Login refused (00c):** EXISTS. Wrong credentials give 401 `{status, type:"UNAUTHORIZED", message, timestamp}` (`GlobalExceptionHandler.java:22-25`). A disabled account gives the same 401 with a different message.
- **Employee ↔ user link:** EXISTS but cannot be used for the profile.
  - `Employee.userId` is unique and nullable (`rh/employee/entity/Employee.java:50-51`).
  - Nothing checks that the account exists in auth.
  - There is no `?userId=` filter.
  - `/api/rh` is reserved to RH and ADMIN, so VENTE, STOCK and CRM users cannot read it.

## 3. rh-service: Équipe (07) and Accueil leaves
**Entities as they exist:**
- `employees` (`Employee.java`): firstName and lastName (not null); email (not null, unique); position (nullable); departmentId (not null); hireDate (not null); baseSalary numeric(14,2) not null; userId; leaveBalance (default 25); createdAt.
- `departments`: id, name (unique), createdAt.
- `leave_requests`: employeeId, startDate, endDate, days, reason, status DRAFT/VALIDATED/REJECTED, createdAt, decidedAt. **There is no `decidedBy`.**
- `attendances`: employeeId, workDate, checkIn, checkOut.

**Endpoints:**
- `/employees`: GET with `search` (first name, last name, email) and `departmentId`; GET by id; POST; PUT; DELETE. Delete is refused when the employee has leaves or attendance records.
- `/departments`: CRUD.
- `/leaves`: GET with `employeeId` and `status`; GET by id; POST; `POST /{id}/validate|reject`. A request that is no longer DRAFT returns 409 IMMUTABLE. The balance is debited on validation.
- `/attendances`: GET, `check-in`, `check-out`.
- `/summary`: `{totalEmployees, onLeaveToday, headcountByDepartment[]}`.

| Figma element | Status |
|---|---|
| Full name and initials | EXISTS: first and last name |
| "Chauffeur" | EXISTS: `position`, nullable |
| Service | DERIVABLE: `departmentId` joined with `GET /departments` (the response has no name) |
| Contrat CDI/CDD | **MISSING** |
| "mars 2021" | EXISTS: `hireDate`, formatted in the front with `Intl` fr, `{month:'short', year:'numeric'}` |
| Présent / En congé | DERIVABLE: `GET /leaves?status=VALIDATED`, then filter start ≤ today ≤ end in the front. That list contains the whole history; there is no date filter (`LeaveReaderService.java:30-36`) |
| KPI Effectif and "4 services" | EXISTS: `/summary`. `headcountByDepartment` includes departments with 0 employees because of the left join, so decide whether to filter `employeeCount > 0` |
| KPI En congé aujourd'hui | EXISTS: `onLeaveToday`, computed on the **UTC** date (`HrSummaryReaderService.java:25`). Madagascar is UTC+3, so between 21:00 and 24:00 UTC the server's "today" is the previous local day |
| KPI Demandes | DERIVABLE: `GET /leaves?status=DRAFT`, count |
| Tous/Présents/En congé filters with counts, sort by service | DERIVABLE in the front |
| Search | EXISTS server-side (does not search `position`) |
| Ajouter un employé | EXISTS: `POST /employees`. Email is required and unique, and `hireDate` must be today or earlier; check this fits the form |
| After IA: salary | EXISTS: `baseSalary`. "Plafond 30 %" is DERIVABLE as baseSalary × 0.30 (800 000 → 240 000 Ar, matching the Figma) |
| Monthly advances, "à valider", KPI Avances du mois, Avance demandée filter | **MISSING** |
| Validation by the manager | **MISSING**: there is no manager concept and no actor is recorded on decisions |

**Accueil: "3 demandes de congé du 23 au 27 décembre → Valider"**
- The list is DERIVABLE from `GET /api/rh/leaves?status=DRAFT`.
  - The date range is min(start) to max(end).
  - Names need a join with `/employees`, because `LeaveResponse` only carries `employeeId`.
- Validation exists one leave at a time; there is no bulk endpoint. I suggest "Valider" opens the Équipe "Demandes" list, so each failure (409 or 422) is handled on its own.
- Only RH and ADMIN may call `/api/rh`. The front must not call it for the other roles: a 403 would otherwise open the error page.

## 4. crm-service: Relations (06-relations)
- **What exists:**
  - `interactions`: partyType CUSTOMER/SUPPLIER, partyId, type APPEL/EMAIL/REUNION/VISITE/NOTE, occurredAt, summary (2000 chars), author (taken from `X-User-Email`), createdAt. Interactions are create and read only, which already satisfies the immutability rule.
  - `GET/POST /parties/{type}/{id}/interactions` and `GET /parties/{type}/{id}/timeline`. The timeline merges interactions with validated sales, or with VALIDATED/RECEIVED purchase orders.
  - **There is no list endpoint**: everything is per contact.
- **How crm calls vente and stock today:**
  - `RestClient` to `VENTE_URL` / `STOCK_URL` **directly**, bypassing the gateway (`application.yml` `remote.*`; compose `http://vente-service:8082`).
  - `IdentityForwarding.java:18-35` copies `X-User-Id/Email/Role` from the current servlet request. That relies on the request thread, so the headers are lost in async or parallel calls.
  - On the vente and stock side, `GatewayIdentityFilter` (line 39) only checks that `X-User-Id` and `X-User-Role` are present, not which role they carry. A CRM user therefore reads customers and suppliers through crm-service, even though the gateway refuses `/api/vente` and `/api/stock` to the CRM role (`CrmRoleAccessTest.java:15-16`).
  - `RemoteCaller` maps remote errors: 404 → NOT_FOUND, other 4xx → 422 BUSINESS, network failure → 503.
- **Who sees Relations:** CRM and ADMIN only. 00g confirms that VENTE does not see Relations.

**Field by field:**
- **Name and phone:** EXISTS for both customers (vente) and suppliers (stock).
- **City:** EXISTS for customers (`Customer.city`). **MISSING for suppliers**, which only have `address`; that is a stock-service change.
- **"Client · professionnel / particulier":** `CustomerType` ENTREPRISE/PARTICULIER, nullable.
- **Dernier échange:** DERIVABLE, but only per contact (one call per row). **MISSING as an aggregate.**
- **À relancer, Clients actifs ce trimestre, commandes en cours, Nouveau contact for the CRM role:** MISSING.
  - CRM-19 in `PLAN_CRM.md` is planned (not done) as `GET /follow-ups?days=30`, based on purchases and open tasks, whereas the Figma bases it on exchanges. The two need reconciling.
- **After IA (NIF/STAT, credit limit, credit outstanding):** MISSING, and owned by vente and stock.

## 5. Minimal proposals
Every schema change below is a nullable column or a new table, so `ddl-auto: update` can apply it without a data migration (the convention in `docs/NEW_FEATURES_IMPLEMENTATION.md:214`).

1. **auth: user name.**
   - Schema: `user_accounts.full_name varchar(120) NULL`, no backfill. The front falls back to the part of the email before the @.
   - DTOs: `UserCreateRequest.fullName` optional `@Size(max=120)`; add `fullName` to `UserResponse` and `LoginResponse`.
   - Endpoint: new `GET /users/me` that reads `X-User-Id` through `UserReader.findById` and returns `UserResponse` (404 if the account was deleted). No gateway change.
   - Optional: an `ADMIN_NAME` setting for `AdminSeeder`. For admin edits, `PUT /users/{id}/name` with an `@NotBlank` body.
   - Tests: WebMvc (200, 401 without the header, 404) and the reader.
2. **rh: contract type.**
   - Schema: enum `ContractType {CDI, CDD}`, column `employees.contract_type varchar(16) NULL`. Optional in `EmployeeRequest`, so existing PUTs keep working. The front shows "—" when it is null.
3. **rh: leaves.**
   - Add an `activeOn` (LocalDate) filter to `GET /leaves` (new parameter on `LeaveReader.search` plus a repository query).
   - Add `pendingLeaves` to `HrSummaryResponse` (additive field).
   - Add `leave_requests.decided_by bigint NULL`, filled from `X-User-Id` (audit).
4. **rh: salary advances.**
   - New table `salary_advances`:
     - `employee_id NOT NULL`, `month date NOT NULL` (first day of the month)
     - `amount numeric(14,2) NOT NULL > 0`, `reason NULL`
     - `status varchar NOT NULL` (DRAFT, VALIDATED or REJECTED)
     - `created_at`, `decided_at NULL`, `decided_by NULL`
   - Services and endpoints: `AdvanceReader`, `AdvanceCreator`, `AdvanceValidator`, `AdvanceRejecter`; `GET /advances?month=&employeeId=&status=`, `POST /advances`, `POST /advances/{id}/validate|reject`.
   - Rule: the month's DRAFT and VALIDATED advances plus the new amount must stay ≤ baseSalary × `rh.advance.cap-percent` (30), otherwise 422. Re-check the cap at validation.
   - A decision on a request that is no longer DRAFT returns 409 IMMUTABLE.
   - "Gérant" = ADMIN, checked in the service through `X-User-Role`, because the gateway cannot do it. This needs a new `ForbiddenException` mapped to 403 FORBIDDEN.
   - `EmployeeDeleterService` must also refuse deletion when the employee has advances.
5. **crm: contacts (no schema change).**
   - Endpoints: `GET /contacts?type=&search=&followUp=` and `GET /contacts/summary`.
   - New remote calls:
     - `CustomerClient.findAll()` → `GET /customers`; with no `size` it returns everything.
     - `SupplierClient.findAll()` → `GET /suppliers`.
     - Sales `?status=VALIDATED&from=&to=` for "clients actifs".
     - `/purchase-orders?status=` for "commandes en cours".
   - New records: `RemoteCustomer` and `RemoteSupplier`.
   - Interactions: one group-by query for the latest exchange per contact.
   - Follow-up rule: max(last interaction, createdAt) older than `crm.follow-up-days` (30).
   - New contact: `POST /contacts` forwarding to vente `POST /customers` or stock `POST /suppliers` (headers forwarded), or reserve it to ADMIN.
   - Tests:
     - A contact with no interaction.
     - The 29-day and 31-day boundaries.
     - Vente down → 503.
6. **stock (another team):** `suppliers.city varchar NULL`.

## 6. Risks
- Hibernate 6.6 (Boot 3.4.1) generates CHECK constraints for `@Enumerated(STRING)` columns. As far as I know, `ddl-auto: update` does not update them when a value is added to the enum; I could not confirm this on a populated database. **To check:** values already added (Role.CRM), and any future value of ContractType, LeaveStatus or InteractionType.
- Adding a NOT NULL column without a default to a populated table fails on PostgreSQL; keep new columns nullable.
- Readers load whole tables and filter in memory (`EmployeeReaderService.java:33`, `LeaveReaderService.java:31`), and there is no pagination in rh. This is acceptable at the size of a small business.
- No extension or custom-field mechanism exists in rh, crm or auth. The "after IA" columns must therefore be built in advance.