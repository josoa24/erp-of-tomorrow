import { HUILE, RIZ } from '../../test/saleFixtures'
import {
  EMPTY_DRAFT,
  addProduct,
  canSaveDraft,
  canValidateDraft,
  draftTotal,
  removeLine,
  setLineQuantity,
} from '../saleDraft'

describe('saleDraft', () => {
  it('shouldAddProductWithQuantityOne', () => {
    const draft = addProduct(EMPTY_DRAFT, RIZ)

    expect(draft.lines).toEqual([
      { productId: 7, productName: 'Riz blanc 50 kg', unitPrice: 186000, available: 14, quantity: 1 },
    ])
  })

  it('shouldIncrementQuantity_whenProductIsAddedTwice', () => {
    const draft = addProduct(addProduct(EMPTY_DRAFT, RIZ), RIZ)

    expect(draft.lines).toHaveLength(1)
    expect(draft.lines[0].quantity).toBe(2)
  })

  it('shouldComputeLiveTotal', () => {
    const draft = setLineQuantity(addProduct(addProduct(EMPTY_DRAFT, RIZ), HUILE), HUILE.id, 2)

    expect(draftTotal(draft)).toBe(186000 + 84000)
  })

  it('shouldIgnoreInvalidQuantity_inTotal', () => {
    const draft = setLineQuantity(addProduct(EMPTY_DRAFT, RIZ), RIZ.id, Number.NaN)

    expect(draftTotal(draft)).toBe(0)
    expect(canSaveDraft(draft)).toBe(false)
  })

  it('shouldRemoveLine', () => {
    const draft = removeLine(addProduct(EMPTY_DRAFT, RIZ), RIZ.id)

    expect(draft.lines).toEqual([])
    expect(canSaveDraft(draft)).toBe(false)
  })

  it('shouldAllowDraftButNotValidation_whenQuantityExceedsStock', () => {
    const draft = setLineQuantity(addProduct(EMPTY_DRAFT, HUILE), HUILE.id, 5)

    expect(canSaveDraft(draft)).toBe(true)
    expect(canValidateDraft(draft)).toBe(false)
  })

  it('shouldAllowValidation_whenStockIsEnough', () => {
    expect(canValidateDraft(addProduct(EMPTY_DRAFT, RIZ))).toBe(true)
  })
})
