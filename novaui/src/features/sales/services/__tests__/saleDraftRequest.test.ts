import { HERY, RIZ, saleWith } from '../../test/saleFixtures'
import { EMPTY_DRAFT, addProduct } from '../saleDraft'
import { draftFromSale, toSaleRequest } from '../saleDraftRequest'

describe('saleDraftRequest', () => {
  it('shouldSendCounterSaleWithoutPaymentTerms_whenModuleIsGeneric', () => {
    const draft = { ...addProduct(EMPTY_DRAFT, RIZ), paymentMethod: 'MVOLA' as const }

    expect(toSaleRequest(draft, false)).toEqual({
      customerId: null,
      lines: [{ productId: 7, quantity: 1 }],
      toDeliver: false,
    })
  })

  it('shouldSendCreditDays_whenPaymentIsCredit', () => {
    const draft = { ...addProduct(EMPTY_DRAFT, RIZ), customer: HERY, toDeliver: true, paymentMethod: 'CREDIT' as const }

    expect(toSaleRequest(draft, true)).toEqual({
      customerId: 3,
      lines: [{ productId: 7, quantity: 1 }],
      toDeliver: true,
      paymentMethod: 'CREDIT',
      creditDays: 30,
    })
  })

  it('shouldOmitCreditDays_whenPaymentIsMobileMoney', () => {
    const draft = { ...addProduct(EMPTY_DRAFT, RIZ), paymentMethod: 'ORANGE_MONEY' as const, creditDays: 45 as const }

    expect(toSaleRequest(draft, true)).not.toHaveProperty('creditDays')
  })

  it('shouldRebuildDraft_fromSavedSale', () => {
    const draft = draftFromSale(saleWith({ status: 'DRAFT', paymentMethod: 'CREDIT', creditDays: 15, toDeliver: true }))

    expect(draft.customer).toEqual({ id: 3, name: 'Épicerie Rova', city: null, phone: null })
    expect(draft.lines[0]).toEqual({
      productId: 7,
      productName: 'Riz blanc 50 kg',
      unitPrice: 186000,
      available: null,
      quantity: 1,
    })
    expect(draft).toMatchObject({ toDeliver: true, paymentMethod: 'CREDIT', creditDays: 15 })
  })

  it('shouldUseCounterAndDefaults_whenSaleHasNoCustomerNorTerms', () => {
    const draft = draftFromSale(saleWith({ customerId: null, paymentMethod: null, creditDays: 20 }))

    expect(draft).toMatchObject({ customer: null, paymentMethod: 'CASH', creditDays: 30 })
  })
})
