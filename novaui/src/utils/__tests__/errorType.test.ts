import { toErrorType } from '../errorType'

describe('toErrorType', () => {
  it('shouldUseBackendType_whenKnown', () => {
    expect(toErrorType(409, 'IMMUTABLE')).toBe('business')
    expect(toErrorType(400, 'NOT_FOUND')).toBe('notFound')
  })

  it.each([
    [401, 'unauthorized'],
    [403, 'forbidden'],
    [404, 'notFound'],
    [409, 'business'],
    [422, 'validation'],
    [500, 'server'],
  ])('shouldMapStatus_%s_to_%s_whenBackendTypeIsMissing', (status, expected) => {
    expect(toErrorType(status, 'UNKNOWN')).toBe(expected)
  })
})
