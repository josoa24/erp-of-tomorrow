import { availabilityRate, percentChange } from '../trend'

describe('percentChange', () => {
  it('shouldReturnRoundedIncrease', () => {
    expect(percentChange(1120, 1000)).toBe(12)
  })

  it('shouldReturnNegativeValue_whenDecrease', () => {
    expect(percentChange(948, 1000)).toBe(-5.2)
  })

  it('shouldReturnNull_whenNoPreviousValue', () => {
    expect(percentChange(500, 0)).toBeNull()
  })
})

describe('availabilityRate', () => {
  it('shouldReturnShareOfProductsWithoutAlert', () => {
    expect(availabilityRate(5, 2)).toBe(60)
  })

  it('shouldReturnNull_whenNoProducts', () => {
    expect(availabilityRate(0, 0)).toBeNull()
  })
})
