import { isCollectMethod, isCreditDays, paymentTermsLabel } from '../paymentTerms'

describe('paymentTerms', () => {
  it.each([
    [{ paymentMethod: 'MVOLA' as const, creditDays: null }, 'MVola'],
    [{ paymentMethod: 'ORANGE_MONEY' as const, creditDays: null }, 'Orange Money'],
    [{ paymentMethod: 'CASH' as const, creditDays: null }, 'Espèces'],
    [{ paymentMethod: 'CREDIT' as const, creditDays: 30 }, 'Crédit · 30 jours'],
    [{ paymentMethod: null, creditDays: null }, null],
  ])('shouldLabelPaymentTerms_%#', (terms, label) => {
    expect(paymentTermsLabel(terms)).toBe(label)
  })

  it('shouldAcceptOnlyOfferedCreditDays', () => {
    expect(isCreditDays(45)).toBe(true)
    expect(isCreditDays(20)).toBe(false)
    expect(isCreditDays(null)).toBe(false)
  })

  it('shouldExcludeCredit_fromCollectMethods', () => {
    expect(isCollectMethod('AIRTEL_MONEY')).toBe(true)
    expect(isCollectMethod('CREDIT')).toBe(false)
  })
})
