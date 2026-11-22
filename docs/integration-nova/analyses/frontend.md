# novaui/ audit for integrating the NOVA Figma screens (read-only)

## 1. Toolchain results
I ran these on a copy at `docs/integration-nova/analyses/novaui`, with Node 24.20 and npm 11.19. CI uses Node 22.
- **`npm ci`**: OK, 567 packages. npm warned that esbuild's postinstall script is not in allowScripts. The build was not affected.
- **`npm run lint`**: exit 0, no warnings.
- **`npx tsc -b`**: exit 0.
- **`npm test`**: 35 files and 110 tests, all passing, in 18 s. Stderr shows 6 times "Not implemented: window.scrollTo". This comes from TanStack scroll restoration in `ErrorScreen.test.tsx` and does not fail anything.
- **`npm run build`**: OK. JS is 402.8 kB (128.6 kB gzip) in a single chunk. CSS is 57.2 kB. The PWA service worker is generated and precaches 6 entries.
- **CI** (`.github/workflows/ci.yml:68-86`) runs `npm ci`, test and build, but **not lint**.
- **Files over 100 lines**: only `src/styles.css` (177) and `src/eclipse.css` (116). The largest TS file is `components/ui/tabs.tsx` (91).
- **Pitfall**: `novaui/.vite/deps/*` is tracked by git, and `.gitignore` does not list `.vite`.

## 2. Routing and session
**EXISTS**
- `navigation/appRouter.tsx` is code-based TanStack Router.
  - Root: `notFoundComponent=ErrorScreen`.
  - `/connexion`: `beforeLoad` redirects to `/` when a session exists.
  - `/erreur`: public.
  - A layout route `id:'protected'` whose `beforeLoad` redirects to `/connexion` when there is no session (l.29-36). Its component is a bare `Outlet`, which is the obvious place to mount an app shell.
  - `/` renders `DashboardScreen`.
  - `Register` is declared (l.50-54) and `createAppRouter(history)` is testable.
- `routes.ts` holds 3 constants. There is no lazy loading, no `validateSearch` (zod is installed but unused) and no per-route role guard.
- `sessionStore.ts` stores `{token,email,role}` under `eclipse.session`, in localStorage when "remember me" is checked, otherwise sessionStorage. `readSession` checks `exp` synchronously. There is no React context and no listener for changes in another tab.
- `utils/jwt.ts` decodes `sub/role/exp`. The token also carries `userId` (`JwtTokenIssuer.java:28-30`), which `jwt.ts` does not decode.
- `utils/roleAccess.ts` defines `ErpModule = stock|vente|rh|crm` and `canAccessModule`, where ADMIN gets everything. This duplicates the gateway's `RoleAccessPolicy`.
- `useLogout` clears the session and navigates to `/connexion`.

**DERIVABLE**
- The modules listed under "Mes accès" and the nav filtered by role can be computed with `canAccessModule`.
- Role labels ("Administrateur") need a new constant.

**MISSING**
- **No display name anywhere.** `UserAccount` has only email/role/enabled/createdAt, and `LoginResponse(token,email,role)` has no name either. "Bonjour, Hery." and "Hery Rabe" therefore cannot come from the API.
  - Minimal front-end fix: a tested util that derives a name from the email's local part (`hery@…` gives "Hery").
  - Full fix: add a `displayName` field in auth-service.
- When React Query starts being used, logout must call `queryClient.clear()`.
- Livraisons, "Mes modules" and the "+" button have no backend at all.

## 3. HTTP and error flow
**EXISTS**
- `services/api/httpClient.ts`: `request/httpGet/httpPost`, which prefixes `VITE_API_URL`, adds the Bearer token and throws `ApiError(type,status,message)`. A network failure gives type `network`.
- `utils/errorType.ts` maps the backend `type` (including IMMUTABLE → business), then falls back to the HTTP status.
- `toApiError` wraps unknown errors as `server`.
- `useApiError` clears the session on `unauthorized`, then `navigate('/erreur', state:{error:{type,message,from}})`. That state is typed in `types/errorRoute.ts` through a HistoryState augmentation.
- `ErrorScreen` reads that state and shows `ERROR_CONTENT[type]` with "Réessayer" (`history.push(from)`) and "Retour".

**MISSING** (minimal changes)
- No `httpPut/httpDelete/httpPatch`, no query-string builder, no reading of `X-Total-Count`.
- No timeout (`AbortSignal.timeout`).
- No handling of 204 or non-JSON bodies (l.36).
- 502/503/504 are classified as `server`; they should be `network`.
- No SSE client for `/api/ai/agent/chat/stream`.
- Figma 00c shows "UNAUTHORIZED · 401" and a per-type call to action. `ErrorRouteState` should gain `status` and the raw backend `code`; `ApiError.status` already exists but is dropped by `useApiError`.
- There is no "Session expirée" or "Service indisponible" content.
- The "API" files currently present are auth, stock (summary, status), vente (sales summary) and rh (summary). Nothing is there for lists or CRM.

## 4. Design tokens compared with NOVA
- **`styles.css`** carries a *different* design system ("02 — Design Foundations DS v0.2"):
  - Light neutrals, primary `#101828`, orange accent `#ee7d01`, orange focus `#c96800`, `bg-primary-subtle #fff6ec`.
  - A `.dark` block, a type scale in Geist, Tailwind default radii (md = 6 px).
- **`eclipse.css`** is the dark "space" theme:
  - Body `#080a2b` with `color-scheme: dark` (l.91-102).
  - `nova-*` colours (violet `#7b5cff`), `--radius-card: 18px`, glass and orb shadows.
- **Font, verified in the built CSS**: `--font-sans` resolves to **Geist**. `eclipse.css` sets Figtree, but it is imported at `styles.css:2` and the later `@theme` (l.12) overrides it. `main.tsx:5` bundles Figtree but it is unused, while `index.html:11-14` loads Geist from Google Fonts, which the service worker does not precache.
- **Result**: none of this matches NOVA. NOVA uses Figtree, a lavender canvas `#f3f0fb`, night `#17123a`, violet focus `#5b3fe0`, white or translucent panels, and pill buttons.
- The target values are in `FLUID-FOUNDATIONS-HANDOFF.md` §3.1:
  - Colours: ink `#17123a/#4a4668/#6b6789`, lines `#e6e1f5/#cfc7ec`, status success `#ddf9e8/#0e6b43`, warning `#fff3b8/#6b5300`, critical `#ffe1db/#a3271a`, info `#ece6ff/#3b2a9e`.
  - Radii 8/12/16/24/32/999 and spacing on a base of 4.
  - Typography: Display Light 88/64/44, Number M Light 32, Title 28, Heading SemiBold 20, Body 16, Body S 14, Label Medium 14, Caption Medium 12 in capitals +6 %.
- **PWA and branding**:
  - `vite.config.ts:16-24` manifest is "ERP of Tomorrow", theme `#0f172a`, with a generic "N" in `public/icon.svg`.
  - `index.html` title is "Eclipse".
  - `constants/appInfo.ts` sets `APP_NAME='Eclipse'` and `AI_NAME='Eclipse AI'`.
- **Vite proxy**: `/api` goes to `API_PROXY_TARGET` (default `:8080`) with `changeOrigin`. It is dev-only; there is no production reverse proxy.

## 5. Components and the Figma elements they can serve
None of the `components/ui/*` are used by any screen today; only their tests import them. All are token-based, so they will take the NOVA look once the tokens change.

| Component | What it renders / props | Figma element | Verdict |
|---|---|---|---|
| `ui/button` (+`button-variants.ts`) | Variants primary/secondary/ghost/danger, sizes m(40/44)/s(32), `leadingIcon/trailingIcon`, `loading` (spinner, aria-busy, click blocked) | Login button, "Entrée de stock", "Commander/Valider/Voir/Annuler" | DERIVABLE: add `rounded-full`, size l (48) and an `ai` gradient variant ("Appliquer pour l'équipe", "Reprendre les 9 ventes") |
| `ui/ai-button` | Secondary button with sparkle icon | AI actions | Restyle to the gradient |
| `ui/icon-button` | ghost/secondary, required `aria-label` | Row "…", nav "+" | EXISTS |
| `ui/chip` | `label`, `count`, `selected` (aria-pressed, check icon) | Filter chips "Tous 64 ×" | DERIVABLE: count shown as a pill, optional × clear, lilac selected state |
| `ui/badge` | Tones neutral/primary/success/warning/danger/ai, dot indicator, `rounded-sm` | Status pills, "Nouveau", role badge | DERIVABLE: `rounded-full`, icon slot, presets tone→icon (FW StatusBadge) |
| `ui/status` | 8 px dot + label | Dense status ("Présent/En congé") | EXISTS |
| `ui/avatar` | Initials from `name`, sizes 24/32/40 | Header avatar (56), menu (36), profile | Add size 56 and lilac colour; needs a name (§2) |
| `ui/search-field` | Search icon, `type=search`, optional global Ctrl/⌘K shortcut + `Kbd` | Page-header search | DERIVABLE: rounded-full; move the shortcut effect into a hook for the Eclipse bar |
| `ui/kbd` | `<kbd>` in mono font | "Ctrl K" | Add an inverse variant for the dark bar |
| `ui/tabs` (+`use-tabs-keyboard`) | role=tablist, underline/segmented, counts, arrow keys | Look of the nav pill only | Do **not** use for routing: semantics are tabs, not links |
| `ui/count` | neutral/attention(orange)/muted/plain | "À décider 3" | Add a night tone |
| `ui/input` | Visible label, helper, error, `hideLabel` | Login fields | Add a trailing slot for the password eye |
| `ui/checkbox`, `ui/tooltip` (CSS hover/focus, 300 ms), `ui/spinner`, `ui/alert` (tones incl. ai) | — | Tooltips on icon buttons, info banners | EXISTS. `Alert` must never carry API errors (rule 2.5) |
| `common/CenteredLayout` | SpaceBackground + EclipseLogo + centred main | Login/Error layout | Keep the structure, swap the background and logo |
| `common/EclipseLogo` | Crescent SVG + "ECLIPSE" | — | Replace with the NOVA logo (Figma vector 67:9596) |
| `GlassCard`, `MetricCard`, `PillButton`, `TextField`, `TrendBadge`, `SpaceBackground`, `Sparkline` | Dark glass styling, hard-coded rgba values | — | Obsolete for NOVA (`Sparkline` could be kept for later) |

**MISSING primitives**: Card/Panel (light), PageHeader, KpiStrip/Kpi, DataTable (no table exists), StatusPill presets, EmptyState, Skeleton, Toast (04c, success only), Dialog/Modal (04a/04b), Menu/Popover (avatar menu and row actions), NovaBackground (lavender gradient with lines), MainNav built on `Link`/`aria-current`, EclipseBar.

## 6. Screens compared with the Figma
**Login.** Today: dark background, glass card with a lock icon, placeholder-only pill fields (`TextField`), "Se souvenir de moi", white `PillButton` that reads "Connexion…" while sending.
- Figma: centred NOVA logo and tagline, white card with title and subtitle, labelled fields, password eye, night pill button, disabled when fields are empty (00a), "Exécution…" while sending (00b), footer text. There is no remember-me checkbox in the Figma; that needs a decision.
- **Reuse**: `useLogin` (submit, submitting, error page) and `LoginForm`'s state, including the trim.

**Dashboard (Accueil).** Today: `DashboardStage`, which is an orb and connectors around 4 `MetricCard`s; an icon-only `NavPill` with 4 of 5 entries disabled and `ACTIVE_ID` hard-coded; `PeriodSelect`; a bell that does nothing; an avatar whose click **logs out immediately**; "Bonjour." followed by a filler sentence. Data comes through `useDashboard` (useEffect, not React Query) and `dashboardLoader` (`Promise.all`, so one failing service fails the whole page, l.32-38).

Figma Accueil, field by field:
- **Ventes du jour 1,24 M Ar**: EXISTS (sales summary with `period='today'`; `formatCompactAmount` gives "1,24 M Ar").
- **"+8 % vs mardi dernier"**: DERIVABLE with a second call for today minus 7 days. The UTC/local day bug P1-5 still applies.
- **Stock disponible 92 % / 5 en alerte**: EXISTS (`availabilityRate`, `lowStockProducts`).
- **Livraisons 7/9**: MISSING.
- **"Huile 5 L en rupture dans 4 jours"**: DERIVABLE from `/api/stock/status` (`daysRemaining`, `risk`). The proposed quantity is unverified.
- **"3 demandes de congé"**: DERIVABLE from `/api/rh/leaves` (per the handoff); the API function is missing.
- **Eclipse suggestion**: MISSING.
- **Eclipse hero bar**: MISSING; nothing in the app calls `/api/ai`.

## 7. What is still relevant in the two root documents
**From `FRONTEND-AUDIT…md`.** Its stack description is stale: it describes CSS Modules and react-router, and the README also still says react-router. Still valid:
- P0-2 / W-1: `allSettled`, so one block can fail without killing the page.
- P1-2: timeout.
- P1-3: 502-504 should be `network`.
- P2-3: 204 handling.
- P1-5: UTC vs local day.
- P1-1: wrong password currently sends the user to the error page and loses the form. An exception to rule 2.5 is undecided.
- P1-7: the avatar needs a menu.
- P1-12: no production proxy and no CORS.
- P2-11: session is not reactive.
- P2-12: role policy is duplicated.
- P2-18: no "session expired" message.
- S-3: token in localStorage.
- S-6: personal data sent to Gemini; the user must be told.
- W-10: front types are subsets of the DTOs.

**From `FLUID-FOUNDATIONS-HANDOFF.md`.**
- Tokens and typography as listed in §4 above.
- Behaviours:
  - Decision is separate from execution.
  - Validated data is immutable: no Modifier/Supprimer.
  - A Dialog before any irreversible write; success shown only after the server answers.
  - No double submit.
  - Eclipse proposals go through `confirm`/`cancel`.
  - Filters, sort and page live in the URL.
  - Colour never carries meaning alone.
  - Unauthorised modules and actions are **hidden**, not disabled.
- Blockers:
  - D1, unresolved: rule 2.5 against field-level or block-level errors.
  - BE-12: VENTE cannot read `/api/stock/products`, and CRM cannot list customers or suppliers. This affects Relations and the product pickers.
  - Only customers are paginated by the server.
- Dark mode is not planned. Breakpoints: ≥1440 / 1280 / 768 / <768 (BottomBar on mobile).

## 8. Conclusion
**Reusable building blocks**
- Router skeleton: the protected layout route becomes the `AppShell` mount point.
- `useApiError` → `/erreur` flow, `ApiError`, `errorType`.
- `httpClient` (to be extended), `sessionStore`, `canAccessModule`.
- `utils/format` (fr-FR, Ar), `utils/period`, `utils/trend`, `dashboardIssues`.
- The `ui/*` kit: Button, IconButton, Chip, Badge, Status, Avatar, SearchField + Kbd shortcut, Input, Tooltip, Spinner, Alert, Count.
- `cn()`, which already extends tailwind-merge (`lib/utils.ts:7-29`).
- Test helpers: `test/routerMock.ts`, `test/makeToken.ts`.

**Proposed app shell**
- `AppShell` = NovaBackground + header (NovaLogo, MainNav from a nav config filtered by `canAccessModule`, a "Mes modules" group with the "+" button, UserMenu) + `Outlet` + EclipseBar.
- Routes: `/stock`, `/ventes`, `/equipe`, `/relations`, `/profil`, `/modules/$id`, each with a role guard in `beforeLoad`, all registered in `ROUTES`.

**Proposed generic module page**
- `ModulePage` config = PageHeader (title, subtitle, search, primary action) + KpiStrip + ListCard (chips with counts, sort hint, DataTable column config, StatusPill, row Menu) + EmptyState/Skeleton.
- Data through a per-feature React Query hook that routes errors to `useApiError`.
- Filters kept in the URL with zod `validateSearch`.

**Gaps to fill, in order**
1. Move tokens to NOVA (FW §3.1, Figtree; remove Geist and the dark body). Split the CSS files to stay at or under 100 lines.
2. Branding: logo, icon, manifest, page title, `APP_NAME`.
3. The missing primitives listed in §5.
4. HTTP verbs, timeout, 204 handling, SSE client, feature APIs.
5. `useSession` with display name and role label.
6. Restyle the error page and add status/code.
7. Rewrite Login and Accueil.
8. Delete the dark components and the orb utilities.
9. Add an ESLint `max-lines` rule at 100, run lint in CI, untrack `.vite`.

**Conventions an integrator must follow**
- **Layers**: `services/api` (pure HTTP, one file per resource) → `services` (pure logic) → `hooks` (state + `useApiError`) → components → screens. Module code goes in `src/features/<module>/…`, which currently holds only `.gitkeep`.
- **File size**: 100 lines maximum per file, including tests and CSS.
- **TypeScript**: `strict`; use `import type` (`verbatimModuleSyntax`); no enums or parameter properties (`erasableSyntaxOnly`); no `any`.
- **Variants**: put cva variants in a separate `*-variants.ts` file because of the react-refresh lint rule.
- **Styling**: token classes only, no hex values in components (the current dark components break this). Any new `text-*` or `shadow-*` token must be registered in the twMerge extension.
- **Errors**: never inline, popup or toast. Toasts are for success only.
- **Permissions and immutability**: hide what the role cannot use; give validated data no edit or delete controls.
- **Tests**: Vitest globals, files in `__tests__/`, names in `shouldX_whenY` form, `vi.mock` for API modules and the router, at least 80 % coverage on services and hooks.
- **Copy**: UI text in French, fr-FR `Intl` formatting, "Ar" as the currency.
- **Code style**: `ui/*` uses semicolons, `function` components and `@/` imports; common/screens use arrow components without semicolons. No Prettier config arbitrates, so pick one per file.
