import type { SalesKpis } from '../../types/salesReport'
import { KPI_PLACEHOLDER, buildSalesKpiItems } from '../salesKpiItems'

const KPIS: SalesKpis = {
  monthRevenue: 18450000,
  previousMonthRevenue: 16600000,
  todaySalesCount: 23,
  todayRevenue: 1500000,
  todayCollected: 1240000,
  sameWeekdayLastWeekRevenue: 1300000,
  averageBasket: 80200,
  previousMonthAverageBasket: 77100,
}
const NOW = new Date('2026-11-24T08:00:00Z')
const normalize = (value: unknown): string => String(value).replace(/\s/g, ' ')

describe('buildSalesKpiItems', () => {
  it('shouldBuildGenericIndicators', () => {
    const items = buildSalesKpiItems({ kpis: KPIS, credit: null, showCredit: false, highlightCredit: false, now: NOW })

    expect(items.map((item) => item.label)).toEqual(['Chiffre d’affaires', 'Ventes du jour', 'Panier moyen'])
    expect(items.map((item) => normalize(item.value))).toEqual(['18,45 M Ar', '23', '80 200 Ar'])
    expect(items.map((item) => normalize(item.hint))).toEqual([
      '+11,1 % vs octobre',
      '1,24 M Ar encaissés',
      '+4 % ce mois',
    ])
  })

  it('shouldShowPlaceholders_whileLoading', () => {
    const items = buildSalesKpiItems({ kpis: null, credit: null, showCredit: true, highlightCredit: false })

    expect(items.every((item) => item.value === KPI_PLACEHOLDER)).toBe(true)
    expect(items).toHaveLength(4)
  })

  it('shouldAddHighlightedCreditIndicator_whenAdaptationIsPreviewed', () => {
    const credit = { outstanding: 2300000, customers: 8, overdueCustomers: 2 }

    const items = buildSalesKpiItems({ kpis: KPIS, credit, showCredit: true, highlightCredit: true, now: NOW })

    expect(items[3]).toMatchObject({ label: 'Crédit en cours', highlighted: true })
    expect(normalize(items[3].value)).toBe('2,3 M Ar')
    expect(items[3].hint).toBe('8 clients · 2 en retard')
  })
})
