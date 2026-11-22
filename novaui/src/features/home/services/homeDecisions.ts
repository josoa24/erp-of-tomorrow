import { formatPeriod } from '../../team/services/teamFormat'
import type { Leave } from '../../team/types/leave'
import type { EclipseSuggestion, Replenishment } from '../types/home'

export type DecisionKind = 'order' | 'leaves' | 'suggestion'

export interface Decision {
  key: string
  kind: DecisionKind
  title: string
  detail: string
  action: string
}

export const orderDecision = (item: Replenishment | undefined): Decision[] =>
  item
    ? [
        {
          key: `order-${item.productId}`,
          kind: 'order',
          title: `${item.productName} en rupture${item.coverageDays === null ? '' : ` dans ${Math.floor(item.coverageDays)} jours`}`,
          detail: `Stock${item.recommendedQuantity === null ? '' : ` · commande proposée : ${item.recommendedQuantity} unités`}`,
          action: 'Commander',
        },
      ]
    : []

export const leavesDecision = (leaves: Leave[] | undefined): Decision[] => {
  if (!leaves || leaves.length === 0) return []
  const start = leaves.map((leave) => leave.startDate).sort()[0]!
  const end = leaves.map((leave) => leave.endDate).sort().reverse()[0]!
  return [
    {
      key: 'leaves',
      kind: 'leaves',
      title: `${leaves.length} ${leaves.length > 1 ? 'demandes' : 'demande'} de congé`,
      detail: `Équipe · ${formatPeriod(start, end)}`,
      action: 'Valider',
    },
  ]
}

export const suggestionDecision = (items: EclipseSuggestion[] | undefined): Decision[] =>
  items?.[0]
    ? [{ key: `suggestion-${items[0].adaptationKey}`, kind: 'suggestion', title: `Eclipse suggère : ${items[0].title}`, detail: items[0].reason, action: 'Voir' }]
    : []
