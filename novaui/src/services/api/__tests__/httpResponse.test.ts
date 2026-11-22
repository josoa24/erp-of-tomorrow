import { readTotalCount } from '../httpResponse'

const headersWith = (value?: string) => new Headers(value === undefined ? {} : { 'X-Total-Count': value })

describe('readTotalCount', () => {
  it('shouldReturnTotal_whenHeaderIsAPositiveInteger', () => {
    expect(readTotalCount(headersWith('42'))).toBe(42)
  })

  it.each([undefined, '', 'abc', '-1', '2.5'])('shouldReturnNull_whenHeaderIs_%s', (value) => {
    expect(readTotalCount(headersWith(value))).toBeNull()
  })
})
