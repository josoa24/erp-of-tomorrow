import { defaultCollectAmount, defaultCollectMethod, isCollectAmountValid, parseAmount } from '../collectForm'

describe('collectForm', () => {
  it('shouldParseAmountWithSpacesAndComma', () => {
    expect(parseAmount('186 000')).toBe(186000)
    expect(parseAmount('12,5')).toBe(12.5)
    expect(parseAmount(' ')).toBeNaN()
  })

  it.each([
    [186000, true],
    [1, true],
    [0, false],
    [186001, false],
    [Number.NaN, false],
    [10.555, false],
  ])('shouldValidateAmount_%s', (amount, valid) => {
    expect(isCollectAmountValid(amount, 186000)).toBe(valid)
  })

  it('shouldDefaultToSaleMobileMoney_orCash', () => {
    expect(defaultCollectMethod({ paymentMethod: 'ORANGE_MONEY' })).toBe('ORANGE_MONEY')
    expect(defaultCollectMethod({ paymentMethod: 'CREDIT' })).toBe('CASH')
    expect(defaultCollectMethod({ paymentMethod: null })).toBe('CASH')
  })

  it('shouldProposeRemainingAmount', () => {
    expect(defaultCollectAmount({ totalAmount: 420000, paidAmount: 120000 })).toBe('300000')
  })
})
