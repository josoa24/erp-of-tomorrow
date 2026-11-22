import { isSalesFilterKey, resolveSalesFilter, visibleSalesFilters } from '../salesFilters'

describe('salesFilters', () => {
  it('shouldHideCreditFilter_whenModuleIsNotAdapted', () => {
    expect(visibleSalesFilters(false).map((filter) => filter.label)).toEqual([
      'Toutes',
      'À encaisser',
      'Payées',
      'Annulées',
    ])
  })

  it('shouldShowCreditFilter_whenModuleIsAdapted', () => {
    expect(visibleSalesFilters(true).map((filter) => filter.key)).toContain('a-credit')
  })

  it('shouldIncludeOverdue_whenFilteringSalesToCollect', () => {
    expect(resolveSalesFilter('a-encaisser', false).criteria).toEqual({ paymentStatus: ['TO_COLLECT', 'OVERDUE'] })
  })

  it('shouldFallBackToAllSales_whenCreditFilterIsNotAvailable', () => {
    expect(resolveSalesFilter('a-credit', false).key).toBe('toutes')
  })

  it('shouldFilterOnCreditMethod_whenCreditFilterIsAvailable', () => {
    expect(resolveSalesFilter('a-credit', true).criteria).toEqual({ paymentMethod: 'CREDIT' })
  })

  it('shouldRecognizeOnlyKnownKeys', () => {
    expect(isSalesFilterKey('payees')).toBe(true)
    expect(isSalesFilterKey('brouillons')).toBe(false)
  })
})
