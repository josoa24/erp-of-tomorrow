import { makeToken } from '../../test/makeToken'
import { decodeJwtPayload, isTokenExpired } from '../jwt'

describe('decodeJwtPayload', () => {
  it('shouldReturnPayload_whenTokenIsWellFormed', () => {
    const token = makeToken({ sub: 'éa@erp.local', role: 'ADMIN', exp: 10 })

    expect(decodeJwtPayload(token)).toEqual({ sub: 'éa@erp.local', role: 'ADMIN', exp: 10 })
  })

  it('shouldReturnNull_whenTokenHasNoPayload', () => {
    expect(decodeJwtPayload('invalid')).toBeNull()
  })

  it('shouldReturnNull_whenPayloadIsNotJson', () => {
    expect(decodeJwtPayload('a.bm90LWpzb24.c')).toBeNull()
  })
})

describe('isTokenExpired', () => {
  const now = 1_000_000

  it('shouldBeFalse_whenExpirationIsInTheFuture', () => {
    expect(isTokenExpired(makeToken({ exp: 2000 }), now)).toBe(false)
  })

  it('shouldBeTrue_whenExpirationIsPassed', () => {
    expect(isTokenExpired(makeToken({ exp: 1000 }), now)).toBe(true)
  })

  it('shouldBeTrue_whenTokenIsUnreadable', () => {
    expect(isTokenExpired('garbage', now)).toBe(true)
  })
})
