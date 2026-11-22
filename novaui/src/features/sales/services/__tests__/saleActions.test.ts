import { saleWith } from '../../test/saleFixtures'
import { remainingAmount, saleActionKeys } from '../saleActions'

describe('saleActions', () => {
  it('shouldOfferDraftActions_whenSaleIsDraft', () => {
    expect(saleActionKeys(saleWith({ paymentStatus: 'DRAFT', status: 'DRAFT' }))).toEqual([
      'edit',
      'validate',
      'delete',
    ])
  })

  it('shouldAllowCancel_whenNothingWasCollected', () => {
    expect(saleActionKeys(saleWith({ paymentStatus: 'OVERDUE' }))).toEqual(['collect', 'cancel', 'detail'])
  })

  it('shouldForbidCancel_whenAPaymentExists', () => {
    expect(saleActionKeys(saleWith({ paymentStatus: 'TO_COLLECT', paidAmount: 50000 }))).toEqual([
      'collect',
      'detail',
    ])
  })

  it.each(['PAID', 'CANCELLED'] as const)('shouldOnlyShowDetail_whenSaleIs%s', (paymentStatus) => {
    expect(saleActionKeys(saleWith({ paymentStatus }))).toEqual(['detail'])
  })

  it('shouldNeverReturnNegativeRemaining', () => {
    expect(remainingAmount({ totalAmount: 100, paidAmount: 40 })).toBe(60)
    expect(remainingAmount({ totalAmount: 100, paidAmount: 140 })).toBe(0)
  })
})
