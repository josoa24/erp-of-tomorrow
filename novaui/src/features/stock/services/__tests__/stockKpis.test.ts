import { Clock } from 'lucide-react'
import { STOCK_SUMMARY } from '../../test/stockFixtures'
import { GENERIC_STOCK_VIEW } from '../stockAdaptation'
import { buildStockKpis, formatAvailability } from '../stockKpis'

const normalize = (value: unknown): string => String(value).replace(/\s/g, ' ')

describe('buildStockKpis', () => {
  it('shouldBuildTheThreeGenericIndicators', () => {
    const kpis = buildStockKpis(STOCK_SUMMARY, GENERIC_STOCK_VIEW, '…')

    expect(kpis.map((kpi) => kpi.label)).toEqual(['Disponibilité', 'En alerte', 'Valeur du stock'])
    expect(normalize(kpis[0].value)).toBe('92,2 %')
    expect(kpis[0].hint).toBe('59 produits au-dessus du seuil')
    expect(kpis[1].value).toBe(5)
    expect(kpis[1].hint).toBe('dont 2 en risque de rupture')
    expect(normalize(kpis[2].value)).toBe('42,6 M Ar')
    expect(kpis[2].hint).toBe('au prix d’achat')
  })

  it('shouldAddHighlightedExpiryIndicator_whenPreviewing', () => {
    const kpis = buildStockKpis(STOCK_SUMMARY, { enabled: true, highlighted: true }, '…')

    expect(kpis[3]).toMatchObject({
      label: 'Expire bientôt',
      value: 3,
      hint: 'dans moins de 30 jours',
      icon: Clock,
      highlighted: true,
    })
  })

  it('shouldNotHighlightExpiryIndicator_whenApplied', () => {
    const kpis = buildStockKpis(STOCK_SUMMARY, { enabled: true, highlighted: false }, '…')

    expect(kpis[3].highlighted).toBe(false)
  })

  it('shouldShowPlaceholders_whileSummaryIsLoading', () => {
    const kpis = buildStockKpis(undefined, GENERIC_STOCK_VIEW, '…')

    expect(kpis.every((kpi) => kpi.value === '…' && !kpi.hint)).toBe(true)
  })

  it('shouldMentionSalePriceValuation_whenPurchasePricesAreMissing', () => {
    const kpis = buildStockKpis({ ...STOCK_SUMMARY, productsWithoutPurchasePrice: 3 }, GENERIC_STOCK_VIEW, '…')

    expect(kpis[2].hint).toBe('au prix d’achat · 3 produits au prix de vente')
  })
})

describe('formatAvailability', () => {
  it('shouldRenderRateAsFrenchPercent', () => {
    expect(formatAvailability(1)).toBe('100 %')
    expect(formatAvailability(0.5)).toBe('50 %')
  })
})
