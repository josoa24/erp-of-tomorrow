# NOVA Figma: design system and screen specs (file Zdv2NuVCiDtDrlAQA0vyh0, page 80:2)

Figma session: 664947fa-2d09-446f-9ac9-c29b95adf0bd. I read: variables on 41:37, 107:375, 42:2 and 64:3252; get_design_context on 107:375, 109:1995, 138:4108, 135:4481, 130:3574, 130:3702, 130:3669, 135:4377, 135:4400, 80:46, and on components 42:84, 42:216, 66:4043, 42:196, 42:366, 64:3924, 64:4046 and 45:485; and the metadata for the whole page. The page XML is extracted to `docs/integration-nova/analyses/page.xml`. Nothing in the repo was modified.

## 1. Screen node map (for later calls)
| Screen | Node | Notes |
|---|---|---|
| 00 Connexion / 00a / 00b / 00c / 00d mobile 390 | 130:3574 / 130:3607 / 130:3636 / 130:3669 / 130:3697 | |
| 00e avatar menu / 00f profile (admin) / 00g profile (Ventes) | 138:4106 (menu 138:4108) / 138:4137 / 138:4405 | |
| 01 Accueil | 80:46 | |
| 04 Départ / 04a / 04b / 04c / 04d | 135:3634 / 135:4406 / 135:4481 / 135:4084 / 135:3787 | |
| Ventes générique / après IA | 107:355 / 107:365 | result bar 108:907 |
| Stock générique / après IA | 107:375 / 107:385 | result bar 109:1197 |
| Équipe générique / après IA | 107:395 / 107:405 | result bar 109:1995 |
| Relations générique / après IA | 107:415 / 107:425 | result bar 110:1831 |
| Components | 42:2 (02.1 Base), 64:3252 (02.2 Applicatifs) | 79:4261 "02.3 Transversaux NEXORA" is labelled "à valider, non utilisés" |

## 2. Tokens (EXISTS in Figma; MISSING in novaui)
Figma calls this collection "Fluid Workspace / tokens" (41:39).

**Colours**
| Token | Value |
|---|---|
| bg/canvas | #f3f0fb |
| bg/lilac | #e7e0ff |
| bg/mint | #ddf3ec |
| bg/blush | #f7e4f1 |
| surface/panel | #ffffffc7 (white 78 %) |
| surface/solid | #ffffff |
| surface/sunken | #f6f3fd |
| surface/night | #17123a |
| ink/primary | #17123a |
| ink/secondary | #4a4668 |
| ink/tertiary | #6b6789 |
| ink/disabled | #9a96b5 |
| ink/inverse | #ffffff |
| line/subtle | #e6e1f5 |
| line/strong | #cfc7ec |
| brand/indigo | #2a1b6e |
| brand/violet | #5b3fe0 |
| brand/lilac | #b9a8ff |
| brand/pink | #e7a3e0 |
| brand/mint | #7fe3c4 |
| status/success bg / fg / vivid | #ddf9e8 / #0e6b43 / #9df5c0 |
| status/warning bg / fg / vivid | #fff3b8 / #6b5300 / #ffe873 |
| status/critical bg / fg / vivid | #ffe1db / #a3271a / #ffb3a6 |
| status/info bg / fg / vivid | #ece6ff / #3b2a9e / #d6c9ff |
| focus/ring | #5b3fe0 |
| state/hover | #efebfa |
| state/pressed | #e4ddf8 |
| state/selected | #ece6ff |
| action/primary-hover | #2a2357 |
| action/danger | #a3271a |
| text/link | #5b3fe0 |
| overlay/scrim | rgba(23,18,58,.32) |

- `overlay/scrim` appears only as a CSS-variable fallback in 135:4482.
- The Danger button's hover (#8a1f15) and active (#701810) colours are hardcoded, not tokens.

**Gradient "FW/Gradient nuit".** The style value comes back empty from the variables call. The real values come from the inline code and from Branding 123:1433–1445: `#1B1464 0%, #3B2A9E 55%, #7B4FD8 85%, #C98BE0 100%`. The angle changes per element: 104° on 44 px orbs, 128° on buttons, 165° on the Accueil bar.

**Canvas.** bg/canvas plus a radial gradient centred at the top-right corner (1440, 0), an 864×585 ellipse going from #DED4FF to transparent. The error page uses the canvas at 55 % opacity.

**Radii:** xs 8, sm 12, md 16, lg 24, xl 32, pill 999.

**Spacing:** 1=4, 2=8, 3=12, 4=16, 5=20, 6=24, 8=32, 10=40, 14=56, 18=72.

**Effects**
- Elevation 1: `0 1px 2px #17123A0F, 0 8px 24px #2A1B6E14`
- Elevation 2: `0 2px 6px #17123A0F, 0 24px 60px #2A1B6E29`
- Focus ring: `0 0 0 2px #fff, 0 0 0 4px #5B3FE0`

**Typography (Figtree).** Figma gives letter-spacing in %. The rendered value is in brackets.
| Style | Spec |
|---|---|
| Display XL | Light 88 / 1.0 / −2 % |
| Display L | Light 64 / 1.04 / −2 % (−1.28 px) |
| Display M | Light 44 / 1.1 / −1 % |
| Number M | Light 32 / 1.1 / −1 % |
| Title | Regular 28 / 1.2 / −0.5 % |
| Heading | SemiBold 20 / 1.3 |
| Body | Regular 16 / 1.5 |
| Body strong | SemiBold 16 / 1.5 |
| Body S | Regular 14 / 1.45 |
| Label | Medium 14 / 1.4 |
| Caption | Medium 12 / 1.4 / +6 % (0.72 px), UPPERCASE |
| Micro | Regular 12 / 1.4 |

**What novaui has today**
- **EXISTS:** Figtree is installed and imported (`novaui/src/main.tsx:5`, `@fontsource-variable/figtree`). lucide-react `^0.577.0` matches the Figma icon frame "lucide-react 0.577, 24 px, trait 1.75" (41:40). A 767 px media query exists (`novaui/src/styles.css` ~line 116).
- **CONFLICT:**
  - `novaui/src/styles.css:11-103` holds an older "DS v0.2" theme: Geist font, orange/grey colours, `--color-text-*`, `--color-bg-*`.
  - `novaui/src/eclipse.css:1-34` holds a dark "nova-*" glass theme.
  - Both define `--font-sans`. styles.css (Geist) is declared after the eclipse.css import, so it likely wins. Not verified at runtime.
- **MISSING (proposed minimal change):**
  - One `@theme` token file mapping the FW tokens to Tailwind 4 names, e.g. `--color-ink-primary`, `--color-surface-night`, `--radius-pill`, `--shadow-elevation-2`, `--shadow-focus-ring`.
  - Split it into colour and type files to stay under 100 lines.
  - Retire the conflicting `--color-*`/`--font-sans` definitions instead of keeping three palettes side by side.

## 3. Shared module-screen layout (1440×900)
**Page frame**
- Gutter 32; content width 1376.
- Logo at (32, 30), 88.8×23.
- Avatar L 56 at x 1352, y 23.
- Nav pill centred at y 17, size 751×68.
- Page header at y 112, height 72, inner left padding 8.
- KPI strip at y 204: height 132, or 150 when it holds an Eclipse-added KPI.
- List card at y 352 (generic) or 370 (after IA).

**Eclipse bar**
- 760×64, centred, 28 px from the bottom.
- Équipe générique 109:1570 is 782 wide (inconsistency).
- The "after IA" result bar is 1017×72, centred, 28 px from the bottom.
- The decor "lignes signature" sit at x 640, y 0, 1400×320.

**Accueil and profile.** Centred 960 column (Accueil x 240).

**Table column widths**
- Stock générique: 420 / 260 / 200 / 160 / 240 / 56.
- Stock après IA: 280 / 180 / 140 / 120 / 180 / 180 / 200 / 56.
- Header row: 48 high, sunken background, Caption in ink/tertiary.
- Body rows: 52 high, bottom border line/subtle, cell padding x 16.
  - First column: Label in ink/primary.
  - Other cells: Body S in ink/secondary.

## 4. Component specs
**Nav pill (109:475)**
- Container: surface/panel, padding 8, gap 4, radius pill.
- FW NavTab (42:216): height 44, padding-left 16, padding-right 18, gap 8, icon 20, Label.
  - Repos: no background, ink/secondary.
  - Actif: surface/night background, ink/inverse text.
  - Survol: state/hover.
  - Focus: FW/Focus ring. The generated code shows 0 spread, which looks like a codegen bug; the effect style is the real 2 px + 4 px.
  - Doc: `role=link`, `aria-current=page`, "rendu uniquement si canAccessModule(role, module)".
- "Mes modules" group: bg/lilac, padding 4, gap 4, pill.
- "+" button: 36 px, surface/solid, Plus icon 18.

**FW Button (42:84)**
- Pill. Size M: height 48, padding x 24, Body strong, icon 20. Size S: height 36, padding x 16, Label, icon 16.
- Variants:
  - Primaire: surface/night; hover action/primary-hover; active brand/indigo.
  - Secondaire: solid background, border line/strong, ink/primary; hover state/hover; active state/pressed.
  - Fantôme: transparent; hover state/hover; active state/pressed.
  - Eclipse: gradient nuit; hover shadow `0 6px 18px rgba(123,79,216,.4)`; active opacity .9.
  - Danger: #a3271a.
- States: Focus uses FW/Focus ring; Désactivé is opacity 40 % plus `aria-disabled`; Chargement shows LoaderCircle and "Exécution…".

**FW SearchField (66:3859)**
- 340–360 × 44, pill, border line/strong, padding-left 16, padding-right 12, gap 10.
- Search icon 18; placeholder Body in ink/tertiary.
- States: Vide / Focus / Rempli.
- Doc: server-side `search=` when the API accepts it, 250 ms debounce, "/" gives focus.

**FW Stat (70:3958)**
- Column, gap 6.
- Pastille 28 px in bg/lilac, bg/mint or bg/blush, with a 14 px icon.
- Caption label in ink/secondary.
- Value in Number M (32 Light).
- Sub-line in Micro, ink/tertiary.
- KPI card: surface/panel, padding 20 (24 on Accueil), radius xl, gap 16.

**FW FilterChip (66:4043)**
- Height 36, pill, padding x 14, gap 8, Label.
- Count badge: height 20, padding x 6, pill, Micro.
- Inactif: solid background, line/strong border, sunken count badge.
- Survol: state/hover.
- Actif: state/selected background, brand/lilac border, text status/info-fg, white count badge, X icon 14, padding-right 10.
- Focus: focus ring.
- Doc: `aria-pressed`; active filters are reflected in the URL.
- Toolbar: chip gap 8; sort hint in Micro, ink/tertiary, right-aligned.

**FW StatusPill (42:196)**
- Height 30, padding-left 10, padding-right 12, gap 6, icon 16, Label.
- Tones: Succès / Avertissement / Critique / Info / Neutre.
- Douce: x-bg background with x-fg text. Vive: x-vivid background with ink/primary text. Neutre: sunken (Douce) or line/subtle (Vive) background, ink/secondary text.
- Doc: always icon plus label, contrast ≥ 5.8:1.

**Row "…" menu (64:4046)**
- Container: white, radius md, padding 6, gap 2, Elevation 2.
- FW MenuItem: height 40, padding x 12, radius sm, icon 18, Body S.
- Hover: state/hover. Danger item: action/danger.
- Doc: actions not allowed by the record's status are absent.

**FW CommandBar / Eclipse bar (42:366)**
- Height 64, padding x 10, gap 12, pill.
- Contents: orb 44 with Sparkles 20; placeholder in Body; "Ctrl K" key chip (height 26, padding x 8, radius xs, Micro); send button 44 with ArrowUp.
- States:
  - Nuit · repos: gradient, used on Accueil 95:108, with an inner signature-lines asset. Nuit · saisie adds the focus ring.
  - Clair · repos: white, border line/strong, Elevation 2. This is the module-screen bar.
  - Clair · réponse en cours: the send button becomes a stop button (Square).
  - Indisponible: sunken background, send button at 40 %, text "clé Gemini absente".
- Doc: `POST /api/ai/agent/chat/stream`; Enter sends, Shift+Enter adds a line, Ctrl K focuses, Esc closes, AbortController stops; a 503 shows Indisponible.

**Result bar "Annuler / Appliquer" (109:1995)**
- surface/night, pill, padding-left 16, padding-right 12, padding y 12, gap 16, Elevation 2.
- White circle 40 holding Sparkles 20.
- Request text in Body strong, white; summary in Micro, brand/lilac.
- Buttons: Secondaire S "Annuler" and Eclipse M "Appliquer pour l'équipe".

**"Added by Eclipse" highlight**
- Added KPI or filter wrapper: status/success-bg background with a 1 px dashed status/success-fg border. KPI wrapper is radius lg with padding 12/8; filter wrapper is a pill with padding 3.
- Added table cells: status/success-bg background.

**Modals 04a/04b (135:4483)**
- Window: white, radius 32, padding 32, gap 24, Elevation 2. Width 880 (04b) or 640 (04a).
- Scrim: overlay/scrim; clicking it closes.
- Header: gradient orb 44, Heading title, Body S subtitle in ink/secondary, close button 36 with X 18 (Esc).
- Body:
  - FW Field (66:3746): label in Label, input 48 high, radius sm, padding x 14; help text in Micro.
  - FW Checkbox: box 22, radius xs, 1.5 px border; checked = night fill with Check 16.
  - Side panel: sunken, radius 24, padding 20.
- 04a example chips are FW ChoiceChip: height 44, padding-left 14, padding-right 16, pill, icon 18; selected = night.
- Actions row: Fantôme, spacer, Secondaire, Primaire with Check.

**Empty state (135:4377)**
- Column, gap 12.
- Circle 56 in bg/lilac with Truck 24.
- Heading, then Body in ink/secondary.
- Buttons: Eclipse "Reprendre les 9 ventes" and Secondaire.

**Toast (135:4400)**
- `role=status`, night pill, padding-left 12, padding-right 20, padding y 12, gap 12, Elevation 2.
- Circle 28 in success-vivid with Check 16.
- Body strong white text, then an "Annuler" link in Label, brand/lilac.

**Avatar menu (138:4108)**
- 300 wide, white, radius 24, padding 8, gap 4, Elevation 2.
- Identity block (padding 12, gap 12): Avatar M 36, name in Body strong, email in Micro, role shown as a Neutre StatusPill with ShieldCheck.
- Separators: line/subtle.
- Items: "Mon profil" (User) and "Se déconnecter" (LogOut, action/danger).

**FW Avatar (64:3389)**
- Sizes S 28, M 36, L 56. Tones Lilas (info-vivid background, brand/indigo initials), Menthe, Rose.
- Doc: initials only; the RH API provides no photo.

**Login (130:3579)**
- 440 column, gap 32.
- Logo 185.4×48, then the tagline in Body, ink/secondary.
- Form card: surface/panel, radius 32, padding 32, gap 20. Title in Heading, subtitle in Body S, then email Field, FW PasswordField (130:3573: Eye toggle 32 with icon 20, `aria-pressed`, never pre-filled), and a full-width Primaire button.
- Help line in Micro.

**Error page 00c (130:3676, FW ErrorState 70:4399)**
- White card 560, radius 32, padding 40/40/36, gap 16, Elevation 1.
- Circle 72 in critical-bg with LogIn 32.
- Title in Display M, message in Body.
- Code chip (sunken, Micro): "UNAUTHORIZED · 401".
- Primary button "Revenir à la connexion".
- Types: Connexion refusée, Session expirée, Accès refusé, Introuvable, Données invalides, Opération impossible, Service indisponible, Erreur serveur.
- Doc: the message comes from the API when present; the action returns to `state.from`.

**Accueil (80:46)**
- Greeting in Display L; subtitle in Body.
- Eclipse bar (gradient variant) 960×64.
- KPI card, padding 24.
- "À décider" title in Heading, with a night count badge in Caption.
- List card: surface/panel, radius 32, padding 4/8.
  - Rows: padding 16, gap 16, bottom border line/subtle.
  - Each row: icon circle (padding 8, icon 20; critical-bg OctagonAlert, info-bg CalendarDays, lilac Sparkles), title in Body strong, meta in Body S tertiary, Secondaire S button.
- The logo frame 95:14 is named "Logo Eclipse" but shows the NOVA logotype (naming bug).

## 5. Component inventory (variants and states)
**02.1 Base**
- FW Button: 5 variants × 6 states, sizes M/S.
- FW IconButton (42:116): Surface / Nuit / Transparent × Défaut / Survol / Focus / Désactivé.
- FW NotificationButton.
- FW StatusPill: 5 tones × Douce/Vive.
- FW NavTab: Actif / Repos / Survol / Focus.
- FW TopNav (ADMIN) 42:262.
- FW SegmentedControl (période).
- FW CommandBar: 5 states.
- FW SuggestionChip 42:375.
- FW ProfileChip 42:385.
- FW KPI (88 px, overview only): Ventes/Stock/Équipe × Données/Chargement.
- FW StockRiskBar.
- FW PanelTab.
- FW TabPill.
- FW ActivityCard.
- FW TableHeader / TableRow (densities Confortable 56 / Compacte 44; Survol = sunken; doc points to `GET /api/stock/status`).
- FW AreaChart.
- FW TopProduct.
- Assistant blocks: Msg Utilisateur / Assistant, ToolTrace, Result Tableau / Graphique, ActionCard (6 states), AssistantError (Quota / Réseau / Session / Indisponible), AssistantHeader.
- FW Signature lines 41:33; EclipseMark 12:172.

**02.2 Applicatifs**
- FW Avatar.
- FW EclipseTrigger (Complet / Icône).
- FW TopBar (Actif = 5 modules).
- FW BottomBar (mobile).
- FW PageHeader.
- FW Banner (Succès / Info / Avertissement).
- FW Tooltip.
- FW MenuItem (Défaut / Survol / Danger / Désactivé).
- FW Menu.
- FW Dialog (Confirmation / Destructive / Refus avec motif / Envoi en cours).
- FW SheetHeader / SheetFooter.
- FW Field (9 states incl. À compléter, Erreur, Succès, Lecture seule).
- FW Select + Option.
- FW SearchField.
- FW NumberField.
- FW DateField.
- FW Textarea.
- FW Checkbox.
- FW ChoiceChip.
- FW FilterChip.
- FW TableHeaderCell (alignment × sort).
- FW TableCell (Texte, Texte double, Nombre, Montant, Statut, Lien, Avatar + nom, Actions).
- FW Pagination.
- FW ListItem.
- FW Stat.
- FW KeyValue.
- FW TimelineItem (18 types).
- FW StatusBadge presets (25 statuses).
- FW EmptyState (Aucune donnée / Aucun résultat / Tout est traité).
- FW Skeleton (tableau).
- FW ErrorState (8 types).
- FW Tag "Données illustratives".
- FW PasswordField.

**Doc conflict to arbitrate.** FW Field's own description says the NEXORA-requested *server* error shown on the field conflicts with rule 2.5 (errors go to the dedicated error page).

## 6. Icons (lucide-react, strokeWidth 1.75)
| Area | Icons |
|---|---|
| Nav | House, ShoppingCart, Package, Users, Handshake, Truck, Plus |
| Page chrome | Search, Ellipsis, X, Sparkles, ArrowUp, Square, LoaderCircle |
| Status pills | Check, TriangleAlert, OctagonAlert, Info, Clock |
| KPI icons per frame | Stock: Package, TriangleAlert (probable), Receipt · Ventes: ChartColumn, Receipt, TrendingUp, Percent · Relations: Handshake, Building2, Phone, Percent · Équipe: Users, CalendarDays, ClipboardList, Percent · Livraisons: Truck, Clock, Receipt |
| Menus / profile | User, LogOut, ShieldCheck, Eye, ArrowDownToLine, Pencil, Trash2 |
| Login / error | Eye, EyeOff (library), LogIn |
| 04a chips | Truck, Wrench, CalendarDays |

- These sets come from each frame's component descriptions, not from an exact icon-to-tile mapping.
- Rendered sizes: 20 (nav, buttons), 18 (search, menu, Plus), 16 (pills, S buttons), 14 (KPI pastille), 22 (bottom bar), 24 (empty state), 32 (error state).
- The 41:40 frame lists about 85 more icons available.

## 7. Logo and decor assets
**Logo: MISSING in the repo**
- `novaui/public/icon.svg` is a generic "N" on #0f172a.
- `novaui/src/components/common/EclipseLogo.tsx` is the old Eclipse mark. Per Branding 123:1566, Eclipse must never appear in the logo slot.

**Figma nodes**
- NOVA / Logo component set 123:1379:
  - Logotype: Couleur 123:1364, Monochrome sombre 123:1366, Sur fond sombre 123:1372, Monochrome clair 123:1374.
  - Symbole: 123:1368 / 123:1370 / 123:1376 / 123:1378.
- Screen instances: e.g. 124:59, inside the "Logo NOVA" wrapper frames (109:1221 is the wrapper on Équipe générique).
- 67:9595 is the original branding "Cover" (vector 67:9596, 1054×273). It is not the UI asset.
- Branding rules:
  - Top bar: logotype Couleur, 86×23 at 32 px (instances are actually 88.8×23).
  - Login: 48 px high.
  - Logotype minimum width 72 px; symbol minimum 16 px.
  - Favicon: symbol on a Nuit background.

**Signature lines**
- Component 41:33. Instances are named "lignes signature (décor)": 80:47, 107:376, 130:3575, and others.
- Doc: `<svg aria-hidden>`, absolute, no filter.
- The SVG overflows its 1400×320 box: inset −60.24 % / −4.44 % / −195.89 % / −2.38 %.
- The gradient Eclipse bar has its own 640×64 lines asset.

**Getting the SVGs.** get_design_context does return asset URLs, valid for 7 days, for example:
- logo `…/616be.svg` (23 px, 107:375), `…/f5f00.svg` (48 px, 130:3574), `…/97dc5.svg` (Couleur variant, 130:3702)
- signature `…/75c92.svg`
- bar lines `…/1a861.svg`

The plan is to fetch them with curl (or download_assets) into e.g. `novaui/src/assets/`. Not done.

## 8. Responsive guidance in Figma
- Only two mobile items exist.
- **00d Connexion · mobile 390 (130:3697):**
  - Column 358 wide (16 px side margins), starting at y 166, gap 24.
  - Logo 154.5×40.
  - Card padding 20/24.
  - The rest is the same as desktop.
- **FW BottomBar (mobile) 64:3924:** doc says "Mobile < 768 px".
  - Layout: Accueil, up to 2 permitted modules, a centred gradient Eclipse button (60, white 4 px border, raised −18), then "Plus" (a sheet with the other modules, profile and logout). A role with 1 module gets 3 tabs.
  - Size: 390×92, top radius 28, Elevation 2.
  - Tabs: 78 wide, icon 22, Micro label. Active tab: brand/violet. Others: ink/secondary.
  - This matches the existing 767 px breakpoint in `novaui/src/styles.css`.
- **No** mobile or tablet versions exist for the module screens, Accueil, modals or profile. Their responsive behaviour has to be decided outside Figma.
