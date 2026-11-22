import { PENDING_VALUE, relationsKpis, relationsSubtitle } from '../relationsKpis'
import type { ContactSummary } from '../../types/contact'

const SUMMARY: ContactSummary = {
  activeCustomers: 186,
  totalCustomers: 214,
  suppliers: 12,
  openPurchaseOrders: 3,
  followUp: 3,
  creditOutstanding: 4_100_000,
  overLimitCustomers: 2,
}

const GENERIC = { visible: false, highlighted: false }

describe('relationsKpis', () => {
  it('shouldShowThreeBaseIndicators_forTheGenericScreen', () => {
    const kpis = relationsKpis(SUMMARY, GENERIC)

    expect(kpis.map((kpi) => [kpi.label, kpi.value, kpi.hint])).toEqual([
      ['Clients actifs', 186, 'sur 214 ce trimestre'],
      ['Fournisseurs', 12, '3 commandes en cours'],
      ['À relancer', 3, 'sans nouvelles depuis 30 jours'],
    ])
  })

  it('shouldAddHighlightedCreditIndicator_whilePreviewing', () => {
    const credit = relationsKpis(SUMMARY, { visible: true, highlighted: true })[3]

    expect(credit).toMatchObject({ label: 'Encours crédit', highlighted: true })
    expect(credit?.value).toMatch(/^4,1\sM Ar$/)
    expect(credit?.hint).toBe('2 clients au-dessus du plafond')
  })

  it('shouldUseSingular_whenOneOrderAndOneClient', () => {
    const kpis = relationsKpis({ ...SUMMARY, openPurchaseOrders: 1, overLimitCustomers: 1 }, { visible: true, highlighted: false })

    expect(kpis[1]?.hint).toBe('1 commande en cours')
    expect(kpis[3]).toMatchObject({ hint: '1 client au-dessus du plafond', highlighted: false })
  })

  it('shouldShowPlaceholders_whileSummaryIsLoading', () => {
    const kpis = relationsKpis(undefined, { visible: true, highlighted: false })

    expect(kpis.map((kpi) => kpi.value)).toEqual([PENDING_VALUE, PENDING_VALUE, PENDING_VALUE, PENDING_VALUE])
  })
})

describe('relationsSubtitle', () => {
  it('shouldCountClientsAndSuppliers', () => {
    expect(relationsSubtitle(SUMMARY)).toBe('214 clients · 12 fournisseurs')
  })

  it('shouldBeEmpty_whileLoading', () => {
    expect(relationsSubtitle(undefined)).toBeUndefined()
  })
})
