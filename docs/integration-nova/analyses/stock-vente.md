# stock-service and vente-service compared with the NOVA Figma screens (Stock, Ventes, Accueil, Relations, Livraisons)

S = `services/stock-service/src/main/java/com/hackaton/stock`, V = `services/vente-service/src/main/java/com/hackaton/vente`. I did not modify the repo (`git status` is unchanged). I ran the tests and the DDL checks on copies in `docs/integration-nova/analyses/`.

## 0. Test results (Docker, maven:3.9-eclipse-temurin-17, `mvn -B test`)
- **stock-service: 193 tests, 0 failures, 0 errors, BUILD SUCCESS** (46 classes, 9 min 40 s with a cold `.m2`). The one "Erreur inattendue" ERROR line in the log is expected: it comes from `GlobalExceptionHandlerFallbackTest`.
- **vente-service: 78 tests, 0 failures, BUILD SUCCESS** (18 classes, 3 min 28 s).
- I copied each service to the scratchpad before running, so `target/` was not created in the repo. Logs: `docs/integration-nova/analyses/stock-test.log` and `docs/integration-nova/analyses/vente-test.log`.
- I also generated the PostgreSQL DDL from the entities (a throwaway `@DataJpaTest` with PostgreSQLDialect, scripts.action=create). Output: `docs/integration-nova/analyses/ddl-stock/target/ddl-pg.sql` and `docs/integration-nova/analyses/ddl-vente/target/ddl-pg.sql`.

## 1. What exists today (EXISTS)

**Stock (schema `stock`, port 8083, ddl-auto=update, no Flyway or Liquibase)**
- `products`
  - id IDENTITY; name NOT NULL; sku NOT NULL UNIQUE; unit NOT NULL; quantity int NOT NULL; min_threshold int NOT NULL; unit_price numeric(14,2) NOT NULL; created_at NOT NULL.
  - Source: `S/product/entity/Product.java:23-46`.
  - There is **no category, no purchase price, no lot and no expiry**.
- `stock_movements`
  - product_id bigint NOT NULL (no DB foreign key); type IN|OUT (CHECK); quantity; reason (nullable); stock_after; created_at.
  - Source: `S/movement/entity/StockMovement.java`.
- `suppliers`
  - name UNIQUE NOT NULL; phone, email, address, payment_terms, usual_delivery_days, bank_account all nullable.
  - The response exposes only `bankAccountLast4`.
  - **No city, no NIF/STAT.**
- `purchase_orders` and `purchase_order_lines`
  - Order status DRAFT → VALIDATED → RECEIVED. Lines store productName and unitPrice as snapshots.
  - Receiving an order creates IN movements: `S/purchaseorder/service/receiver/PurchaseOrderReceiverService.java:34-38`.
- Supplier invoices (Invoice Shield) with an append-only event log. Not relevant to these screens.

**Stock endpoints** (all are full lists: no pagination, no sort parameter)
- `GET /products?search=&lowStock=`
  - The search matches name or sku in JPQL: `S/product/repository/ProductRepository.java:14-17`.
  - `lowStock` is filtered in memory: `ProductReaderService.java:28-33`.
- `GET /products/low-stock`, `GET/POST/PUT/DELETE /products/{id}`.
  - `PUT` cannot change quantity or sku: `ProductUpdateRequest.java:9-13`.
- `GET/POST /products/{id}/movements` with body `{type, quantity, reason}`. This is the existing **"Entrée de stock"** action. It refuses an OUT larger than the stock with a 422.
- `GET /products/{id}/autonomy`
  - 7-day window. CRITICAL below 3 days, WARNING below 7 days: `S/product/service/reader/AutonomyCalculator.java:13-35`.
- `GET /status?risk=` returns StockStatusItem: productId, productName, sku, unit, quantity, minThreshold, lowStock, averageDailyOut, daysRemaining, risk.
  - Quantity 0 ⇒ CRITICAL. Below the threshold with OK autonomy ⇒ WARNING.
  - Sorted by risk, then days remaining, then name: `S/status/service/StockStatusReaderService.java:28-72`.
- `GET /summary` returns `{totalProducts, lowStockProducts, totalStockValue}`, where totalStockValue = Σ quantity × unit_price (`ProductRepository.java:25`).
- `GET /replenishment?onlyNeeded=true` and `GET /products/{id}/replenishment`.
  - Returns currentStock, averageDailyOut over 28 days, safetyStock, reorderPoint, recommendedQuantity, coverageDays, needsReorder, suggestedSupplierId, assumptions.
  - Source: `S/replenishment/service/ReplenishmentCalculator.java:25-56`.
- Suppliers: `GET /suppliers?search=` (in memory), CRUD, `GET /suppliers/{id}/profile`.
- Purchase orders: `GET /purchase-orders?supplierId=&status=`, `POST`, `POST /{id}/validate`, `POST /{id}/receive`.

**Vente (schema `vente`, port 8082)**
- `sales`
  - customer_id nullable (no DB foreign key); status DRAFT|VALIDATED (CHECK); total_amount numeric(14,2); created_at; validated_at (nullable).
  - Source: `V/sale/entity/Sale.java:32-52`.
- `sale_lines`
  - product_id, product_name and unit_price are snapshots copied from stock at line build time: `V/sale/service/SaleLineFactory.java:26-33`.
- `customers`
  - name NOT NULL; phone, email, address, city, notes(2000) nullable; type PARTICULIER|ENTREPRISE (nullable column).
  - The mapper defaults a null type to PARTICULIER: `V/customer/mapper/CustomerMapper.java:24,36`.
- Sale endpoints
  - `GET /sales?customerId=&status=&from=&to=`: in-memory filtering over `findAllByOrderByCreatedAtDesc` (`V/sale/service/reader/SaleReaderService.java:33-41`). No search, no paging.
  - `POST /sales`, `PUT/DELETE` only while DRAFT (409 IMMUTABLE otherwise).
  - `POST /sales/{id}/validate`: one OUT movement per line through `StockClient`, with compensation on failure (`SaleValidatorService.java:30-58`).
- Reports
  - `GET /sales/summary?from=&to=` returns `{salesCount, totalRevenue, averageBasket, daily[]}` over VALIDATED sales.
  - `GET /sales/top-products?limit=`.
- Customers
  - `GET /customers?search=&page=&size=` with an `X-Total-Count` header (`V/customer/controller/CustomerController.java:38-47`). Search covers name, phone, email and city; paging is in memory (`V/common/page/PageResult.java`, max 100).
  - CRUD (delete refused if the customer has sales).
  - `GET /customers/{id}/profile` returns salesCount, totalPurchased, averageBasket, lastPurchaseAt, topProducts, recentSales.

**Conventions**
- Each service has an interface plus a `*Service` for Reader, Creator, Updater and Deleter, plus Validator, Receiver or Decider where needed.
- Records as DTOs, `@Component` mappers, `@Transactional(readOnly=true)` on readers.
- Errors use `ApiError{status,type,message,timestamp}`:
  - stock `S/common/exception/GlobalExceptionHandler.java:21-92`;
  - vente `V/common/exception/GlobalExceptionHandler.java:21-82`;
  - types NOT_FOUND, VALIDATION, IMMUTABLE, BUSINESS, SERVER (vente adds 503), UNAUTHORIZED from `GatewayIdentityFilter` when X-User-Id/Role is missing.
- Tests
  - Controller tests use `@WebMvcTest(XController.class)` + `@MockitoBean` on every service interface, with identity headers from `AuthHeaders.admin()` (stock) or `UserHeaders` (vente).
  - Service tests use `@ExtendWith(MockitoExtension)` + fixtures. Method names follow `shouldX_whenY`.
  - **There is no `@DataJpaTest` or `@SpringBootTest`.** The JPQL queries are never run against a database, and `application-h2.yml` is not used by any test.
  - 7 test files exceed 100 lines (for example `PurchaseOrderServicesTest` at 178). All main files are ≤ 100 lines.
- Seeding: there is none inside either service. `services/ai-service/scripts/seed_demo.py` seeds through the gateway: 5 products (units sac, bidon, paquet, pièce), 1 supplier, 2 customers, 6 validated sales. The Figma volumes (64 products, 412 sales) do not exist in any data set.

## 2. Stock screen (generic + after IA)

| Figma | Status | How / proposal |
|---|---|---|
| Product name, stock qty + unit ("40 sacs", "4 u."), threshold | EXISTS | `name`, `quantity`, `unit`, `minThreshold`. Plural or abbreviation is a front-end formatter. |
| Category | MISSING | `products.category varchar(80) NULL`. Optional `@Size(max=80)` in Create/Update requests; add to ProductResponse and StockStatusItem; optional `?category=`. Existing rows stay null and display "—". |
| Status pill | DERIVABLE from `/status` | CRITICAL ⇒ "Risque de rupture"; else `lowStock` ⇒ "Sous le seuil"; else "En stock". **Open product question:** WARNING without lowStock (autonomy under 7 days but above the threshold) has no Figma state. |
| KPI Disponibilité 92,2 % / 59 above the threshold | DERIVABLE | `/summary`: (total − lowStock) / total. `lowStock` uses `<=`, so "au-dessus" is consistent. |
| KPI En alerte 5, dont N en risque de rupture | DERIVABLE | 5 = `lowStockProducts`; N = count over `/status` of lowStock && CRITICAL. CRITICAL alone can include products above the threshold (autonomy under 3 days), so it is not a strict subset. |
| KPI Valeur du stock **au prix d'achat** | MISSING (semantics) | `unit_price` is the **selling** price: it is copied into sale lines at `V/sale/service/SaleLineFactory.java:31`. `totalStockValue` is therefore valued at sale price. Proposal: `products.purchase_price numeric(14,2) NULL`; summary = Σ qty × coalesce(purchase_price, unit_price) plus a count of products without a purchase price. Optional one-off backfill from the last `purchase_order_lines.unit_price` per product. |
| Filters Tous / En alerte / Rupture with counts | DERIVABLE client-side | `/status` returns every product (64 rows is fine). Server side only `?risk=` exists; no counts endpoint. |
| Search | EXISTS on `/products?search=`; MISSING on `/status` | Filter on the client, or add `search` to `/status` (same in-memory style). |
| Sort "par niveau de stock" | EXISTS | Server-side urgency order. |
| Primary action "Entrée de stock" | EXISTS | `POST /products/{id}/movements {type:IN,…}`. |
| After IA: N° de lot, Péremption, "Expire dans 23 j", KPI and filter "Expire bientôt" (J-30) | MISSING | See the proposal below. |

**Lot and expiry proposal.** It respects immutability by writing on the movement:
- Columns: `stock_movements.lot_number varchar(64) NULL`, `stock_movements.expiry_date date NULL`, accepted only on IN movements.
- `MovementRequest` gains the two optional fields.
- `StockStatusItem` gains lotNumber, expiryDate and daysToExpiry, taken from the most recent IN movement that has an expiry.
- `/status?expiringWithinDays=30`, `StockSummaryResponse.expiringSoon`, and a config key `stock.expiry-alert-days: 30`.
- Limitation: remaining quantity per lot (FIFO) is not tracked; a later `stock_lots` table would cover it.
- Old rows stay null and display "—", as Bougies does in the Figma.

**Generic gap.** "Appliquer pour l'équipe" implies fields added at runtime. Neither service has a custom-field or module mechanism. The after-IA screens can only be served by pre-built nullable columns plus a feature toggle, and where that toggle is stored is undecided.

## 3. Ventes screen (generic + after IA)

| Figma | Status | How / proposal |
|---|---|---|
| N° "V-0412" | DERIVABLE | `V-` + id padded to 4 digits. Gaps appear when DRAFT sales are deleted. A persisted number is MISSING only if legal numbering is required. |
| Client name, "Client comptoir" | DERIVABLE / proposed | `SaleResponse` only has `customerId` (`V/sale/dto/SaleResponse.java:8-16`). The front can join with `/customers`. Better, with no schema change: add `customerName` to the DTO, filled by a batch `customerRepository.findAllById` in the reader. Null customer ⇒ "Client comptoir". |
| Date and time, Montant | EXISTS | `createdAt`/`validatedAt`, `totalAmount`. Format in Indian/Antananarivo. |
| Statut Payée / À encaisser / En retard / Annulée | MISSING | Only DRAFT/VALIDATED exist (`V/common/status/DocumentStatus.java`). DRAFT has no Figma state. Proposal below. |
| Filters with counts (Toutes 412 = 9 + 398 + 5) | MISSING server side | Proposal: `GET /sales?search=&paymentStatus=&page=&size=` with `X-Total-Count` (reuse `PageResult`, as in `CustomerController`) and `GET /sales/counts?from=&to=`. |
| Search "vente, client" | MISSING | `search` parameter on number and customer name. |
| CA du mois + delta vs October; panier moyen + delta | DERIVABLE | 2 calls to `/sales/summary` (month to date vs previous month). Product decision: compare same period or full month. |
| Ventes du jour 23 | DERIVABLE | `/sales/summary?from=today&to=today` → `salesCount`. |
| "1,24 M Ar encaissés" | MISSING | Needs payments. |
| After IA: Paiement column (MVola, Orange Money, Espèces, Crédit · N jours), filter À crédit, KPI Crédit en cours (amount, clients, en retard) | MISSING | Proposal below, plus `GET /sales/credit-summary` → `{outstanding, customers, overdue}`. |

**Payment and cancellation proposal:**
- New columns on `sales`:
  - `payment_method varchar(20) NULL`, with enum CASH, MVOLA, ORANGE_MONEY, AIRTEL_MONEY, CREDIT, set while DRAFT.
  - `credit_days int NULL`; due date = validatedAt + creditDays.
- New write-once table `sale_payments`:
  - id, `sale_id` bigint NOT NULL with a foreign key to sales, `amount` numeric(14,2) NOT NULL CHECK > 0, `method` NOT NULL, `paid_at` timestamptz NOT NULL, `reference` varchar(64) NULL, `recorded_by` bigint NULL.
  - Endpoints: `POST /sales/{id}/payments` (VALIDATED only, amount ≤ remaining) and `GET /sales/{id}/payments`.
- Cancellation as a new write:
  - New table `sale_cancellations`: `sale_id` UNIQUE with a foreign key, `reason` NOT NULL, `cancelled_by`, `created_at`.
  - `POST /sales/{id}/cancel` re-enters stock with IN movements, reusing the compensation logic.
- Derived in `SaleResponse`: paymentStatus (CANCELLED / PAID / OVERDUE / TO_COLLECT), paidAmount, dueDate, paymentMethod, creditDays.
- Existing validated sales: a mapper rule "payment_method null ⇒ legacy, considered paid", following the pattern at `CustomerMapper.java:24`. The alternative is a one-off SQL insert of payment rows.

## 4. Accueil
- **Ventes du jour 1,24 M Ar, +8 % vs mardi dernier:** DERIVABLE. Call `/sales/summary?from=today-7&to=today` and read `daily[]` for today and today − 7.
- **Stock disponible 92 %, 5 produits en alerte:** DERIVABLE from `/summary`.
- **"Huile 5 L en rupture dans 4 jours · commande proposée 120 unités":** EXISTS through `/replenishment` (coverageDays, recommendedQuantity, suggestedSupplierId). Caveats:
  - `recommendedQuantity` is null when there are under 14 days of history (`ReplenishmentInputLoader.java:20,30-32`).
  - `coverageDays` (28-day window) differs from `/status` `daysRemaining` (7-day window). Pick one source for the screen.
  - `findAll` runs about 4 queries per product.
  - "Commander" goes through `POST /purchase-orders`, which needs a supplierId (may be null) and a purchase unitPrice per line (not stored on the product).
- **Roles:** the gateway gives `/api/stock` to STOCK and ADMIN only (`gateway/.../RoleAccessPolicy.java:17-24`). A VENTE user gets a 403 on the stock KPIs, so the front must hide them for that role.
- **Livraisons 7/9:** MISSING (see §6).

## 5. Relations (fields owned by vente and stock)
- **Customers:** city, phone, type, name and email EXIST; search and paging EXIST.
  - Filter "Professionnels" (type ENTREPRISE): MISSING `type` parameter; client-side derivable.
- **Suppliers:** phone EXISTS. City is MISSING (only a free-text `address`). There is no paging.
- **NIF/STAT:** MISSING on both customers and suppliers. Proposal: `nif varchar(20) NULL`, `stat varchar(30) NULL`.
- **Credit limit:** MISSING. Proposal: `customers.credit_limit numeric(14,2) NULL`, where null means no credit.
- **Encours crédit / "dépassé":** derived per customer as Σ(total − paid) over non-cancelled credit sales; depends on §3. Expose as `outstanding` and `overLimit` on CustomerResponse or through `GET /customers/credit`.
- **Clients actifs sur trimestre:** DERIVABLE from VALIDATED sales since the start of the quarter (distinct customerId). Heavy; a summary field would be better.
- **"3 commandes en cours":** `/purchase-orders?status=VALIDATED`.
- **Dernier échange / À relancer:** belong to crm-service interactions, outside this scope.

## 6. Livraisons: what can be reused
- **Reusable:**
  - client: `/api/vente/customers/{id}` (name, phone, address, city);
  - vente d'origine: `/api/vente/sales/{id}` (customerId, totalAmount, lines);
  - stock: already deducted when the sale is validated, so a delivery needs no stock call.
- **No delivery entity exists anywhere.** Quartier, livreur (rh-service), heure prévue, statut and montant à encaisser (needs payments) are all MISSING.
- **Minimal proposal:** a `delivery/` package in vente-service, so it shares the schema and can have a foreign key to sales.
  - Table `deliveries`:
    - id; `sale_id` bigint NOT NULL UNIQUE with a foreign key to sales; `customer_id` NULL;
    - `district` varchar(120) NOT NULL; `courier_id` bigint NULL (no cross-schema foreign key); `courier_name` varchar(120) snapshot;
    - `scheduled_at` timestamptz NOT NULL; `status` (TO_PREPARE, IN_TRANSIT, DELIVERED) NOT NULL;
    - `amount_to_collect` numeric(14,2) NOT NULL DEFAULT 0; `created_at`; `delivered_at` NULL.
  - An append-only `delivery_events` table records transitions.
  - Endpoints: `GET /deliveries?date=&status=&search=` (+ counts), `POST /deliveries`, `POST /{id}/start`, `POST /{id}/deliver`. Transitions only move forward and DELIVERED is immutable.
  - Payment at the door goes through `POST /sales/{id}/payments`.
- **Access conflict:** the Figma says "Accès : toute l'équipe", but `/api/vente` is VENTE-only, so the gateway rule would have to change.

## 7. Safety of `ddl-auto=update` on populated databases
- **New tables and nullable columns are safe.** That covers every proposal above.
- **NOT NULL columns without a default fail on a non-empty table** in PostgreSQL. To my understanding Hibernate only logs the error (halt_on_error defaults to false), so it would show up later as runtime SQL errors. New fields must stay nullable in the entity or carry a `columnDefinition` default.
- **New enum values are not safe.** I confirmed from the generated DDL that Hibernate creates CHECK constraints for enum columns:
  - `sales.status in ('DRAFT','VALIDATED')`
  - `stock_movements.type in ('IN','OUT')`
  - `customers.type`
  - `purchase_orders.status`

  From what I know of Hibernate (not tested here), `update` never changes existing constraints. Adding an enum value such as CANCELLED would therefore break inserts unless someone runs a manual `ALTER TABLE … DROP CONSTRAINT`. That is why the proposals use separate tables or new columns instead.
- **Few database foreign keys:** sales→customers, movements→products and purchase_orders→suppliers have none. Integrity is enforced only in the deleters.

## 8. Other issues
- **Days are cut in UTC:** `SaleReaderService.java:44` (uses createdAt), `SalesSummaryReaderService.java:53` (uses validatedAt) and `DailyOutSeries.java:20`. Madagascar is UTC+3, so sales made between 00:00 and 02:59 local time land on the previous day. The list and the summary also use different timestamps. Proposal: a single `app.zone=Indian/Antananarivo`.
- **Pagination is rare:** only `/customers` has it, and it pages in memory. Every other endpoint returns the full list, filters in memory and has no sort parameter.
- **What the front calls today:** `novaui/src/services/api` uses only `/api/stock/summary`, `/api/stock/status` and `/api/vente/sales/summary`.
