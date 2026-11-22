import type { Issue } from '../types/dashboard'
import type { StockRisk, StockStatusItem } from '../types/stock'
import { formatSignedPercent, pluralize } from '../utils/format'

const countByRisk = (items: StockStatusItem[], risk: StockRisk): number =>
  items.filter((item) => item.risk === risk).length

export const buildIssues = (stockStatus: StockStatusItem[], salesTrend: number | null): Issue[] => {
  const issues: Issue[] = []
  const critical = countByRisk(stockStatus, 'CRITICAL')
  const warning = countByRisk(stockStatus, 'WARNING')

  if (critical > 0) {
    const label = `${pluralize(critical, 'produit', 'produits')} en risque de rupture`
    issues.push({ id: 'stock-critical', label, severity: 'critical' })
  }
  if (warning > 0) {
    const label = `${pluralize(warning, 'produit', 'produits')} sous le seuil d'alerte`
    issues.push({ id: 'stock-warning', label, severity: 'warning' })
  }
  if (salesTrend !== null && salesTrend < 0) {
    const label = `Ventes en baisse (${formatSignedPercent(salesTrend)})`
    issues.push({ id: 'sales-drop', label, severity: 'warning' })
  }
  return issues
}
