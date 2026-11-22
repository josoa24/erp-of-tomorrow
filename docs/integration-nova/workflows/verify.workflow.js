export const meta = {
  name: 'nova-verify',
  description: 'Global end-to-end verification of the merged NOVA integrations on the test branch (real stack, seed, journeys, widths)',
  phases: [
    { title: 'Stack', detail: 'build + run full stack, migrations, seed' },
    { title: 'Journeys', detail: 'parallel end-to-end journeys by area' },
  ],
}

const B = '<SCRATCH>'
const WT = args.worktree
const ENV = `${B}/work/verify/stack.env`

const STACK = {
  type: 'object',
  properties: {
    up: { type: 'boolean' },
    gateway: { type: 'string' },
    web: { type: 'string' },
    composeProject: { type: 'string' },
    commands: { type: 'array', items: { type: 'string' } },
    accounts: { type: 'array', items: { type: 'object', properties: { email: { type: 'string' }, password: { type: 'string' }, role: { type: 'string' } }, required: ['email', 'role'] } },
    problems: { type: 'array', items: { type: 'string' } },
  },
  required: ['up', 'commands', 'problems'],
}

const JOURNEY = {
  type: 'object',
  properties: {
    area: { type: 'string' },
    results: { type: 'array', items: { type: 'object', properties: { journey: { type: 'string' }, widths: { type: 'string' }, result: { type: 'string', enum: ['pass', 'fail', 'blocked'] }, evidence: { type: 'string' }, ownerBranch: { type: 'string' } }, required: ['journey', 'result', 'evidence'] } },
    defects: { type: 'array', items: { type: 'object', properties: { severity: { type: 'string', enum: ['blocker', 'major', 'minor'] }, ownerBranch: { type: 'string' }, problem: { type: 'string' }, evidence: { type: 'string' }, suggestedFix: { type: 'string' } }, required: ['severity', 'ownerBranch', 'problem', 'evidence'] } },
  },
  required: ['area', 'results', 'defects'],
}

phase('Stack')
const stack = await agent(`You are the VERIFICATION STACK operator for the NOVA integration. Read ${B}/briefs/common-rules.md first.
Worktree with the merged test branch: ${WT} (branch ${args.branch}). Do NOT commit anything there.
1. Build and start the full stack with a dedicated compose project \`-p nova-e2e\` from this worktree: erp-db, auth, stock, vente, rh, crm, module-service, gateway, ai-service (profile ai, without GEMINI_API_KEY unless one is present in the environment), and the web app (either the compose novaui service or a Vite dev server from ${WT}/novaui on a free port with API_PROXY_TARGET to the gateway). Use free host ports (check ss -ltn; do not collide with other projects; never stop containers of other projects). Start services gradually if memory is tight (free -m), wait for health.
2. Apply migrations with the repo's documented script (docker/postgres/apply-migrations.sh or equivalent) twice (idempotence) and report output.
3. Seed demo data with the repo's documented seed script if present (scripts/demo/seed.py), otherwise with ${B}/briefs/R11-demo-data.md as guidance write a throwaway seed under ${B}/work/verify/ (NOT in the repo) that creates realistic data through the gateway.
4. Write the connection info (gateway URL, web URL, accounts with roles and passwords) to ${ENV} and return it. Leave the stack RUNNING for the journey agents.`, { label: 'verify:stack', phase: 'Stack', schema: STACK })

if (!stack || !stack.up) return { stack, journeys: [] }

const AREAS = [
  { key: 'accueil-nav', focus: 'Connexion (00, 00a disabled, 00b sending, 00c refused -> error page -> back with email kept, 00d mobile 390), avatar menu (00e), Mon profil admin (00f) and VENTE user (00g: nav shows only Accueil, Ventes, Mes modules), logout, forbidden route redirect, Accueil (01) numbers vs API, À décider actions (Commander creates a draft PO; Valider -> Équipe demandes; Voir -> module preview).' },
  { key: 'eclipse-modules', focus: 'Eclipse flows (04, 04a, 04b, 04c, 04d): + opens create dialog, examples chips, Préparer avec Eclipse, uncheck a column, Créer le module (ADMIN) -> nav Mes modules shows it after reload, toast Annuler deletes it, recreate, Reprendre les N ventes import, Nouvelle livraison form with references, advance statuses, indicators 7/9 style; non-ADMIN cannot create (no button + 403 by API); out-of-catalog request gets an honest answer; question goes to chat or shows unavailable state without Gemini key.' },
  { key: 'stock-ventes', focus: 'Stock (05/05b) and Ventes (02/02b): lists, search, filters with counts consistent with API, KPIs, Entrée de stock with lot -> reload persists, FEFO after a sale validation, Eclipse sentence « Ajoute le n° de lot et la date de péremption, avec une alerte 30 jours avant » -> preview highlighted -> Annuler -> again -> Appliquer (ADMIN) -> reload keeps columns; Nouvelle vente -> valider -> encaisser -> Payée after reload; annulation re-enters stock; Mobile Money sentence -> preview/apply; VENTE role cannot apply.' },
  { key: 'relations-equipe', focus: 'Relations (05/05b) and Équipe (04/04b): contacts list merged customers+suppliers, filters, À relancer, Nouveau contact persists, Enregistrer un échange updates Dernier échange; NIF/STAT sentence -> preview/apply; plafond dépassé computed from real unpaid credit sales; Équipe list, Présent/En congé, Demandes dialog validate/refuse, Ajouter un employé persists, avances sentence -> preview/apply, advance over 30 % refused (error page with API message), ADMIN validates advance, RH user cannot validate.' },
]

phase('Journeys')
const journeys = await parallel(AREAS.map(a => () => agent(`You are an END-TO-END VERIFIER for area "${a.key}" of the NOVA integration. Read ${B}/briefs/common-rules.md and the connection info in ${ENV}. The stack is running; do not restart or stop it, do not modify the repo (${WT}). Use Playwright (Chromium) scripts under ${B}/work/verify/${a.key}/ against the real web app + gateway (no mocks). For every journey: test at 1440, 768 and 375 px (and the nav/column breakpoint transitions), keyboard path, visible focus, labels, touch targets on mobile, long labels, no page-level horizontal scroll; for writes reload and verify persistence (also via the API); for reads compare displayed numbers/counts/totals with the API. Compare with the Figma captures in ${B}/figma/. Journeys: ${a.focus}
Return results per journey with evidence (screenshot paths, API outputs) and defects with the owning branch (web/feat-nova-socle, modules/feat-module-engine, eclipse/feat-eclipse-flows, stock/feat-nova-stock, vente/feat-nova-ventes, crm/feat-nova-relations, rh/feat-nova-equipe, accueil/feat-nova-accueil-navigation).`, { label: `verify:${a.key}`, phase: 'Journeys', schema: JOURNEY })))

return { stack, journeys: journeys.filter(Boolean) }
