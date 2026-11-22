import { Clock, Package, Receipt, TriangleAlert } from 'lucide-react'
import type { ReactNode } from 'react'
import type { KpiItem } from '../../../components/module/types'
import { formatCompactAmount, formatDecimal, pluralize } from '../../../utils/format'
import type { StockSummary } from '../types/stock'
import type { StockAdaptationView } from './stockAdaptation'

export const formatAvailability = (rate: number): string => `${formatDecimal(rate * 100)} %`

const valueHint = (summary: StockSummary): string =>
  summary.productsWithoutPurchasePrice > 0
    ? `au prix d’achat · ${pluralize(summary.productsWithoutPurchasePrice, 'produit', 'produits')} au prix de vente`
    : 'au prix d’achat'

const aboveThreshold = (summary: StockSummary): string =>
  pluralize(
    summary.totalProducts - summary.lowStockProducts,
    'produit au-dessus du seuil',
    'produits au-dessus du seuil',
  )

const expiryKpi = (summary: StockSummary | undefined, placeholder: ReactNode, highlighted: boolean): KpiItem => ({
  key: 'expiring',
  icon: Clock,
  tone: 'mint',
  label: 'Expire bientôt',
  value: summary ? summary.expiringSoon : placeholder,
  hint: 'dans moins de 30 jours',
  highlighted,
})

export const buildStockKpis = (
  summary: StockSummary | undefined,
  adaptation: StockAdaptationView,
  placeholder: ReactNode,
): KpiItem[] => {
  const base: KpiItem[] = [
    {
      key: 'availability',
      icon: Package,
      tone: 'mint',
      label: 'Disponibilité',
      value: summary ? formatAvailability(summary.availabilityRate) : placeholder,
      hint: summary && aboveThreshold(summary),
    },
    {
      key: 'alert',
      icon: TriangleAlert,
      tone: 'blush',
      label: 'En alerte',
      value: summary ? summary.lowStockProducts : placeholder,
      hint: summary && `dont ${summary.criticalLowStock} en risque de rupture`,
    },
    {
      key: 'value',
      icon: Receipt,
      tone: 'lilac',
      label: 'Valeur du stock',
      value: summary ? formatCompactAmount(summary.totalPurchaseValue) : placeholder,
      hint: summary && valueHint(summary),
    },
  ]
  return adaptation.enabled ? [...base, expiryKpi(summary, placeholder, adaptation.highlighted)] : base
}

export const stockSubtitle = (summary: StockSummary): string =>
  `${pluralize(summary.totalProducts, 'produit', 'produits')} · ${summary.lowStockProducts} en alerte`
