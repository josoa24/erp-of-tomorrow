import { Building2, Handshake, Percent, Phone } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import { formatCompactAmount, pluralize } from '../../../utils/format'
import { FOLLOW_UP_HINT } from '../constants/followUp'
import type { AdaptationView } from '../types/adaptationView'
import type { ContactSummary } from '../types/contact'

export const PENDING_VALUE = '—'

const creditKpi = (summary: ContactSummary | undefined, highlighted: boolean): KpiItem => ({
  key: 'credit',
  icon: Percent,
  tone: 'mint',
  label: 'Encours crédit',
  value: summary ? formatCompactAmount(summary.creditOutstanding) : PENDING_VALUE,
  hint: summary && `${pluralize(summary.overLimitCustomers, 'client', 'clients')} au-dessus du plafond`,
  highlighted,
})

export const relationsKpis = (summary: ContactSummary | undefined, adaptation: AdaptationView): KpiItem[] => {
  const kpis: KpiItem[] = [
    {
      key: 'active',
      icon: Handshake,
      tone: 'lilac',
      label: 'Clients actifs',
      value: summary?.activeCustomers ?? PENDING_VALUE,
      hint: summary && `sur ${summary.totalCustomers} ce trimestre`,
    },
    {
      key: 'suppliers',
      icon: Building2,
      tone: 'mint',
      label: 'Fournisseurs',
      value: summary?.suppliers ?? PENDING_VALUE,
      hint: summary && `${pluralize(summary.openPurchaseOrders, 'commande', 'commandes')} en cours`,
    },
    {
      key: 'followUp',
      icon: Phone,
      tone: 'blush',
      label: 'À relancer',
      value: summary?.followUp ?? PENDING_VALUE,
      hint: FOLLOW_UP_HINT,
    },
  ]
  return adaptation.visible ? [...kpis, creditKpi(summary, adaptation.highlighted)] : kpis
}

export const relationsSubtitle = (summary: ContactSummary | undefined): string | undefined =>
  summary &&
  `${pluralize(summary.totalCustomers, 'client', 'clients')} · ${pluralize(summary.suppliers, 'fournisseur', 'fournisseurs')}`
