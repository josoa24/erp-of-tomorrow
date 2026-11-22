import { EXPIRING_ITEM, makeStatusItem, RUPTURE_ITEM, STOCK_COUNTS } from '../../test/stockFixtures'
import { effectiveFilter, filterStockItems, isStockFilter, stockFilterChips } from '../stockFilters'

const OK_ITEM = makeStatusItem()
const ITEMS = [RUPTURE_ITEM, EXPIRING_ITEM, OK_ITEM]

describe('filterStockItems', () => {
  it('shouldKeepEveryItem_forTous', () => {
    expect(filterStockItems(ITEMS, 'tous')).toEqual(ITEMS)
  })

  it('shouldKeepLowStock_forAlerte', () => {
    expect(filterStockItems(ITEMS, 'alerte')).toEqual([RUPTURE_ITEM])
  })

  it('shouldKeepRuptureRisk_forRupture', () => {
    expect(filterStockItems(ITEMS, 'rupture')).toEqual([RUPTURE_ITEM])
  })

  it('shouldKeepExpiringSoon_forExpireBientot', () => {
    expect(filterStockItems(ITEMS, 'expire-bientot')).toEqual([EXPIRING_ITEM])
  })
})

describe('stockFilterChips', () => {
  it('shouldListGenericChipsWithServerCounts', () => {
    const chips = stockFilterChips(STOCK_COUNTS, false)

    expect(chips.map((chip) => [chip.label, chip.count])).toEqual([
      ['Tous', 64],
      ['En alerte', 5],
      ['Rupture', 2],
    ])
  })

  it('shouldAddExpiryChip_whenAdapted', () => {
    const chips = stockFilterChips(STOCK_COUNTS, true)

    expect(chips[3]).toEqual({ key: 'expire-bientot', label: 'Expire bientôt', count: 3, adapted: true })
  })

  it('shouldLeaveCountsEmpty_whileCountsAreLoading', () => {
    expect(stockFilterChips(undefined, false)[0].count).toBeUndefined()
  })
})

describe('effectiveFilter', () => {
  it('shouldFallBackToTous_whenExpiryFilterWithoutAdaptation', () => {
    expect(effectiveFilter('expire-bientot', false)).toBe('tous')
    expect(effectiveFilter('expire-bientot', true)).toBe('expire-bientot')
    expect(effectiveFilter('rupture', false)).toBe('rupture')
  })

  it('shouldRecognizeKnownFilters', () => {
    expect(isStockFilter('alerte')).toBe(true)
    expect(isStockFilter('inconnu')).toBe(false)
  })
})
