import { emptyMovementForm, hasErrors, toMovementRequest, validateMovementForm } from '../movementForm'

const TODAY = '2026-11-22'
const ENTRY = { type: 'IN' as const, withLot: true, today: TODAY }
const filled = { ...emptyMovementForm(4), quantity: '12', reason: '  Livraison Antsirabe ' }

describe('validateMovementForm', () => {
  it('shouldAcceptEntryWithoutLot', () => {
    expect(hasErrors(validateMovementForm(filled, ENTRY))).toBe(false)
  })

  it('shouldRequireProductAndPositiveIntegerQuantity', () => {
    const errors = validateMovementForm({ ...emptyMovementForm(), quantity: '1.5' }, ENTRY)

    expect(errors.productId).toBe('Choisissez un produit.')
    expect(errors.quantity).toBe('Saisissez une quantité entière supérieure à 0.')
  })

  it('shouldRefuseZeroQuantity', () => {
    expect(validateMovementForm({ ...filled, quantity: '0' }, ENTRY).quantity).toBeDefined()
  })

  it('shouldRequireExpiry_whenLotNumberIsGiven', () => {
    const errors = validateMovementForm({ ...filled, lotNumber: 'LOT-2611-A' }, ENTRY)

    expect(errors.expiryDate).toBe('Indiquez la date de péremption du lot.')
  })

  it('shouldRequireLotNumber_whenExpiryIsGiven', () => {
    const errors = validateMovementForm({ ...filled, expiryDate: '2027-02-12' }, ENTRY)

    expect(errors.lotNumber).toBe('Indiquez le numéro du lot.')
  })

  it('shouldRefusePastExpiry', () => {
    const errors = validateMovementForm({ ...filled, lotNumber: 'LOT-1', expiryDate: '2026-11-21' }, ENTRY)

    expect(errors.expiryDate).toBe('La date de péremption doit être aujourd’hui ou plus tard.')
  })

  it('shouldRefuseExitAboveAvailableStock', () => {
    const exit = { type: 'OUT' as const, withLot: false, today: TODAY, available: 10 }

    expect(validateMovementForm({ ...filled, quantity: '11' }, exit).quantity).toBe(
      'La quantité dépasse le stock disponible.',
    )
    expect(hasErrors(validateMovementForm({ ...filled, quantity: '10' }, exit))).toBe(false)
  })
})

describe('toMovementRequest', () => {
  it('shouldSendLotAndExpiry_whenLotIsEnabled', () => {
    const values = { ...filled, lotNumber: ' LOT-2611-A ', expiryDate: '2027-02-12' }

    expect(toMovementRequest(values, ENTRY)).toEqual({
      type: 'IN',
      quantity: 12,
      reason: 'Livraison Antsirabe',
      lotNumber: 'LOT-2611-A',
      expiryDate: '2027-02-12',
    })
  })

  it('shouldOmitLotAndEmptyReason_whenLotIsDisabled', () => {
    const values = { ...filled, reason: ' ', lotNumber: 'LOT-1', expiryDate: '2027-02-12' }

    expect(toMovementRequest(values, { ...ENTRY, withLot: false })).toEqual({
      type: 'IN',
      quantity: 12,
      reason: undefined,
    })
  })
})
