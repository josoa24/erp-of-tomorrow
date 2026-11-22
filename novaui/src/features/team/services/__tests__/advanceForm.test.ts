import { capHelper, remainingCap, toAdvanceRequest, validateAdvanceAmount } from '../advanceForm'

const normalize = (value: string): string => value.replace(/[  ]/g, ' ')
const employee = { advanceCap: 240000, monthAdvance: 150000 }

describe('advanceForm', () => {
  it('shouldComputeRemainingCap_neverBelowZero', () => {
    expect(remainingCap(employee)).toBe(90000)
    expect(remainingCap({ advanceCap: 100, monthAdvance: 300 })).toBe(0)
  })

  it.each([
    ['', 'Champ obligatoire'],
    ['0', expect.stringMatching(/montant/)],
    ['abc', expect.stringMatching(/montant/)],
    ['90 000', undefined],
  ])('shouldValidateAmount_%s', (value, expected) => {
    expect(validateAdvanceAmount(value)).toEqual(expected)
  })

  it('shouldShowRemainingCap', () => {
    expect(normalize(capHelper(employee, '50000'))).toBe('Plafond restant ce mois : 90 000 Ar sur 240 000 Ar')
  })

  it('shouldWarn_whenAmountExceedsRemainingCap', () => {
    expect(capHelper(employee, '90001')).toMatch(/dépasse le plafond restant/)
  })

  it('shouldBuildRequest', () => {
    expect(toAdvanceRequest(3, { amount: '80 000', reason: 'Rentrée scolaire' })).toEqual({
      employeeId: 3,
      amount: 80000,
      reason: 'Rentrée scolaire',
    })
  })
})
