# API publique du socle NOVA (figée)

Pour les postes 2 (Stock, Ventes) et 3 (Relations, Équipe, Accueil). Le socle (poste 1) est le seul à modifier ces éléments : si un écran a besoin d'un changement, il le demande au poste 1. Les chemins sont relatifs à `novaui/src/`.

Règles de consommation : ne pas copier ni redéfinir ces composants dans `features/<module>/`, ne pas changer leurs props, ne pas les styler de l'extérieur autrement que par `className` quand il existe.

## 1. Gabarit d'écran de module (`components/module/`)

| Export | Fichier | Props |
|---|---|---|
| `ModulePage` | `ModulePage.tsx` | `title: string`, `subtitle?: ReactNode`, `search?: SearchFieldProps`, `primaryAction?: ReactNode`, `kpis?: KpiItem[]`, `kpiLabel?: string`, `children?: ReactNode` |
| `KpiStrip` | `KpiStrip.tsx` | `items: KpiItem[]`, `label?: string` (défaut « Indicateurs »). Utilisé par `ModulePage` quand `kpis` est fourni |
| `ListCard` | `ListCard.tsx` | `label: string`, `toolbar?: ReactNode`, `children: ReactNode` |
| `FilterBar` | `FilterBar.tsx` | `children: ReactNode` (des `FilterChip`), `sortHint?: string`, `label?: string` (défaut « Filtres ») |
| `DataTable` | `DataTable.tsx` | `DataTableProps<Row>` ci-dessous |
| `AdaptationBar` | `AdaptationBar.tsx` | Rendu par le shell, pas par les écrans (voir § 4) |

Types (`components/module/types.ts`) :

```ts
type KpiItem = StatProps & { key: string }

interface DataTableColumn<Row> {
  key: string
  header: string
  render: (row: Row) => ReactNode
  align?: 'start' | 'center' | 'end'
  width?: number | string
  isNew?: boolean
  sort?: 'ascending' | 'descending' | 'none'
  onSort?: () => void
}

interface RowAction {
  key: string
  label: string
  icon?: LucideIcon
  tone?: 'default' | 'danger'
  onSelect: () => void
}

interface DataTableProps<Row> {
  caption: string
  columns: DataTableColumn<Row>[]
  rows: Row[]
  getRowKey: (row: Row) => string | number
  getRowLabel: (row: Row) => string
  rowActions?: (row: Row) => RowAction[]
  loading?: boolean
  empty?: ReactNode
  className?: string
}
```

- `isNew: true` : en-tête avec l'étiquette « Nouveau » et cellules sur fond succès. À utiliser seulement pendant l'aperçu d'une adaptation (`preview`), jamais une fois l'adaptation `active`.
- `getRowLabel` sert au libellé accessible du menu d'actions (« Actions pour <libellé> »). Ne pas proposer d'action de modification ou de suppression sur une donnée validée.
- Moins de 768 px : chaque ligne devient une carte. De 768 à 1279 px : la table défile dans sa carte, première colonne figée. La page ne défile jamais horizontalement.

## 2. Primitives (`components/ui/`)

| Export | Fichier | Props principales |
|---|---|---|
| `FilterChip` | `filter-chip.tsx` | props d'un `button` + `label: string`, `count?: number`, `active?: boolean`, `highlighted?: boolean` (filtre ajouté par Eclipse) |
| `StatusPill` | `status-pill.tsx` | props d'un `span` + `label: string`, `tone?: 'success' \| 'warning' \| 'critical' \| 'info' \| 'neutral'` (défaut neutral), `emphasis?: 'soft' \| 'vivid'` (défaut soft), `icon?: LucideIcon` (sinon icône du ton) |
| `Dialog` | `dialog.tsx` | `open: boolean`, `onClose: () => void`, `title: string`, `description?: ReactNode`, `icon?: ReactNode`, `size?: 'm' \| 'l'` (640 / 880 px), `placement?: 'center' \| 'sheet'`, `footer?: ReactNode`, `closeLabel?: string`, `className?: string`, `children?: ReactNode` |
| `Stat` | `stat.tsx` | `icon: LucideIcon`, `label: string`, `value: ReactNode`, `hint?: ReactNode`, `tone?: 'lilac' \| 'mint' \| 'blush'`, `highlighted?: boolean`, `className?: string` |
| `Button` | `button.tsx` | `variant?: 'primary' \| 'secondary' \| 'ghost' \| 'eclipse' \| 'danger'`, `size?: 'm' \| 's'`, `loading?: boolean` (affiche « Exécution… » et bloque le double envoi) |
| `SearchField` | `search-field.tsx` | `onSearch: (value: string) => void` (appelé après 250 ms), `defaultValue?: string`, `delay?: number`, `slashShortcut?: boolean`, `containerClassName?: string` |

Les autres primitives (`IconButton`, `Avatar`, `Menu`, `MenuItem`, `EmptyState`, `Skeleton`, `Field`, `Input`, `PasswordField`, `Textarea`, `Checkbox`, `ChoiceChip`, `Kbd`, `Panel`) sont dans le même dossier. Leur API suit le même principe.

## 3. Eclipse

| Export | Fichier | Rôle |
|---|---|---|
| `EclipseBar` | `components/eclipse/EclipseBar.tsx` | Barre présentationnelle, sans appel à l'IA |
| `useModuleAdaptation` | `features/eclipse/hooks/useModuleAdaptation.ts` | État d'une adaptation pour un écran de module |
| `ADAPTATION_CATALOG`, `ADAPTATION_MODULE`, `ADAPTATION_KEYS`, `ADAPTATION_MODULE_KEYS`, `isAdaptationKey` | `features/eclipse/constants/adaptations.ts` | Catalogue fermé des 4 adaptations |
| `AdaptationModuleKey`, `AdaptationKey`, `AdaptationProposal`, `AdaptationActivation` | `features/eclipse/types/adaptation.ts` | Types |

```ts
interface EclipseBarProps {
  variant?: 'night' | 'light'
  placeholder: string
  value: string
  onChange: (value: string) => void
  onSubmit: (text: string) => void
  status?: 'idle' | 'streaming' | 'unavailable'
  onStop?: () => void
  unavailableLabel?: string
  label?: string
  shortcut?: boolean
  inputRef?: RefObject<HTMLTextAreaElement | null>
  onBlur?: () => void
  className?: string
}

type AdaptationModuleKey = 'stock' | 'ventes' | 'relations' | 'equipe'

useModuleAdaptation(moduleKey: AdaptationModuleKey): {
  active: boolean
  preview: AdaptationProposal | null
  isApplying: boolean
  apply: () => void
  cancel: () => void
  canApply: boolean
}
```

Utilisation dans un écran de module :

```tsx
const { active, preview } = useModuleAdaptation('stock')
const adapted = active || preview !== null
const highlight = preview !== null
```

- Afficher les colonnes, filtres et indicateurs de l'adaptation quand `adapted` est vrai ; les mettre en valeur (`isNew`, `highlighted`) seulement quand `highlight` est vrai.
- Ne pas appeler `apply` ni `cancel` depuis l'écran : la barre d'adaptation du shell s'en charge.
- `active` vient de `GET /api/modules/adaptations` ; une réponse 404 ou 503 vaut « aucune adaptation active », sans page d'erreur.
- Ouvrir `/stock?eclipse=stock.lots-peremption` démarre l'aperçu avec les textes du catalogue.

## 4. Shell et routes

- Une route de module déclare dans `staticData` : `eclipsePlaceholder?: string`, `hideEclipseDock?: boolean`, `adaptationModule?: AdaptationModuleKey` (`types/routeStaticData.ts`). Les routes `/stock`, `/ventes`, `/equipe` et `/relations` le font déjà dans `navigation/moduleRoutes.ts`.
- Le shell (`components/shell/ShellBottomArea.tsx`) affiche en bas de page le dock Eclipse et, sur mobile, la barre de navigation. Quand un aperçu existe pour le module de la route, il les remplace par `AdaptationBar` (Annuler, et « Appliquer pour l'équipe » pour ADMIN seulement). Les écrans n'ont rien à monter eux-mêmes.
- Laisser au moins 160 px libres en bas de page : le contenu principal a déjà `pb-40` pour ne pas passer sous le dock.

## 5. Erreurs, messages et appels HTTP

| Export | Fichier | Usage |
|---|---|---|
| `useApiError()` | `hooks/useApiError.ts` | Renvoie `(error: unknown) => void`, qui ouvre la page d'erreur dédiée. Seul moyen d'afficher une erreur |
| `useToast()` | `components/ui/toast-context.ts` | `showToast({ message, actionLabel?, onAction? })`, pour les succès seulement |
| `useCurrentUser()` | `hooks/useCurrentUser.ts` | `{ email, role, roleLabel, displayName, initials } \| null` |
| `httpGet`, `httpGetPage`, `httpPost`, `httpPut`, `httpPatch`, `httpDelete` | `services/api/httpClient.ts` | Appels via la gateway, délai de 15 s. `httpGetPage` renvoie `{ items, total }` à partir de `X-Total-Count` |
| `buildQuery(params)` | `services/api/queryString.ts` | Paramètres de requête ; ignore `null` et `undefined` |

Rappel : les appels HTTP vont dans `features/<module>/api/`, la logique dans `services/`, l'état et les erreurs dans `hooks/`, l'affichage dans `components/` et l'assemblage dans `screens/`.
