import type { StockRisk, StockStatusItem } from '../../types/stock'
import { buildIssues } from '../dashboardIssues'

const item = (productId: number, risk: StockRisk): StockStatusItem => ({
  productId,
  productName: `Produit ${productId}`,
  quantity: 1,
  minThreshold: 5,
  daysRemaining: 2,
  risk,
})

describe('buildIssues', () => {
  it('shouldReportCriticalAndWarningProducts', () => {
    const status = [item(1, 'CRITICAL'), item(2, 'CRITICAL'), item(3, 'WARNING'), item(4, 'OK')]

    const issues = buildIssues(status, null)

    expect(issues.map((i) => i.label)).toEqual([
      '2 produits en risque de rupture',
      "1 produit sous le seuil d'alerte",
    ])
    expect(issues[0].severity).toBe('critical')
  })

  it('shouldReportSalesDrop_whenTrendIsNegative', () => {
    const [issue] = buildIssues([], -5.2)

    expect(issue.id).toBe('sales-drop')
    expect(issue.label.replace(/\s/g, ' ')).toBe('Ventes en baisse (-5,2 %)')
  })

  it('shouldReturnNoIssue_whenEverythingIsFine', () => {
    expect(buildIssues([item(1, 'OK')], 8)).toEqual([])
  })
})
